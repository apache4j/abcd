package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.SiteCurrencyDownBoxVO;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyFromTransferVO;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyToTransferVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyBatchReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyConvertRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInitReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyStatusReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.service.SiteCurrencyInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/9/3 15:46
 * @Version: V1.0
 **/
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class SiteCurrencyInfoApiImpl implements SiteCurrencyInfoApi {

    private SiteCurrencyInfoService siteCurrencyInfoService;

    @Override
    public ResponseVO<Boolean> batchSaveRate(SiteCurrencyBatchReqVO siteCurrencyBatchReqVO) {
        return siteCurrencyInfoService.batchSaveRate(siteCurrencyBatchReqVO);
    }

    @Override
    public ResponseVO<Boolean> batchSave(String currentUserAccount, List<SortNewReqVO> siteCurrencyInfoSortNewReqVOS){
        return siteCurrencyInfoService.batchSave(currentUserAccount,siteCurrencyInfoSortNewReqVOS);
    }

    @Override
    public ResponseVO<Boolean> init(SiteCurrencyInitReqVO siteCurrencyBatchReqVO) {
        return siteCurrencyInfoService.init(siteCurrencyBatchReqVO);
    }

    @Override
    public ResponseVO<Boolean> enableOrDisable(SiteCurrencyStatusReqVO siteCurrencyStatusReqVO) {
        return siteCurrencyInfoService.enableOrDisable(siteCurrencyStatusReqVO);
    }

    @Override
    public void disableCurrency(String currencyCode,String operatorUserNo) {
         siteCurrencyInfoService.disableCurrency(currencyCode,operatorUserNo);
    }

    @Override
    public ResponseVO<List<SiteCurrencyDownBoxVO>> getSiteCurrencyDownBox(String siteCode) {
        return siteCurrencyInfoService.getSiteCurrencyDownBox(siteCode);
    }

    @Override
    public SiteCurrencyInfoRespVO getByCurrencyCode(String siteCode, String currencyCode) {
        return siteCurrencyInfoService.getByCurrencyCode(siteCode, currencyCode);
    }

    @Override
    public void enableAdminCurrency(String currencyCode) {
        siteCurrencyInfoService.enableAdminCurrency(currencyCode);
    }

    @Override
    public ResponseVO<Page<SiteCurrencyInfoRespVO>> selectPage(SiteCurrencyInfoReqVO siteCurrencyInfoReqVO) {
        return siteCurrencyInfoService.selectPage(siteCurrencyInfoReqVO);
    }

    @Override
    public ResponseVO<List<SiteCurrencyInfoRespVO>> selectAllBySort(String siteCode) {
        return siteCurrencyInfoService.selectAllBySort(siteCode);
    }


    @Override
    public List<SiteCurrencyInfoRespVO> getBySiteCode(String siteCode) {
        return siteCurrencyInfoService.getBySiteCode(siteCode);
    }

    @Override
    public List<SiteCurrencyInfoRespVO> getValidBySiteCode(String siteCode) {
        return siteCurrencyInfoService.getValidBySiteCode(siteCode);
    }


    @Override
    public List<CodeValueVO> getCurrencyList(String siteCode) {
        return siteCurrencyInfoService.getCurrencyList(siteCode);
    }


    @Override
    public List<CodeValueVO> getCurrencyListNo(String siteCode) {
        return siteCurrencyInfoService.getCurrencyListNo(siteCode);
    }




    @Override
    public List<CodeValueVO> getCurrencyDownBox(String siteCode) {
        return siteCurrencyInfoService.getCurrencyDownBox(siteCode);
    }


    @Override
    public List<SiteCurrencyInfoRespVO> getListBySiteCodes(List<String> siteCodeList) {
        return siteCurrencyInfoService.getListBySiteCodes(siteCodeList);
    }

    @Override
    public ResponseVO<BigDecimal> transferPlatToMainCurrency(PlatCurrencyFromTransferVO platCurrencyTransferVO) {
        return siteCurrencyInfoService.transferPlatToMainCurrency(platCurrencyTransferVO);
    }

    @Override
    public  ResponseVO<SiteCurrencyConvertRespVO> transferToMainCurrency(PlatCurrencyFromTransferVO platCurrencyFromTransferVO) {
        return siteCurrencyInfoService.transferToMainCurrency(platCurrencyFromTransferVO);
    }

    @Override
    public ResponseVO<BigDecimal> transferMainCurrencyToPlat(PlatCurrencyToTransferVO platCurrencyToTransferVO) {
        return siteCurrencyInfoService.transferMainCurrencyToPlat(platCurrencyToTransferVO);
    }

    @Override
    public  ResponseVO<SiteCurrencyConvertRespVO> transferToPlat(PlatCurrencyToTransferVO platCurrencyToTransferVO) {
        return siteCurrencyInfoService.transferToPlat(platCurrencyToTransferVO);
    }

    @Override
    public Map<String, BigDecimal> getAllFinalRate(String siteCode) {
        return siteCurrencyInfoService.getAllFinalRate(siteCode);
    }

    @Override
    public Map<String, Map<String, BigDecimal>> getAllSiteFinalRate(List<String> siteCode) {
        return siteCurrencyInfoService.getAllSiteFinalRate(siteCode);
    }

    @Override
    public Map<String, Map<String, BigDecimal>> getAllPlateFinalRate() {
        return siteCurrencyInfoService.getAllPlateFinalRate();
    }


    @Override
    public  Map<String,Map<String, BigDecimal> >  getAllFinalRateBySiteList(List<String> siteCodeList) {
        return siteCurrencyInfoService.getAllFinalRateBySiteList(siteCodeList);
    }


    @Override
    public BigDecimal getCurrencyFinalRate(String siteCode,String currencyCode){
        return siteCurrencyInfoService.getCurrencyFinalRate(siteCode,currencyCode);
    }

    @Override
    public Map<String, List<SiteCurrencyInfoRespVO>> getCurrencyBySiteCodes(List<String> siteCodes) {
        return siteCurrencyInfoService.getCurrencyBySiteCodes(siteCodes);
    }



    @Override
    public ResponseVO<SiteCurrencyInfoRespVO> findPlatCurrencyNameBySiteCode(String siteCode) {
        return siteCurrencyInfoService.findPlatCurrencyNameBySiteCode(siteCode);
    }


}
