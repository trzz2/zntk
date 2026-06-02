package com.zntk.dto;

import com.zntk.entity.AnswerRecord;
import com.zntk.entity.ExamRecord;
import lombok.Data;

import java.util.List;

/**
 * 考试结果响应。
 *
 * 返回考试记录 + 每道题答题情况。
 */
@Data
public class ExamResultResponse {

    /**
     * 考试记录。
     */
    private ExamRecord examRecord;

    /**
     * 答题记录列表。
     */
    private List<AnswerRecord> answerRecords;
}