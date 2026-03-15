package com.cloud.baowang.service.vendor.TSPay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@SuppressWarnings("SpellCheckingInspection")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TSCurrencyBankCodePayEnum {

    //巴西
    PIX("BRL","PIX", "/cashin/bra/order/create"),

    //印度
    UPI("INR","UPI", "/cashin/india/order/create"),

    //菲律宾
    MAYA("PHP","MAYA", "/cashin/vn/order/create"),
    GCASH("PHP","GCASH", "/cashin/vn/order/create"),
    GCASH_SCAN("PHP","GCASH_SCAN", "/cashin/vn/order/create"),

    //越南
    KVMOMO("KVND","MOMO", "/cashin/vn/order/create"),
    KVBANK("KVND","BANK", "/cashin/vn/order/create"),
    KVVN_SCAN("KVND","VN_SCAN", "/cashin/vn/order/create"),
    KVZALOPAY("KVND","ZALOPAY", "/cashin/vn/order/create"),

    MOMO("VND","MOMO", "/cashin/vn/order/create"),
    BANK("VND","BANK", "/cashin/vn/order/create"),
    VN_SCAN("VND","VN_SCAN", "/cashin/vn/order/create"),
    ZALOPAY("VND","ZALOPAY", "/cashin/vn/order/create"),

    //泰国
    PROMPT("THB", "PROMPT", "/cashin/thb/order/create"),
    BANKTHB("THB", "BANK", "/cashin/thb/order/create"),
    TRUEMONEY("THB", "TRUEMONEY", "/cashin/thb/order/create"),

    //孟加拉
    UPAY("BDT", "UPAY", "/cashin/bengal/order/create"),
    NAGAD("BDT", "NAGAD", "/cashin/bengal/order/create"),
    BKASH("BDT", "BKASH", "/cashin/bengal/order/create"),

    //巴基斯坦
    JAZZCASH("PKR", "JAZZCASH", "/cashin/pakistan/order/create"),
    EASYPAISA("PKR", "EASYPAISA", "/cashin/pakistan/order/create");


    String currency;
    String code;
    String uri;

    public static Optional<TSCurrencyBankCodePayEnum> findByCurrencyAndCode(String currency, String code) {
        return Arrays.stream(values())
                .filter(bankCode -> bankCode.getCurrency().equalsIgnoreCase(currency) && bankCode.getCode().equalsIgnoreCase(code))
                .findFirst();
    }

}
