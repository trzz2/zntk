package com.zntk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "题目选项请求")
public class QuestionOptionRequest {

    @Schema(description = "选项标识", example = "A")
    @NotBlank(message = "Option label cannot be empty")
    private String optionLabel;

    @Schema(description = "选项内容", example = "读写速度快")
    @NotBlank(message = "Option content cannot be empty")
    private String optionContent;

    @Schema(description = "排序值", example = "1")
    @NotNull(message = "Sort order cannot be empty")
    private Integer sortOrder;
}
