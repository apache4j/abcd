package com.cloud.baowang.agent.api.enums;

/**
 * @author: fangfei
 * @createTime: 2024/10/21 23:51
 * @description:
 */
public enum SettleCycleEnum {
    DAY(1, "自然日"),
    WEEK(2, "自然周"),
    MONTH(3, "自然月"),
    ;

    private Integer code;
    private String desc;

    SettleCycleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
