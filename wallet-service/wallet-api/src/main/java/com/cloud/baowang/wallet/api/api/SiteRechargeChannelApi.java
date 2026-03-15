package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeChannelReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChangeVipUseScopeVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChannelBatchReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChannelRecvInfoVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChannelReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChannelResVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChannelRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChannelStatusReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteSiteRechargeChannelApi",value = ApiConstants.NAME)
@Tag(name = "RPC服务-站点充值通道api")
public interface SiteRechargeChannelApi {

    String PREFIX = ApiConstants.PREFIX + "/site/rechargeChannel/";

    @PostMapping(value = PREFIX + "batchSave")
    @Operation(summary = "充值通道批量新增")
    ResponseVO<Void> batchSave(@RequestBody List<SiteRechargeChannelBatchReqVO> siteRechargeChannelBatchReqVO,
                               @RequestParam("siteCode") String siteCode,@RequestParam("handicapMode")Integer handicapMode);


    @PostMapping(value = PREFIX + "enableOrDisable")
    @Operation(summary = "充值通道启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody SiteRechargeChannelStatusReqVO siteRechargeChannelStatusReqVO);

    @PostMapping(value = PREFIX + "queryPlatformAuthorize")
    @Operation(summary = "站点充值通道查询")
    ResponseVO<SiteRechargeChannelResVO> queryPlatformAuthorize(@RequestBody RechargeChannelReqVO reqVO);

    @PostMapping(value = PREFIX + "selectRechargePage")
    @Operation(summary = "站点下充值通道分页查询")
    ResponseVO<Page<SiteRechargeChannelRespVO>> selectRechargePage(@RequestBody SiteRechargeChannelReqVO siteRechargeChannelReqVO);

    @PostMapping(value = PREFIX + "selectBySort")
    @Operation(summary = "站点下充值通道排序查询")
    ResponseVO<List<SiteRechargeChannelRespVO>> selectBySort(@RequestBody SiteRechargeChannelReqVO siteRechargeChannelReqVO);

    @PostMapping(value = PREFIX + "batchSaveSort")
    @Operation(summary = "站点下充值通道批量保存排序")
    ResponseVO<Boolean> batchSaveSort(@RequestParam("userAccount") String account, @RequestBody List<SortNewReqVO> sortNewReqVOS);


    @PostMapping(value = PREFIX + "saveReceiveInfo")
    @Operation(summary = "站点下充值通道收款信息保存")
    ResponseVO<Boolean> saveReceiveInfo(@RequestBody SiteRechargeChannelRecvInfoVO siteRechargeChannelRecvInfoVO);

    @PostMapping(value = PREFIX + "saveVipGradeUseScope")
    @Operation(summary = "vip等级使用范围")
    ResponseVO<Boolean> saveVipGradeUseScope(@RequestBody SiteRechargeChangeVipUseScopeVO siteRechargeChangeVipUseScopeVO);
}
