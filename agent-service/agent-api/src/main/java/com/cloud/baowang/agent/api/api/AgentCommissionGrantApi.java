package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.commission.AgentGranRecordPageAllVO;
import com.cloud.baowang.agent.api.vo.commission.CommissionGranRecordReqVO;
import com.cloud.baowang.agent.api.vo.commission.CommissionGrantRecordDetailVO;
import com.cloud.baowang.agent.api.vo.commission.IdPageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/8 10:11
 * @description:
 */

@FeignClient(contextId = "remoteAgentCommissionGrantApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 佣金发放记录 ")
public interface AgentCommissionGrantApi {
    String PREFIX = ApiConstants.PREFIX+"/commissionGrant/api";

    @Operation(summary = "佣金发放分页列表")
    @PostMapping(value = PREFIX+"/getGrantRecordPageList")
    ResponseVO<AgentGranRecordPageAllVO> getGrantRecordPageList(@RequestBody CommissionGranRecordReqVO requestVO);

    @Operation(summary = "查看佣金详情")
    @PostMapping(value = PREFIX+"/getCommissionDetail")
    ResponseVO<CommissionGrantRecordDetailVO> getCommissionDetail(@RequestBody IdPageVO idVO);

    @Operation(summary = "统计条数")
    @PostMapping(value = PREFIX+"/getGrantRecordPageCount")
    Long getGrantRecordPageCount(@RequestBody CommissionGranRecordReqVO requestVO);

    @Operation(summary = "佣金汇总")
    @PostMapping(value = PREFIX+"/agentCommissionSum")
    BigDecimal agentCommissionSum(@RequestBody CommissionGranRecordReqVO requestVO);
}
