package com.zntk.enums;

/**
 * 题型枚举。
 *
 * 用来集中管理题型数字和中文含义的对应关系。
 */
public enum QuestionTypeEnum {

    /**
     * 单选题。
     */
    SINGLE_CHOICE(1, "单选题"),

    /**
     * 多选题。
     */
    MULTIPLE_CHOICE(2, "多选题"),

    /**
     * 判断题。
     */
    TRUE_FALSE(3, "判断题"),

    /**
     * 简答题。
     */
    SHORT_ANSWER(4, "简答题");

    /**
     * 存到数据库里的数字值。
     */
    private final Integer code;

    /**
     * 给人看的中文描述。
     */
    private final String desc;

    QuestionTypeEnum(Integer code, String desc) {
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
     * 判断传入的 code 是否是合法题型。
     */
    public static boolean isValid(Integer code) {
        if (code == null) {
            return false;
        }

        for (QuestionTypeEnum item : values()) {
            if (item.getCode().equals(code)) {
                return true;
            }
        }

        return false;
    }
}