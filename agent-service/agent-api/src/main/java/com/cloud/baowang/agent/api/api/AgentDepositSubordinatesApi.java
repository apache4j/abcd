package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentDepositSubordinatesPageReqVo;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositOfSubordinatesResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.GetAgentDepositAmountByAgentVO;
import com.cloud.baowang.agent.api.vo.user.AgentComprehensiveReportVO;
import com.cloud.baowang.agent.api.vo.user.AgentStoredMemberVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteAgentDepositSubordinatesApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理下级存款记录 服务")
public interface AgentDepositSubordinatesApi {

    String PREFIX = ApiConstants.PREFIX + "/agent-sub-deposit-record/api/";

    @Operation(summary = "查询某个代理 在某时间内的代存金额(按照会员分组)")
    @PostMapping(value = PREFIX + "getAgentDepositAmountByAgent")
    GetAgentDepositAmountByAgentVO getAgentDepositAmountByAgent(@RequestParam("siteCode") String siteCode,
                                                                @RequestParam(value = "agentAccount") String agentAccount,
                                                                @RequestParam(value = "userAccount", required = false) String userAccount,
                                                                @RequestParam(value = "startTime", required = false) Long startTime,
                                                                @RequestParam(value = "endTime", required = false) Long endTime);

    @GetMapping( PREFIX +"getAgentDepositAmountByUserAccount")
    @Operation(summary = "查询某个会员的代理代存信息")
    List<AgentDepositOfSubordinatesResVO> getAgentDepositAmountByUserAccount(@RequestParam("siteCode") String siteCode, @RequestParam(value = "userAccount") String userAccount);

    @GetMapping( PREFIX +"getAgentDepositAmountByUserId")
    @Operation(summary = "查询某个会员的代理代存信息-根据userId")
    List<AgentDepositOfSubordinatesResVO> getAgentDepositAmountByUserId(@RequestParam("userId") String userId);


    @GetMapping( PREFIX +"getAgentDepositAmountByOderNo")
    @Operation(summary = "根据订单号查询代理代存信息")
    AgentDepositOfSubordinatesResVO getAgentDepositAmountByOderNo(@RequestParam("orderNo") String orderNo);

    @PostMapping( PREFIX +"listPage")
    @Operation(summary = "代理代存分页查询")
    Page<AgentDepositOfSubordinatesResVO> listPage(@RequestBody AgentDepositSubordinatesPageReqVo vo);

    @PostMapping(PREFIX+"getAgentDepositSum")
    @Operation(summary = "综合报表统计代存汇总")
    ResponseVO<Map<String, AgentStoredMemberVO>> getAgentDepositSum(@RequestBody AgentComprehensiveReportVO vo);


}
