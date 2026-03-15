package com.cloud.baowang.activity.biz;

import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/9/27 13:29
 * @Version: V1.0
 **/
public class CurrencyTest {
    public static void main(String[] args) {
        Set<Currency> currencySet = Currency.getAvailableCurrencies();
        List<Currency> currencyList=currencySet.stream().sorted(Comparator.comparingInt(Currency::getNumericCode)).toList();
        currencyList.forEach(o->System.out.println(o.getCurrencyCode()+"\t"+o.getSymbol()+"\t"+o.getDisplayName()+"\t"+o.getDisplayName(Locale.ENGLISH)+"\t"+o.getNumericCode()));
    }
}
