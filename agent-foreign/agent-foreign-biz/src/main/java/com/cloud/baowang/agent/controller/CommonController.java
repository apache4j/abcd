package com.cloud.baowang.agent.controller;

import com.cloud.baowang.agent.api.api.AgentCommissionPlanApi;
import com.cloud.baowang.agent.service.CommonService;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.system.api.api.operations.CustomerChannelApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.api.site.area.AreaSiteManageApi;
import com.cloud.baowang.system.api.vo.operations.ClientCustomerChannelVO;
import com.cloud.baowang.system.api.vo.operations.MeiQiaChannelVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.site.area.AreaSiteLangVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyToTransferVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyConvertRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Tag(name = "公共controller")
@RestController
@RequestMapping("/common")
@AllArgsConstructor
public class CommonController {

    private CommonService commonService;
    private PlayVenueInfoApi playVenueInfoApi;
    private AgentCommissionPlanApi agentCommissionPlanApi;
    private SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final SiteApi siteApi;

    private final AreaSiteManageApi areaSiteManageApi;

    private final CustomerChannelApi customerChannelApi;

    @Operation(summary = "下拉框-通用")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox(@RequestBody List<String> types) {
        return ResponseVO.success(commonService.getSystemParamsByList(types));
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

    @Operation(summary = "下拉框-佣金方案")
    @PostMapping(value = "/getCommissionPlanDownBox")
    public ResponseVO<List<CodeValueNoI18VO>> getCommissionPlanDownBox() {
        String siteCode=CurrReqUtils.getSiteCode();
        return ResponseVO.success(agentCommissionPlanApi.getCommissionPlanSelect(siteCode));
    }

    @Operation(summary = "下拉框-币种列表-包含平台币")
    @PostMapping(value = "/getCurrencyList")
    public ResponseVO<List<CodeValueVO>> getCurrencyList() {
        return ResponseVO.success(siteCurrencyInfoApi.getCurrencyList(CurrReqUtils.getSiteCode()));
    }

    @Operation(summary = "下拉框-币种列表-不包含平台币")
    @PostMapping(value = "/getCurrencyListNo")
    public ResponseVO<List<CodeValueVO>> getCurrencyListNo() {
        return ResponseVO.success(siteCurrencyInfoApi.getCurrencyListNo(CurrReqUtils.getSiteCode()));
    }

    @Operation(summary = "法币转平台币")
    @PostMapping(value = "/transferToPlat")
    public ResponseVO<SiteCurrencyConvertRespVO> transferToPlat(@RequestBody PlatCurrencyToTransferVO platCurrencyToTransferVO) {
        platCurrencyToTransferVO.setSiteCode(CurrReqUtils.getSiteCode());
        return siteCurrencyInfoApi.transferToPlat(platCurrencyToTransferVO);
    }

    @Operation(summary = "获取站点下单个币种的汇率")
    @PostMapping(value = "/getCurrencyFinalRate")
    public ResponseVO<BigDecimal> getCurrencyFinalRate(@RequestParam("currencyCode") String currencyCode) {
        return ResponseVO.success(siteCurrencyInfoApi.getCurrencyFinalRate(CurrReqUtils.getSiteCode(), currencyCode));
    }

    @GetMapping("getSiteInfo")
    @Operation(summary = "获取当前代理所属站点基础信息")
    public ResponseVO<SiteVO> getSiteInfo() {
        String siteCode = CurrReqUtils.getSiteCode();
        return siteApi.getCustomerSiteInfo(siteCode);
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

    @Operation(summary = "获取美洽客服信息")
    @GetMapping(value = "/getMeiQiaChannelInfo")
    public ResponseVO<MeiQiaChannelVO> getMeiQiaChannelInfo() {
        String siteCode = CurrReqUtils.getSiteCode();
        if (siteCode == null) {
            return ResponseVO.fail(ResultCode.REFERER_EMPTY);
        }

        return ResponseVO.success(customerChannelApi.getMeiQiaChannelInfo(siteCode));

    }
}
