package com.zntk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "用户登录请求")
public class LoginRequest {

    @Schema(description = "用户名", example = "zhangying")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "Password cannot be empty")
    private String password;
}
