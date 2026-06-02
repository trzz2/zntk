package com.zntk.dto;

import com.zntk.entity.Question;
import com.zntk.entity.QuestionOption;
import lombok.Data;

import java.util.List;

/**
 * 题目详情响应对象。
 *
 * 用于返回：
 * 题目基础信息 + 题目选项列表。
 */
@Data
public class QuestionDetailResponse {

    /**
     * 题目基础信息。
     */
    private Question question;

    /**
     * 题目选项列表。
     */
    private List<QuestionOption> options;
}