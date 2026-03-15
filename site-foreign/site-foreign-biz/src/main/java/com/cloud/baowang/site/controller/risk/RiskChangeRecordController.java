package com.cloud.baowang.site.controller.risk;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskChangeRecordVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDownReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskRecordReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "风控变更记录查询")
@RequestMapping("/risk/record")
@AllArgsConstructor
public class RiskChangeRecordController {

    private final RiskApi riskApi;


    @PostMapping("/getRiskLevelList")
    @Operation(summary = "风控层级下拉框")
    public ResponseVO getRiskLevelList(@RequestBody RiskLevelDownReqVO riskLevelDownReqVO) {
        riskLevelDownReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        if (riskLevelDownReqVO.getRiskControlType() == null) {
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        return riskApi.getRiskLevelList(riskLevelDownReqVO);
    }

    @PostMapping("/getAgentRiskRecordList")
    @Operation(summary = "查询代理风控变更记录列表")
    public ResponseVO<Page<RiskChangeRecordVO>> getAgentRiskRecordList(@RequestBody RiskRecordReqVO riskRecordReqVO) {
        riskRecordReqVO.setRiskControlTypeCode(RiskTypeEnum.RISK_AGENT.getCode());
        riskRecordReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        ResponseVO<Page<RiskChangeRecordVO>> responseVO = riskApi.getRiskRecordListPage(riskRecordReqVO);
        if (responseVO != null) {
            return responseVO;
        }

        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    @PostMapping("/getUserRiskRecordList")
    @Operation(summary = "查询会员风控变更记录列表")
    public ResponseVO<Page<RiskChangeRecordVO>> getUserRiskRecordList(@RequestBody RiskRecordReqVO riskRecordReqVO) {
        riskRecordReqVO.setRiskControlTypeCode(RiskTypeEnum.RISK_MEMBER.getCode());
        riskRecordReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        ResponseVO<Page<RiskChangeRecordVO>> responseVO = riskApi.getRiskRecordListPage(riskRecordReqVO);
        if (responseVO != null) {
            return responseVO;
        }

        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    @PostMapping("/getBankRiskRecordList")
    @Operation(summary = "查询银行卡风控变更记录列表")
    public ResponseVO<Page<RiskChangeRecordVO>> getBankRiskRecordList(@RequestBody RiskRecordReqVO riskRecordReqVO) {
        riskRecordReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        riskRecordReqVO.setRiskControlTypeCode(RiskTypeEnum.RISK_BANK.getCode());
       /* riskrecordreqvo.setdatadesensitization(currrequtils.getdatadesensity());*/
        ResponseVO<Page<RiskChangeRecordVO>> responseVO = riskApi.getRiskRecordListPage(riskRecordReqVO);
        if (responseVO != null) {
            return responseVO;
        }

        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    @PostMapping("/getVirtualRiskRecordList")
    @Operation(summary = "查询虚拟币风控变更记录列表")
    public ResponseVO<Page<RiskChangeRecordVO>> getVirtualRiskRecordList(@RequestBody RiskRecordReqVO riskRecordReqVO) {
        riskRecordReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        riskRecordReqVO.setRiskControlTypeCode(RiskTypeEnum.RISK_VIRTUAL.getCode());
/*
        riskRecordReqVO.setDataDesensitization(CurrReqUtils.getDataDesensity());
*/
        ResponseVO<Page<RiskChangeRecordVO>> responseVO = riskApi.getRiskRecordListPage(riskRecordReqVO);
        if (responseVO != null) {
            return responseVO;
        }

        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    @PostMapping("/getIpRiskRecordList")
    @Operation(summary = "查询IP风控变更记录列表")
    public ResponseVO<Page<RiskChangeRecordVO>> getIpRiskRecordList(@RequestBody RiskRecordReqVO riskRecordReqVO) {
        riskRecordReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        riskRecordReqVO.setRiskControlTypeCode(RiskTypeEnum.RISK_IP.getCode());
        ResponseVO<Page<RiskChangeRecordVO>> responseVO = riskApi.getRiskRecordListPage(riskRecordReqVO);
        if (responseVO != null) {
            return responseVO;
        }

        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    @PostMapping("/getDeviceRiskRecordList")
    @Operation(summary = "查询设备风控变更记录列表")
    public ResponseVO<Page<RiskChangeRecordVO>> getDeviceRiskRecordList(@RequestBody RiskRecordReqVO riskRecordReqVO) {
        riskRecordReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        riskRecordReqVO.setRiskControlTypeCode(RiskTypeEnum.RISK_DEVICE.getCode());
        ResponseVO<Page<RiskChangeRecordVO>> responseVO = riskApi.getRiskRecordListPage(riskRecordReqVO);
        if (responseVO != null) {
            return responseVO;
        }

        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    @PostMapping("/getWalletRiskRecordList")
    @Operation(summary = "查询电子钱包风控变更记录")
    public ResponseVO<Page<RiskChangeRecordVO>> getWalletRiskRecordList(@RequestBody RiskRecordReqVO riskRecordReqVO) {
        riskRecordReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        riskRecordReqVO.setRiskControlTypeCode(RiskTypeEnum.RISK_WALLET.getCode());
        ResponseVO<Page<RiskChangeRecordVO>> responseVO = riskApi.getRiskRecordListPage(riskRecordReqVO);
        if (responseVO != null) {
            return responseVO;
        }

        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }

    @PostMapping("/getBusinessRecordList")
    @Operation(summary = "查询风险商务变更记录")
    public ResponseVO<Page<RiskChangeRecordVO>> getBusinessRecordList(@RequestBody RiskRecordReqVO riskRecordReqVO) {
        riskRecordReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        riskRecordReqVO.setRiskControlTypeCode(RiskTypeEnum.RISK_BUSINESS.getCode());
        ResponseVO<Page<RiskChangeRecordVO>> responseVO = riskApi.getRiskRecordListPage(riskRecordReqVO);
        if (responseVO != null) {
            return responseVO;
        }

        return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
    }
}
