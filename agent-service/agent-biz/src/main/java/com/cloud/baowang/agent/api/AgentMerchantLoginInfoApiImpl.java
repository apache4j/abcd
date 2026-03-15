package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentMerchantLoginInfoApi;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentMerchantVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantLoginInfoPageQueryVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantLoginInfoRespVO;
import com.cloud.baowang.agent.service.AgentMerchantLoginInfoService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentMerchantLoginInfoApiImpl implements AgentMerchantLoginInfoApi {
    private final AgentMerchantLoginInfoService loginInfoService;


    @Override
    public boolean addLoginInfo(AgentMerchantVO agentMerchantVO) {
        return loginInfoService.addLoginInfo(agentMerchantVO);
    }

    @Override
    public ResponseVO<AgentMerchantLoginInfoRespVO> pageQuery(AgentMerchantLoginInfoPageQueryVO queryVO) {
        return loginInfoService.pageQuery(queryVO);
    }
}
