package com.cloud.baowang.agent.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.AgentReviewOrderNumVO;
import com.cloud.baowang.agent.api.vo.AgentWithdrawChannelResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewAddressReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewAddressResponseVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewDetailReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewDetailsVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewLockOrUnLockVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewPageReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewPageResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawReviewReqVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteAgentWithdrawReviewApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理提款审核 服务")
public interface AgentWithdrawReviewApi {

    String PREFIX = ApiConstants.PREFIX + "/agentWithdrawReview/api/";

    @Operation(summary = "分页列表")
    @PostMapping(value = PREFIX + "withdrawalRecordPageList")
    Page<AgentWithdrawReviewPageResVO> withdrawReviewPage(@RequestBody AgentWithdrawReviewPageReqVO vo);


    @Operation(summary = "审核详情")
    @PostMapping(value = PREFIX + "withdrawReviewDetail")
    AgentWithdrawReviewDetailsVO withdrawReviewDetail(@RequestBody AgentWithdrawReviewDetailReqVO vo);


    @Operation(summary = "一审锁定/解锁")
    @PostMapping(value = PREFIX + "oneLockOrUnLock")
    ResponseVO<Boolean> oneLockOrUnLock(@RequestBody AgentWithdrawReviewLockOrUnLockVO vo);

    @Operation(summary = "待出款锁定/解锁")
    @PostMapping(value = PREFIX + "paymentLockOrUnLock")
    ResponseVO<Boolean> paymentLockOrUnLock(@RequestBody AgentWithdrawReviewLockOrUnLockVO vo);

    @Operation(summary = "一审成功")
    @PostMapping(value = PREFIX + "oneReviewSuccess")
    ResponseVO<Boolean> oneReviewSuccess(@RequestBody AgentWithdrawReviewReqVO vo);


    @Operation(summary = "待出款成功")
    @PostMapping(value = PREFIX + "paymentReviewSuccess")
    ResponseVO<Boolean> paymentReviewSuccess(@RequestBody AgentWithdrawReviewReqVO vo);

    @Operation(summary = "一审拒绝")
    @PostMapping(value = PREFIX + "oneReviewFail")
    ResponseVO<Boolean> oneReviewFail(@RequestBody AgentWithdrawReviewReqVO vo);


    @Operation(summary = "三审拒绝")
    @PostMapping(value = PREFIX + "paymentReviewFail")
    ResponseVO<Boolean> paymentReviewFail(@RequestBody AgentWithdrawReviewReqVO vo);


    /**
     * 代理提款审核角标数量
     *
     * @return
     */
    @Operation(summary = "代理提款审核角标数量")
    @PostMapping(value = PREFIX + "getAgentWithdrawReviewNum")
    AgentReviewOrderNumVO getAgentWithdrawReviewNum(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "统计当前站点所有待审核代理提款记录数")
    @GetMapping(PREFIX + "getTotalPendingReviewBySiteCode")
    long getTotalPendingReviewBySiteCode(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "根据当前站点代理对应币种,获取提款通道")
    @PostMapping(value = PREFIX + "getChannelByChannelTypeAndReviewId")
    ResponseVO<List<AgentWithdrawChannelResVO>> getChannelByChannelTypeAndReviewId(
            @RequestParam("siteCode") String siteCode,
            @RequestParam("channelType") String channelType,
            @RequestParam("id") String id);

    @PostMapping(PREFIX+"getAddressInfoList")
    @Operation(summary = "获取收款账户详情列表")
    ResponseVO<Page<AgentWithdrawReviewAddressResponseVO>> getAddressInfoList(@RequestBody AgentWithdrawReviewAddressReqVO vo);

}
