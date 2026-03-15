package com.cloud.baowang.report.service;

import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @className: CurrencyConversionService
 * @author: wade
 * @description: 把返回转换为指定的平台币
 * @date: 11/10/24 11:54
 */
@Slf4j
@Service
@AllArgsConstructor
public class CurrencyConversionService {

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

  /*  private  Map<String, BigDecimal> exchangeRates(String siteCode){

    }*/


}
