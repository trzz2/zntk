package com.zntk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "新增题目请求")
public class QuestionCreateRequest {

    @Schema(description = "题目标题", example = "Redis 为什么适合做缓存？")
    @NotBlank(message = "Question title cannot be empty")
    private String title;

    @Schema(description = "题型：1 单选，2 多选，3 判断，4 简答", example = "1")
    @NotNull(message = "Question type cannot be empty")
    private Integer questionType;

    @Schema(description = "难度：1 简单，2 中等，3 困难", example = "2")
    @NotNull(message = "Difficulty cannot be empty")
    private Integer difficulty;

    @Schema(description = "知识点", example = "Redis")
    private String knowledgePoint;

    @Schema(description = "正确答案", example = "A")
    private String answer;

    @Schema(description = "题目解析", example = "Redis 数据存储在内存中，读写速度快。")
    private String analysis;

    @Schema(description = "状态：0 禁用，1 启用", example = "1")
    private Integer status;

    @Schema(description = "题目选项列表")
    @Valid
    private List<QuestionOptionRequest> options;
}
