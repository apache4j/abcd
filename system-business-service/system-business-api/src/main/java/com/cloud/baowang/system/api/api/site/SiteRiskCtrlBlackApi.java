package com.cloud.baowang.system.api.api.site;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountIsBlackReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "remoteSiteRiskBlackApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 黑名单")
public interface SiteRiskCtrlBlackApi {

    String PREFIX = ApiConstants.PREFIX + "/risk/siteBlack/api/";

    @PostMapping(PREFIX + "addBlackAccount")
    @Operation(summary = "添加风控黑名单")
    ResponseVO<Boolean> addBlackAccount(@RequestBody RiskBlackAccountVO vo);

    @PostMapping(PREFIX + "updateBlackAccount")
    @Operation(summary = "更新风控黑名单")
    ResponseVO<Boolean> updateBlackAccount(@RequestBody RiskBlackAccountVO vo);
    @PostMapping(PREFIX +"removeBlackAccount")
    @Operation(summary = "删除风控黑名单")
    ResponseVO<Boolean> removeBlackAccount(@RequestBody IdVO vo);
    @PostMapping(PREFIX +"getRiskBlackListPage")
    @Operation(summary = "查询风控黑名单列表")
    ResponseVO<Page<RiskBlackAccountVO>> getRiskBlackListPage(@RequestBody RiskBlackAccountReqVO queryVO);

    @PostMapping(PREFIX +"getRiskBlack")
    @Operation(summary = "查询风控黑名单")
    ResponseVO<List<RiskBlackAccountVO>> getRiskBlack(@RequestBody RiskBlackAccountVO queryVO);

    @PostMapping(PREFIX +"isRiskBlack")
    @Operation(summary = "判断是否是风控账户")
    ResponseVO<Boolean> isRiskBlack(@RequestBody RiskBlackAccountIsBlackReqVO queryVO);


    @PostMapping(PREFIX +"getRiskIpBlack")
    @Operation(summary = "查询风控IP黑名单")
    ResponseVO<Boolean> getRiskIpBlack(@RequestBody RiskBlackAccountVO queryVO);


}