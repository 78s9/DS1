package com.ds1.config;

import com.ds1.service.OperationLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {

    private final OperationLogService logService;

    public LogAspect(OperationLogService logService) {
        this.logService = logService;
    }

    /**
     * Intercept all methods in @RestController classes to auto-log requests.
     * Skips the OperationLogController itself to avoid infinite loops.
     */
    @Around("within(@org.springframework.web.bind.annotation.RestController *) " +
            "&& !within(com.ds1.controller.OperationLogController)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String username = getCurrentUsername();
        String action = getActionName(joinPoint);
        String module = getModuleName(joinPoint);
        String description = buildDescription(joinPoint);
        String ip = getClientIp();

        try {
            Object result = joinPoint.proceed();
            // Log success (skip GET queries to avoid noise — only log mutating operations)
            if (!"QUERY".equals(action)) {
                logService.log(username, action, module, description, ip, "SUCCESS");
            }
            return result;
        } catch (Throwable t) {
            // Log failure
            logService.log(username, action, module,
                    description + " | 异常: " + t.getMessage(), ip, "FAIL");
            throw t;
        }
    }

    /**
     * Determine action name from HTTP method and method name
     */
    private String getActionName(ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return "UNKNOWN";

        String httpMethod = attrs.getRequest().getMethod();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();

        // Map HTTP method + method name to action
        switch (httpMethod) {
            case "GET":    return "QUERY";
            case "POST":   return methodName.contains("register") ? "REGISTER"
                           : methodName.contains("login") ? "LOGIN" : "CREATE";
            case "PUT":    return "UPDATE";
            case "PATCH":  return "UPDATE";
            case "DELETE": return "DELETE";
            default:       return httpMethod;
        }
    }

    /**
     * Determine module name from the controller class
     */
    private String getModuleName(ProceedingJoinPoint joinPoint) {
        Class<?> controllerClass = joinPoint.getTarget().getClass();
        String simpleName = controllerClass.getSimpleName();

        if (simpleName.contains("Auth"))       return "认证";
        if (simpleName.contains("User"))       return "用户";
        if (simpleName.contains("Dashboard"))  return "仪表盘";
        return simpleName.replace("Controller", "");
    }

    /**
     * Build human-readable description from method and URI
     */
    private String buildDescription(ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return joinPoint.getSignature().toShortString();

        HttpServletRequest request = attrs.getRequest();
        String method = request.getMethod();
        String uri = request.getRequestURI();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return method + " " + uri;
    }

    /**
     * Get current authenticated username
     */
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "anonymous";
    }

    /**
     * Extract client IP address
     */
    private String getClientIp() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return "unknown";

        HttpServletRequest request = attrs.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
