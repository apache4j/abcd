package com.cloud.baowang.agent.api.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 代理审核状态枚举
 */
public enum AgentReviewStatusEnum {

    PENDING("1", "待处理"),
    PROCESSING("2", "处理中"),
    PASS("3", "审核通过"),
    REJECT("4", "一审拒绝"),

    ;

    private String code;
    private String name;

    AgentReviewStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static AgentReviewStatusEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentReviewStatusEnum[] types = AgentReviewStatusEnum.values();
        for (AgentReviewStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentReviewStatusEnum> getList() {
        return Arrays.asList(values());
    }
}
