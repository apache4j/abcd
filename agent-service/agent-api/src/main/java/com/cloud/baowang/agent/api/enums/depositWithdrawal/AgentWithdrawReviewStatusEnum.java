package com.cloud.baowang.agent.api.enums.depositWithdrawal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgentWithdrawReviewStatusEnum {


    SUCCESS(1, "通过"),
    FAIL(2, "驳回"),
    ;

    private final Integer code;
    private final String name;

}
