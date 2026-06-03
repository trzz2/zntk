package com.zntk.controller;

import com.zntk.common.Result;
import com.zntk.dto.PaperCreateRequest;
import com.zntk.dto.PaperDetailResponse;
import com.zntk.dto.RandomPaperRequest;
import com.zntk.service.PaperService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 试卷接口控制器。
 *
 * 负责接收试卷相关 HTTP 请求。
 */
@RestController
@RequestMapping("/papers")
public class PaperController {

    private final PaperService paperService;

    /**
     * 构造器注入 PaperService。
     */
    public PaperController(PaperService paperService) {
        this.paperService = paperService;
    }

    /**
     * 创建试卷。
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody PaperCreateRequest request) {
        Long id = paperService.createPaper(request);
        return Result.success(id);
    }

    /**
     * 查询试卷详情。
     */
    @GetMapping("/{id}")
    public Result<PaperDetailResponse> getById(@PathVariable Long id) {
        PaperDetailResponse paper = paperService.getPaperById(id);
        return Result.success(paper);
    }


    /**
     * 随机组卷
     *
     * 请求示例：
     * POST /papers/random
     *
     * 根据题型、难度、知识点、题目数量等条件，
     * 自动从题库中抽题生成一张试卷。
     */
    @PostMapping("/random")
    public Result<Long> randomPaper(@RequestBody @Valid RandomPaperRequest request) {
        // 调用 Service 完成随机组卷逻辑。
        // 返回值是新生成的试卷 ID。
        Long paperId = paperService.randomPaper(request);

        return Result.success(paperId);
    }
}