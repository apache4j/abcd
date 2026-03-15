package com.cloud.baowang.admin.controller.site;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/29 16:36
 * @Version: V1.0
 **/
@RestController
@Tag(name = "基础设置-站点币种信息")
@RequestMapping("/siteCurrency/api")
@AllArgsConstructor
public class SiteCurrencyController {

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    @PostMapping("selectPage")
    @Operation(summary = "站点币种分页查询")
    ResponseVO<Page<SiteCurrencyInfoRespVO>> selectPage(@RequestBody @Validated SiteCurrencyInfoReqVO siteCurrencyInfoReqVO){
        siteCurrencyInfoReqVO.setSiteCode(CommonConstant.ADMIN_CENTER_SITE_CODE);
        siteCurrencyInfoReqVO.setLanguageCode(CurrReqUtils.getLanguage());
        return siteCurrencyInfoApi.selectPage(siteCurrencyInfoReqVO);
    }

    @RequestMapping(value = "selectAllBySort",method = {RequestMethod.GET,RequestMethod.POST})
    @Operation(summary = "站点币种查询排序")
    ResponseVO<List<SiteCurrencyInfoRespVO>> selectAllBySort(){
        return siteCurrencyInfoApi.selectAllBySort(CommonConstant.ADMIN_CENTER_SITE_CODE);
    }

    @PostMapping("enableOrDisable")
    @Operation(summary = "启用禁用")
    ResponseVO<Boolean> enableOrDisable(@RequestBody @Validated SiteCurrencyStatusReqVO siteCurrencyStatusReqVO){
        siteCurrencyStatusReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return siteCurrencyInfoApi.enableOrDisable(siteCurrencyStatusReqVO);
    }


    @PostMapping("batchSave")
    @Operation(summary = "批量保存站点币种")
    ResponseVO<Boolean> batchSave(@RequestBody List<SortNewReqVO> siteCurrencyInfoSortNewReqVOS){
        return siteCurrencyInfoApi.batchSave(CurrReqUtils.getAccount(),siteCurrencyInfoSortNewReqVOS);
    }

    @PostMapping("findPlatCurrencyNameBySiteCode")
    @Operation(summary = "按照站点查询平台币")
    ResponseVO<SiteCurrencyInfoRespVO> findPlatCurrencyNameBySiteCode(){
        String siteCode= CommonConstant.ADMIN_CENTER_SITE_CODE;
        return siteCurrencyInfoApi.findPlatCurrencyNameBySiteCode(siteCode);
    }

    @PostMapping("batchSaveRate")
    @Operation(summary = "批量保存 汇率转换")
    ResponseVO<Boolean> batchSaveRate(@RequestBody SiteCurrencyBatchReqVO siteCurrencyBatchReqVO){
        siteCurrencyBatchReqVO.setSiteCode(CommonConstant.ADMIN_CENTER_SITE_CODE);
        siteCurrencyBatchReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return siteCurrencyInfoApi.batchSaveRate(siteCurrencyBatchReqVO);
    }
}
