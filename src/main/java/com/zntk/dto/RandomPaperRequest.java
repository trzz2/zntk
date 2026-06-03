package com.zntk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "随机组卷请求")
public class RandomPaperRequest {

    @Schema(description = "试卷标题", example = "Redis 随机测试卷")
    @NotBlank(message = "Paper title cannot be empty")
    private String title;

    @Schema(description = "试卷说明")
    private String description;

    @Schema(description = "考试时长，单位：分钟", example = "60")
    @NotNull(message = "Duration minutes cannot be empty")
    @Min(value = 1, message = "Duration minutes must be greater than 0")
    private Integer durationMinutes;

    @Schema(description = "单个题型：1 单选，2 多选，3 判断，4 简答")
    private Integer questionType;

    @Schema(description = "多个题型，优先级高于 questionType")
    private List<Integer> questionTypes;

    @Schema(description = "单个难度：1 简单，2 中等，3 困难")
    private Integer difficulty;

    @Schema(description = "多个难度，优先级高于 difficulty")
    private List<Integer> difficulties;

    @Schema(description = "知识点", example = "Redis")
    @NotBlank(message = "Knowledge point cannot be empty")
    private String knowledgePoint;

    @Schema(description = "抽题数量", example = "10")
    @NotNull(message = "Question count cannot be empty")
    @Min(value = 1, message = "Question count must be greater than 0")
    private Integer questionCount;

    @Schema(description = "每题分值", example = "5")
    @NotNull(message = "Score per question cannot be empty")
    @Min(value = 1, message = "Score per question must be greater than 0")
    private Integer scorePerQuestion;
}
