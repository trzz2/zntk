package com.zntk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "收藏题目请求")
public class FavoriteQuestionRequest {

    @Schema(description = "用户 ID，后端会从 Token 中自动设置，前端不用传")
    private Long userId;

    @Schema(description = "题目 ID")
    @NotNull(message = "Question ID cannot be empty")
    private Long questionId;
}
