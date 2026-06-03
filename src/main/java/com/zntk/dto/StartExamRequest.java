package com.zntk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "开始考试请求")
public class StartExamRequest {

    @Schema(description = "试卷 ID")
    @NotNull(message = "Paper ID cannot be empty")
    private Long paperId;

    @Schema(description = "用户 ID，后端会从 Token 中自动设置，前端不用传")
    private Long userId;
}
