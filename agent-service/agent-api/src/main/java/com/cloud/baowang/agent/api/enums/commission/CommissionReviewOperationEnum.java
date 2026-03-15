package com.cloud.baowang.agent.api.enums.commission;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 同system_param COMMISSION_OPERATION code
 * 审核单据-审核操作相关枚举类型
 */
@Getter
public enum CommissionReviewOperationEnum {
    NO_LOCK(0, "待审核"),
    REVIEWING(1, "审核中"),
    REVIEW_SUCCESS(2, "审核通过"),
    REVIEW_FAIL(3, "审核拒绝"),
    REVIEW_VIEW(4, "查看");
    private final Integer code;
    private final String name;

    CommissionReviewOperationEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static CommissionReviewOperationEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        CommissionReviewOperationEnum[] types = CommissionReviewOperationEnum.values();
        for (CommissionReviewOperationEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<CommissionReviewOperationEnum> getList() {
        return Arrays.asList(values());
    }

//    public static Integer getCodeByOrderStatus(Integer orderStatus) {
//        if (orderStatus == CommissionReviewOrderStatusEnum.WAIT_REVIEW.getCode()) {
//            return CommissionReviewOperationEnum.NO_LOCK.getCode();
//        } else if (orderStatus == CommissionReviewOrderStatusEnum.ONE_REVIEWING.getCode()) {
//            return CommissionReviewOperationEnum.REVIEWING.getCode();
//        } else if (orderStatus == CommissionReviewOrderStatusEnum.REVIEW_SUCCESS.getCode()) {
//            return CommissionReviewOperationEnum.REVIEW_SUCCESS.getCode();
//        }  else if (orderStatus == CommissionReviewOrderStatusEnum.REVIEW_FAIL.getCode()) {
//            return CommissionReviewOperationEnum.REVIEW_FAIL.getCode();
//        }
//
//        return CommissionReviewOperationEnum.NO_LOCK.getCode();
//    }
}
