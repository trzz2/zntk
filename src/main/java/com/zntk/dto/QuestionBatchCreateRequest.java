package com.zntk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "批量新增题目请求")
public class QuestionBatchCreateRequest {

    @Schema(description = "题目列表")
    @NotEmpty(message = "Question list cannot be empty")
    @Valid
    private List<QuestionCreateRequest> questions;
}
