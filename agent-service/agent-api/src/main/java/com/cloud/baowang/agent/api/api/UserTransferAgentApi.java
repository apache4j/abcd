package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentreview.UserAccountUpdateVO;
import com.cloud.baowang.agent.api.vo.member.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteUserTransferAgentApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 会员转代 UserTransferAgentApi")
public interface UserTransferAgentApi {
    String PREFIX = ApiConstants.PREFIX + "/user-transfer-agent/api/";

    @Operation(summary = "会员转代申请")
    @PostMapping(PREFIX + "apply")
    ResponseVO<Boolean> apply(@Valid @RequestBody MemberTransferAgentApplyVO vo, @RequestParam("operator") String operator);

    @Operation(summary = "会员转代审核分页查询")
    @PostMapping(PREFIX + "listPage")
    ResponseVO<Page<MemberTransferReviewPageResVO>> listPage(@Valid @RequestBody MemberTransferReviewPageReqVO vo, @RequestParam("operator") String operator);

    @Operation(summary = "会员转代锁单/解单")
    @PostMapping(PREFIX + "lockOrder")
    ResponseVO<?> lockOrder(@Valid @RequestBody MemberTransferLockReqVO vo, @RequestParam("operator") String operator);

    @Operation(summary = "会员转代详情")
    @PostMapping(PREFIX + "detail")
    ResponseVO<MemberTransferDetailResVO> detail(@Valid @RequestBody MemberTransferLockReqVO vo);

    @Operation(summary = "会员转代审核")
    @PostMapping(PREFIX + "audit")
    ResponseVO<?> audit(@Valid @RequestBody MemberTransferAuthReqVO vo, @RequestParam("operator") String operator);

    @Operation(summary = "会员转代会员查询")
    @PostMapping(PREFIX + "queryUser")
    ResponseVO<MemberTransferUserRespVO> queryUser(@RequestBody MemberTransferUserReqVO vo);

    @Operation(summary = "会员转代会员次数")
    @PostMapping(PREFIX + "queryUserTransferCount")
    List<ReportUserTransferRespVO> queryUserTransferCount(@RequestBody ReportUserTransferReqVO vo);

    @Operation(summary = "会员转代会员次数")
    @PostMapping(PREFIX + "queryUserTransferCountAllPlatForm")
    List<ReportUserTransferRespVO> queryUserTransferCountAllPlatForm(@RequestBody ReportUserTransferReqVO vo);

    @Operation(summary = "统计站点下待处理审核记录条数")
    @GetMapping(PREFIX + "getPendingCountBySiteCode")
    UserAccountUpdateVO getPendingCountBySiteCode(@RequestParam("siteCode") String siteCode);

    //按照会员账号、时间区间获取 会员转代记录
    @PostMapping(PREFIX + "getRecordListByAccounts")
    @Operation(summary = "根据站点下会员账号,批量获取已审核通过的转代记录")
    List<MemberTransferReviewPageResVO> getRecordListByAccounts(@RequestBody MemberTransferReviewPageReqVO memberTransferReviewPageReqVO);

    @Operation(summary = "报表导出总数")
    @PostMapping(PREFIX + "getTotal")
    Long getTotal(@RequestBody MemberTransferReviewPageReqVO vo);


    @PostMapping(PREFIX + "listByAuditTime")
    @Operation(summary = "按照审核时间获取转移记录")
    Page<MemberTransferReviewPageResVO> listByAuditTime(@RequestBody MemberTransferReviewPageReqVO vos);

}
