package com.zntk.service;

import com.zntk.dto.ExamResultResponse;
import com.zntk.dto.StartExamRequest;
import com.zntk.dto.SubmitExamRequest;

/**
 * 考试业务接口。
 */
public interface ExamService {

    /**
     * 开始考试，生成考试记录。
     */
    Long startExam(StartExamRequest request);

    /**
     * 提交考试，自动判分。
     */
    Boolean submitExam(SubmitExamRequest request);

    /**
     * 查询考试结果。
     */
    ExamResultResponse getExamResult(Long id);
}