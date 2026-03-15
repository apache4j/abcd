package com.cloud.baowang.handler;

import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.system.api.api.redissonOperate.RedissonOperateApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class RedisHandler {

    private final RedissonOperateApi redissonOperateApi;

    /**
     * 清空i18n redisson
     */
    @XxlJob(value = "redissonOperateI18nClear")
    public void refreshExchangeActRate() {
        log.info("***************** 清空i18n redisson-XxlJob-start *****************");
        redissonOperateApi.clear();
        log.info("***************** 清空i18n redisson-XxlJob-end *****************");
    }
}
