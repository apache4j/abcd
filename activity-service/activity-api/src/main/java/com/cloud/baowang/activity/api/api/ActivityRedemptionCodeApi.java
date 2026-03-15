package com.cloud.baowang.activity.api.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.ApiConstants;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.free.FreeGameReqVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 兑换码API
 */
@FeignClient(contextId = "activityRedemptionCodeApi", value = ApiConstants.NAME)
@Tag(name = "兑换码-接口")
public interface ActivityRedemptionCodeApi {
    String PREFIX = ApiConstants.PREFIX + "/activityRedemptionCodeApi/api/";
    @Operation(summary = "添加兑换码基础")
    @PostMapping(PREFIX +"save")
    ResponseVO<Boolean> save(@RequestBody ActivityRedemptionCodeConfigVO activityRedemptionCodeConfigVO);

    @Operation(summary = "编辑兑换码信息")
    @PostMapping(PREFIX +"update")
    ResponseVO<Boolean> update(@RequestBody ActivityRedemptionCodeConfigVO activityRedemptionCodeConfigVO);

    @Operation(summary = "兑换码基础信息列表")
    @PostMapping(PREFIX +"redemptionCodeBasePageList")
    ResponseVO<Page<SiteActivityRedemptionCodeBaseRespVO>> redemptionCodeBasePageList(@RequestBody ActivityRedemptionCodeReqVO vo);

    @Operation(summary = "兑换码详情")
    @PostMapping(PREFIX +"info")
    ResponseVO<SiteActivityRedemptionCodeBaseRespVO> info(@RequestBody ActivityIdReqVO activityIdReqVO);

    @Operation(summary = "删除兑换码-基础信息,详情,兑换码均需要删除")
    @PostMapping(PREFIX +"delete")
    ResponseVO<Boolean> delete(@RequestParam("activityId") String activityId);

    @Operation(summary = "导出未兑换的兑换码")
    @PostMapping(PREFIX +"export")
    ResponseVO<List<SiteActivityRedemptionGenCodeVO>> export(@RequestParam("activityDetailId") String activityDetailId);

    @Operation(summary = "兑换码保存修改参数校验")
    @PostMapping(PREFIX +"check")
    ResponseVO<Boolean> check(@RequestBody ActivityRedemptionCodeConfigVO vo);

    @Operation(summary = "会员领取兑换码奖励")
    @PostMapping(PREFIX +"exchange")
    ResponseVO<Boolean> exchange(@RequestParam("userId") String userId,@RequestParam("code") String code);

    @Operation(summary = "根据兑换码获取兑换码对象")
    @PostMapping(PREFIX +"getGenCodeByCode")
    ResponseVO<SiteActivityRedemptionGenCodeVO> getGenCodeByCode(@RequestParam("code") String code);

    @Operation(summary = "根据兑换码或批次号获取已生成的兑换码列表")
    @PostMapping(PREFIX +"queryGenCodeList")
    ResponseVO<List<SiteActivityRedemptionGenCodeVO>> queryGenCodeList(@RequestParam("activityDetailId") String activityDetailId,
                                                                       @RequestParam("batchNo") String batchNo);
    @Operation(summary = "根据兑换码详情id获取详情对象")
    @PostMapping(PREFIX +"getActivityRedemptionCodeDetailVOById")
    ResponseVO<SiteActivityRedemptionCodeDetailVO> getActivityRedemptionCodeDetailVOById(@RequestParam("activityDetailId") long activityDetailId);

    @Operation(summary = "获取已领取奖励的兑换码对象")
    @PostMapping(PREFIX +"getRedemptionCodeExchangeVO")
    ResponseVO<SiteActivityRedemptionCodeExchangeVO> getRedemptionCodeExchangeVO(@RequestBody SiteActivityRedemptionCodeExchangeVO exchangeVO);

    @Operation(summary = "客户端开关,0:关闭,1:开启,默认1")
    @PostMapping(PREFIX +"clientSwitch")
    ResponseVO<Boolean> clientSwitch(@RequestParam("flag") Integer flag);

    @Operation(summary = "根据指定查询条件统计记录数")
    @PostMapping(PREFIX +"countRedemption")
    ResponseVO<Long> countRedemption(@RequestBody ActivityRedemptionCodeReqVO vo);

}
