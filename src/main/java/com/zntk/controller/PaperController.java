package com.zntk.controller;

import com.zntk.common.RequireAdmin;
import com.zntk.common.Result;
import com.zntk.dto.PaperCreateRequest;
import com.zntk.dto.PaperDetailResponse;
import com.zntk.dto.RandomPaperRequest;
import com.zntk.service.PaperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "试卷管理", description = "创建试卷、查询试卷详情和随机组卷")
@RestController
@RequestMapping("/papers")
public class PaperController {

    private final PaperService paperService;

    public PaperController(PaperService paperService) {
        this.paperService = paperService;
    }

    @RequireAdmin
    @Operation(summary = "创建试卷", description = "管理员创建试卷，并配置试卷中的题目、分值和顺序")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody PaperCreateRequest request) {
        return Result.success(paperService.createPaper(request));
    }

    @Operation(summary = "查询试卷详情", description = "根据试卷 ID 查询试卷基础信息和题目列表")
    @GetMapping("/{id}")
    public Result<PaperDetailResponse> getById(@Parameter(description = "试卷 ID") @PathVariable Long id) {
        return Result.success(paperService.getPaperById(id));
    }

    @RequireAdmin
    @Operation(summary = "随机组卷", description = "根据题型、难度、知识点等条件随机抽题并生成试卷")
    @PostMapping("/random")
    public Result<Long> randomPaper(@RequestBody @Valid RandomPaperRequest request) {
        return Result.success(paperService.randomPaper(request));
    }
}
