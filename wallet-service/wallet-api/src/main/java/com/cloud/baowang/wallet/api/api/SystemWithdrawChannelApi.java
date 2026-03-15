package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.recharge.ChannelQueryReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteSystemWithdrawChannelApi", value = ApiConstants.NAME)
@Tag(name = "RPC服务-提现通道接口")
public interface SystemWithdrawChannelApi {

    String PREFIX = ApiConstants.PREFIX + "/withdrawChannel/api";


    @PostMapping(value = PREFIX + "selectPage")
    @Operation(summary = "提现通道分页查询")
    ResponseVO<Page<SystemWithdrawChannelResponseVO>> selectPage(@RequestBody SystemWithdrawChannelRequestVO withdrawChannelRequestVO);


    @PostMapping(value = PREFIX + "insert")
    @Operation(summary = "提现通道新增")
    ResponseVO<Void> insert(@RequestBody SystemWithdrawChannelAddVO withdrawChannelAddVO);

    @PostMapping(value = PREFIX + "update")
    @Operation(summary = "提现通道修改")
    ResponseVO<Void> update(@RequestBody SystemWithdrawChannelUpdateVO withdrawChannelUpdateVO);


    @PostMapping(value = PREFIX + "enableOrDisable")
    @Operation(summary = "提现通道启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody SystemWithdrawChannelStatusVO withdrawChannelStatusVO);

    @PostMapping(value = PREFIX + "getByWayIds")
    @Operation(summary = "根据方式批量获取通道")
    ResponseVO<List<SystemWithdrawChannelResponseVO>> getByWayIds(@RequestBody List<String> emptyRechargeWayIds);

    @PostMapping(value = PREFIX + "getByIds")
    @Operation(summary = "根据id批量获取通道")
    ResponseVO<List<SystemWithdrawChannelResponseVO>> getByIds(@RequestBody List<String> platformList);


    @PostMapping(value = PREFIX + "selectBySort")
    @Operation(summary = "提现通道排序查询")
    ResponseVO<List<SystemWithdrawChannelResponseVO>> selectBySort(@RequestBody SystemWithdrawChannelRequestVO withdrawChannelRequestVO);

    @PostMapping(value = PREFIX + "batchSave")
    @Operation(summary = "提现通道排序保存")
    ResponseVO<Boolean> batchSave(@RequestParam("userAccount") String userAccount,@RequestBody List<SortNewReqVO> sortNewReqVOS);

    @PostMapping(value = PREFIX + "getChannelById")
    @Operation(summary = "根据ID获取通道信息")
    SystemWithdrawChannelResponseVO getChannelById(@RequestBody IdVO idVO);

    @PostMapping(value = PREFIX + "getChannelByCode")
    @Operation(summary = "根据通道code获取通道信息")
    SystemWithdrawChannelResponseVO getChannelByCode(@RequestBody ChannelQueryReqVO reqVO);

    @PostMapping(value = PREFIX + "getChannelInfoByMerNo")
    @Operation(summary = "根据商户号获取通道信息")
    SystemWithdrawChannelResponseVO getChannelInfoByMerNo(@RequestParam("channelName") String channelName, @RequestParam("merchantNo") String merchantNo);

    @PostMapping(value = PREFIX + "selectAll")
    @Operation(summary = "提现通道查询所有")
    ResponseVO<List<SystemWithdrawChannelResponseVO>> selectAll();

    @PostMapping(value = PREFIX+"getListByIdsType")
    @Operation(summary = "获取总台配置提款通道")
    List<SiteWithdrawChannelVO> getListByWayId(@RequestParam("wayId") String wayId,
                                               @RequestParam("siteCode")String siteCode);

    @PostMapping(value = PREFIX+"getChannelByIdAndChannelType")
    List<SystemWithdrawChannelResponseVO> getChannelByIdAndChannelType(@RequestParam("channelType") String channelType,
                                                                       @RequestParam("currencyCode") String currencyCode,
                                                                       @RequestBody List<String> systemChannelIds);


    @GetMapping(value = PREFIX + "selectBankAll")
    @Operation(summary = "查询所有银行卡提现方式-根据channelCode,channelName 去重")
    ResponseVO<List<SystemWithdrawChannelResponseVO>> selectBankAll(@RequestParam("currencyCode") String currencyCode);

}
