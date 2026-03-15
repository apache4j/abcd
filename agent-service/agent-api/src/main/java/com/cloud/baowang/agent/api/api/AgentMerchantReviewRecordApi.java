package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.merchant.AddMerchantVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantReviewRecordVO;
import com.cloud.baowang.agent.api.vo.merchant.AuditVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantReviewRecordPageQueryVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(contextId = "agentMerchantReviewRecordApi", value = ApiConstants.NAME)
@Tag(name = "RPC 商务基础信息 服务")
public interface AgentMerchantReviewRecordApi {

    String PREFIX = ApiConstants.PREFIX + "/agentMerchantReviewRecordApi/api/";

    @Operation(description = "新增商务")

    @PostMapping(value = PREFIX + "addMerchant")
    ResponseVO<Boolean> addMerchant(@RequestBody AddMerchantVO vo);

    @PostMapping(value = PREFIX + "pageQuery")
    @Operation(summary = "列表")
    ResponseVO<Page<AgentMerchantReviewRecordVO>> pageQuery(@RequestBody MerchantReviewRecordPageQueryVO queryVO);

    @GetMapping(value = PREFIX + "lock")
    @Operation(summary = "锁单")
    ResponseVO<Boolean> lock(@RequestParam("id") String id, @RequestParam("account") String account);

    @GetMapping(value = PREFIX + "unLock")
    @Operation(summary = "解锁")
    ResponseVO<Boolean> unLock(@RequestParam("id") String id, @RequestParam("account") String account);

    @PostMapping(value = PREFIX + "approveReview")
    @Operation(summary = "审核通过")
    ResponseVO<Boolean> approveReview(@RequestBody AuditVO auditVO);

    @PostMapping(value = PREFIX + "rejectReview")
    @Operation(summary = "审核驳回")
    ResponseVO<Boolean> rejectReview(@RequestBody AuditVO auditVO);

    @GetMapping(PREFIX+"detail")
    @Operation(summary = "审核详情")
    ResponseVO<AgentMerchantReviewRecordVO> detail(@RequestParam("id") String id);
}
