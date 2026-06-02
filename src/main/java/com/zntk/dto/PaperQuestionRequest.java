package com.zntk.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建试卷时传入的题目配置。
 *
 * 表示：某张试卷中包含哪道题、
 * 这道题多少分、排在第几个。
 */
@Data
public class PaperQuestionRequest {

    /**
     * 题目 ID。
     *
     * 用来关联 question 表。
     */
    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    /**
     * 本题分值。
     */
    @NotNull(message = "题目分值不能为空")
    private Integer score;

    /**
     * 题目顺序。
     */
    @NotNull(message = "题目顺序不能为空")
    private Integer sortOrder;
}