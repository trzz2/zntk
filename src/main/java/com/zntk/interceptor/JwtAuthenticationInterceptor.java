package com.zntk.interceptor;

import com.zntk.common.ForbiddenException;
import com.zntk.common.RequireAdmin;
import com.zntk.common.UnauthorizedException;
import com.zntk.common.UserContext;
import com.zntk.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * JWT authentication interceptor.
 *
 * It reads Authorization: Bearer token, verifies the token, checks logout blacklist,
 * then stores current user info in UserContext for this request.
 */
@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String BLACKLIST_KEY_PREFIX = "jwt:blacklist:";

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    public JwtAuthenticationInterceptor(JwtUtil jwtUtil, StringRedisTemplate stringRedisTemplate) {
        this.jwtUtil = jwtUtil;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith(TOKEN_PREFIX)) {
            throw new UnauthorizedException("Please login first");
        }

        String token = authorization.substring(TOKEN_PREFIX.length());
        Map<String, Object> payload = jwtUtil.parseToken(token);

        String tokenId = payload.get("jti").toString();
        Boolean blacklisted = stringRedisTemplate.hasKey(BLACKLIST_KEY_PREFIX + tokenId);
        if (Boolean.TRUE.equals(blacklisted)) {
            throw new UnauthorizedException("Login expired, please login again");
        }

        Long userId = Long.valueOf(payload.get("userId").toString());
        String username = payload.get("username").toString();
        Integer role = Integer.valueOf(payload.get("role").toString());

        UserContext.set(new UserContext.LoginUser(userId, username, role));

        if (handler instanceof HandlerMethod handlerMethod
                && handlerMethod.hasMethodAnnotation(RequireAdmin.class)
                && !Integer.valueOf(1).equals(role)) {
            throw new ForbiddenException("Admin permission required");
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
