package com.zntk.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 新增题目请求对象。
 *
 * 这个类负责接收前端新增题目时传来的 JSON。
 * 使用参数校验注解后，Spring 会在进入 Controller 方法前自动检查字段是否合法。
 */
@Data
public class QuestionCreateRequest {

    /**
     * 题干不能为空。
     *
     * @NotBlank 用于 String 类型：
     * 不能为 null，也不能是空字符串或全空格。
     */
    @NotBlank(message = "题干不能为空")
    private String title;

    /**
     * 题型不能为空。
     *
     * @NotNull 用于 Integer、Long 等对象类型：
     * 不能为 null。
     */
    @NotNull(message = "题型不能为空")
    private Integer questionType;

    /**
     * 难度不能为空。
     */
    @NotNull(message = "难度不能为空")
    private Integer difficulty;

    /**
     * 知识点。
     * 暂时允许为空。
     */
    private String knowledgePoint;

    /**
     * 答案。
     */
    private String answer;

    /**
     * 解析。
     */
    private String analysis;

    /**
     * 状态：0-禁用，1-启用。
     */
    private Integer status;

    /**
     * 题目选项列表。
     *
     * @Valid 表示继续校验 List 里面每一个 QuestionOptionRequest。
     * 例如选项里的 optionLabel、optionContent、sortOrder。
     */
    @Valid
    private List<QuestionOptionRequest> options;
}