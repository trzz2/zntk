package com.zntk.service;

import com.zntk.dto.PaperCreateRequest;
import com.zntk.dto.PaperDetailResponse;

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
}