package com.cloud.baowang.agent.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentTransferApi;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferPageRecordVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferParam;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordParam;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentDetailParam;
import com.cloud.baowang.agent.api.vo.info.AgentPayPasswordParam;
import com.cloud.baowang.agent.service.AgentTokenService;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 */
@Tag(name = "下级信息-下级管理-下级转账")
@RestController
@RequestMapping("agent-transfer")
@Slf4j
@AllArgsConstructor
public class AgentTransferController {

    private final AgentTransferApi agentTransferApi;

    @Operation(summary = "获取代理转账对象")
    @PostMapping(value = "/queryAgentTransfer")
    public ResponseVO<AgentTransferVO> queryAgentTransfer(@RequestBody AgentDetailParam param){
        param.setAgentAccount(Objects.requireNonNull(AgentTokenService.getCurrentAgent()).getAgentAccount());
        param.setSiteCode(CurrReqUtils.getSiteCode());
        log.info(" queryAgentTransfer : param : {} " , param);
        return agentTransferApi.queryAgentTransfer(param);
    }

    @Operation(summary = "支付密码验证")
    @PostMapping(value = "/verifyPayPassword")
    public ResponseVO<?> verifyPayPassword(@RequestBody AgentPayPasswordParam param){
        param.setAgentAccount(Objects.requireNonNull(AgentTokenService.getCurrentAgent()).getAgentAccount());
        return agentTransferApi.verifyPayPassword(param);
    }

    @Operation(summary = "代理转账保存")
    @PostMapping(value = "/saveAgentTransfer")
    public ResponseVO<?> saveAgentTransfer(@Valid @RequestBody AgentTransferParam param){
        param.setAgentId(CurrReqUtils.getOneId());
        param.setAgentAccount(CurrReqUtils.getAccount());
        param.setSiteCode(CurrReqUtils.getSiteCode());
        return agentTransferApi.saveAgentTransfer(param);
    }
    @Operation(summary = "代理转账分页查询")
    @PostMapping(value = "/queryAgentTransferRecord")
    public ResponseVO<Page<AgentTransferPageRecordVO>> queryAgentTransferRecord(
            @RequestBody AgentTransferRecordParam param){
        param.setAgentId(CurrReqUtils.getOneId());
        if(ObjectUtil.isEmpty(param.getAgentAccount())){
            param.setIsMe(true);
            param.setAgentAccount(Objects.requireNonNull(CurrReqUtils.getAccount()));
        }else{
            param.setIsMe(false);
        }
        return agentTransferApi.queryAgentTransferRecord(param);
    }

}
