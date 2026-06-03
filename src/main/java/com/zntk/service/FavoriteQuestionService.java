package com.zntk.service;

import com.zntk.dto.FavoriteQuestionDetailResponse;
import com.zntk.dto.FavoriteQuestionRequest;

import java.util.List;

public interface FavoriteQuestionService {

    Boolean favorite(FavoriteQuestionRequest request);

    List<FavoriteQuestionDetailResponse> listByUserId(Long userId);

    Boolean cancel(Long userId, Long questionId);
}
