package com.zntk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目选项实体类。
 *
 * 对应数据库中的 question_option 表。
 * 一道选择题可以有多个选项，例如 A、B、C、D。
 */
@Data
public class QuestionOption {

    /**
     * 选项 ID。
     *
     * 使用 MyBatis-Plus 的雪花算法自动生成。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 题目 ID。
     *
     * 用来关联 question 表中的 id。
     * 多个选项可以拥有同一个 questionId，
     * 表示它们属于同一道题。
     */
    private Long questionId;

    /**
     * 选项标识。
     *
     * 例如 A、B、C、D。
     */
    private String optionLabel;

    /**
     * 选项内容。
     *
     * 例如：“查询不存在的数据导致请求直接打到数据库”。
     */
    private String optionContent;

    /**
     * 排序字段。
     *
     * 用来控制选项返回顺序。
     * 例如 A=1，B=2，C=3，D=4。
     */
    private Integer sortOrder;

    /**
     * 创建时间。
     *
     * 对应数据库字段 create_time。
     */
    private LocalDateTime createTime;

    /**
     * 更新时间。
     *
     * 对应数据库字段 update_time。
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除字段。
     *
     * 0 表示未删除，1 表示已删除。
     */
    @TableLogic
    private Integer deleted;
}