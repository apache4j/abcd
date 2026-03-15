package com.cloud.baowang.agent.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.depositWithdraw.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteAgentWithdrawReviewRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理提款审核记录 服务")
public interface AgentWithdrawReviewRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/agentWithdrawReviewRecord/api/";


    @Operation(summary = "审核记录列表")
    @PostMapping(value = PREFIX + "withdrawalReviewRecordPageList")
    Page<AgentWithdrawReviewRecordVO> withdrawalReviewRecordPageList(@RequestBody AgentWithdrawReviewRecordPageReqVO vo);


    /**
     * 提款审核记录详情
     *
     * @param vo
     * @return
     */
    @Operation(summary = "提款审核记录详情")
    @PostMapping(value = PREFIX + "withdrawReviewRecordDetail")
    AgentWithdrawReviewDetailsVO withdrawReviewRecordDetail(@RequestBody AgentWithdrawReviewDetailReqVO vo);

    @Operation(summary = "获取代理提款记录统计")
    @PostMapping(value = PREFIX + "getWithdrawTotal")
    AgentWithdrawalStatisticsVO getWithdrawTotal(@RequestBody AgentWithdrawalRecordReqVO recordReqVO);

    @Operation(summary = "获取提款审核记录总数")
    @PostMapping(value = PREFIX+"getTotal")
    Long getTotal(@RequestBody AgentWithdrawReviewRecordPageReqVO vo);
}
