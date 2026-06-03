package com.zntk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "提交答案请求")
public class SubmitAnswerRequest {

    @Schema(description = "题目 ID")
    @NotNull(message = "Question ID cannot be empty")
    private Long questionId;

    @Schema(description = "用户答案，单选如 A，多选如 A,B")
    private String userAnswer;
}
