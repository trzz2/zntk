package com.zntk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zntk.dto.FavoriteQuestionDetailResponse;
import com.zntk.dto.FavoriteQuestionRequest;
import com.zntk.dto.QuestionOptionRequest;
import com.zntk.entity.FavoriteQuestion;
import com.zntk.entity.Question;
import com.zntk.entity.QuestionOption;
import com.zntk.mapper.FavoriteQuestionMapper;
import com.zntk.mapper.QuestionMapper;
import com.zntk.mapper.QuestionOptionMapper;
import com.zntk.service.FavoriteQuestionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FavoriteQuestionServiceImpl implements FavoriteQuestionService {

    private final FavoriteQuestionMapper favoriteQuestionMapper;
    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper questionOptionMapper;

    public FavoriteQuestionServiceImpl(
            FavoriteQuestionMapper favoriteQuestionMapper,
            QuestionMapper questionMapper,
            QuestionOptionMapper questionOptionMapper
    ) {
        this.favoriteQuestionMapper = favoriteQuestionMapper;
        this.questionMapper = questionMapper;
        this.questionOptionMapper = questionOptionMapper;
    }

    @Override
    public Boolean favorite(FavoriteQuestionRequest request) {
        LambdaQueryWrapper<FavoriteQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FavoriteQuestion::getUserId, request.getUserId());
        wrapper.eq(FavoriteQuestion::getQuestionId, request.getQuestionId());

        FavoriteQuestion oldFavorite = favoriteQuestionMapper.selectOne(wrapper);
        if (oldFavorite == null) {
            FavoriteQuestion favoriteQuestion = new FavoriteQuestion();
            favoriteQuestion.setUserId(request.getUserId());
            favoriteQuestion.setQuestionId(request.getQuestionId());
            favoriteQuestion.setCreateTime(LocalDateTime.now());
            favoriteQuestion.setDeleted(0);
            favoriteQuestionMapper.insert(favoriteQuestion);
            return true;
        }

        if (Integer.valueOf(0).equals(oldFavorite.getDeleted())) {
            throw new RuntimeException("Question already favorited");
        }

        oldFavorite.setDeleted(0);
        favoriteQuestionMapper.updateById(oldFavorite);
        return true;
    }

    @Override
    public List<FavoriteQuestionDetailResponse> listByUserId(Long userId) {
        LambdaQueryWrapper<FavoriteQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FavoriteQuestion::getUserId, userId);
        wrapper.eq(FavoriteQuestion::getDeleted, 0);
        wrapper.orderByDesc(FavoriteQuestion::getCreateTime);

        List<FavoriteQuestionDetailResponse> resultList = new ArrayList<>();
        for (FavoriteQuestion favoriteQuestion : favoriteQuestionMapper.selectList(wrapper)) {
            Question question = questionMapper.selectById(favoriteQuestion.getQuestionId());
            if (question == null) {
                continue;
            }

            FavoriteQuestionDetailResponse response = new FavoriteQuestionDetailResponse();
            response.setFavoriteQuestionId(favoriteQuestion.getId());
            response.setQuestionId(favoriteQuestion.getQuestionId());
            response.setCreateTime(favoriteQuestion.getCreateTime());
            response.setTitle(question.getTitle());
            response.setQuestionType(question.getQuestionType());
            response.setDifficulty(question.getDifficulty());
            response.setKnowledgePoint(question.getKnowledgePoint());
            response.setAnswer(question.getAnswer());
            response.setAnalysis(question.getAnalysis());
            response.setOptions(listOptions(question.getId()));

            resultList.add(response);
        }

        return resultList;
    }

    @Override
    public Boolean cancel(Long userId, Long questionId) {
        LambdaQueryWrapper<FavoriteQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FavoriteQuestion::getUserId, userId);
        wrapper.eq(FavoriteQuestion::getQuestionId, questionId);

        FavoriteQuestion favoriteQuestion = favoriteQuestionMapper.selectOne(wrapper);
        if (favoriteQuestion == null || Integer.valueOf(1).equals(favoriteQuestion.getDeleted())) {
            throw new RuntimeException("Favorite record not found");
        }

        favoriteQuestion.setDeleted(1);
        favoriteQuestionMapper.updateById(favoriteQuestion);
        return true;
    }

    private List<QuestionOptionRequest> listOptions(Long questionId) {
        LambdaQueryWrapper<QuestionOption> optionWrapper = new LambdaQueryWrapper<>();
        optionWrapper.eq(QuestionOption::getQuestionId, questionId);
        optionWrapper.orderByAsc(QuestionOption::getSortOrder);

        List<QuestionOptionRequest> options = new ArrayList<>();
        for (QuestionOption questionOption : questionOptionMapper.selectList(optionWrapper)) {
            QuestionOptionRequest option = new QuestionOptionRequest();
            option.setOptionLabel(questionOption.getOptionLabel());
            option.setOptionContent(questionOption.getOptionContent());
            option.setSortOrder(questionOption.getSortOrder());
            options.add(option);
        }
        return options;
    }
}
