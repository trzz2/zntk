package com.zntk.dto;

import lombok.Data;

/**
 * 新增题目时传入的选项数据。
 *
 * 它不是数据库实体类，
 * 只是用来接收前端请求里的 options 数组。
 */
@Data
public class QuestionOptionRequest {

    /**
     * 选项标识。
     * 例如 A、B、C、D。
     */
    private String optionLabel;

    /**
     * 选项内容。
     */
    private String optionContent;

    /**
     * 选项排序。
     * 例如 A=1，B=2，C=3，D=4。
     */
    private Integer sortOrder;
}