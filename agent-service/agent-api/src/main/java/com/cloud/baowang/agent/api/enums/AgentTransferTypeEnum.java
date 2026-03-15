package com.cloud.baowang.agent.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum AgentTransferTypeEnum {

    IN(1, "转入"),
    OUT(2, "转出");

    private final Integer type;
    private final String desc;

    public static List<AgentTransferTypeEnum> getList() {
        return Arrays.asList(values());
    }
}
