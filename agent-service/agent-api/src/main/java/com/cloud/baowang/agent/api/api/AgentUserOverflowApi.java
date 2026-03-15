package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.member.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteAgentUserOverflowApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 会员溢出 AgentUserOverflowApi")
public interface AgentUserOverflowApi {
    String PREFIX = ApiConstants.PREFIX + "/agent-user-overflow/api";

    @Operation(summary = "会员溢出申请")
    @PostMapping(value = PREFIX + "/apply")
    ResponseVO<Boolean> agentUserOverflowApply(@RequestBody AgentUserOverflowApplyVO vo, @RequestParam("adminName") String adminName);

    @Operation(description = "会员溢出审核分页查询")
    @PostMapping(value = PREFIX + "/listPage")
    ResponseVO<Page<MemberOverflowReviewPageResVO>> agentUserOverflowListPage(@RequestBody MemberOverflowReviewPageReqVO vo, @RequestParam("adminName") String adminName);

    @Operation(description = "会员转代锁单/解单")
    @PostMapping(value = PREFIX + "/lockOrder")
    ResponseVO<?> agentUserOverflowLockOrder(@RequestBody MemberOverflowLockReqVO vo, @RequestParam("adminName") String adminName);

    @Operation(description = "会员溢出详情")
    @PostMapping(value = PREFIX + "/detail")
    ResponseVO<MemberOverflowDetailResVO> detail(@RequestBody MemberOverflowLockReqVO vo);

    @Operation(description = "会员溢出审核")
    @PostMapping(value = PREFIX + "/audit")
    ResponseVO<?> audit(@RequestBody MemberOverflowAuthReqVO vo, @RequestParam("adminName") String adminName);

    @PostMapping(value = PREFIX + "/clientListPage")
    ResponseVO<Page<MemberOverflowClientPageResVO>> clientListPage(@RequestBody MemberOverflowClientPageReqVO vo);

    @PostMapping(PREFIX + "/clientApply")
    ResponseVO<?> clientApply(@RequestBody AgentUserOverflowClientApplyVO vo);

    @Operation(description = "会员溢出-根据会员账号获取会员账号类型，当前上级")
    @PostMapping(PREFIX + "/queryUser")
    ResponseVO<MemberTransferUserRespVO> queryUser(@RequestBody MemberTransferUserReqVO vo);

    @PostMapping(PREFIX + "/getUserOverflowByAccount")
    @Operation(description = "根据会员账号,批量查询溢出记录")
    List<MemberOverflowReviewPageResVO> getUserOverflowByAccount(@RequestBody MemberOverflowReviewPageReqVO memberOverflowReviewPageReqVO);

    @PostMapping(PREFIX + "/getTotal")
    @Operation(summary = "获取总条数")
    Long getTotal(@RequestBody MemberOverflowReviewPageReqVO vo);

    @PostMapping(PREFIX + "/listByAuditTime")
    @Operation(summary = "按照审核时间获取溢出记录")
    Page<MemberOverflowReviewPageResVO> listByAuditTime(@RequestBody MemberOverflowReviewPageReqVO memberOverflowReviewPageReqVO);

}
