package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentTradeRecordDetailRequestVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentDepositOrderDetailVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentDepositOrderFileVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentRechargeConfigRequestVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentRechargeConfigVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentOrderNoVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentRechargeRecordDetailResponseVO;
import com.cloud.baowang.agent.api.vo.recharge.AgentRechargeReqVO;
import com.cloud.baowang.agent.api.vo.recharge.ClientAgentRechargeRecordRequestVO;
import com.cloud.baowang.agent.api.vo.recharge.ClientAgentRechargeRecordResponseVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteAgentRechargeApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理存款 服务")
public interface AgentRechargeApi {

    String PREFIX = ApiConstants.PREFIX + "/agentRecharge/api/";


    @Operation(summary = "代理充值")
    @PostMapping(value = PREFIX + "agentRecharge")
    ResponseVO<AgentOrderNoVO> agentRecharge(@RequestBody AgentRechargeReqVO userRechargeReqVo);
    @Operation(summary = "获取充值订单详情")
    @PostMapping(value = PREFIX + "depositOrderDetail")
    ResponseVO<AgentDepositOrderDetailVO> depositOrderDetail(@RequestBody AgentOrderNoVO orderNoVO);


    @Operation(summary = "上传凭证")
    @PostMapping(value = PREFIX + "uploadVoucher")
    ResponseVO<Integer> uploadVoucher(@RequestBody AgentDepositOrderFileVO depositOrderFileVO);

    @Operation(summary = "撤销充值订单")
    @PostMapping(value = PREFIX + "cancelDepositOrder")
    ResponseVO<Integer> cancelDepositOrder(@RequestBody AgentOrderNoVO orderNoVO);

    @Operation(summary = "催单")
    @PostMapping(value = PREFIX + "urgeOrder")
    void urgeOrder(@RequestBody AgentOrderNoVO vo);

    /**
     * 获取充值提款配置
     * @param rechargeConfigRequestVO
     * @return
     */
    @Operation(summary = "充值提款配置")
    @PostMapping(value = PREFIX + "getRechargeConfig")
    ResponseVO<AgentRechargeConfigVO> getRechargeConfig(@RequestBody AgentRechargeConfigRequestVO rechargeConfigRequestVO);

    /**
     * 代理端充值记录
     * @param vo
     * @return
     */
    @Operation(summary = "代理端充值记录")
    @PostMapping(value = PREFIX + "clientAgentRechargeRecorder")
    Page<ClientAgentRechargeRecordResponseVO> clientAgentRechargeRecorder(@RequestBody ClientAgentRechargeRecordRequestVO vo);

    @Operation(summary = "代理端充值记录")
    @PostMapping(value = PREFIX + "clientAgentRechargeRecordDetail")
    AgentRechargeRecordDetailResponseVO clientAgentRechargeRecordDetail(@RequestBody AgentTradeRecordDetailRequestVO vo);
}
