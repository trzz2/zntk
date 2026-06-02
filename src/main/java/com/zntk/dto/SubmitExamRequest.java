package com.zntk.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 提交考试请求。
 *
 * 前端提交某次考试的所有答案。
 */
@Data
public class SubmitExamRequest {

    /**
     * 考试记录 ID。
     */
    @NotNull(message = "考试记录ID不能为空")
    private Long examRecordId;

    /**
     * 用户答案列表。
     */
    @Valid
    private List<SubmitAnswerRequest> answers;
}