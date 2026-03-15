package com.cloud.baowang.agent.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 同system_param agent_change_type
 */
@Getter
@AllArgsConstructor
public enum AgentInfoChangeTypeEnum {
    ACCOUNT_STATUS(1, "账号状态"),
    RISK_LEVEL(2, "风控层级"),
    AGENT_LABEL(3, "代理标签"),
    AGENT_BELONGING(4, "代理归属"),
    COMMISSION_PLAN(5, "佣金方案"),
    MEMBER_BENEFITS(6, "会员福利"),
    ENTRANCE_PERM(7, "入口权限开启"),
    PAYMENT_PASSWORD_RESET(8, "支付密码重置"),
    EMAIL(9, "重置邮箱"),
    ACCOUNT_REMARK(10, "账号备注"),
    ADS_PROMOTION(11, "推广信息"),

    ;

    private final Integer code;
    private final String name;

    public static AgentInfoChangeTypeEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        AgentInfoChangeTypeEnum[] types = AgentInfoChangeTypeEnum.values();
        for (AgentInfoChangeTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentInfoChangeTypeEnum> getList() {
        return Arrays.asList(values());
    }
}
