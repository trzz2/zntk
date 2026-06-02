package com.zntk.common;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器。
 *
 * 统一捕获 Controller 层抛出的异常，
 * 并转换成统一的 Result 返回给前端。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获 @Valid 参数校验失败异常。
     *
     * 例如：
     * @NotBlank(message = "题干不能为空")
     *
     * 如果前端没有传 title，
     * Spring 会抛出 MethodArgumentNotValidException，
     * 然后进入这个方法。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        // 获取第一个字段校验错误的提示信息
        String message = e.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return Result.fail(message);
    }

    /**
     * 捕获 RuntimeException 异常。
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        return Result.fail(e.getMessage());
    }

    /**
     * 捕获所有其他异常。
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.fail("系统异常，请稍后重试");
    }
}