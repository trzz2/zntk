package com.zntk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "提交试卷请求")
public class SubmitExamRequest {

    @Schema(description = "考试记录 ID")
    @NotNull(message = "Exam record ID cannot be empty")
    private Long examRecordId;

    @Schema(description = "用户答案列表")
    @NotEmpty(message = "Answer list cannot be empty")
    @Valid
    private List<SubmitAnswerRequest> answers;
}
