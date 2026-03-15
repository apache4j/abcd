package com.cloud.baowang.system.api;

import com.cloud.baowang.system.api.api.RiskCtrlEditApi;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.risk.RiskEditReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskInfoReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskInfoRespVO;
import com.cloud.baowang.system.service.RiskCtrlEditService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class RiskCtrlEditApiImpl implements RiskCtrlEditApi {

    private final RiskCtrlEditService riskCtrlEditService;


    @Override
    public ResponseVO<RiskInfoRespVO> getRiskInfoByType(RiskInfoReqVO riskInfoReqVO) {
        String riskControlTypeCode = riskInfoReqVO.getRiskControlTypeCode();
        String riskControlAccount = riskInfoReqVO.getRiskControlAccount();
        if(StringUtils.isBlank(riskControlTypeCode)){
            return ResponseVO.fail(ResultCode.RISK_CTRL_TYPE_NULL);
        }
        if(StringUtils.isBlank(riskControlAccount)){
            return ResponseVO.fail(ResultCode.RISK_CTRL_ACCOUNT_NULL);
        }
        return riskCtrlEditService.getRiskInfoByType(riskInfoReqVO);
    }

    @Override
    public ResponseVO<Boolean> submitRiskRecord(RiskEditReqVO riskEditReqVO) {
        return riskCtrlEditService.submitRiskRecord(riskEditReqVO);
    }
}
