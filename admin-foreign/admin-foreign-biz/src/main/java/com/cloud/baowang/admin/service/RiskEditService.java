package com.cloud.baowang.admin.service;


import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskCtrlEditApi;
import com.cloud.baowang.system.api.vo.risk.RiskEditReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskInfoReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskInfoRespVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class RiskEditService {

    private final RiskCtrlEditApi riskCtrlEditApi;
    public ResponseVO<RiskInfoRespVO> getRiskInfoByType(RiskInfoReqVO riskInfoReqVO) {
        return riskCtrlEditApi.getRiskInfoByType(riskInfoReqVO);
    }

    public ResponseVO<Boolean> riskEditService(RiskEditReqVO riskEditReqVO) {
        return riskCtrlEditApi.submitRiskRecord(riskEditReqVO);
    }
}
