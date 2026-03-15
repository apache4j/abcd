package com.cloud.baowang.agent.api.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 代理审核操作枚举
 */
public enum AgentReviewOperationEnum {
    //审核操作 1一审审核 2结单查看
    FIRST_INSTANCE_REVIEW("1", "一审审核"),
    CHECK("2", "结单查看");

    private String code;
    private String name;

    AgentReviewOperationEnum(String code, String name) {
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

    public static AgentReviewOperationEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentReviewOperationEnum[] types = AgentReviewOperationEnum.values();
        for (AgentReviewOperationEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentReviewOperationEnum> getList() {
        return Arrays.asList(values());
    }
}
