package com.cloud.baowang.play.api.enums.JILI;

import com.cloud.baowang.common.core.enums.CurrencyEnum;
import lombok.Getter;

@Getter
public enum JILICurrencyEnum {

    PHP("PHP", "PHP", "Philippine Peso"),
    IDR("IDR(K)", "IDR", "Indonesian Rupiah"), // 游戏供应商的码是: IDR(K)
    VND("VND(K)", "KVND", "Vietnamese Dong"),// 游戏供应商的码是: VND(K)
    INR("INR", "INR", "Indian Rupee"),
    BRL("BRL", "BRL", "Brazilian Real"),
    MYR("MYR", "MYR", "Malaysian Ringgit"),
    THB("THB", "THB", "Thai Baht"),
    COP("COP(K)", "COP", "Colombian Peso"), // 游戏供应商的码是: COP(K)
    NGN("NGN", "NGN", "Nigerian Naira"),
    BWP("BWP", "BWP", "Botswana Pula"),
    ZAR("ZAR", "ZAR", "South African Rand"),
    GHS("GHS", "GHS", "Ghanaian Cedi"),
    KES("KES", "KES", "Kenyan Shilling"),
    CNY("CNY", "CNY", "Chinese Yuan"),
    JPY("JPY", "JPY", "Japanese Yen"),
    MMK("MMK(K)", "MMK", "Myanma Kyat"), // 游戏供应商的码是: MMK(K)
    KRW("KRW", "KRW", "South Korean Won"),
    HKD("HKD", "HKD", "Hong Kong Dollar"),
    TRY("TRY", "TRY", "Turkish Lira"),
    USD("USD", "USD", "United States Dollar"),
    USDT("USDT", "USDT", "Tether"),
    PKR("PKR", "PKR","Pakistani Rupees");
    private final String code;
    private final String plat;
    private final String name;

    // 构造函数
    JILICurrencyEnum(String code, String plat, String name) {
        this.code = code;
        this.plat = plat;
        this.name = name;
    }


    public static JILICurrencyEnum getFromPlat(String code) {
        for (JILICurrencyEnum currency : JILICurrencyEnum.values()) {
            if (currency.getPlat().equalsIgnoreCase(code)) {
                return currency;
            }
        }
        return null;
    }
}

