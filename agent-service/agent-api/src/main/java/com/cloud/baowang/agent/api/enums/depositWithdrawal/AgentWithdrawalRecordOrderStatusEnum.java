/**
 * @(#)AgentWithdrawalRecordOrderStatusEnum.java, 11月 01, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.agent.api.enums.depositWithdrawal;

import java.util.Arrays;
import java.util.List;

/**
 * <h2></h2>
 * @author wayne
 * date 2023/11/1
 */
public enum AgentWithdrawalRecordOrderStatusEnum {



    FIRST_AUDIT_REJECT("3", "一审拒绝"),

    SECOND_AUDIT_REJECT("6", "二审拒绝"),

    THIRD_AUDIT_REJECT("9", "三审拒绝"),

    FAIL("100", "出款拒绝"),

    SUCCEED("101", "已出款"),

    ;
    private String code;
    private String name;

    AgentWithdrawalRecordOrderStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentWithdrawalRecordOrderStatusEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentWithdrawalRecordOrderStatusEnum[] types = AgentWithdrawalRecordOrderStatusEnum.values();
        for (AgentWithdrawalRecordOrderStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }


    public static List<AgentWithdrawalRecordOrderStatusEnum> getList() {
        return Arrays.asList(values());
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
}
