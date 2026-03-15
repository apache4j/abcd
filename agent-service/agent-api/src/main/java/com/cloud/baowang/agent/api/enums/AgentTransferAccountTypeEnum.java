package com.cloud.baowang.agent.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum AgentTransferAccountTypeEnum {

    MEMBER(1, "会员"),
    AGENT(2, "代理");

    private final Integer type;
    private final String desc;

    public static List<AgentTransferAccountTypeEnum> getList() {
        return Arrays.asList(values());
    }
}
