package com.cloud.baowang.agent.controller;

import com.cloud.baowang.agent.api.api.DepositOfSubordinatesAddApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositOfSubordinatesVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.HttpHeaderUtil;
import com.cloud.baowang.common.core.utils.ServletUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "代会员存款")
@RestController
@RequestMapping("/agent-deposit-subordinates/api")
@Slf4j
@AllArgsConstructor
public class AgentDepositSubordinatesController {


    private final DepositOfSubordinatesAddApi depositOfSubordinatesAddApi;


    @Operation(summary = "代会员存款")
    @PostMapping("/depositOfSubordinates")
    public ResponseVO depositOfSubordinates(@Valid @RequestBody AgentDepositOfSubordinatesVO vo) {
        vo.setAgentAccount(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        HttpServletRequest request = ServletUtil.getRequest();
        if (request != null) {
            Integer deviceType = HttpHeaderUtil.getDeviceType(request);
            vo.setDeviceType(deviceType);
        }
        return depositOfSubordinatesAddApi.depositOfSubordinates(vo);
    }

   /* @Operation(summary = "代存记录")
    @PostMapping("/depositOfSubordinatesRecord")
    public ResponseVO<Page<AgentDepositOfSubordinatesResVO>> depositOfSubordinatesRecord(@RequestBody AgentDepositOfSubordinatesPageVO  vo){
        AgentAccountVO agentAccountVO =  JwtUtil.getCurrentAgent();
        vo.setAgentAccount(agentAccountVO.getAgentAccount());
        return agentFeignResource.depositOfSubordinatesRecord(vo);
    }*/



}
