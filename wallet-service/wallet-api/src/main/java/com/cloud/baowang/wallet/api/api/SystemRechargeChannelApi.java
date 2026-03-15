package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.recharge.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteSystemRechargeChannelApi", value = ApiConstants.NAME)
@Tag(name = "RPC服务-充值通道接口")
public interface SystemRechargeChannelApi {

    String PREFIX = ApiConstants.PREFIX + "/exchange/rechargeChannel/";


    @PostMapping(value = PREFIX + "selectPage")
    @Operation(summary = "充值通道分页查询")
    ResponseVO<Page<SystemRechargeChannelRespVO>> selectPage(@RequestBody SystemRechargeChannelReqVO systemRechargeChannelReqVO);
    @PostMapping(value = PREFIX + "selectBySort")
    @Operation(summary = "充值通道排序查询")
    ResponseVO<List<SystemRechargeChannelRespVO>> selectBySort(@RequestBody SystemRechargeChannelReqVO systemRechargeChannelReqVO);

    @PostMapping(value = PREFIX + "insert")
    @Operation(summary = "充值通道新增")
    ResponseVO<Void> insert(@RequestBody SystemRechargeChannelNewReqVO systemRechargeChannelNewReqVO);

    @PostMapping(value = PREFIX + "update")
    @Operation(summary = "充值通道修改")
    ResponseVO<Void> update(@RequestBody SystemRechargeChannelUpdateReqVO systemRechargeChannelUpdateReqVO);

    @PostMapping(value = PREFIX + "batchSave")
    @Operation(summary = "充值通道保存")
    ResponseVO<Boolean> batchSave(@RequestParam("userAccount") String userAccount, @RequestBody List<SortNewReqVO> sortNewReqVOS);

    @PostMapping(value = PREFIX + "enableOrDisable")
    @Operation(summary = "充值通道启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody SystemRechargeChannelStatusReqVO systemRechargeChannelStatusReqVO);

    @PostMapping(value = PREFIX + "getChannelById")
    @Operation(summary = "根据ID获取通道信息")
    SystemRechargeChannelBaseVO getChannelById(@RequestBody IdVO idVO);

    @PostMapping(value = PREFIX + "getChannelByCode")
    @Operation(summary = "根据通道code获取通道信息")
    SystemRechargeChannelBaseVO getChannelByCode(@RequestBody ChannelQueryReqVO reqVO);

    @PostMapping(value = PREFIX + "getChannelInfoByMerNo")
    @Operation(summary = "根据商户号获取通道信息")
    SystemRechargeChannelBaseVO getChannelInfoByMerNo(@RequestParam("channelName") String channelName, @RequestParam("merchantNo") String merchantNo);

    @PostMapping(value = PREFIX + "getChannelInfoByCurrencyAndWayId")
    @Operation(summary = "根据通道币种和方式ID获取通道信息")
    SystemRechargeChannelBaseVO getChannelInfoByCurrencyAndWayId(@RequestParam("currencyCode") String currencyCode,
                                                                 @RequestParam("rechargeWayId")String rechargeWayId,
                                                                 @RequestParam("siteCode")String siteCode,
                                                                 @RequestParam("channelId")String channelId);

    @PostMapping(value = PREFIX + "getByWayIds")
    @Operation(summary = "根据方式id批量获取通道信息")
    ResponseVO<List<SystemRechargeChannelBaseVO>> getByWayIds(@RequestBody List<String> emptyRechargeWayIds);

    @PostMapping(value = PREFIX + "getByIds")
    @Operation(summary = "根据id批量获取通道信息")
    ResponseVO<List<SystemRechargeChannelBaseVO>> getByIds(@RequestBody List<String> platformList);

    @PostMapping(value = PREFIX + "selectAll")
    @Operation(summary = "获取所有通道信息")
    ResponseVO<List<SystemRechargeChannelRespVO>> selectAll();

    @PostMapping(value = PREFIX + "getChannelInfoByChannelId")
    @Operation(summary = "根据通道币种和方式ID获取通道信息")
    SystemRechargeChannelBaseVO getChannelInfoByChannelId(@RequestParam("currencyCode") String currencyCode,
                                                                 @RequestParam("rechargeWayId")String rechargeWayId,
                                                                 @RequestParam("siteCode")String siteCode,
                                                                 @RequestParam("channelId")String channelId);
}
