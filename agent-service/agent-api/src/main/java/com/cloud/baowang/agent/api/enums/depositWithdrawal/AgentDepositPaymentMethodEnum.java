package com.cloud.baowang.agent.api.enums.depositWithdrawal;

import com.cloud.baowang.common.core.vo.base.CodeNameVO;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 代理支付方式
 **/
@Getter
public enum AgentDepositPaymentMethodEnum {
    VIRTUAL_CURRENCY("local_virtual_currency", "虚拟币支付"),
    BANK_CARD("local_bank_card", "银行卡转卡"),
    ALIPAY("local_alipay", "支付宝转账"),
    ;

    private String code;
    private String name;
    private CodeNameVO codeNameVO;

    public static final String VIRTUAL_CURRENCY_USDT = "USDT";

    AgentDepositPaymentMethodEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AgentDepositPaymentMethodEnum nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        AgentDepositPaymentMethodEnum[] types = AgentDepositPaymentMethodEnum.values();
        for (AgentDepositPaymentMethodEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<AgentDepositPaymentMethodEnum> getList() {
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
