package com.cloud.baowang.wallet.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * system_param agent_manual_adjust_down_type code
 * 代理人工扣除 调整类型 枚举类
 */
@AllArgsConstructor
@Getter
public enum AgentManualDownAdjustTypeEnum {
    //代理人工减额-佣金钱包-佣金，代理提款（后台）
    COMMISSION_AGENT_WITHDRAWAL(1, "1", "代理提款(后台)-佣金钱包"),
    COMMISSION_COMMISSION(1, "2", "佣金-佣金钱包"),
    //代理人工减额-额度钱包
    QUOTA_AGENT_ACTIVITY(2, "3", "代理活动-额度钱包"),
    QUOTA_OTHER_ADJUSTMENTS(2, "4", "其他调整-额度钱包"),
    QUOTA_QUOTA_ENUM(2,"5","额度"),

    ;
    /**
     * 钱包类型 1.佣金钱包，2.额度钱包
     */
    private final Integer walletType;
    private final String code;
    private final String name;

    public static AgentManualDownAdjustTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        AgentManualDownAdjustTypeEnum[] types = AgentManualDownAdjustTypeEnum.values();
        for (AgentManualDownAdjustTypeEnum type : types) {
            if (code.toString().equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentManualDownAdjustTypeEnum> getList() {
        return Arrays.asList(values());
    }

    public static List<AgentManualDownAdjustTypeEnum> listByWalletType(Integer walletType) {
        List<AgentManualDownAdjustTypeEnum> result = new ArrayList<>();
        AgentManualDownAdjustTypeEnum[] types = AgentManualDownAdjustTypeEnum.values();
        for (AgentManualDownAdjustTypeEnum type : types) {
            if (type.walletType.equals(walletType)) {
                result.add(type);
            }
        }
        return result;
    }
}
