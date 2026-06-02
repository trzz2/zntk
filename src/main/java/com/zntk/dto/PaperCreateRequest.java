package com.zntk.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 创建试卷请求对象。
 *
 * 前端创建试卷时，会传入试卷基础信息，
 * 以及这张试卷包含的题目列表。
 */
@Data
public class PaperCreateRequest {

    /**
     * 试卷标题不能为空。
     */
    @NotBlank(message = "试卷标题不能为空")
    private String title;

    /**
     * 试卷描述。
     */
    private String description;

    /**
     * 考试时长，单位分钟。
     */
    @NotNull(message = "考试时长不能为空")
    private Integer durationMinutes;

    /**
     * 状态：0-禁用，1-启用。
     */
    private Integer status;

    /**
     * 试卷题目列表。
     *
     * @Valid 表示继续校验 questions 里面每一个 PaperQuestionRequest。
     */
    @Valid
    private List<PaperQuestionRequest> questions;
}