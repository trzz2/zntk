package com.zntk.controller;

import com.zntk.common.Result;
import com.zntk.dto.LoginRequest;
import com.zntk.dto.LoginResponse;
import com.zntk.dto.RegisterRequest;
import com.zntk.entity.User;
import com.zntk.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录注册接口。
 */
@Tag(name = "登录认证", description = "用户注册、登录、退出登录和当前用户查询")
@RestController
public class AuthController {

    private static final String TOKEN_PREFIX = "Bearer ";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "用户注册", description = "创建普通用户账号，密码会使用 BCrypt 加密后保存")
    @PostMapping("/auth/register")
    public Result<Long> register(@RequestBody @Valid RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @Operation(summary = "用户登录", description = "校验用户名和密码，登录成功后返回 JWT Token")
    @PostMapping("/auth/login")
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @Operation(summary = "退出登录", description = "把当前 JWT Token 的 jti 加入 Redis 黑名单")
    @PostMapping("/auth/logout")
    public Result<Boolean> logout(
            @Parameter(description = "登录后获得的 JWT，格式：Bearer token")
            @RequestHeader("Authorization") String authorization
    ) {
        String token = authorization.startsWith(TOKEN_PREFIX)
                ? authorization.substring(TOKEN_PREFIX.length())
                : authorization;
        return Result.success(authService.logout(token));
    }

    @Operation(summary = "获取当前登录用户", description = "根据请求头中的 JWT Token 查询当前用户信息")
    @GetMapping("/auth/me")
    public Result<User> me() {
        return Result.success(authService.currentUser());
    }
}
