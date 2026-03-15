package com.cloud.baowang.admin.controller.site;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.SystemActivityTemplateApi;
import com.cloud.baowang.activity.api.vo.SiteActivityTemplateVO;
import com.cloud.baowang.admin.vo.CurrencyCodeReqVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.vo.venue.SiteGameRequestVO;
import com.cloud.baowang.play.api.vo.venue.SiteGameResponseVO;
import com.cloud.baowang.play.api.vo.venue.SiteVenueRequestVO;
import com.cloud.baowang.play.api.vo.venue.SiteVenueResponseVO;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.api.operations.CustomerChannelApi;
import com.cloud.baowang.system.api.api.operations.DomainInfoApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.api.timezone.SystemTimezoneApi;
import com.cloud.baowang.system.api.api.verify.MailChannelConfigApi;
import com.cloud.baowang.system.api.api.verify.SmsChannelConfigApi;
import com.cloud.baowang.system.api.vo.language.SiteLanguageVO;
import com.cloud.baowang.system.api.vo.operations.*;
import com.cloud.baowang.system.api.vo.site.SiteAddVO;
import com.cloud.baowang.system.api.vo.site.SiteEnableVO;
import com.cloud.baowang.system.api.vo.site.SiteRequestVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.timezone.SystemTimezoneVO;
import com.cloud.baowang.system.api.vo.verify.MailChannelQueryVO;
import com.cloud.baowang.system.api.vo.verify.SiteEmailChannelVO;
import com.cloud.baowang.system.api.vo.verify.SiteSmsChannelVO;
import com.cloud.baowang.system.api.vo.verify.SmsChannelQueryVO;
import com.cloud.baowang.wallet.api.api.*;
import com.cloud.baowang.wallet.api.vo.SiteCurrencyDownBoxVO;
import com.cloud.baowang.wallet.api.vo.recharge.*;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawAuthorizeResVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawChannelResVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawAuthorizeReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawChannelReqVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author : 小智
 * @Date : 2024/7/26 10:17
 * @Version : 1.0
 */
@Tag(name = "站点列表")
@RestController
@RequestMapping("/site/api")
@AllArgsConstructor
public class SiteController {

    private final SiteCurrencyInfoApi currencyInfoApi;
    private final SiteApi siteApi;
    private final PlayVenueInfoApi playVenueInfoApi;
    private final GameInfoApi gameInfoApi;
    private final SiteRechargeApi siteRechargeApi;
    private final SiteRechargeChannelApi siteRechargeChannelApi;
    private final SiteWithdrawApi siteWithdrawApi;
    private final SiteWithdrawChannelApi siteWithdrawChannelApi;
    private final CustomerChannelApi customerChannelApi;
    private final SmsChannelConfigApi smsChannelConfigApi;
    private final MailChannelConfigApi mailChannelConfigApi;
    private final DomainInfoApi domainInfoApi;

    private final SystemRechargeTypeApi systemRechargeTypeApi;
    private final SystemRechargeWayApi systemRechargeWayApi;
    private final SystemTimezoneApi timezoneApi;
    private final LanguageManagerApi languageManagerApi;
    private final SystemActivityTemplateApi systemActivityTemplateApi;

    @Operation(summary = "下拉框-币种和语言")
    @GetMapping(value = "/getLanAndCurrencyDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getLanAndCurrencyDownBox() {
        return siteApi.getLanAndCurrencyDownBox();
    }

    @GetMapping("getSiteCurrencyDownBox")
    @Operation(summary = "获取币种下拉框(新增-编辑站点使用)")
    public ResponseVO<List<SiteCurrencyDownBoxVO>> getSiteCurrencyDownBox(@RequestParam(value = "siteCode", required = false, defaultValue = "") String siteCode) {
        return currencyInfoApi.getSiteCurrencyDownBox(siteCode);
    }

    @GetMapping("getSiteLanguageDownBox")
    @Operation(summary = "获取语言下拉列表(新增-编辑站点使用)")
    public ResponseVO<List<SiteLanguageVO>> getSiteLanguageDownBox(@RequestParam(value = "siteCode", required = false, defaultValue = "") String siteCode) {
        return languageManagerApi.getSiteLanguageDownBox(siteCode);
    }

    @GetMapping("getTimezoneDownBox")
    @Operation(summary = "获取时区下拉框")
    public ResponseVO<List<CodeValueNoI18VO>> getTimezoneDownBox() {
        ResponseVO<List<SystemTimezoneVO>> all = timezoneApi.getAll();
        if (all.isOk()) {
            List<CodeValueNoI18VO> timeZoneArr = Lists.newArrayList();
            List<SystemTimezoneVO> data = all.getData();
            data.forEach(item -> timeZoneArr.add(CodeValueNoI18VO.builder()
                    .code(item.getTimezoneCode()).value(item.getTimezoneCode()).build()));
            return ResponseVO.success(timeZoneArr);
        }
        return ResponseVO.success();
    }


    @Operation(summary = "获取时区列表")
    @GetMapping("getTimezoneList")
    public ResponseVO<List<SystemTimezoneVO>> getTimezoneList() {
        return timezoneApi.getAll();
    }

    @Operation(summary = "下拉框-站点列表")
    @GetMapping(value = "/getSiteDownBox")
    public ResponseVO<List<CodeValueVO>> getSiteDownBox() {
        return siteApi.getSiteDownBox();
    }

    @Operation(summary = "下拉框 充值方式,充值类型,提款方式,提款类型 ")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox(@RequestBody @Validated CurrencyCodeReqVO currencyCodeReqVO) {
        List<CodeValueVO> rechargeTypeEnums = Lists.newArrayList();
        ResponseVO<List<SystemRechargeTypeRespVO>> listRechargeTypeResponseVO = systemRechargeTypeApi.selectAll();
        if (listRechargeTypeResponseVO.isOk()) {
            List<SystemRechargeTypeRespVO> systemRechargeTypeRespVOS = listRechargeTypeResponseVO.getData();
            systemRechargeTypeRespVOS = systemRechargeTypeRespVOS.stream().filter(o -> o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for (SystemRechargeTypeRespVO systemRechargeTypeRespVO : systemRechargeTypeRespVOS) {
                CodeValueVO codeValueVO = new CodeValueVO();
                codeValueVO.setType(systemRechargeTypeRespVO.getCurrencyCode());
                codeValueVO.setCode(systemRechargeTypeRespVO.getId());
                codeValueVO.setValue(systemRechargeTypeRespVO.getRechargeTypeI18());
                rechargeTypeEnums.add(codeValueVO);
            }
        }

        List<CodeValueVO> rechargeWayEnums = Lists.newArrayList();
        ResponseVO<List<SystemRechargeWayRespVO>> listResponseVO = systemRechargeWayApi.selectAll();
        if (listResponseVO.isOk()) {
            List<SystemRechargeWayRespVO> systemRechargeWayRespVOS = listResponseVO.getData();
            systemRechargeWayRespVOS = systemRechargeWayRespVOS.stream().filter(o -> o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for (SystemRechargeWayRespVO systemRechargeWayRespVO : systemRechargeWayRespVOS) {
                CodeValueVO codeValueVO = new CodeValueVO();
                codeValueVO.setType(systemRechargeWayRespVO.getCurrencyCode());
                codeValueVO.setCode(systemRechargeWayRespVO.getId());
                codeValueVO.setValue(systemRechargeWayRespVO.getRechargeWayI18());
                rechargeWayEnums.add(codeValueVO);
            }
        }
        Map<String, List<CodeValueVO>> result = Maps.newHashMap();
        result.put("rechargeWayEnums", rechargeWayEnums);
        result.put("rechargeTypeEnums", rechargeTypeEnums);
        return ResponseVO.success(result);
    }


    @Operation(summary = "站点查询分页列表")
    @PostMapping("/pageList")
    public ResponseVO<Page<SiteVO>> querySiteInfo(@RequestBody SiteRequestVO siteRequestVO) {
        return siteApi.querySiteInfo(siteRequestVO);
    }

    @Operation(summary = "站点查询")
    @PostMapping("/allSite")
    public ResponseVO<List<SiteVO>> allSiteInfo() {
        return siteApi.allSiteInfo();
    }

    @Operation(summary = "站点查询-全部状态")
    @PostMapping("/getAllSiteList")
    public ResponseVO<List<SiteVO>> getSiteList() {
        return siteApi.getSiteList();
    }

    @Operation(summary = "禁用/启用/维护中")
    @PostMapping("/isEnable")
    public ResponseVO<?> isEnable(@RequestBody SiteEnableVO siteEnableVO) {
        siteEnableVO.setOperator(CurrReqUtils.getAccount());
        siteEnableVO.setOperatorTime(System.currentTimeMillis());
        return siteApi.isEnable(siteEnableVO);
    }

    @Operation(summary = "重置密码")
    @PostMapping("/resetPassword")
    public ResponseVO<?> resetPassword(@RequestBody SiteEnableVO siteEnableVO) {
        return siteApi.resetPassword(siteEnableVO);
    }

    @Operation(summary = "新增站点 根据不同的step保存各自相关信息(站点模式:site_model, 抽成方案:commission_plan)")
    @PostMapping("/judgeAndAddSite")
    public ResponseVO<?> addSite(@Valid @RequestBody SiteAddVO siteAddVO) {
        siteAddVO.setCreator(CurrReqUtils.getAccount());
        return siteApi.judgeAndAddSite(siteAddVO);
    }

    @PostMapping("updateSiteInfo")
    @Operation(summary = "编辑站点某一步骤信息")
    public ResponseVO<Boolean> updateSiteInfo(@RequestBody SiteAddVO siteAddVO) {
        siteAddVO.setCreator(CurrReqUtils.getAccount());
        return siteApi.updateSiteInfo(siteAddVO);
    }

    @Operation(summary = "查询场馆授权")
    @PostMapping("/queryVenueAuthorize")
    public ResponseVO<SiteVenueResponseVO> queryVenueAuthorize(@RequestBody SiteVenueRequestVO siteVenueRequestVO) {
        return playVenueInfoApi.queryVenueAuthorize(siteVenueRequestVO);
    }

    @Operation(summary = "查询游戏授权")
    @PostMapping("/queryGameAuthorize")
    public ResponseVO<SiteGameResponseVO> queryGameAuthorize(@RequestBody SiteGameRequestVO siteGameRequestVO) {
        return gameInfoApi.queryGameAuthorize(siteGameRequestVO);
    }

    @Operation(summary = "已选中的币种")
    @PostMapping(value = "/chooseCurrency")
    public ResponseVO<List<CodeValueVO>> chooseCurrency(@RequestBody SiteEnableVO siteEnableVO) {
        return siteApi.chooseCurrency(siteEnableVO.getSiteCode());
    }

    @Operation(summary = "查询站点新增充值方式授权")
    @PostMapping("/queryDepositAuthorize")
    public ResponseVO<SiteRechargeAuthorizeResVO> queryDepositAuthorize(
            @RequestBody RechargeAuthorizeReqVO reqVO) {
        return siteRechargeApi.queryDepositAuthorize(reqVO);
    }

    @Operation(summary = "查询站点充值通道")
    @PostMapping("/queryPlatformAuthorize")
    public ResponseVO<SiteRechargeChannelResVO> queryPlatformAuthorize(
            @RequestBody RechargeChannelReqVO reqVO) {
        return siteRechargeChannelApi.queryPlatformAuthorize(reqVO);
    }

    @Operation(summary = "查询站点新增提款方式授权")
    @PostMapping("/queryWithdrawAuthorize")
    public ResponseVO<SiteWithdrawAuthorizeResVO> queryWithdrawAuthorize(
            @RequestBody WithdrawAuthorizeReqVO reqVO) {
        return siteWithdrawApi.queryWithdrawAuthorize(reqVO);
    }

    @Operation(summary = "查询站点提款通道")
    @PostMapping("/queryWithdrawPlatformAuthorize")
    public ResponseVO<SiteWithdrawChannelResVO> queryWithdrawPlatformAuthorize(
            @RequestBody WithdrawChannelReqVO reqVO) {
        return siteWithdrawChannelApi.queryWithdrawPlatformAuthorize(reqVO);
    }

    @Operation(summary = "查询客服通道")
    @PostMapping("/queryCustomerChannel")
    public ResponseVO<SiteCustomerChannelResVO> queryCustomerChannel(
            @RequestBody CustomerChannelRequestVO reqVO) {
        return customerChannelApi.queryCustomerChannel(reqVO);
    }

    @Operation(summary = "查询短信通道")
    @PostMapping("/querySmsChannel")
    public ResponseVO<SiteSmsChannelVO> querySmsChannel(
            @RequestBody SmsChannelQueryVO reqVO) {
        return smsChannelConfigApi.querySmsChannel(reqVO);
    }

    @Operation(summary = "查询邮箱通道")
    @PostMapping("/queryEmailChannel")
    public ResponseVO<SiteEmailChannelVO> queryEmailChannel(
            @RequestBody MailChannelQueryVO reqVO) {
        return mailChannelConfigApi.queryEmailChannel(reqVO);
    }

    @Operation(summary = "查询域名设置列表")
    @PostMapping("/queryDomainList")
    public ResponseVO<Page<DomainVO>> queryDomainList(@RequestBody DomainRequestVO domainRequestVO) {
        return domainInfoApi.queryUnBindDomainPage(domainRequestVO);
    }


    @Operation(summary = "绑定域名")
    @PostMapping("/bindDomain")
    public ResponseVO<?> bindDomain(@RequestBody DomainBindVO domainBindVO) {
        domainBindVO.setOperator(CurrReqUtils.getAccount());
        return domainInfoApi.bind(domainBindVO);
    }


    @GetMapping("getSiteActivityTemplate")
    @Operation(summary = "获取站点模版列表信息")
    public ResponseVO<List<SiteActivityTemplateVO>> getSiteActivityTemplate(@RequestParam(value = "siteCode", required = false, defaultValue = "") String siteCode,
                                                                            @RequestParam(value = "handicapMode", required = false, defaultValue = "0") Integer handicapMode) {
        return systemActivityTemplateApi.querySiteActivityTemplate(siteCode,handicapMode);
    }


}
