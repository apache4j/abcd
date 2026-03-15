package com.cloud.baowang.user.api.api.vip;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.vip.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "vipRankApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - vipRank")
public interface VipRankApi {

    String PREFIX = ApiConstants.PREFIX + "/vipRank/api";

    @Operation(summary = "站点VIP段位配置查询")
    @PostMapping(value = PREFIX + "/queryVIPRank")
    ResponseVO<Page<SiteVIPRankVO>> queryVIPRankPage(@RequestBody PageVO pageVO);

    @Operation(summary = "批量插入站点VIP段位")
    @PostMapping(value = PREFIX + "/batchVIPRank")
    ResponseVO<Boolean> batchVIPRank(@RequestParam("siteCode") String siteCode, @RequestBody List<String> currency  , @RequestParam(value = "handicapMode",required = false,defaultValue = "0") Integer handicapMode);

    @Operation(summary = "编辑站点VIP段位")
    @PostMapping(value = PREFIX + "/updateVIPRank")
    ResponseVO<?> updateVIPRank(@RequestBody VIPRankUpdateVO vipRankUpdateVO);

    @Operation(summary = "获取总台VIP段位下拉")
    @PostMapping(value = PREFIX + "/getVipRank")
    ResponseVO<List<CodeValueNoI18VO>> getVipRank();

    @Operation(summary = "查询VIP段位变更记录")
    @PostMapping(value = PREFIX + "/queryVIPRankOperation")
    ResponseVO<Page<SiteVipChangeRecordVO>> queryVIPRankOperation(@RequestBody SiteVipChangeRecordPageQueryVO reqVO);

    @Operation(summary = "查询VIP段位变更记录数量")
    @PostMapping(value = PREFIX + "/queryOperationCount")
    ResponseVO<Long> queryOperationCount(@RequestBody SiteVipChangeRecordPageQueryVO reqVO);

    @Operation(summary = "VIP段位变更记录总条数")
    @PostMapping(value = PREFIX + "/getTotalCount")
    ResponseVO<Long> getTotalCount(SiteVipChangeRecordPageQueryVO reqVO);

    @Operation(summary = "根据id获取段位信息详情-编辑时回显使用")
    @GetMapping(value = PREFIX + "/queryVIPRankDetailById")
    ResponseVO<SiteVIPRankVO> queryVIPRankDetailById(@RequestParam("id") String id);

    @GetMapping(value = PREFIX + "/getVipRankListBySiteCode")
    @Operation(summary = "获取站点VIP段位下拉")
    ResponseVO<List<SiteVIPRankVO>> getVipRankListBySiteCode(@RequestParam("siteCode") String siteCode);

    @GetMapping(value = PREFIX + "/getVipRankSiteCodeAndCurrency")
    @Operation(summary = "获取VIP段位信息")
    SiteVipFeeRateVO getVipRankSiteCodeAndCurrency(@RequestParam("siteCode") String siteCode,
                                                   @RequestParam("vipRankCode") Integer vipRankCode,
                                                   @RequestParam("currencyCode") String currencyCode,
                                                   @RequestParam("withdrawWayId") String withdrawWayId);


    @GetMapping(value = PREFIX + "/getVipRankListBySiteCodes")
    @Operation(summary = "获取相关站点配置信息")
    Map<String, List<SiteVIPRankVO>> getVipRankListBySiteCodes(@RequestParam("siteCode") List<String> siteCodeList);

    @GetMapping(value = PREFIX + "/getVipRankListBySiteCodeAndCode")
    @Operation(summary = "获取VIP段位下拉")
    ResponseVO<SiteVIPRankVO> getVipRankListBySiteCodeAndCode(@RequestParam("siteCode") String siteCode, @RequestParam("vipRankCode") Integer vipRankCode);

    @PostMapping(value = PREFIX + "/getVipRankListBySiteCodeAndCodes")
    @Operation(summary = "根据siteCode,vipRankCode，批量获取vip段位信息")
    ResponseVO<List<SiteVIPRankVO>> getVipRankListBySiteCodeAndCodes(@RequestParam("siteCode") String siteCode, @RequestBody() List<Integer> vipRankCode);

    @GetMapping(value = PREFIX + "/getVipRankByCode")
    @Operation(summary = "获取总台vip段位配置信息")
    VIPRankVO getVipRankByCode(@RequestParam("vipRankCode") Integer vipRankCode);

    @GetMapping(value = PREFIX + "/getVipRankList")
    @Operation(summary = "获取总台vip段位配置列表")
    List<VIPRankVO> getVipRankList();


    @GetMapping(value = PREFIX + "/initSystemVipRank")
    @Operation(summary = "初始化总台vip段位数据")
    ResponseVO<Boolean> initSystemVipRank();

    @GetMapping("getFirstVipRankBySiteCode")
    @Operation(summary = "获取当前站点最低段位")
    SiteVIPRankVO getFirstVipRankBySiteCode(@RequestParam("siteCode") String siteCode);

    @GetMapping("getVIPRankGradeList")
    @Operation(summary = "站点-VIP段位等级联动下拉框")
    Map<String, List<CodeValueNoI18VO>> getVIPRankGradeList(@RequestParam("siteCode") String siteCode);

    @GetMapping(PREFIX + "getAllSiteVipRank")
    @Operation(summary = "分组获取所有站点vip段位信息")
    Map<String, List<SiteVIPRankVO>> getAllSiteVipRank();


    @GetMapping(PREFIX + "getVipRankBySiteCodeAndCode")
    @Operation(summary = "获取站点VIP段位信息")
    SiteVIPRankVO getVipRankBySiteCodeAndCode(@RequestParam("siteCode")String siteCode,@RequestParam("vipRank")Integer vipRank);


    @GetMapping(PREFIX + "getVipRankBySiteCode")
    @Operation(summary = "获取站点VIP段位信息")
    List<SiteVIPRankRabateVO> getVipRankBySiteCode(@RequestParam("siteCode")String siteCode);

    @GetMapping(PREFIX + "initUserWithdrawConfig")
    @Operation(summary = "初始化冲提款信息")
    ResponseVO<Boolean> initUserWithdrawConfig(@RequestParam("siteCode")String siteCode,
                                               @RequestParam("currency") List<String> currency,
                                               @RequestParam("handicapMode")Integer handicapMode);
}
