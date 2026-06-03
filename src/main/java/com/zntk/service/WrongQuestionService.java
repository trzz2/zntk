package com.zntk.service;

import com.zntk.dto.WrongQuestionDetailResponse;
import com.zntk.entity.WrongQuestion;

import java.util.List;

/**
 * 错题本业务接口
 *
 * Controller 调用 Service。
 * Service 再调用 Mapper 查询数据库。
 */
public interface WrongQuestionService {

    /**
     * 查询某个用户的错题列表
     *
     * @param userId 用户 ID
     * @return 错题列表
     */
    /**
     * 查询某个用户的错题详情列表
     *
     * @param userId 用户 ID
     * @return 错题详情列表
     */
    List<WrongQuestionDetailResponse> listByUserId(Long userId);
}