package com.cloud.baowang.user.api.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 代理审核操作枚举
 */
public enum UserChangeAgentReviewOperationEnum {
    //审核操作 1一审审核 2结单查看
    FIRST_INSTANCE_REVIEW("1", "一审审核"),
    CHECK("2", "结单查看");

    private String code;
    private String name;

    UserChangeAgentReviewOperationEnum(String code, String name) {
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

    public static UserChangeAgentReviewOperationEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        UserChangeAgentReviewOperationEnum[] types = UserChangeAgentReviewOperationEnum.values();
        for (UserChangeAgentReviewOperationEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<UserChangeAgentReviewOperationEnum> getList() {
        return Arrays.asList(values());
    }
}
