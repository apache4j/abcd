package com.cloud.baowang.agent.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewPageReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewPageResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewRecordPageReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewRecordPageResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewLockOrUnLockVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteAgentDepositReviewApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理存款款审核 服务")
public interface AgentDepositReviewApi {

    String PREFIX = ApiConstants.PREFIX + "/agentDepositReview/api/";

    @Operation(summary = "分页列表")
    @PostMapping(value = PREFIX + "depositRecordPageList")
    Page<AgentDepositReviewPageResVO> depositReviewPage(@RequestBody AgentDepositReviewPageReqVO vo);


    @Operation(summary = "锁定/解锁")
    @PostMapping(value = PREFIX + "lockOrUnLock")
    ResponseVO<Boolean> lockOrUnLock(@RequestBody AgentWithdrawReviewLockOrUnLockVO vo);



    @Operation(summary = "存款成功")
    @PostMapping(value = PREFIX + "paymentReviewSuccess")
    ResponseVO<Boolean> paymentReviewSuccess(@RequestBody AgentDepositReviewReqVO vo);


    @Operation(summary = "三审拒绝")
    @PostMapping(value = PREFIX + "paymentReviewFail")
    ResponseVO<Boolean> paymentReviewFail(@RequestBody AgentDepositReviewReqVO vo);


    @Operation(summary = "代理人工存款审核记录列表")
    @PostMapping(value = PREFIX + "depositReviewRecordPage")
    Page<AgentDepositReviewRecordPageResVO> depositReviewRecordPage(@RequestBody AgentDepositReviewRecordPageReqVO vo);

    @Operation(summary = "代理人工存款审核记录计数")
    @PostMapping(value = PREFIX + "agentManualDepositReviewRecordExportCount")
    ResponseVO<Long> agentManualDepositReviewRecordExportCount(@RequestBody AgentDepositReviewRecordPageReqVO vo);

    @Operation(summary = "代理人工存款审核计数")
    @PostMapping(value = PREFIX + "depositReviewCount")
    ResponseVO<Long> depositReviewCount(@RequestBody AgentDepositReviewPageReqVO vo);
}
