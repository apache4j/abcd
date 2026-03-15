package com.cloud.baowang.handler;

import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AgentRelationJobHandler {
    @Autowired
    private AgentInfoApi agentInfoApi;


    @XxlJob(value = "agentRelationRefresh")
    public void agentRelationRefresh() {
        return;
//        log.info("代理关系刷新job开始");
//        agentInfoApi.agentRelationRefresh();
//        log.info("代理关系刷新job结束");
    }

}
