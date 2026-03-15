package com.cloud.baowang.wallet.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.SiteCurrencyDownBoxVO;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyFromTransferVO;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyToTransferVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyBatchReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyConvertRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInitReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyStatusReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 站点币种信息
 *
 * @Author ford
 * @Date 2024-09-03
 */
@FeignClient(contextId = "remoteSiteCurrencyInfoApi", value = ApiConstants.NAME)
@Tag(name = "RPC 站点币种信息 服务")
public interface SiteCurrencyInfoApi {


    String PREFIX = ApiConstants.PREFIX + "/site/currency/";

    @PostMapping(value = PREFIX + "findPlatCurrencyNameBySiteCode")
    @Operation(summary = "按照站点查询平台币")
    ResponseVO<SiteCurrencyInfoRespVO> findPlatCurrencyNameBySiteCode(@RequestParam("siteCode") String siteCode);

    @PostMapping(value = PREFIX + "batchSaveRate")
    @Operation(summary = "站点币种汇率批量保存")
    ResponseVO<Boolean> batchSaveRate(@RequestBody SiteCurrencyBatchReqVO siteCurrencyBatchReqVO);

    @PostMapping(value = PREFIX + "batchSave")
    @Operation(summary = "站点币种批量保存")
    ResponseVO<Boolean> batchSave(@RequestParam("currentUserAccount") String currentUserAccount, @RequestBody List<SortNewReqVO> siteCurrencyInfoSortNewReqVOS);

    @PostMapping(value = PREFIX + "init")
    @Operation(summary = "站点币种初始化")
    ResponseVO<Boolean> init(@RequestBody SiteCurrencyInitReqVO siteCurrencyBatchReqVO);


    @PostMapping(value = PREFIX + "enableOrDisable")
    @Operation(summary = "启用禁用")
    ResponseVO<Boolean> enableOrDisable(@RequestBody SiteCurrencyStatusReqVO siteCurrencyStatusReqVO);


    @PostMapping(value = PREFIX + "selectPage")
    @Operation(summary = "站点币种分页查询")
    ResponseVO<Page<SiteCurrencyInfoRespVO>> selectPage(@RequestBody SiteCurrencyInfoReqVO siteCurrencyInfoReqVO);

    @GetMapping(value = PREFIX + "selectAllBySort")
    @Operation(summary = "站点币种查询排序")
    ResponseVO<List<SiteCurrencyInfoRespVO>> selectAllBySort(@RequestParam("siteCode") String siteCode);


    @GetMapping(value = PREFIX + "getBySiteCode")
    @Operation(summary = "获取当前站点全部币种信息")
    List<SiteCurrencyInfoRespVO> getBySiteCode(@RequestParam("siteCode") String siteCode);


    @GetMapping(value = PREFIX + "getValidBySiteCode")
    @Operation(summary = "获取当前站点全部有效币种信息")
    List<SiteCurrencyInfoRespVO> getValidBySiteCode(@RequestParam("siteCode") String siteCode);

    @GetMapping(value = PREFIX + "getCurrencyList")
    @Operation(summary = "获取当前站点全部币种信息下拉框")
    List<CodeValueVO> getCurrencyList(@RequestParam("siteCode") String siteCode);


    @GetMapping(value = PREFIX + "getCurrencyListNo")
    @Operation(summary = "获取当前站点全部币种信息下拉框-不包含平台币")
    List<CodeValueVO> getCurrencyListNo(@RequestParam("siteCode") String siteCode);

    @GetMapping(value = PREFIX + "getCurrencyDownBox")
    @Operation(summary = "代理端-当前站点全部币种信息下拉框-包含转换平台币")
    List<CodeValueVO> getCurrencyDownBox(@RequestParam("siteCode") String siteCode);

    @PostMapping(value = PREFIX + "getListBySiteCodes")
    @Operation(summary = "批量获取站点对应币种列表")
    List<SiteCurrencyInfoRespVO> getListBySiteCodes(@RequestBody List<String> siteCodeList);


    @PostMapping(value = PREFIX + "transferPlatToMainCurrency")
    @Operation(summary = "平台币转法币")
    ResponseVO<BigDecimal> transferPlatToMainCurrency(@RequestBody PlatCurrencyFromTransferVO platCurrencyTransferVO);

    @PostMapping(value = PREFIX + "transferToMainCurrency")
    @Operation(summary = "平台币转法币")
    ResponseVO<SiteCurrencyConvertRespVO> transferToMainCurrency(@RequestBody PlatCurrencyFromTransferVO platCurrencyTransferVO);

    @PostMapping(value = PREFIX + "transferMainCurrencyToPlat")
    @Operation(summary = "法币转平台币")
    ResponseVO<BigDecimal> transferMainCurrencyToPlat(@RequestBody PlatCurrencyToTransferVO platCurrencyToTransferVO);


    @PostMapping(value = PREFIX + "transferToPlat")
    @Operation(summary = "法币转平台币")
    ResponseVO<SiteCurrencyConvertRespVO> transferToPlat(@RequestBody PlatCurrencyToTransferVO platCurrencyToTransferVO);

    @PostMapping(value = PREFIX + "getAllFinalRate")
    @Operation(summary = "获取站点下的所有汇率")
    Map<String, BigDecimal> getAllFinalRate(@RequestParam("siteCode") String siteCode);

    @PostMapping(value = PREFIX + "getAllSiteFinalRate")
    @Operation(summary = "获取某些站点的所有汇率")
    Map<String, Map<String, BigDecimal>> getAllSiteFinalRate(@RequestBody List<String> siteCodes);

    @PostMapping(value = PREFIX + "getAllPlateFinalRate")
    @Operation(summary = "获取平台下的所有汇率")
    Map<String, Map<String, BigDecimal>> getAllPlateFinalRate();

    @PostMapping(value = PREFIX + "getAllFinalRateBySiteList")
    @Operation(summary = "获取指定站点下的所有汇率")
    Map<String,Map<String, BigDecimal> >  getAllFinalRateBySiteList(@RequestBody List<String> siteCodeList);

    @PostMapping(value = PREFIX + "getCurrencyFinalRate")
    @Operation(summary = "获取站点下单个币种的汇率")
    BigDecimal getCurrencyFinalRate(@RequestParam("siteCode") String siteCode, @RequestParam("currencyCode") String currencyCode);

    @PostMapping(value = PREFIX + "getCurrencyBySiteCodes")
    @Operation(summary = "批量获取站点币种")
    Map<String, List<SiteCurrencyInfoRespVO>> getCurrencyBySiteCodes(@RequestBody List<String> siteCodes);

    @PostMapping(value = PREFIX + "disableCurrency")
    @Operation(summary = "批量禁用某个币种")
    void disableCurrency(@RequestParam("currencyCode") String  currencyCode,@RequestParam("operatorUserNo") String operatorUserNo);

    @GetMapping("getSiteCurrencyDownBox")
    @Operation(summary = "获取币种下拉框(新增-编辑站点使用)")
    ResponseVO<List<SiteCurrencyDownBoxVO>> getSiteCurrencyDownBox(@RequestParam(value = "siteCode",required = false,defaultValue = "") String siteCode);

    @GetMapping(value = PREFIX + "getByCurrencyCode")
    @Operation(summary = "获取当前站点某个币种信息")
    SiteCurrencyInfoRespVO getByCurrencyCode(@RequestParam("siteCode") String siteCode, @RequestParam("currencyCode") String currencyCode);


    @PostMapping(value = PREFIX + "enableCurrency")
    @Operation(summary = "开启总站下某个币种")
    void enableAdminCurrency(@RequestParam("currencyCode")String currencyCode);
}
