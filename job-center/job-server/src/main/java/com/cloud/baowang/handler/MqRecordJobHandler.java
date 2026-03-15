package com.cloud.baowang.handler;

import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.play.api.api.third.MqRecordApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class MqRecordJobHandler {
    private final MqRecordApi mqRecordApi;

    /**
     * mq执行失败注单
     */
    @XxlJob(value ="mqRecordTask")
    public void mqRecordTask(){
        log.info("mq执行失败注单任务开始");
        mqRecordApi.errRecordDeal();
    }





}
