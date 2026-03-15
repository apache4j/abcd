package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayBatchReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayRequestVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayResVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayResponseVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayStatusReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayVipUseScopeVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteSiteRechargeWayApi",value = ApiConstants.NAME)
@Tag(name = "RPC服务-站点充值方式api")
public interface SiteRechargeWayApi {

    String PREFIX = ApiConstants.PREFIX + "/site/rechargeWay/";

    @PostMapping(value = PREFIX + "batchSave")
    @Operation(summary = "充值方式批量新增")
    ResponseVO<Void> batchSave(@RequestBody SiteRechargeWayBatchReqVO siteRechargeWayBatchReqVO);


    @PostMapping(value = PREFIX + "enableOrDisable")
    @Operation(summary = "充值方式启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody SiteRechargeWayStatusReqVO siteRechargeWayStatusReqVO);


    @PostMapping(value = PREFIX + "queryBySite")
    @Operation(summary = "查询站点下充值方式")
    ResponseVO<List<SiteRechargeWayResVO>> queryBySite();


    @PostMapping(value = PREFIX + "queryRechargeWay")
    @Operation(summary = "查询站点下充值方式")
    SiteRechargeWayVO queryRechargeWay(@RequestParam("siteCode") String siteCode, @RequestParam("rechargeWayId") String rechargeWayId);

    @PostMapping(value = PREFIX + "selectRechargePage")
    @Operation(summary = "站点下充值方式分页查询")
    ResponseVO<Page<SiteRechargeWayResponseVO>> selectRechargePage(@RequestBody SiteRechargeWayRequestVO siteRechargeWayRequestVO);

    @PostMapping(value = PREFIX + "selectBySort")
    @Operation(summary = "站点下充值方式排序查询")
    ResponseVO<List<SiteRechargeWayResponseVO>> selectBySort(@RequestBody SiteRechargeWayRequestVO siteRechargeWayRequestVO);

    @PostMapping(value = PREFIX + "batchSaveSort")
    @Operation(summary = "批量保存充值方式排序")
    ResponseVO<Boolean> batchSaveSort(@RequestParam("userAccount") String userAccount, @RequestBody List<SortNewReqVO> sortNewReqVOS);

    @PostMapping(value = PREFIX + "saveVipGradeUseScope")
    @Operation(summary = "vip等级使用范围")
    ResponseVO<Boolean> saveVipGradeUseScope(@RequestBody SiteRechargeWayVipUseScopeVO siteRechargeWayVipUseScopeVO);
}
