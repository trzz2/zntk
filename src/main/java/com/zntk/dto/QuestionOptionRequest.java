package com.zntk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增题目时传入的选项数据。
 *
 * 它用于接收前端传来的 options 数组中的每一项。
 */
@Data
public class QuestionOptionRequest {

    /**
     * 选项标识不能为空。
     * 例如 A、B、C、D。
     */
    @NotBlank(message = "选项标识不能为空")
    private String optionLabel;

    /**
     * 选项内容不能为空。
     */
    @NotBlank(message = "选项内容不能为空")
    private String optionContent;

    /**
     * 排序不能为空。
     */
    @NotNull(message = "选项排序不能为空")
    private Integer sortOrder;
}