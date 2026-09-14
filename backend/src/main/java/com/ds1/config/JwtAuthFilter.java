package com.ds1.config;

import com.ds1.entity.User;
import com.ds1.repository.UserRepository;
import com.ds1.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (StringUtils.hasText(token)) {
            // Parse once; null means invalid/expired token
            Claims claims = jwtUtil.parseToken(token);
            if (claims != null) {
                authenticate(claims);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 用 token 里的用户名回查一次数据库，而不是无条件信任 token 里的 role 声明。
     *
     * <p>无状态 JWT 的通病：token 一旦签发，其中的 role 就固定了。管理员把某人降级为
     * USER、或直接删号之后，对方手上那个 token 在过期前（默认 24 小时）仍然自称
     * ADMIN —— 权限回收等于没做。这里改为以数据库为准：账号不存在就不认证，
     * 角色以库里的为准（降权与升权都即时生效）。
     *
     * <p>代价是每个带 token 的请求多一次按用户名索引的查询。换来的是一条真实存在的
     * 越权路径被堵上。若日后成为瓶颈，可加一层秒级 TTL 缓存 —— 但那个 TTL 就是
     * 权限回收的最大延迟，需要显式选一个可接受的值，而不是随手加。
     *
     * <p>查询异常时按「不认证」处理（fail-closed）：这是安全边界，宁可让用户重新登录，
     * 也不能因为 DB 抖动就放行一个权限不明的身份。
     */
    private void authenticate(Claims claims) {
        String username = claims.getSubject();
        if (!StringUtils.hasText(username)) {
            return;
        }

        Optional<User> found;
        try {
            found = userRepository.findByUsername(username);
        } catch (Exception e) {
            log.warn("校验 token 时查询用户失败，按未认证处理: username={}", username, e);
            return;
        }

        if (!found.isPresent()) {
            // 账号已被删除，其 token 立即作废
            return;
        }

        String role = found.get().getRole();
        String authority = "ADMIN".equals(role) ? "ROLE_ADMIN" : "ROLE_USER";

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        username, null,
                        Collections.singletonList(new SimpleGrantedAuthority(authority))
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
