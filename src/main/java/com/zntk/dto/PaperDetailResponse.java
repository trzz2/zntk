package com.zntk.dto;

import com.zntk.entity.Paper;
import com.zntk.entity.Question;
import lombok.Data;

import java.util.List;

/**
 * 试卷详情响应对象。
 *
 * 查询试卷详情时返回：
 * 试卷基础信息 + 题目列表。
 */
@Data
public class PaperDetailResponse {

    /**
     * 试卷基础信息。
     */
    private Paper paper;

    /**
     * 试卷中的题目列表。
     */
    private List<Question> questions;
}