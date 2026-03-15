package com.cloud.baowang.common.core.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 同system_param review_operation code
 * 审核单据-审核操作相关枚举类型
 */
@Getter
public enum ReviewOperationEnum {

    //审核操作 1一审审核 2结单查看
    FIRST_INSTANCE_REVIEW(1, "一审审核"),
    CHECK(2, "结单查看");
    private final Integer code;
    private final String name;

    ReviewOperationEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ReviewOperationEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        ReviewOperationEnum[] types = ReviewOperationEnum.values();
        for (ReviewOperationEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<ReviewOperationEnum> getList() {
        return Arrays.asList(values());
    }
}
