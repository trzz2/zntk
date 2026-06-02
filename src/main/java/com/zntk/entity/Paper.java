package com.zntk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 试卷实体类。
 *
 * 对应数据库 paper 表。
 */
@Data
public class Paper {

    /**
     * 试卷 ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 试卷标题。
     */
    private String title;

    /**
     * 试卷描述。
     */
    private String description;

    /**
     * 总分。
     */
    private Integer totalScore;

    /**
     * 考试时长，单位分钟。
     */
    private Integer durationMinutes;

    /**
     * 状态：0-禁用，1-启用。
     */
    private Integer status;

    /**
     * 创建时间。
     */
    private LocalDateTime createTime;

    /**
     * 更新时间。
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除。
     */
    @TableLogic
    private Integer deleted;
}