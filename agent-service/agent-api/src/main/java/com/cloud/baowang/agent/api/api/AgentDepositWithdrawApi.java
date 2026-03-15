package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithDrawReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithDrawSumReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithdrawRespVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithdrawSumRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteAgentDepositWithdrawApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理存取款 服务")
public interface AgentDepositWithdrawApi {

    String PREFIX = ApiConstants.PREFIX + "/remoteAgentDepositWithdrawApi/api/";

    @Operation(summary = "代理存取款分页查询")
    @PostMapping(value = PREFIX + "listPage")
    Page<AgentDepositWithdrawRespVO> listPage(@RequestBody AgentDepositWithDrawReqVO vo);

    @GetMapping(PREFIX + "getListByTypeAndAddress")
    @Operation(summary = "根据提款类型code,账号,查询对应的提款记录")
    List<AgentDepositWithdrawRespVO> getListByTypeAndAddress(
            @RequestParam("withdrawTypeCode") String withdrawTypeCode,
            @RequestParam("riskControlAccount") String riskControlAccount,
            @RequestParam(value = "wayId", required = false) String wayId,
            @RequestParam("siteCode") String siteCode);

    @GetMapping(PREFIX + "getDepositWithdrawOrderByOrderNo")
    @Operation(summary = "根据订单编号查询代理存提订单")
    AgentDepositWithdrawRespVO getDepositWithdrawOrderByOrderNo(@RequestParam("orderNo")String orderNo);


    @PostMapping(PREFIX + "queryAgentReportAmountGroupBy")
    @Operation(summary = "代理存取款金额汇总")
    List<AgentDepositWithdrawSumRespVO> queryAgentReportAmountGroupBy(@RequestBody AgentDepositWithDrawSumReqVO vo);

    @PostMapping(PREFIX + "queryAgentReportCountGroupBy")
    @Operation(summary = "代理存取款人数汇总")
    AgentDepositWithdrawSumRespVO queryAgentReportCountGroupBy(@RequestBody AgentDepositWithDrawSumReqVO vo);

}
