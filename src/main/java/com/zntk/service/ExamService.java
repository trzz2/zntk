package com.zntk.service;

import com.zntk.dto.ExamRankingResponse;
import com.zntk.dto.ExamResultResponse;
import com.zntk.dto.StartExamRequest;
import com.zntk.dto.SubmitExamRequest;

import java.util.List;

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
    /**
     * 查询某张试卷的成绩排行榜
     *
     * @param paperId 试卷 ID
     * @param limit 查询前多少名
     * @return 排行榜列表
     */
    List<ExamRankingResponse> getRanking(Long paperId, Integer limit);
}