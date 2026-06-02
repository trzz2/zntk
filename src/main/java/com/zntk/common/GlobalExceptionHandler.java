package com.zntk.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器。
 *
 * 这个类负责统一捕获 Controller 层抛出的异常，
 * 并把异常转换成统一的 Result 返回给前端。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获 RuntimeException 异常。
     *
     * 例如 Service 中抛出：
     * throw new RuntimeException("题目不存在");
     *
     * 这里会把它转换成：
     * {
     *   "code": 500,
     *   "message": "题目不存在",
     *   "data": null
     * }
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        return Result.fail(e.getMessage());
    }

    /**
     * 捕获所有其他异常。
     *
     * 如果不是 RuntimeException，
     * 就返回一个通用错误提示，避免把太多内部细节暴露给前端。
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.fail("系统异常，请稍后重试");
    }
}