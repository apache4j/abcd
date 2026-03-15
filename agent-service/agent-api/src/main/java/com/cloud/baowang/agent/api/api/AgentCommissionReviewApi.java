package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.AdjustCommissionVO;
import com.cloud.baowang.agent.api.vo.AgentReviewOrderNumVO;
import com.cloud.baowang.agent.api.vo.agent.commission.AgentValidAmountVo;
import com.cloud.baowang.agent.api.vo.agentreview.ReviewListVO;
import com.cloud.baowang.agent.api.vo.commission.*;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: fangfei
 * @createTime: 2024/10/20 10:11
 * @description:
 */

@FeignClient(contextId = "remoteAgentCommissionReviewApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 佣金审核 ")
public interface AgentCommissionReviewApi {
    String PREFIX = ApiConstants.PREFIX+"/commissionReview/api";

    @Operation(summary = "审核列表")
    @PostMapping(value = PREFIX+"/getReviewPage")
    ResponseVO<Page<AgentCommissionReviewVO>> getReviewPage(@RequestBody CommissionReviewReq vo);

    @Operation(summary = "锁单或解锁")
    @PostMapping(value = PREFIX+"/lockCommission")
    ResponseVO<?> lockCommission(@RequestBody StatusListVO vo);

    @Operation(summary = "佣金审核详情")
    @PostMapping(value = PREFIX+"/getAgentCommissionDetail")
    ResponseVO<AgentCommissionReviewDetailVO> getAgentCommissionDetail(@RequestBody IdVO idVO);

    @Operation(summary = "一审通过-提交")
    @PostMapping(value = PREFIX+"/oneCommissionReviewSuccess")
    ResponseVO<?> oneCommissionReviewSuccess(@RequestBody ReviewListVO vo);

    @Operation(summary = "一审拒绝-提交")
    @PostMapping(value =PREFIX+ "/oneCommissionReviewFail")
    ResponseVO<?> oneCommissionReviewFail(@RequestBody ReviewListVO vo);

    @Operation(summary = "获取未审核订单总数")
    @PostMapping(value = PREFIX+"/getUnreviewedRecordCount")
    Integer getUnreviewedRecordCount(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "查询-佣金审核-未审核数量角标")
    @PostMapping(value = PREFIX+"/getNotReviewNum")
    AgentReviewOrderNumVO getNotReviewNum(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "二审锁定/解锁")
    @PostMapping(value = PREFIX+"/twoLockOrUnLock")
    ResponseVO<?> secondLockOrUnLock(@RequestBody StatusListVO vo);

    @Operation(summary = "二审通过")
    @PostMapping(value = PREFIX+"/secondReviewSuccess")
    ResponseVO<?> secondReviewSuccess(@RequestBody ReviewListVO vo);

    @Operation(summary = "二审拒绝")
    @PostMapping(value = PREFIX+"/secondReviewRejected")
    ResponseVO<?> secondReviewRejected(@RequestBody ReviewListVO vo);

    @Operation(summary = "二审驳回")
    @PostMapping(value = PREFIX+"/secondReviewReturned")
    ResponseVO<?> secondReviewReturned(@RequestBody ReviewListVO vo);

    @Operation(summary = "佣金调整")
    @PostMapping(value = PREFIX+"/adjustCommission")
    ResponseVO<Boolean> adjustCommission(@RequestBody AdjustCommissionVO vo);

    @Operation(summary = "佣金计算")
    @PostMapping(value = PREFIX+"/calcAgentCommission")
    ResponseVO<?> calcAgentCommission(@RequestBody AgentValidAmountVo vo);
}
