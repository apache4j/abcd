package com.cloud.baowang.site.controller.risk;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.api.RiskCtrlLevelApi;
import com.cloud.baowang.system.api.vo.risk.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 创建风控层级类型接口
 */

@RestController
@Tag(name = "创建风控层级")
@RequestMapping("/risk/level")
@AllArgsConstructor
public class RiskLevelController {

    private final RiskCtrlLevelApi riskCtrlLevelApi;
    private final RiskApi riskApi;


    @PostMapping("/selectRiskLevelList")
    @Operation(summary = "风控-创建风控层级-查询")
    public ResponseVO<Page<RiskLevelResVO>> selectRiskLevelList(@RequestBody RiskLevelReqVO riskLevelReqVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        riskLevelReqVO.setSiteCode(siteCode);
        return riskCtrlLevelApi.selectRiskLevelList(riskLevelReqVO);
    }

    @PostMapping("/insertRiskLevel")
    @Operation(summary = "风控-创建风控层级-新增")
    public ResponseVO<Boolean> insertRiskLevel(@RequestBody RiskLevelAddVO riskLevelAddVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        riskLevelAddVO.setOperator(CurrReqUtils.getAccount());
        riskLevelAddVO.setSiteCode(siteCode);
        return riskCtrlLevelApi.insertRiskLevel(riskLevelAddVO);
    }

    @PostMapping("/deleteRiskLevel")
    @Operation(summary = "风控-创建风控层级-删除")
    public ResponseVO<Boolean> deleteRiskLevel(@RequestBody IdVO idVO) {
        return riskCtrlLevelApi.deleteRiskLevel(idVO);
    }

    @PostMapping("/updateRiskLevel")
    @Operation(summary = "风控-创建风控层级-编辑信息")
    public ResponseVO<Boolean> updateRiskLevel(@RequestBody RiskLevelEditVO riskLevelEditVO) {
        riskLevelEditVO.setOperator(CurrReqUtils.getAccount());
        riskLevelEditVO.setSiteCode(CurrReqUtils.getSiteCode());
        return riskCtrlLevelApi.updateRiskLevel(riskLevelEditVO);
    }

    @PostMapping("/getRiskLevelList")
    @Operation(summary = "风控层级下拉框")
    public ResponseVO<?> getRiskLevelList(@RequestBody RiskLevelDownReqVO riskLevelDownReqVO) {
        riskLevelDownReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return riskApi.getRiskLevelList(riskLevelDownReqVO);
    }
}
