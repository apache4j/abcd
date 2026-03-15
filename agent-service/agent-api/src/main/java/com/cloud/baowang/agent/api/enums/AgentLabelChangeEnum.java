/**
 * @(#)AgentContractTemplateRateBaseEnum.java, 10月 13, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.agent.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * <h2></h2>
 *
 * @author wayne
 * date 2023/10/13
 */
@Getter
public enum AgentLabelChangeEnum {

    EDIT_NAME(0, "标签名称"),
    EDIT_DESCRIPTION(1, "标签描述"),
    DELETE(3, "删除"),
    ADD(2, "增加"),

    ;

    private final Integer code;
    private final String name;

    AgentLabelChangeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }


    public static AgentLabelChangeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentLabelChangeEnum[] types = AgentLabelChangeEnum.values();
        for (AgentLabelChangeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentLabelChangeEnum> getList() {
        return Arrays.asList(values());
    }
}
