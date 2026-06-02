package com.zntk.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 提交考试时的一道题答案。
 */
@Data
public class SubmitAnswerRequest {

    /**
     * 题目 ID。
     */
    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    /**
     * 用户答案。
     *
     * 单选题例如 A，多选题例如 A,B。
     */
    private String userAnswer;
}