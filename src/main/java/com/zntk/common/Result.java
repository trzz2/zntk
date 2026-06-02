package com.zntk.common;

import lombok.Data;

/**
 * 统一接口返回结果。
 *
 * 前端不直接接收零散的 List、Boolean、Long，
 * 而是统一接收 code、message、data 三部分。
 *
 * @param <T> data 的数据类型
 */
@Data
public class Result<T> {

    /**
     * 业务状态码。
     * 200 表示成功，其他值以后可以表示不同错误。
     */
    private Integer code;

    /**
     * 返回给前端的提示信息。
     */
    private String message;

    /**
     * 真正的数据内容。
     * 例如题目列表、题目详情、新增后的 ID、删除结果等。
     */
    private T data;

    /**
     * 私有构造方法。
     * 外部不要直接 new Result，而是通过 success/fail 方法创建。
     */
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回，有数据。
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /**
     * 成功返回，没有数据。
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    /**
     * 失败返回。
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(500, message, null);
    }
}