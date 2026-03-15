package com.cloud.baowang.agent.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * system_param agent_domain_change_type 类型
 */
@Getter
@AllArgsConstructor
public enum RecordDomainEnum {

    DESC(1, "描述"),
    SORT(2, "排序"),
    ;

    private final Integer type;
    private final String description;
}
