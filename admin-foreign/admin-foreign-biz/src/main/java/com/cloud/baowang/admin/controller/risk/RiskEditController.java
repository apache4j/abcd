/*
package com.cloud.baowang.admin.controller.risk;

import com.cloud.baowang.common.core.utils.CurrentRequestUtils;
import com.cloud.baowang.admin.service.RiskEditService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.risk.RiskEditReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskInfoReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskInfoRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "编辑风控")
@RequestMapping("/risk/edit")
@AllArgsConstructor
public class RiskEditController {
    

    private final RiskEditService riskEditService;

    @PostMapping("getRiskInfoByType")
    @Operation(summary = "风控-编辑风控-查询")
    public ResponseVO<RiskInfoRespVO> getRiskInfoByType(@RequestBody RiskInfoReqVO riskInfoReqVO) {
        return riskEditService.getRiskInfoByType(riskInfoReqVO);
    }
    
    @PostMapping("submitRiskRecord")
    @Operation(summary = "风控-编辑风控-提交")
    public ResponseVO<Boolean> submitRiskRecord(@RequestBody RiskEditReqVO riskEditReqVO) {
        riskEditReqVO.setCreator(CurrentRequestUtils.getCurrentOneId());
        riskEditReqVO.setCreatorName(CurrentRequestUtils.getCurrentUserAccount());
        return riskEditService.riskEditService(riskEditReqVO);
    }


}
*/
