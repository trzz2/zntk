package com.zntk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目选项实体类。
 *
 * 一道选择题通常会有多个选项，
 * 例如 A、B、C、D。
 */
@Data
public class QuestionOption {

    /**
     * 选项 ID。
     * 使用 MyBatis-Plus 雪花算法自动生成。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 题目 ID。
     * 用来关联 question 表中的 id。
     */
    private Long questionId;

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
     * 排序字段。
     * 用于控制选项显示顺序。
     */
    private Integer sortOrder;

    /**
     * 创建时间。
     */
    private LocalDateTime createTime;

    /**
     * 更新时间。
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除字段。
     * 0 表示未删除，1 表示已删除。
     */
    @TableLogic
    private Integer deleted;
}