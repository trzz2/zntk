package com.zntk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "创建试卷请求")
public class PaperCreateRequest {

    @Schema(description = "试卷标题", example = "Java 基础测试卷")
    @NotBlank(message = "Paper title cannot be empty")
    private String title;

    @Schema(description = "试卷说明", example = "用于测试 Java、Redis、Spring Boot 基础知识")
    private String description;

    @Schema(description = "考试时长，单位：分钟", example = "60")
    @NotNull(message = "Duration minutes cannot be empty")
    private Integer durationMinutes;

    @Schema(description = "状态：0 禁用，1 启用", example = "1")
    private Integer status;

    @Schema(description = "试卷题目配置列表")
    @Valid
    private List<PaperQuestionRequest> questions;
}
