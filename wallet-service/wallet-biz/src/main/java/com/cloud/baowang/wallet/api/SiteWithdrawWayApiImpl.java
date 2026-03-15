package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SiteWithdrawWayApi;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayBatchRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayResVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayStatusRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayVO;
import com.cloud.baowang.wallet.service.SiteWithdrawWayService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Desciption:
 * @Author: qiqi
 **/
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class SiteWithdrawWayApiImpl implements SiteWithdrawWayApi {
    @Resource
    private SiteWithdrawWayService siteWithdrawWayService;

    @Override
    public ResponseVO<Void> batchSave(SiteWithdrawWayBatchRequestVO siteWithdrawWayBatchReqVO) {
        return siteWithdrawWayService.batchSave(siteWithdrawWayBatchReqVO);
    }

    @Override
    public ResponseVO<Void> enableOrDisable(SiteWithdrawWayStatusRequestVO siteWithdrawWayStatusReqVO) {
        return siteWithdrawWayService.enableOrDisable(siteWithdrawWayStatusReqVO);
    }

    @Override
    public ResponseVO<List<SiteWithdrawWayResVO>> queryBySite() {
        return ResponseVO.success(siteWithdrawWayService.queryBySite());
    }

    @Override
    public ResponseVO<List<SiteWithdrawWayResVO>> queryBySiteAndTypeCode(String siteCode, String typeCode) {
        return ResponseVO.success(siteWithdrawWayService.queryBySiteAndTypeCode(siteCode,typeCode));
    }

    @Override
    public ResponseVO<List<SiteWithdrawWayResVO>> queryWithdrawListBySite(String siteCode) {
        return siteWithdrawWayService.queryWithdrawListBySite(siteCode);
    }

    @Override
    public SiteWithdrawWayVO queryWithdrawWay(String siteCode, String withdrawWayId){
        return siteWithdrawWayService.queryWithdrawWay(siteCode,withdrawWayId);
    }

    @Override
    public ResponseVO<List<CodeValueVO>> queryListBySiteAndCurrencyCode(String siteCode, String currencyCode) {
        return siteWithdrawWayService.queryListBySiteAndCurrencyCode(siteCode,currencyCode);
    }

    @Override
    public ResponseVO<Page<SiteWithdrawWayResponseVO>> selectWithdrawPage(SiteWithdrawWayRequestVO siteWithdrawWayRequestVO) {
        return siteWithdrawWayService.selectWithdrawPage(siteWithdrawWayRequestVO);
    }

    @Override
    public ResponseVO<List<SiteWithdrawWayResponseVO>> selectBySort(SiteWithdrawWayRequestVO siteWithdrawWayRequestVO) {
        return siteWithdrawWayService.selectBySort(siteWithdrawWayRequestVO);
    }

    @Override
    public ResponseVO<Boolean> batchSaveSort(String userAccount, List<SortNewReqVO> sortNewReqVOS) {
        return siteWithdrawWayService.batchSaveSort(userAccount,sortNewReqVOS);
    }
}
