package com.cloud.baowang.system.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelAddVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelEditVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelResVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelReqVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteRiskLevelApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 风控层级")
public interface RiskCtrlLevelApi {

    String PREFIX = ApiConstants.PREFIX + "/risk/level/api/";

    @PostMapping(PREFIX + "selectRiskLevelList")
    @Operation(summary = "按条件分页查询风控层级数据列表")
    ResponseVO<Page<RiskLevelResVO>> selectRiskLevelList(@RequestBody RiskLevelReqVO riskLevelReqVO);

    @PostMapping(PREFIX + "insertRiskLevel")
    @Operation(summary = "新增风控层级")
    ResponseVO<Boolean> insertRiskLevel(@RequestBody RiskLevelAddVO riskLevelAddVO);

    @PostMapping("deleteRiskLevel")
    @Operation(summary = "删除风控层级")
    ResponseVO<Boolean> deleteRiskLevel(@RequestBody IdVO idVO);

    @PostMapping(PREFIX + "updateRiskLevel")
    @Operation(summary = "编辑风控层级")
    ResponseVO<Boolean> updateRiskLevel(@RequestBody RiskLevelEditVO riskLevelEditVO);

    @PostMapping(PREFIX + "getAllRiskLevelList")
    @Operation(summary = "获取风控层级集合")
    List<RiskLevelResVO> getAllRiskLevelList();
}
