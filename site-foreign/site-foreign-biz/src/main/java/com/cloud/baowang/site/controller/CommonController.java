package com.cloud.baowang.site.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityBaseApi;
import com.cloud.baowang.activity.api.api.SystemActivityTemplateApi;
import com.cloud.baowang.activity.api.api.v2.ActivityBaseV2Api;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.vo.ActivityBaseRespVO;
import com.cloud.baowang.activity.api.vo.ActivityBaseVO;
import com.cloud.baowang.activity.api.vo.SiteActivityTemplateVO;
import com.cloud.baowang.agent.api.api.AgentLabelApi;
import com.cloud.baowang.agent.api.api.PromotionDomainApi;
import com.cloud.baowang.agent.api.vo.PromotionDomainRespVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainPageQueryVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueResVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.site.service.CommonService;
import com.cloud.baowang.site.vo.ActivityVO;
import com.cloud.baowang.system.api.api.exchange.SystemCurrencyInfoApi;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.api.site.area.AreaSiteManageApi;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDownReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskTypeListResVO;
import com.cloud.baowang.system.api.vo.site.SiteLoginVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteLangVO;
import com.cloud.baowang.user.api.api.vip.SiteVipOptionApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.cloud.baowang.user.api.vo.vip.VIPGradeVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.SiteSecurityBalanceApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Tag(name = "公共controller")
@RestController
@RequestMapping("/common")
@AllArgsConstructor
public class CommonController {

    private final CommonService commonService;

    private final SiteApi siteApi;

    private final LanguageManagerApi languageManagerApi;

    private final AreaSiteManageApi areaSiteManageApi;

    private final VipGradeApi vipGradeApi;

    private final VipRankApi vipRankApi;

    private final AgentLabelApi agentLabelApi;

    private SiteCurrencyInfoApi siteCurrencyInfoApi;

    private SiteSecurityBalanceApi siteSecurityBalanceApi;

    private SystemActivityTemplateApi systemActivityTemplateApi;

    private SiteVipOptionApi siteVipOptionApi;

    private PlayVenueInfoApi playVenueInfoApi;

    private ActivityBaseApi activityBaseApi;

    private ActivityBaseV2Api activityBaseV2Api;

    @Operation(summary = "获取当前登陆站点的详细信息")
    @GetMapping("/site/siteDetail")
    public ResponseVO<SiteVO> siteDetail() {
        String siteCode = CurrReqUtils.getSiteCode();
        return siteApi.getSiteInfo(siteCode);
    }

    @Operation(summary = "获取当前登陆站点的详细信息")
    @GetMapping("/siteInfo")
    public ResponseVO<SiteLoginVO> siteInfo() {
        String siteCode = CurrReqUtils.getSiteCode();
        ResponseVO<SiteVO> siteVORes = siteApi.getSiteInfo(siteCode);
        return ResponseVO.success(ConvertUtil.entityToModel(siteVORes.getData(), SiteLoginVO.class));
    }

    @Operation(summary = "获取当前登陆站点的备用金")
    @GetMapping("/getSecurityBalance")
    public ResponseVO<SiteSecurityBalanceRespVO> getSecurityBalance() {
        String siteCode = CurrReqUtils.getSiteCode();
        SiteSecurityBalanceRespVO siteSecurityBalanceRespVO = siteSecurityBalanceApi.findBySiteCode(siteCode);
        return ResponseVO.success(siteSecurityBalanceRespVO);
    }

    @Operation(summary = "下拉框-通用")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox(@RequestBody List<String> types) {
        return ResponseVO.success(commonService.getSystemParamsByList(types));
    }

    @Operation(summary = "下拉框-通用")
    @PostMapping(value = "/getDownBoxDemo")
    public ResponseVO<List<CodeValueResVO>> getDownBoxDemo(@RequestBody List<String> types) {
        return ResponseVO.success(commonService.getSystemParamsByListVO(types));
    }


    @Operation(summary = "所有币种查询")
    @PostMapping("/currencyInfo/selectAll")
    ResponseVO<List<SiteCurrencyInfoRespVO>> selectAll() {
        String siteCode = CurrReqUtils.getSiteCode();
        List<SiteCurrencyInfoRespVO> siteCurrencyInfoRespVOS = siteCurrencyInfoApi.getBySiteCode(siteCode);
        return ResponseVO.success(siteCurrencyInfoRespVOS);
    }


    @Operation(summary = "所有币种查询-包含平台币")
    @PostMapping("/currencyInfo/selectContainsWtc")
    ResponseVO<List<SiteCurrencyInfoRespVO>> selectContainsWtc() {
        String siteCode = CurrReqUtils.getSiteCode();
        List<SiteCurrencyInfoRespVO> siteCurrencyInfoRespVOS = siteCurrencyInfoApi.getBySiteCode(siteCode);
        SiteCurrencyInfoRespVO wtcCurrency = new SiteCurrencyInfoRespVO();
        wtcCurrency.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        wtcCurrency.setCurrencyName(CommonConstant.PLAT_CURRENCY_CODE);
        wtcCurrency.setCurrencyNameI18(CommonConstant.PLAT_CURRENCY_CODE);
        wtcCurrency.setCurrencyIcon(siteCurrencyInfoRespVOS.get(0).getPlatCurrencyIcon());
        wtcCurrency.setCurrencySymbol(siteCurrencyInfoRespVOS.get(0).getPlatCurrencySymbol());
        wtcCurrency.setFinalRate(BigDecimal.ONE);
        siteCurrencyInfoRespVOS.add(0, wtcCurrency);
        return ResponseVO.success(siteCurrencyInfoRespVOS);
    }


    @Operation(summary = "查询所有语言")
    @PostMapping("/language/selectAll")
    public ResponseVO<List<LanguageManagerListVO>> list() {
        return languageManagerApi.list();
    }

    @Operation(summary = "获取手机区号下拉框")
    @PostMapping(value = "/site/getAreaCodeDownBox")
    public ResponseVO<List<AreaSiteLangVO>> getAreaCodeDownBox() {
        String siteCode = CurrReqUtils.getSiteCode();
        if (siteCode == null) {
            return ResponseVO.fail(ResultCode.REFERER_EMPTY);
        }
        String language = CurrReqUtils.getLanguage();
        if (language == null) {
            // 设置英文
            language = LanguageEnum.EN_US.getLang();
            // return ResponseVO.fail(ResultCode.AREA_LIMIT);
        }
        return areaSiteManageApi.getAreaList(siteCode, language);
    }

    @Operation(summary = "获取当前站点vip段位")
    @GetMapping("/getVipRankList")
    public ResponseVO<List<SiteVIPRankVO>> getVipRankList() {
        String siteCode = CurrReqUtils.getSiteCode();
        return vipRankApi.getVipRankListBySiteCode(siteCode);
    }

    @Operation(summary = "获取当前站点vip等级")
    @GetMapping("/getVipGradeList")
    public ResponseVO<List<SiteVIPGradeVO>> getVipGradeList(@RequestParam("vipRankCode") String vipRankCode) {
        String siteCode = CurrReqUtils.getSiteCode();
        return ResponseVO.success(vipGradeApi.getSiteVipGradeList(siteCode, vipRankCode));
    }

    @Operation(summary = "获取当前站点所有vip等级")
    @GetMapping("/getVipGradeListAll")
    public ResponseVO<List<SiteVIPGradeVO>> getVipGradeListAll() {
        String siteCode = CurrReqUtils.getSiteCode();
        return ResponseVO.success(vipGradeApi.queryAllVIPGrade(siteCode));
    }

    @RequestMapping(value = "/getAgentLabelList", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "获取代理标签下拉")
    public ResponseVO<List<CodeValueNoI18VO>> getDownBox() {
        String siteCode = CurrReqUtils.getSiteCode();
        List<CodeValueNoI18VO> result = new ArrayList<>();
        List<AgentLabelVO> allAgentLabelBySiteCode = agentLabelApi.getAllAgentLabelBySiteCode(siteCode);
        if (CollectionUtil.isNotEmpty(allAgentLabelBySiteCode)) {
            result = allAgentLabelBySiteCode.stream()
                    .map(agentLabel -> new CodeValueNoI18VO(agentLabel.getId(), agentLabel.getName()))
                    .toList();
        }
        return ResponseVO.success(result);
    }


    @Operation(summary = "下拉框-风控层级")
    @PostMapping(value = "/getRiskDownBox")
    public ResponseVO<List<CodeValueNoI18VO>> getRiskDownBox(@Valid @RequestBody RiskLevelDownReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(commonService.getRiskDownBox(vo));
    }

    @Operation(summary = "下拉框-风控类型")
    @PostMapping("getRiskTypeDownBox")
    public ResponseVO<List<RiskTypeListResVO>> getRiskTypeDownBox() {
        return ResponseVO.success(commonService.getRiskTypeDownBox());
    }

    @Operation(summary = "下拉框-币种列表-不包含平台币")
    @PostMapping(value = "/getCurrencyListNo")
    public ResponseVO<List<CodeValueVO>> getCurrencyListNo() {
        return ResponseVO.success(siteCurrencyInfoApi.getCurrencyListNo(CurrReqUtils.getSiteCode()));
    }


    @Operation(summary = "站点-币种下拉框-包含转换平台币")
    @PostMapping(value = "/getCurrencyList")
    public ResponseVO<List<CodeValueVO>> getCurrencyDownBox() {
        return ResponseVO.success(siteCurrencyInfoApi.getCurrencyDownBox(CurrReqUtils.getSiteCode()));
    }


    @Operation(summary = "站点-VIP段位等级联动下拉框")
    @PostMapping(value = "/getVIPRankGradeList")
    public ResponseVO<Map<String, List<CodeValueNoI18VO>>> getVIPRankGradeList() {
        return ResponseVO.success(vipRankApi.getVIPRankGradeList(CurrReqUtils.getSiteCode()));
    }

    @Operation(summary = "下拉框-有效币种 ")
    @PostMapping(value = "/getValidCurrencyList")
    public ResponseVO<List<SiteCurrencyInfoRespVO>> getValidCurrencyList() {
        String siteCode = CurrReqUtils.getSiteCode();
        List<SiteCurrencyInfoRespVO> currencyList = siteCurrencyInfoApi.getValidBySiteCode(siteCode);
        return ResponseVO.success(currencyList);
    }

    private final PromotionDomainApi promotionDomainApi;

    @Operation(summary = "获取推广链接的列表")
    @PostMapping("/getPromotionDomainList")
    public ResponseVO<Page<PromotionDomainRespVO>> getPromotionDomainList(@RequestBody AgentDomainPageQueryVO pageQueryVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        pageQueryVO.setSiteCode(siteCode);
        pageQueryVO.setTimezone(CurrReqUtils.getTimezone());
        // 代理查看，客户端查看，不分类型
        pageQueryVO.setDomainType(null);
        return promotionDomainApi.getPromotionDomainList(pageQueryVO);
    }

    @GetMapping("getSiteActivityTemplate")
    @Operation(summary = "获取站点模版列表信息")
    public ResponseVO<List<JSONObject>> getSiteActivityTemplate() {

        ResponseVO<List<SiteActivityTemplateVO>> listResponseVO = systemActivityTemplateApi.querySiteActivityTemplate(CurrReqUtils.getSiteCode(), CurrReqUtils.getHandicapMode());

        List<JSONObject> list = new ArrayList<>();

        if (listResponseVO.isOk()){
            list = listResponseVO.getData().stream().map(vo -> {
                JSONObject object = new JSONObject();
                object.put("type", "activity_template");
                object.put("code", vo.getActivityTemplate());
                object.put("value", vo.getActivityName());
                return object;
            }).toList();
        }

        return ResponseVO.success(list);
    }

    @Operation(summary = "获取大陆盘所有vip等级")
    @GetMapping("/getNewVipGradeList")
    public ResponseVO<List<VIPGradeVO>> getNewVipGradeList() {
        return siteVipOptionApi.getCnVipGradeList();
    }


    @Operation(summary = "下拉框-场馆列表")
    @PostMapping(value = "/getVenueDownBox")
    public ResponseVO<List<CodeValueNoI18VO>> getVenueDownBox() {
        Map<String, String> venueMap = playVenueInfoApi.getSiteVenueNameMap().getData();
        List<CodeValueNoI18VO> resultList = Lists.newArrayList();
        for (Map.Entry<String, String> map : venueMap.entrySet()) {
            resultList.add(CodeValueNoI18VO.builder().code(map.getKey()).value(map.getValue()).build());
        }
        return ResponseVO.success(resultList);
    }

    @GetMapping("getActivityList")
    @Operation(summary = "获取站点活动列表")
    public ResponseVO<List<ActivityVO>> getActivityList() {
        ResponseVO<List<ActivityBaseRespVO>> activityList;
        ActivityBaseVO baseVO = new ActivityBaseVO();
        baseVO.setSiteCode(CurrReqUtils.getSiteCode());
        if (CurrReqUtils.getHandicapMode()==null||CurrReqUtils.getHandicapMode()==0){
            List<String> templateList = new ArrayList<>();
            templateList.add(ActivityTemplateEnum.ASSIGN_DAY.getType());
            templateList.add(ActivityTemplateEnum.CHECKIN.getType());
            templateList.add(ActivityTemplateEnum.STATIC.getType());
            templateList.add(ActivityTemplateEnum.DAILY_COMPETITION.getType());
            templateList.add(ActivityTemplateEnum.FREE_WHEEL.getType());
            baseVO.setActivityTemplateList(templateList);
            activityList = activityBaseApi.queryActivityList(baseVO);
        }else {
            List<String> templateList = new ArrayList<>();
            templateList.add(ActivityTemplateV2Enum.ASSIGN_DAY_V2.getType());
//            templateList.add(ActivityTemplateV2Enum.STATIC_V2.getType());
            baseVO.setActivityTemplateList(templateList);
            activityList = activityBaseV2Api.queryActivityList(baseVO);
        }
        return ResponseVO.success(activityList.getData().stream().map(vo -> ActivityVO.builder().activityId(vo.getId()).activityNameI18nCode(vo.getActivityNameI18nCode()).build()).toList());
    }
}
