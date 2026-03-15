package com.cloud.baowang.site.controller.site;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyBatchReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyStatusReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
        siteCurrencyInfoReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        siteCurrencyInfoReqVO.setLanguageCode(CurrReqUtils.getLanguage());
        return siteCurrencyInfoApi.selectPage(siteCurrencyInfoReqVO);
    }

    @RequestMapping(value = "selectAllBySort",method = {RequestMethod.GET,RequestMethod.POST})
    @Operation(summary = "站点币种查询排序")
    ResponseVO<List<SiteCurrencyInfoRespVO>> selectAllBySort(){
        return siteCurrencyInfoApi.selectAllBySort(CurrReqUtils.getSiteCode());
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
        String siteCode= CurrReqUtils.getSiteCode();
        return siteCurrencyInfoApi.findPlatCurrencyNameBySiteCode(siteCode);
    }

    @PostMapping("batchSaveRate")
    @Operation(summary = "批量保存 汇率转换")
    ResponseVO<Boolean> batchSaveRate(@RequestBody SiteCurrencyBatchReqVO siteCurrencyBatchReqVO){
        siteCurrencyBatchReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        siteCurrencyBatchReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return siteCurrencyInfoApi.batchSaveRate(siteCurrencyBatchReqVO);
    }
}
