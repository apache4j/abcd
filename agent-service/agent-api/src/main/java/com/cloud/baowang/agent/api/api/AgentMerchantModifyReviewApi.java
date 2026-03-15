package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantModifyPageQueryVO;
import com.cloud.baowang.agent.api.vo.merchant.AgentMerchantModifyReviewVO;
import com.cloud.baowang.agent.api.vo.merchant.AuditVO;
import com.cloud.baowang.agent.api.vo.merchant.MerchantModifyVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "agentMerchantModifyReview", value = ApiConstants.NAME)
@Tag(name = "RPC 商务基础信息 服务")
public interface AgentMerchantModifyReviewApi {
    String PREFIX = ApiConstants.PREFIX + "/agentMerchantModifyReview/api/";

    @PostMapping(PREFIX + "initInfoModify")
    @Operation(summary = "发起信息变更")
    ResponseVO<Boolean> initInfoModify(@RequestBody MerchantModifyVO modifyVO);

    @PostMapping(PREFIX + "pageQuery")
    @Operation(summary = "分页查询")
    ResponseVO<Page<AgentMerchantModifyReviewVO>> pageQuery(@RequestBody AgentMerchantModifyPageQueryVO queryVO);

    @GetMapping(PREFIX + "detail")
    @Operation(summary = "详情")
    ResponseVO<AgentMerchantModifyReviewVO> detail(@RequestParam("id") String id,@RequestParam("account")String account);

    @GetMapping(PREFIX+"lock")
    @Operation(summary = "锁单")
    ResponseVO<Boolean> lock(@RequestParam("id") String id, @RequestParam("account")String account);

    @GetMapping(PREFIX+"unLock")
    @Operation(summary = "解锁")
    ResponseVO<Boolean> unLock(@RequestParam("id")String id, @RequestParam("account")String account);

    @PostMapping(PREFIX+"approveReview")
    @Operation(summary = "审核通过")
    ResponseVO<Boolean> approveReview(@RequestBody AuditVO auditVO);

    @PostMapping(PREFIX+"rejectReview")
    @Operation(summary = "审核驳回")
    ResponseVO<Boolean> rejectReview(@RequestBody AuditVO auditVO);
}
