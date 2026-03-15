package com.cloud.baowang.agent.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentCoinChangeApi;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentCustomerCoinTypGroupEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinChangeDetailReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinChangeReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCustomerCoinRecordDetailVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCustomerCoinRecordVO;
import com.cloud.baowang.agent.service.AgentTokenService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "代理-财务中心-账变明细")
@AllArgsConstructor
@RestController
@RequestMapping("/agentCoinChange/api")
public class AgentCoinChangeController {

    private final AgentCoinChangeApi agentCoinChangeApi;

    private final SystemParamApi systemParamApi;

    @Operation(summary = "代理端账变明细列表列表返回公共下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, Object>> querySelectCommon(){
        Map<String, Object> map = Maps.newHashMap();
        ResponseVO<List<CodeValueVO>> responseVO = systemParamApi.getSystemParamByType(CommonConstant.AGENT_CUSTOMER_SHOW_TYPE);
        List<CodeValueVO> showTpeList = responseVO.getData();
        List<String> quotaTypeCodeList = List.of(AgentCustomerCoinTypGroupEnum.AgentCustomCoinTypeEnum.AGENT_DEPOSIT.getCode());
        List<CodeValueVO> commissionChangeTypeList = showTpeList.stream()
                .filter(obj -> !quotaTypeCodeList.contains(obj.getCode()))
                .collect(Collectors.toList());

        List<String> commissionTypeCodeList = List.of(AgentCustomerCoinTypGroupEnum.AgentCustomCoinTypeEnum.AGENT_WITHDRAWAL.getCode(),
                AgentCustomerCoinTypGroupEnum.AgentCustomCoinTypeEnum.PROMOTIONS_ADD.getCode());
        List<CodeValueVO> quotaChangeTypeList = showTpeList.stream()
                .filter(obj -> !commissionTypeCodeList.contains(obj.getCode()))
                .collect(Collectors.toList());


        map.put(CommonConstant.AGENT_WALLET_TYPE, systemParamApi.getSystemParamByType(CommonConstant.AGENT_WALLET_TYPE).getData());
        map.put("commissionChangeType", commissionChangeTypeList);
        map.put("quotaChangeType", quotaChangeTypeList);
        return ResponseVO.success(map);
    }


    @Operation(summary = "代理客户端账变记录列表")
    @PostMapping(value =  "/listAgentCoinRecordPage")
    public ResponseVO<Page<AgentCustomerCoinRecordVO>> listAgentCustomerCoinRecord(@RequestBody AgentCoinChangeReqVO vo){

        vo.setAgentId(CurrReqUtils.getOneId());
        return agentCoinChangeApi.listAgentCustomerCoinRecord(vo);
    }

    @Operation(description = "代理客户端账变记录详情")
    @PostMapping(value = "/getCoinRecordDetail")
    public ResponseVO<AgentCustomerCoinRecordDetailVO> getCoinRecordDetail(@RequestBody AgentCoinChangeDetailReqVO vo){
        return agentCoinChangeApi.getCoinRecordDetail(vo);
    }



}
