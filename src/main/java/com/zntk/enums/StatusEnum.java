package com.zntk.enums;

/**
 * 通用状态枚举。
 */
public enum StatusEnum {

    /**
     * 禁用。
     */
    DISABLED(0, "禁用"),

    /**
     * 启用。
     */
    ENABLED(1, "启用");

    /**
     * 存到数据库里的数字值。
     */
    private final Integer code;

    /**
     * 中文描述。
     */
    private final String desc;

    StatusEnum(Integer code, String desc) {
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
     * 判断传入的 code 是否是合法状态。
     */
    public static boolean isValid(Integer code) {
        if (code == null) {
            return false;
        }

        for (StatusEnum item : values()) {
            if (item.getCode().equals(code)) {
                return true;
            }
        }

        return false;
    }
}