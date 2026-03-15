package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeConfigVO;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeWayListVO;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeWayRequestVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteSystemRechargeChannelRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayDetailRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayStatusReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayUpdateReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayFeeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteSystemRechargeWayApi",value = ApiConstants.NAME)
@Tag(name = "RPC服务-充值方式信息")
public interface SystemRechargeWayApi {

    String PREFIX = ApiConstants.PREFIX + "/exchange/rechargeWay/";

    @PostMapping(value = PREFIX + "selectAllValid")
    @Operation(summary = "所有有效充值方式列表")
    ResponseVO<List<SystemRechargeWayRespVO>> selectAllValid();

    @PostMapping(value = PREFIX + "selectAll")
    @Operation(summary = "所有充值方式列表")
    ResponseVO<List<SystemRechargeWayRespVO>> selectAll();

    @PostMapping(value = PREFIX + "selectPage")
    @Operation(summary = "充值方式分页查询")
    ResponseVO<Page<SystemRechargeWayRespVO>> selectPage(@RequestBody SystemRechargeWayReqVO systemRechargeWayReqVO);

    @PostMapping(value = PREFIX + "selectBySort")
    @Operation(summary = "所有充值方式排序列表")
    ResponseVO<List<SystemRechargeWayRespVO>> selectBySort(@RequestBody SystemRechargeWayReqVO systemRechargeWayReqVO);

    @PostMapping(value = PREFIX + "insert")
    @Operation(summary = "充值方式新增")
    ResponseVO<Void> insert(@RequestBody SystemRechargeWayNewReqVO systemRechargeWayNewReqVO);

    @PostMapping(value = PREFIX + "update")
    @Operation(summary = "充值方式修改")
    ResponseVO<Void> update(@RequestBody SystemRechargeWayUpdateReqVO systemRechargeWayUpdateReqVO);


    @PostMapping(value = PREFIX + "enableOrDisable")
    @Operation(summary = "充值方式启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody SystemRechargeWayStatusReqVO systemRechargeWayStatusReqVO);

    @PostMapping(value = PREFIX + "queryRechargeWayList")
    @Operation(summary = "查询总站充值方式配置")
    List<SystemRechargeWayRespVO> queryRechargeWayList();

    @PostMapping(value = PREFIX + "rechargeWayList")
    @Operation(summary = "客户端-充值方式列表")
    List<RechargeWayListVO> rechargeWayList(@RequestBody RechargeWayRequestVO rechargeWayRequestVO);


    @PostMapping(value = PREFIX + "agentRechargeWayList")
    @Operation(summary = "代理端-充值方式列表")
    List<RechargeWayListVO> agentRechargeWayList(@RequestBody RechargeWayRequestVO rechargeWayRequestVO);



    @PostMapping(value = PREFIX + "info")
    @Operation(summary = "充值方式详情查询")
    ResponseVO<SystemRechargeWayDetailRespVO> info(@RequestBody  IdReqVO idReqVO);


    @PostMapping(value = PREFIX + "getById")
    @Operation(summary = "充值方式详情查询")
    ResponseVO<SystemRechargeWayDetailRespVO> getInfoById(@RequestBody  IdReqVO idReqVO);


    @PostMapping(value = PREFIX + "batchSave")
    @Operation(summary = "批量保存充值方式")
    ResponseVO<Boolean> batchSave(@RequestParam("userAccount") String userAccount, @RequestBody List<SortNewReqVO> sortNewReqVOS);


    /**
     * 根据站点编码 获取充值方式配置等信息
     * @param siteCode
     * @param rechargeWayId
     * @param vipRank
     * @return
     */
    @PostMapping(value = PREFIX + "getRechargeConfigBySiteCode")
    @Operation(summary = "根据站点编码 获取充值方式配置等信息")
    RechargeConfigVO getRechargeConfigBySiteCode(@RequestParam("siteCode") String siteCode,
                     @RequestParam("rechargeWayId") String rechargeWayId);


    @PostMapping(value = PREFIX + "getChannelGroup")
    @Operation(summary = "根据站点编码 获取通道分组信息")
    Map<String, List<SiteSystemRechargeChannelRespVO>> getChannelGroup(@RequestParam("siteCode") String siteCode);

    @PostMapping(value = PREFIX + "getRechargeWayListBySiteCode")
    @Operation(summary = "根据站点编码 获取充值方式下列表下拉信息")
    List<CodeValueVO> getRechargeWayListBySiteCode(@RequestParam("siteCode") String siteCode);

    @PostMapping(value = PREFIX + "getRechargeWayByCurrencyAndNetworkType")
    @Operation(summary = "根据币种和网络协议获取方式")
    SystemRechargeWayDetailRespVO getRechargeWayByCurrencyAndNetworkType(@RequestParam("currencyCode") String currencyCode,
                                                                         @RequestParam("networkType") String networkType,
                                                                         @RequestParam("siteCode") String siteCode,
                                                                         @RequestParam("wayId") String wayId);

    @PostMapping(value = PREFIX + "calculateSiteRechargeWayFeeRate")
    @Operation(summary = "根据站点编码和充值方式ID 计算充值结算手续费")
    SiteRechargeWayFeeVO calculateSiteRechargeWayFeeRate(@RequestParam("siteCode")String siteCode,@RequestParam("depositWayId") String rechargeWayId,
                                                         @RequestParam("amount")BigDecimal amount,@RequestParam("channelType") String channelType);
}
