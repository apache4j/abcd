package com.cloud.baowang.site.service;

import com.cloud.baowang.agent.api.api.AgentMerchantLoginInfoApi;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantVO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class BusinessLoginInfoService {

    private final AgentMerchantLoginInfoApi agentMerchantLoginInfoApi;


    public void recordLoginInfoRecord(AgentMerchantVO agentMerchantVO) {
        agentMerchantLoginInfoApi.addLoginInfo(agentMerchantVO);
    }
}
