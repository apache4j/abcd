/*
package com.cloud.baowang.admin.controller.risk;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrentRequestUtils;
import com.cloud.baowang.admin.service.RiskLevelService;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelAddVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelEditVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

*/
/**
 *
 * 创建风控层级类型接口
 *//*


@RestController
@Tag(name = "创建风控层级")
@RequestMapping("/risk/level")
@AllArgsConstructor
public class RiskLevelController {
    private final SystemParamApi systemParamApi;

    private final RiskLevelService riskLevelService;



    @PostMapping("/selectRiskLevelList")
    @Operation(summary = "风控-创建风控层级-查询")
    public ResponseVO<Page<RiskLevelResVO>> selectRiskLevelList(@RequestBody RiskLevelReqVO riskLevelReqVO) {
        return riskLevelService.selectRiskLevelList(riskLevelReqVO);
    }

    @PostMapping("/insertRiskLevel")
    @Operation(summary = "风控-创建风控层级-新增")
    public ResponseVO<Boolean> insertRiskLevel(@RequestBody RiskLevelAddVO riskLevelAddVO) {
        return riskLevelService.insertRiskLevel(riskLevelAddVO, CurrentRequestUtils.getCurrentOneId());
    }

    @PostMapping("/deleteRiskLevel")
    @Operation(summary = "风控-创建风控层级-删除")
    public ResponseVO<Boolean> deleteRiskLevel(@RequestBody IdVO idVO) {
        return riskLevelService.deleteRiskLevel(idVO);
    }

    @PostMapping("/updateRiskLevel")
    @Operation(summary = "风控-创建风控层级-编辑信息")
    public ResponseVO<Boolean> updateRiskLevel(@RequestBody RiskLevelEditVO riskLevelEditVO) {
        return riskLevelService.updateRiskLevel(riskLevelEditVO, CurrentRequestUtils.getCurrentOneId());
    }
}
*/
