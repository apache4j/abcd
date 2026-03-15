package com.cloud.baowang.wallet.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteSiteWithdrawChannelApi",value = ApiConstants.NAME)
@Tag(name = "RPC服务-站点提现通道api")
public interface SiteWithdrawChannelApi {

    String PREFIX = ApiConstants.PREFIX + "/withdrawChannel/api/";

    @PostMapping(value = PREFIX + "batchSave")
    @Operation(summary = "提现通道批量新增")
    ResponseVO<Void> batchSave(@RequestBody List<SiteWithdrawChannelBatchRequestVO> siteWithdrawChannelBatchRequestVO, @RequestParam("siteCode")String siteCode);


    @PostMapping(value = PREFIX + "enableOrDisable")
    @Operation(summary = "提现通道启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody SiteWithdrawChannelStatusRequestVO siteWithdrawChannelStatusRequestVO);

    @PostMapping(value = PREFIX + "queryByCond")
    @Operation(summary = "根据条件查询所有站点通道信息")
    ResponseVO<List<SystemWithdrawChannelResponseVO>> queryByCond(@RequestBody SiteWithdrawChannelReqVO siteWithdrawChannelReqVO);


    @PostMapping(value = PREFIX + "queryWithdrawPlatformAuthorize")
    @Operation(summary = "新增站点提现通道查询")
    ResponseVO<SiteWithdrawChannelResVO> queryWithdrawPlatformAuthorize(@RequestBody WithdrawChannelReqVO reqVO);

    @GetMapping(value = PREFIX+"getListBySiteCodeAndWayId")
    @Operation(summary = "获取站点当前提款方式下所有启用通道")
    List<SiteWithdrawChannelVO> getListBySiteCodeAndWayId(@RequestParam("siteCode") String siteCode,
                                                          @RequestParam("wayId") String depositWithdrawWayId);

    @PostMapping(value = PREFIX + "selectWithdrawPage")
    @Operation(summary = "站点下提现通道分页查询")
    ResponseVO<Page<SiteWithdrawChannelResponseVO>> selectWithdrawPage(@RequestBody SiteWithdrawChannelRequestVO siteWithdrawChannelReqVO);

    @PostMapping(value = PREFIX + "selectBySort")
    @Operation(summary = "站点下提现通道排序查询")
    ResponseVO<List<SiteWithdrawChannelResponseVO>> selectBySort(@RequestBody SiteWithdrawChannelRequestVO siteWithdrawChannelReqVO);

    @PostMapping(value = PREFIX + "batchSaveSort")
    @Operation(summary = "站点下提现通道批量保存排序")
    ResponseVO<Boolean> batchSaveSort(@RequestParam("userAccount") String account, @RequestBody List<SortNewReqVO> sortNewReqVOS);
}
