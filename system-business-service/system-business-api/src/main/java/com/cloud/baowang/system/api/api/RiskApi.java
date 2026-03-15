package com.cloud.baowang.system.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.system.api.vo.risk.RiskAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskChangeRecordVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDownReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelResVO;
import com.cloud.baowang.system.api.vo.risk.RiskListAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskRecordReqVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
@FeignClient(contextId = "remoteRiskApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 编辑风控层级")
 public interface RiskApi {

    String PREFIX = ApiConstants.PREFIX + "/risk/api/";


    @PostMapping(PREFIX +"getRiskListAccount")
    @Operation(summary = "查询风控信息")
    List<RiskAccountVO> getRiskListAccount(@RequestBody RiskListAccountQueryVO queryVO);

    @PostMapping(PREFIX +"getAll")
    @Operation(summary = "查询风控信息")
    List<RiskAccountVO> getAll();

    @PostMapping(PREFIX +"getRiskAccountByAccount")
    @Operation(summary = "根据账号查询风控信息")
    RiskAccountVO getRiskAccountByAccount(@RequestBody RiskAccountQueryVO riskAccountQueryVO);

    @PostMapping(PREFIX +"saveRiskListAccount")
    @Operation(summary = "新增风控信息")
    int saveRiskListAccount(@RequestBody RiskAccountVO riskAccountVO);

    @PostMapping(PREFIX +"updateRiskListAccount")
    @Operation(summary = "修改风控信息")
    Integer updateRiskListAccount(@RequestBody RiskAccountVO riskAccountVO);

   @PostMapping(PREFIX +"getById")
   @Operation(summary = "根据id查询风控详情")
   RiskLevelDetailsVO getById(@RequestBody IdVO vo) ;

    @PostMapping(PREFIX +"getByIds")
    @Operation(summary ="根据ids查询风控详情")
    Map<String,RiskLevelDetailsVO> getByIds(@RequestBody List<String> ids) ;

    @PostMapping(PREFIX +"risk/riskLevelDown")
    @Operation(summary ="查询风控等级列表")
    ResponseVO<List<RiskLevelResVO>> getRiskLevelList(@RequestBody RiskLevelDownReqVO riskLevelDownReqVO);

    @PostMapping(PREFIX +"risk/record/getRiskRecordListPage")
    @Operation(summary ="查询风控列表分页")
    ResponseVO<Page<RiskChangeRecordVO>> getRiskRecordListPage(@RequestBody RiskRecordReqVO riskRecordReqVO);
}
