package com.zntk.service;

import com.zntk.dto.WrongQuestionDetailResponse;

import java.util.List;

public interface WrongQuestionService {

    List<WrongQuestionDetailResponse> listByUserId(Long userId);
}
