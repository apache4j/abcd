package com.cloud.baowang.system.api.api.site;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.site.SiteAddVO;
import com.cloud.baowang.system.api.vo.site.SiteEnableVO;
import com.cloud.baowang.system.api.vo.site.SiteRequestVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @Author : 小智
 * @Date : 2024/7/26 11:03
 * @Version : 1.0
 */
@FeignClient(contextId = "remoteSiteApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - siteApi")
public interface SiteApi {

    String PREFIX = ApiConstants.PREFIX + "/siteConfig/api";


    @Operation(summary = "查询站点列表")
    @PostMapping(PREFIX + "/querySiteInfo")
    ResponseVO<Page<SiteVO>> querySiteInfo(@RequestBody SiteRequestVO siteRequestVO);

    @Operation(summary = "查询站点列表")
    @PostMapping(PREFIX + "/allSiteInfo")
    ResponseVO<List<SiteVO>> allSiteInfo();

    @Operation(summary = "校验并且新增站点")
    @PostMapping(PREFIX + "/judgeAndAddSite")
    ResponseVO<?> judgeAndAddSite(@RequestBody SiteAddVO siteAddVO);

    @Operation(summary = "编辑站点")
    @PostMapping(PREFIX + "/updateSiteInfo")
    ResponseVO<Boolean> updateSiteInfo(@RequestBody SiteAddVO siteAddVO);

    @Operation(summary = "站点启用/禁用")
    @PostMapping(PREFIX + "/isEnable")
    ResponseVO<?> isEnable(@RequestBody SiteEnableVO siteEnableVO);

    @Operation(summary = "重置密码")
    @PostMapping(PREFIX + "/resetPassword")
    ResponseVO<?> resetPassword(@RequestBody SiteEnableVO siteEnableVO);

    @Operation(summary = "获取站点详细信息")
    @GetMapping(PREFIX + "/getSiteInfo")
    ResponseVO<SiteVO> getSiteInfo(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "获取站点详细信息 - 排除禁用语言")
    @GetMapping(PREFIX + "/getCustomerSiteInfo")
    ResponseVO<SiteVO> getCustomerSiteInfo(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "根据名称获取站点详细信息")
    @GetMapping(PREFIX + "/getSiteInfoByName")
    ResponseVO<List<SiteVO>> getSiteInfoByName(@RequestParam("siteName") String siteName);

    @Operation(summary = "根据siteCode获取站点基本信息")
    @GetMapping(PREFIX + "/getSiteInfoByCode")
    SiteVO getSiteInfoByCode(@RequestParam("siteCode") String siteCode);

    @Operation(summary = "获取站点详细信息")
    @GetMapping(PREFIX + "/getLanAndCurrencyDownBox")
    ResponseVO<Map<String, List<CodeValueVO>>> getLanAndCurrencyDownBox();

    @Operation(summary = "获取该站点已选中币种")
    @PostMapping(PREFIX + "/chooseCurrency")
    ResponseVO<List<CodeValueVO>> chooseCurrency(@RequestParam("siteCode") String siteCode);

    //checkSiteIncludesRiskControl
    @Operation(summary = "查询站点是否包括风控")
    @PostMapping(PREFIX + "/checkSiteIncludesRiskControl")
    Boolean checkSiteIncludesRiskControl(@RequestParam("siteCode") String siteCode);

    /**
     * 修改当前站点平台币名称，同步更新下缓存
     *
     * @param siteCode
     * @param platCurrencyName
     * @return
     */
    @GetMapping("updPlatCurrency")
    @Operation(summary = "修改当前站点平台币名称，同步更新下缓存")
    ResponseVO<Boolean> updPlatCurrency(
            @RequestParam("siteCode") String siteCode,
            @RequestParam("platCurrencyName") String platCurrencyName,
            @RequestParam("platCurrencySymbol") String platCurrencySymbol,
            @RequestParam("platCurrencyIcon") String platCurrencyIcon);

    @Operation(summary = "下拉框-站点列表")
    @PostMapping(PREFIX + "/getSiteDownBox")
    ResponseVO<List<CodeValueVO>> getSiteDownBox();

    @Operation(summary = "根据时区获取站点列表")
    @PostMapping(PREFIX + "/getSiteInfoByTimezone")
    List<SiteVO> getSiteInfoByTimezone(@RequestParam("timeZone") String timeZone);

    @GetMapping(PREFIX + "/getSiteDetail")
    @Operation(summary = "获取站点基础数据")
    SiteVO getSiteDetail(@RequestParam("siteCode") String siteCode);

    @PostMapping(PREFIX + "/getSiteInfoSByCodes")
    @Operation(summary = "批量获取站点信息")
    List<SiteVO> getSiteInfoSByCodes(@RequestBody List<String> totalSiteCodeList);

    @GetMapping(PREFIX + "getSiteList")
    @Operation(summary = "获取所有站点列表")
    ResponseVO<List<SiteVO>> getSiteList();

    @PostMapping(PREFIX + "updateSiteRebateStatus")
    @Operation(summary = "更新站点返水状态")
    ResponseVO updateSiteRebateStatus(@RequestParam("status") Integer status);

    @Operation(summary = "查询站点列表所有状态")
    @PostMapping(PREFIX + "/siteInfoAllstauts")
    ResponseVO<List<SiteVO>> siteInfoAllstauts();

}
