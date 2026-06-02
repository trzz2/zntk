package com.zntk.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 开始考试请求。
 *
 * 前端传入用户 ID 和试卷 ID，
 * 后端根据这些信息生成一条考试记录。
 */
@Data
public class StartExamRequest {

    /**
     * 试卷 ID。
     */
    @NotNull(message = "试卷ID不能为空")
    private Long paperId;

    /**
     * 用户 ID。
     *
     * 目前项目还没做登录，
     * 所以先由前端直接传 userId。
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
}