package com.cloud.baowang.common.core.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 同system_param review_status code
 * 审核单据-当前提交记录审核状态公共枚举类
 */
@Getter
public enum ReviewStatusEnum {

    REVIEW_PENDING(1, "待审核"),
    /**
     * 锁单后状态变更为处理中
     */
    REVIEW_PROGRESS(2, "处理中"),
    REVIEW_PASS(3, "审核通过"),
    REVIEW_REJECTED(4, "一审拒绝");

    private final Integer code;
    private final String name;

    ReviewStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ReviewStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        ReviewStatusEnum[] types = ReviewStatusEnum.values();
        for (ReviewStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<ReviewStatusEnum> getList() {
        return Arrays.asList(values());
    }
}
