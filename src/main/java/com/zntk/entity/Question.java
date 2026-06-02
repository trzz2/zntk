package com.zntk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Question {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String title;

    private Integer questionType;

    private Integer difficulty;

    private String knowledgePoint;

    private String answer;

    private String analysis;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}