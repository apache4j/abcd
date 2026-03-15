package com.cloud.baowang.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.activity.api.api.v2.ActivityParticipateV2Api;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2AppRespVO;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.api.PromotionDomainApi;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentOtherVO;
import com.cloud.baowang.agent.api.vo.domian.AddVisCountVO;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.CurrentRequestBasicInfoVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.core.vo.IPRespVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.utils.IpAPICoUtils;
import com.cloud.baowang.system.api.api.exchange.SystemCurrencyInfoApi;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.api.operations.CustomerChannelApi;
import com.cloud.baowang.system.api.api.operations.IpAddressAreaCurrencyApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.api.site.SiteSeoApi;
import com.cloud.baowang.system.api.api.site.area.AreaSiteManageApi;
import com.cloud.baowang.system.api.vo.language.LanguageValidListCacheVO;
import com.cloud.baowang.system.api.vo.operations.*;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.site.WebSiteVO;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteLangVO;
import com.cloud.baowang.system.api.vo.site.seo.SiteSeoAppResVO;
import com.cloud.baowang.system.api.vo.site.seo.SiteSeoQueryVO;
import com.cloud.baowang.user.api.api.SiteUserAvatarConfigApi;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigRespVO;
import com.cloud.baowang.user.service.CommonService;
import com.cloud.baowang.user.vo.ActivityRecommendResVO;
import com.cloud.baowang.user.vo.CommonDownBoxIosVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Tag(name = "客户端公共controller")
@RestController
@RequestMapping("/common")
@AllArgsConstructor
public class CommonController {

    private CommonService commonService;

    private final SystemCurrencyInfoApi systemCurrencyInfoApi;

    private final SiteApi siteApi;
    private final SiteSeoApi siteSeoApi;

    private final AreaSiteManageApi areaSiteManageApi;

    private final LanguageManagerApi languageManagerApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final CustomerChannelApi customerChannelApi;

    private final SiteUserAvatarConfigApi userAvatarConfigApi;

    private final PromotionDomainApi promotionDomainApi;

    private final IpAddressAreaCurrencyApi ipAddressAreaCurrencyApi;

    private final AgentInfoApi agentInfoApi;

    private final ActivityParticipateV2Api activityParticipateApi;


    @GetMapping("getSiteInfo")
    @Operation(summary = "获取站点基础信息")
    public ResponseVO<WebSiteVO> getSiteInfo() {
        SiteVO data = siteApi.getCustomerSiteInfo(CurrReqUtils.getSiteCode()).getData();
        WebSiteVO vo = new WebSiteVO();
        BeanUtil.copyProperties(data, vo);
        return ResponseVO.success(vo);
    }

    @Operation(summary = "通用下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox(@RequestBody List<String> types) {
        return ResponseVO.success(commonService.getSystemParamsByList(types));
    }

    @Operation(summary = "通用下拉框-ios专用")
    @PostMapping(value = "/getDownBoxObj")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBoxIos(@Valid @RequestBody CommonDownBoxIosVO vo) {
        return ResponseVO.success(commonService.getSystemParamsByList(vo.getTypes()));
    }

    @Operation(summary = "获取手机区号下拉框")
    @PostMapping(value = "/getAreaCodeDownBox")
    public ResponseVO<List<AreaSiteLangVO>> getAreaCodeDownBox() {
        String siteCode = CurrReqUtils.getSiteCode();
        if (siteCode == null) {
            return ResponseVO.fail(ResultCode.REFERER_EMPTY);
        }
        String language = CurrReqUtils.getLanguage();
        return areaSiteManageApi.getAreaList(siteCode, language);
    }

    @Operation(summary = "获取指定客服信息")
    @GetMapping(value = "/getCustomerByCode")
    public ResponseVO<ClientCustomerChannelVO> getCustomerByCode(@RequestParam("channelCode") String channelCode) {
        String siteCode = CurrReqUtils.getSiteCode();
        if (siteCode == null) {
            return ResponseVO.fail(ResultCode.REFERER_EMPTY);
        }
        CustomerChannelVO customerChannelVO = customerChannelApi.getSiteCustomerChannelByCode(siteCode, channelCode);
        return ResponseVO.success(ConvertUtil.entityToModel(customerChannelVO, ClientCustomerChannelVO.class));
    }

    @Operation(summary = "获取美洽客服信息")
    @GetMapping(value = "/getMeiQiaChannelInfo")
    public ResponseVO<MeiQiaChannelVO> getMeiQiaChannelInfo() {
        String siteCode = CurrReqUtils.getSiteCode();
        if (siteCode == null) {
            return ResponseVO.fail(ResultCode.REFERER_EMPTY);
        }

        return ResponseVO.success(customerChannelApi.getMeiQiaChannelInfo(siteCode));

    }


    @Operation(summary = "获取客服信息")
    @PostMapping(value = "/getSiteCustomerChannel")
    public ResponseVO<ClientCustomerChannelVO> getSiteCustomerChannel() {
        String siteCode = CurrReqUtils.getSiteCode();
        if (siteCode == null) {
            return ResponseVO.fail(ResultCode.REFERER_EMPTY);
        }
        ClientCustomerChannelVO customerChannelVO = customerChannelApi.getSiteCustomerChannel(siteCode);
        if (customerChannelVO == null || customerChannelVO.getChannelCode() == null) {
            return ResponseVO.fail(ResultCode.CUSTOMER_CHANNEL_CLOSE);
        }

        return ResponseVO.success(customerChannelVO);
    }

    @Operation(summary = "下拉框-语言 ")
    @PostMapping(value = "/getLangDownBox")
    public ResponseVO<List<LanguageValidListCacheVO>> getLangDownBox() {
        return languageManagerApi.validList();
    }

    @Operation(summary = "下拉框-有效币种 ")
    @PostMapping(value = "/getCurrencyList")
    public ResponseVO<IpAdsWebResVO> getCurrencyDownBox() {
        String siteCode = CurrReqUtils.getSiteCode();
        String ip = CurrReqUtils.getReqIp();

        IPRespVO response = IpAPICoUtils.getIp(ip);
        IpAdsWebReqVO vo = new IpAdsWebReqVO();
        vo.setIp(ip);

        vo.setSiteCode(siteCode);
        if (ObjectUtils.isNotEmpty(response)) {
            vo.setAreaCode(response.getCountryCode());
            vo.setCity(response.getCountryName()+" "+ response.getProvinceName()+ " "+response.getCityName());
        }
        return ipAddressAreaCurrencyApi.queryWebCurrey(vo);
    }

    /**
     * 会员站点的基础信息
     */
    @Operation(summary = "会员站点的基础信息 ")
    @RequestMapping(value = "/getCurrentBasicInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseVO<CurrentRequestBasicInfoVO> getCurrentBasicInfo() {
        return ResponseVO.success(CurrReqUtils.getCurrentBasicInfo());
    }

    @GetMapping("getAvaList")
    @Operation(summary = "获取当前站点所有已启用的头像列表")
    public ResponseVO<List<SiteUserAvatarConfigRespVO>> getAvaList() {
        return userAvatarConfigApi.getListBySiteCode(CurrReqUtils.getSiteCode());
    }

    @PostMapping("/addVisCount")
    @Operation(summary = "长链接访问量+1")
    public ResponseVO<Boolean> addVisCount(@RequestBody AddVisCountVO countVO) {
        return promotionDomainApi.addVisCount(countVO);
    }

    @GetMapping("/getAgentOtherMessage")
    @Operation(summary = "获取代理其他相关信息参数")
    public ResponseVO<AgentOtherVO> getAgentOtherMessage(@RequestParam("inviteCode") String inviteCode) {
        String siteCode = CurrReqUtils.getSiteCode();
        AgentOtherVO vo = new AgentOtherVO();
        AgentInfoVO agentInfoVO = agentInfoApi.getAgentByInviteCode(inviteCode, siteCode);
        if (agentInfoVO != null) {
            BeanUtil.copyProperties(agentInfoVO, vo);
        }
        return ResponseVO.success(vo);
    }

    @PostMapping("/seo")
    @Operation(summary = "seo检索信息")
    public ResponseVO<SiteSeoAppResVO> seo() {
        List<SiteSeoAppResVO> list = siteSeoApi.findList(SiteSeoQueryVO.builder().siteCode(CurrReqUtils.getSiteCode()).lang(CurrReqUtils.getLanguage()).build());
        return ResponseVO.success(list.stream().findFirst().orElse(new SiteSeoAppResVO()));
    }

    @GetMapping("/activityV2/recommended")
    @Operation(summary = "未登录推荐活动")
    public ResponseVO<ActivityRecommendResVO> recommended() {

        String reqDeviceId = CurrReqUtils.getReqDeviceId();

        if (StrUtil.isNotEmpty(reqDeviceId) || !reqDeviceId.equalsIgnoreCase("unknown")){
            String remindKey = "activityV2::isRemind::" + reqDeviceId;
            if (RedisUtil.isKeyExist(remindKey)) {
                return ResponseVO.success();
            }
        }
        ResponseVO<ActivityBaseV2AppRespVO> recommendedRes = activityParticipateApi.recommended();
        if (recommendedRes.isOk() && recommendedRes.getData() != null) {
            ActivityBaseV2AppRespVO baseV2AppRespVO = recommendedRes.getData();
            ActivityRecommendResVO activityRecommendResVO = BeanUtil.copyProperties(baseV2AppRespVO, ActivityRecommendResVO.class);
            return ResponseVO.success(activityRecommendResVO);
        }
        return ResponseVO.success();
    }

    @GetMapping("/activityV2/recommended/unRemindToday")
    @Operation(summary = "未登录推荐活动今天不提示")
    public ResponseVO<T> unRemindToday() {
        String reqDeviceId = CurrReqUtils.getReqDeviceId();
        if (StrUtil.isNotEmpty(reqDeviceId) || !reqDeviceId.equalsIgnoreCase("unknown")){
            String remindKey = "activityV2::isRemind::" + reqDeviceId;
            long endOfDayInTimeZone = TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone());
            long expireTime =  endOfDayInTimeZone - System.currentTimeMillis();
            RedisUtil.setValue(remindKey, 1, expireTime/1000);
        }
        return ResponseVO.success();
    }
}
