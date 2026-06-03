package com.zntk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "试卷题目配置请求")
public class PaperQuestionRequest {

    @Schema(description = "题目 ID")
    @NotNull(message = "Question ID cannot be empty")
    private Long questionId;

    @Schema(description = "题目分值", example = "5")
    @NotNull(message = "Question score cannot be empty")
    private Integer score;

    @Schema(description = "题目在试卷中的排序", example = "1")
    @NotNull(message = "Sort order cannot be empty")
    private Integer sortOrder;
}
