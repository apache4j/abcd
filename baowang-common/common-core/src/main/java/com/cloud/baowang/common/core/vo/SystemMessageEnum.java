package com.cloud.baowang.common.core.vo;

import java.util.Arrays;
import java.util.List;

/**
 * @Author : amos
 * @Date : 2024-10-31
 *
 */
public enum SystemMessageEnum {



    MEMBER_WELCOME("MEMBER_WELCOME","会员加入"),
    MEMBER_DEPOSIT_SUCCESS("MEMBER_DEPOSIT_SUCCESS","存款成功通知"),
    MEMBER_WITHDRAWAL_SUCCESS("MEMBER_WITHDRAWAL_SUCCESS","提款成功通知"),
    MEMBER_WITHDRAWAL_FAILED("MEMBER_WITHDRAWAL_FAILED","提款失败通知"),
    MEMBER_SECURITY("MEMBER_SECURITY","安全通知"),
    MEMBER_FUNDS_SECURITY("MEMBER_FUNDS_SECURITY","资金安全通知"),
    MEMBER_VERSION_UPGRADE("MEMBER_VERSION_UPGRADE","版本升级通知"),

    AGENT_WELCOME("AGENT_WELCOME","代理加入"),
    AGENT_FUNDS_SECURITY("AGENT_FUNDS_SECURITY","资金安全通知"),
    AGENT_SECURITY("AGENT_SECURITY","安全通知"),
    AGENT_DEPOSIT_SUCCESS("AGENT_DEPOSIT_SUCCESS","存款成功通知"),
    AGENT_COMMISSION_PAYMENT_SUCCESS("AGENT_COMMISSION_PAYMENT_SUCCESS","佣金发放成功通知"),
    AGENT_WITHDRAWAL_SUCCESS("AGENT_WITHDRAWAL_SUCCESS","提款成功通知"),
    AGENT_WITHDRAWAL_FAILED("AGENT_WITHDRAWAL_FAILED","提款失败通知"),
    AGENT_MEMBER_OVERFLOW_SUCCESS("AGENT_MEMBER_OVERFLOW_SUCCESS","会员溢出调线成功通知"),
    AGENT_MEMBER_TRANSFER_SUCCESS("AGENT_MEMBER_TRANSFER_SUCCESS","会员转代成功通知"),
    AGENT_TRANSFERS("AGENT_TRANSFERS","代理转账通知"),
    AGENT_ARRIVAL("AGENT_ARRIVAL","到账通知"),
    ;

    private String code;
    private String name;

    SystemMessageEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SystemMessageEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        SystemMessageEnum[] types = SystemMessageEnum.values();
        for (SystemMessageEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<SystemMessageEnum> getList() {
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
