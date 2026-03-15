package com.cloud.baowang.system.api.api;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.risk.RiskEditReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskInfoReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskInfoRespVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteRiskEditApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 风控层级编辑")
public interface RiskCtrlEditApi {

    String PREFIX = ApiConstants.PREFIX + "/risk/edit/api/";


    @PostMapping(PREFIX + "getRiskInfoByType")
    @Operation(summary = "根据账号查询风控详情")
    ResponseVO<RiskInfoRespVO> getRiskInfoByType(@RequestBody RiskInfoReqVO riskInfoReqVO);


    @PostMapping(PREFIX + "submitRiskRecord")
    @Operation(summary = "风控编辑提交")
    ResponseVO<Boolean> submitRiskRecord(@RequestBody RiskEditReqVO riskEditReqVO);
}