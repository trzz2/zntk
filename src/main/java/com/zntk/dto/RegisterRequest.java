package com.zntk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "用户注册请求")
public class RegisterRequest {

    @Schema(description = "用户名", example = "zhangying")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "Password cannot be empty")
    private String password;

    @Schema(description = "昵称", example = "张莹")
    @NotBlank(message = "Nickname cannot be empty")
    private String nickname;
}
