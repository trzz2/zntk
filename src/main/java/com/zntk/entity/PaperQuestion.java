package com.zntk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 试卷题目关联实体类。
 *
 * 对应 paper_question 表。
 * 用来表示：某张试卷包含某道题。
 */
@Data
public class PaperQuestion {

    /**
     * 关联 ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 试卷 ID。
     */
    private Long paperId;

    /**
     * 题目 ID。
     */
    private Long questionId;

    /**
     * 本题分值。
     */
    private Integer score;

    /**
     * 题目在试卷中的顺序。
     */
    private Integer sortOrder;

    /**
     * 创建时间。
     */
    private LocalDateTime createTime;

    /**
     * 逻辑删除。
     */
    @TableLogic
    private Integer deleted;
}