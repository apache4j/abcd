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
public enum TSChannelEnum {

    TX_INDIA_001("INR", "TX_INDIA_001"),
    TX_BAR_001("BRL", "TX_BAR_001"),
    TX_PHP_001("PHP", "TX_PHP_001"),
    TX_VN_001("VND", "TX_VN_001"),
    KV_TX_VN_001("KVND", "TX_VN_001"),
    TX_THAILAND_001("THB", "TX_THAILAND_001"),
    TX_PAK_001("PKR", "TX_PAK_001"),
    TX_BD_001("BDT", "TX_BD_001"),


    UNKNOWN_001("UNKNOWN", "UNKNOWN"),
    ;

    String currency;
    String channel;

    public static Optional<TSChannelEnum> findByCurrency(String currency) {
        return Arrays.stream(values())
                .filter(temp -> temp.getCurrency().equalsIgnoreCase(currency))
                .findFirst();
    }
}
