package com.cloud.baowang.service.vendor.MIDPay;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: fangfei
 * @createTime: 2024/10/01 10:12
 * @description:
 */
public class Constants {
    public static Map<String, String> countryMap = new HashMap<>();

    static {
        countryMap.put("VND", "VN");
        countryMap.put("USD", "US");
        countryMap.put("BRL", "BR");
        countryMap.put("EUR", "PT");
    }
}
