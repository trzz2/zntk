package com.zntk.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zntk.dto.QuestionBatchCreateRequest;
import com.zntk.dto.QuestionCreateRequest;
import com.zntk.dto.QuestionDetailResponse;
import com.zntk.entity.Question;

import java.util.List;

/**
 * 题目业务接口。
 *
 * 它只定义“题目模块有哪些业务能力”，
 * 具体怎么实现，交给 QuestionServiceImpl。
 */
public interface QuestionService {

    /**
     * 普通列表查询。
     *
     * 这个方法会一次性返回所有符合条件的题目。
     */
    List<Question> listQuestions(Integer questionType, Integer difficulty, String knowledgePoint);

    /**
     * 分页查询题目。
     *
     * @param pageNo 当前第几页，例如 1 表示第一页
     * @param pageSize 每页多少条，例如 10 表示每页 10 条
     * @param questionType 题型筛选条件，可以不传
     * @param difficulty 难度筛选条件，可以不传
     * @param knowledgePoint 知识点筛选条件，可以不传
     * @return Page<Question> 分页结果对象，里面包含 records、total、current、size 等信息
     */
    Page<Question> pageQuestions(
            Long pageNo,
            Long pageSize,
            Integer questionType,
            Integer difficulty,
            String knowledgePoint
    );

    /**
     * 根据 ID 查询题目详情。
     */
    QuestionDetailResponse getQuestionById(Long id);

    /**
     * 新增题目。
     */
    Long createQuestion(QuestionCreateRequest request);

    /**
     * 根据 ID 修改题目。
     */
    Boolean updateQuestion(Long id, Question question);

    /**
     * 根据 ID 删除题目。
     */
    Boolean deleteQuestion(Long id);

    /**
     * 批量新增题目
     *
     * @param request 批量新增请求，里面包含多道题
     * @return 新增成功的题目 ID 列表
     */
    List<Long> batchCreate(QuestionBatchCreateRequest request);
}