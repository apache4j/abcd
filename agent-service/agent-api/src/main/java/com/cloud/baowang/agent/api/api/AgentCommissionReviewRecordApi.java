package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.StatusVO;
import com.cloud.baowang.agent.api.vo.agentreview.ReviewVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionReviewDetailVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionReviewRecordVO;
import com.cloud.baowang.agent.api.vo.commission.CommissionReviewCalculateReq;
import com.cloud.baowang.agent.api.vo.commission.CommissionReviewReq;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/10/20 10:11
 * @description:
 */

@FeignClient(contextId = "remoteAgentCommissionReviewRecordApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 佣金审核记录 ")
public interface AgentCommissionReviewRecordApi {
    String PREFIX = ApiConstants.PREFIX+"/commissionReviewRecord/api";

    @Operation(summary = "审核列表")
    @PostMapping(value = PREFIX+"/getReviewRecordPage")
    ResponseVO<Page<AgentCommissionReviewRecordVO>> getReviewRecordPage(@RequestBody CommissionReviewReq vo);


    @Operation(summary = "佣金审核详情")
    @PostMapping(value = PREFIX+"/getAgentCommissionRecordDetail")
    ResponseVO<AgentCommissionReviewDetailVO> getAgentCommissionRecordDetail(@RequestBody IdVO idVO);

    @Operation(summary = "审核列表条数")
    @PostMapping(value = PREFIX+"/getReviewRecordPageCount")
    Long getReviewRecordPageCount(@RequestBody CommissionReviewReq reviewReq);


    @Operation(summary = "根据代理计算佣金")
    @PostMapping(value = PREFIX+"/calculateAgentCommission")
    ResponseVO<BigDecimal> calculateAgentCommission(@RequestBody CommissionReviewCalculateReq commissionReviewCalculateReq);


    @Operation(summary = "构建导出字段")
    @PostMapping(value = PREFIX+"/buildExportFields")
    CommissionReviewReq buildExportFields(@RequestBody CommissionReviewReq vo);
}
