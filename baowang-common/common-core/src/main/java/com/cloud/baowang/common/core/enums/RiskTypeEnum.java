package com.cloud.baowang.common.core.enums;

import java.util.Arrays;
import java.util.List;

public enum RiskTypeEnum {

    RISK_MEMBER("1", "风险会员", "会员账号"),
    RISK_AGENT("2", "风险代理", "代理账号"),
    RISK_BANK("3", "风险银行卡", "银行卡号"),
    RISK_VIRTUAL("4", "风险虚拟币", "虚拟币地址"),
    RISK_IP("5", "风险IP", "IP地址"),
    RISK_DEVICE("6", "风险终端设备号", "终端设备号"),
    RISK_WALLET("7", "风险电子钱包", "电子钱包"),
    RISK_BUSINESS("8", "风险商务", "风险商务"),
    ;

    private String code;
    private String name;
    private String label;

    RiskTypeEnum(String code, String name, String label) {
        this.code = code;
        this.name = name;
        this.label = label;
    }

    public static RiskTypeEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        RiskTypeEnum[] types = RiskTypeEnum.values();
        for (RiskTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static String labelOfCode(String code) {
        if (null == code) {
            return null;
        }
        RiskTypeEnum[] types = RiskTypeEnum.values();
        for (RiskTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getLabel();
            }
        }
        return null;
    }

    public static List<RiskTypeEnum> getList() {
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
