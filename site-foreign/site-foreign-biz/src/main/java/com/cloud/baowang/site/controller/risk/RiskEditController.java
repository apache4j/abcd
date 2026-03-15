package com.cloud.baowang.site.controller.risk;

import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.RiskCtrlEditApi;
import com.cloud.baowang.system.api.vo.risk.RiskEditReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskInfoReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskInfoRespVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDownReqVO;
import com.cloud.baowang.wallet.api.api.SiteWithdrawWayApi;
import com.cloud.baowang.wallet.api.api.SystemWithdrawWayApi;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "编辑风控")
@RequestMapping("/risk/edit")
@AllArgsConstructor
public class RiskEditController {

    private final RiskApi riskApi;
    private final RiskCtrlEditApi riskCtrlEditApi;
    private final SystemWithdrawWayApi systemWithdrawWayApi;
    private final SiteWithdrawWayApi siteWithdrawWayApi;


    @PostMapping("getRiskInfoByType")
    @Operation(summary = "风控-编辑风控-查询")
    public ResponseVO<RiskInfoRespVO> getRiskInfoByType(@RequestBody RiskInfoReqVO riskInfoReqVO) {
        riskInfoReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return riskCtrlEditApi.getRiskInfoByType(riskInfoReqVO);
    }

    @PostMapping("submitRiskRecord")
    @Operation(summary = "风控-编辑风控-提交")
    public ResponseVO<Boolean> submitRiskRecord(@RequestBody RiskEditReqVO riskEditReqVO) {
        riskEditReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        riskEditReqVO.setCreator(CurrReqUtils.getAccount());
        riskEditReqVO.setCreatorName(CurrReqUtils.getAccount());

        return riskCtrlEditApi.submitRiskRecord(riskEditReqVO);
    }

    @PostMapping("/getRiskLevelList")
    @Operation(summary = "风控层级下拉框")
    public ResponseVO<?> getRiskLevelList(@RequestBody RiskLevelDownReqVO riskLevelDownReqVO) {
        riskLevelDownReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        if (riskLevelDownReqVO.getRiskControlType() == null) {
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        return riskApi.getRiskLevelList(riskLevelDownReqVO);
    }

    @Operation(summary = "获取所有电子钱包提款方式")
    @GetMapping(value = "/withDrawWayList")
    public ResponseVO<List<SiteWithdrawWayResVO>> withDrawTypeDownBox() {
        ResponseVO<List<SiteWithdrawWayResVO>> siteResp = siteWithdrawWayApi.queryBySiteAndTypeCode(CurrReqUtils.getSiteCode(), WithdrawTypeEnum.ELECTRONIC_WALLET.getCode());
        if (siteResp.isOk()) {
            List<SiteWithdrawWayResVO> data = siteResp.getData();
            return ResponseVO.success(data);
        }
        return ResponseVO.success();
    }
}
