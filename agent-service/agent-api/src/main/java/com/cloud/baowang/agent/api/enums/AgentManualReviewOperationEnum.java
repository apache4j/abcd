package com.cloud.baowang.agent.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 代理
 * 同system_param agent_manual_review_operation code
 */
@AllArgsConstructor
@Getter
public enum AgentManualReviewOperationEnum {

    //审核操作 1一审审核 2结单查看
    FIRST_INSTANCE_REVIEW(1, "一审审核"),
    SECOND_INSTANCE_REVIEW(2, "二审审核"),
    CHECK(3, "结单查看");
    private final Integer code;
    private final String name;


    public static AgentManualReviewOperationEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentManualReviewOperationEnum[] types = AgentManualReviewOperationEnum.values();
        for (AgentManualReviewOperationEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

}
