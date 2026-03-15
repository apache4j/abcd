package com.cloud.baowang.agent.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 代理人工加额枚举类型
 * system_param agent_manual_adjust_type code
 */
@AllArgsConstructor
@Getter
public enum AgentManualAdjustTypeEnum {

    //人工加额-佣金钱包-佣金，其他调整（原型调整）
    COMMISSION_COMMISSION(1, "2", "佣金-佣金钱包"),
    COMMISSION_OTHER_ADJUSTMENTS(1, "4", "其他调整-佣金钱包"),
    //人工加额-额度钱包
    QUOTA_AGENT_DEPOSITION(2, "1", "代理存款(后台)-额度钱包"),
    QUOTA_AGENT_ACTIVITY(2, "3", "代理活动-额度钱包"),
    QUOTA_OTHER_ADJUSTMENTS(2, "4", "其他调整-额度钱包"),

    ;
    /**
     * 钱包类型 1.佣金钱包，2.额度钱包
     */
    private final Integer walletType;
    private final String code;
    private final String name;

    public static List<AgentManualAdjustTypeEnum> listByWalletType(Integer walletType) {
        List<AgentManualAdjustTypeEnum> result = new ArrayList<>();
        AgentManualAdjustTypeEnum[] types = AgentManualAdjustTypeEnum.values();
        for (AgentManualAdjustTypeEnum type : types) {
            if (type.walletType.equals(walletType)) {
                result.add(type);
            }
        }
        return result;
    }

    public static AgentManualAdjustTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentManualAdjustTypeEnum[] types = AgentManualAdjustTypeEnum.values();
        for (AgentManualAdjustTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static AgentManualAdjustTypeEnum getByCodeWalletType(Integer walletType, String code) {
        AgentManualAdjustTypeEnum[] types = AgentManualAdjustTypeEnum.values();
        for (AgentManualAdjustTypeEnum type : types) {
            if (code.equals(type.getCode()) && walletType.equals(type.getWalletType())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentManualAdjustTypeEnum> getList() {
        return Arrays.asList(values());
    }
}
