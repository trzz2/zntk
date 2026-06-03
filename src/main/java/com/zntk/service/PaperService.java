package com.zntk.service;

import com.zntk.dto.PaperCreateRequest;
import com.zntk.dto.PaperDetailResponse;
import com.zntk.dto.RandomPaperRequest;

/**
 * 试卷业务接口。
 *
 * 定义试卷模块有哪些业务能力。
 */
public interface PaperService {

    /**
     * 创建试卷。
     *
     * @param request 创建试卷请求，包含试卷基础信息和题目列表
     * @return 新生成的试卷 ID
     */
    Long createPaper(PaperCreateRequest request);

    /**
     * 查询试卷详情。
     *
     * @param id 试卷 ID
     * @return 试卷详情，包含试卷基础信息和题目列表
     */
    PaperDetailResponse getPaperById(Long id);

    /**
     * 随机组卷
     *
     * 根据题型、难度、知识点、题目数量等条件，
     * 自动从题库中抽题生成试卷。
     *
     * @param request 随机组卷请求参数
     * @return 新生成的试卷 ID
     */
    Long randomPaper(RandomPaperRequest request);
}