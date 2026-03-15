package com.cloud.baowang.wallet.api.enums.usercoin;

import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.core.utils.SymbolUtil;
import com.cloud.baowang.common.core.vo.base.CodeNameVO;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 线下支付方式
 **/
@Getter
public enum LocalDepositPaymentMethodEnum {
    VIRTUAL_CURRENCY("local_virtual_currency", "虚拟币支付"),
    BANK_CARD("local_bank_card", "银行卡转卡"),
    ALIPAY("local_alipay", "支付宝转账"),
    WECHAT("local_wechat", "微信转账"),

    ;

    private String code;
    private String name;
    private CodeNameVO codeNameVO;

    public static final String VIRTUAL_CURRENCY_USDT = "USDT";

    LocalDepositPaymentMethodEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String nameOfCode(String code) {
        if (null == code) {
            return null;
        }
        LocalDepositPaymentMethodEnum[] types = LocalDepositPaymentMethodEnum.values();
        for (LocalDepositPaymentMethodEnum type : types) {
            if (code.equals(type.getCode())) {
                return type.getName();
            }
        }
        return null;
    }

    public static LocalDepositPaymentMethodEnum of(String code) {
        if (null == code) {
            return null;
        }
        LocalDepositPaymentMethodEnum[] types = LocalDepositPaymentMethodEnum.values();
        for (LocalDepositPaymentMethodEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<String> codeOfInformation(String code,
                                                 String address,
                                                 String accountBranch,
                                                 String accountType,
                                                 String name,
                                                 Boolean dataDesensitization) {
        LocalDepositPaymentMethodEnum depositPaymentMethodEnum = of(code);
        List<String> list = Lists.newArrayList();
        if (Objects.nonNull(depositPaymentMethodEnum)) {
            switch (depositPaymentMethodEnum) {
                case VIRTUAL_CURRENCY -> {
                    if (StrUtil.isNotEmpty(address)) {
                        if (dataDesensitization) {
                            address = SymbolUtil.showBankOrVirtualNo(address);
                        }
                    }
                    list.add("虚拟币地址:" + address);
                    list.add("协议:" + accountBranch);
                }
                case BANK_CARD -> {
                    if (StrUtil.isNotEmpty(address)) {
                        if (dataDesensitization) {
                            address = SymbolUtil.showBankOrVirtualNo(address);
                        }
                    }
                    list.add("银行卡号:" + address);
                    list.add("银行名称:" + accountType);
                    list.add("持卡人姓名:" + name);
                }
                case ALIPAY -> {
                    list.add("支付宝账号:" + address);
                    list.add("支付宝名称:" + name);
                }
                case WECHAT -> list.add("微信账号:" + address);
            }
        }
        return list;
    }

    public static List<LocalDepositPaymentMethodEnum> getList() {
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
