package com.cloud.baowang.handler;

import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.system.api.api.exchange.EncryptRateConfigApi;
import com.cloud.baowang.system.api.api.exchange.ExchangeRateConfigApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/5/21 09:30
 * @Version: V1.0
 **/
@Slf4j
@Component
@AllArgsConstructor
public class RateRefreshHandler {

    private final ExchangeRateConfigApi exchangeRateConfigApi;

    private final EncryptRateConfigApi encryptRateConfigApi;

    /**
     * 法币汇率刷新 每天四次
     */
    @XxlJob(value = "refreshExchangeActRate")
    public void refreshExchangeActRate(){
        log.info("***************** 法币汇率刷新-XxlJob-start *****************");
        exchangeRateConfigApi.refreshActRate(Boolean.FALSE);
        log.info("***************** 法币汇率刷新-XxlJob-end *****************");
    }


    /**
     * 虚拟币汇率刷新 每半小时一次
     */
    @XxlJob(value = "refreshEncryptActRate")
    public void refreshEncryptActRate(){
        log.info("***************** 虚拟币汇率刷新-XxlJob-start *****************");
        encryptRateConfigApi.refreshActRate(Boolean.FALSE);
        log.info("***************** 虚拟币汇率刷新-XxlJob-end *****************");
    }



}
