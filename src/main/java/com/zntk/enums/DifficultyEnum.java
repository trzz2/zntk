package com.zntk.enums;

/**
 * 题目难度枚举。
 */
public enum DifficultyEnum {

    /**
     * 简单。
     */
    EASY(1, "简单"),

    /**
     * 中等。
     */
    MEDIUM(2, "中等"),

    /**
     * 困难。
     */
    HARD(3, "困难");

    /**
     * 存到数据库里的数字值。
     */
    private final Integer code;

    /**
     * 中文描述。
     */
    private final String desc;

    DifficultyEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 判断传入的 code 是否是合法难度。
     */
    public static boolean isValid(Integer code) {
        if (code == null) {
            return false;
        }

        for (DifficultyEnum item : values()) {
            if (item.getCode().equals(code)) {
                return true;
            }
        }

        return false;
    }
}