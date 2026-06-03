package com.zntk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zntk.dto.QuestionOptionRequest;
import com.zntk.dto.WrongQuestionDetailResponse;
import com.zntk.entity.Question;
import com.zntk.entity.QuestionOption;
import com.zntk.entity.WrongQuestion;
import com.zntk.mapper.QuestionMapper;
import com.zntk.mapper.QuestionOptionMapper;
import com.zntk.mapper.WrongQuestionMapper;
import com.zntk.service.WrongQuestionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 错题本业务实现类
 *
 * 真正查询 wrong_question 表的逻辑写在这里。
 */
@Service
public class WrongQuestionServiceImpl implements WrongQuestionService {

    /**
     * 错题 Mapper
     *
     * 用来操作 wrong_question 表。
     */
    private final WrongQuestionMapper wrongQuestionMapper;
    /**
     * 题目 Mapper
     *
     * 用来根据 questionId 查询题目详情。
     */
    private final QuestionMapper questionMapper;

    /**
     * 题目选项 Mapper
     *
     * 用来查询选择题、判断题的选项列表。
     */
    private final QuestionOptionMapper questionOptionMapper;
    /**
     * 构造器注入。
     *
     * Spring 会自动把 WrongQuestionMapper 对象传进来。
     */
    public WrongQuestionServiceImpl(
            WrongQuestionMapper wrongQuestionMapper,
            QuestionMapper questionMapper,
            QuestionOptionMapper questionOptionMapper
    ) {
        this.wrongQuestionMapper = wrongQuestionMapper;
        this.questionMapper = questionMapper;
        this.questionOptionMapper = questionOptionMapper;
    }

    @Override
    public List<WrongQuestionDetailResponse> listByUserId(Long userId) {
        // 1. 先查询用户错题记录。
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<>();

        // WHERE user_id = ?
        wrapper.eq(WrongQuestion::getUserId, userId);

        // ORDER BY last_wrong_time DESC
        wrapper.orderByDesc(WrongQuestion::getLastWrongTime);

        List<WrongQuestion> wrongQuestions = wrongQuestionMapper.selectList(wrapper);

        // 2. 创建返回给前端的错题详情列表。
        List<WrongQuestionDetailResponse> resultList = new ArrayList<>();

        // 3. 遍历每一条错题记录。
        for (WrongQuestion wrongQuestion : wrongQuestions) {
            // 根据错题记录中的 questionId 查询题目表。
            Question question = questionMapper.selectById(wrongQuestion.getQuestionId());

            // 如果题目不存在，跳过这条错题。
            if (question == null) {
                continue;
            }

            WrongQuestionDetailResponse response = new WrongQuestionDetailResponse();

            // 4. 设置错题记录相关字段。
            response.setWrongQuestionId(wrongQuestion.getId());
            response.setQuestionId(wrongQuestion.getQuestionId());
            response.setWrongAnswer(wrongQuestion.getWrongAnswer());
            response.setCorrectAnswer(wrongQuestion.getCorrectAnswer());
            response.setWrongCount(wrongQuestion.getWrongCount());
            response.setLastWrongTime(wrongQuestion.getLastWrongTime());

            // 5. 设置题目详情字段。
            response.setTitle(question.getTitle());
            response.setQuestionType(question.getQuestionType());
            response.setDifficulty(question.getDifficulty());
            response.setKnowledgePoint(question.getKnowledgePoint());
            response.setAnalysis(question.getAnalysis());

            // 6. 查询题目选项。
            LambdaQueryWrapper<QuestionOption> optionWrapper = new LambdaQueryWrapper<>();

            // WHERE question_id = ?
            optionWrapper.eq(QuestionOption::getQuestionId, question.getId());

            // ORDER BY sort_order ASC
            optionWrapper.orderByAsc(QuestionOption::getSortOrder);

            List<QuestionOption> questionOptions = questionOptionMapper.selectList(optionWrapper);

            // 7. 把 QuestionOption 实体转换成 QuestionOptionRequest DTO。
            //
            // 这里虽然名字叫 Request，但字段刚好也适合返回前端展示选项，
            // 后面如果想更规范，可以再新建 QuestionOptionResponse。
            List<QuestionOptionRequest> optionResponses = new ArrayList<>();

            for (QuestionOption questionOption : questionOptions) {
                QuestionOptionRequest optionResponse = new QuestionOptionRequest();

                optionResponse.setOptionLabel(questionOption.getOptionLabel());
                optionResponse.setOptionContent(questionOption.getOptionContent());
                optionResponse.setSortOrder(questionOption.getSortOrder());

                optionResponses.add(optionResponse);
            }

            response.setOptions(optionResponses);

            resultList.add(response);
        }

        return resultList;
    }
}