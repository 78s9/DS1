package com.ds1.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret:}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * HS256 的密钥就是密钥字符串的原始字节（RFC 7518）。
     *
     * <p>注意别用 {@code signWith(HS256, String)} 那个重载 —— jjwt 0.9.x 会把它当成
     * BASE64 去解码，导致实际参与签名的字节和 {@link com.ds1.config.JwtSecretValidator}
     * 校验的长度对不上（非 BASE64 字符被静默丢弃）。这里显式取原始字节，让校验真正有效。
     */
    private byte[] signingKey() {
        return secret.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Generate JWT token
     */
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, signingKey())
                .compact();
    }

    /**
     * Parse and validate the token in a single pass.
     * Returns the claims, or null if the token is invalid/expired.
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser().setSigningKey(signingKey()).parseClaimsJws(token).getBody();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
