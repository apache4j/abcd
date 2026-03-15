package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SiteRechargeWayApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayBatchReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayRequestVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayResVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayResponseVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayStatusReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayVipUseScopeVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.service.SiteRechargeWayService;
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
public class SiteRechargeWayApiImpl implements SiteRechargeWayApi {
    @Resource
    private SiteRechargeWayService siteRechargeWayService;

    @Override
    public ResponseVO<Void> batchSave(SiteRechargeWayBatchReqVO siteRechargeWayBatchReqVO) {
        return siteRechargeWayService.batchSave(siteRechargeWayBatchReqVO);
    }

    @Override
    public ResponseVO<Void> enableOrDisable(SiteRechargeWayStatusReqVO siteRechargeWayStatusReqVO) {
        return siteRechargeWayService.enableOrDisable(siteRechargeWayStatusReqVO);
    }

    @Override
    public ResponseVO<List<SiteRechargeWayResVO>> queryBySite() {
        return ResponseVO.success(siteRechargeWayService.queryBySite());
    }

    @Override
    public SiteRechargeWayVO queryRechargeWay(String siteCode, String rechargeWayId){
        return siteRechargeWayService.queryRechargeWay(siteCode,rechargeWayId);
    }

    @Override
    public ResponseVO<Page<SiteRechargeWayResponseVO>> selectRechargePage(SiteRechargeWayRequestVO siteRechargeWayRequestVO) {
        return siteRechargeWayService.selectRechargePage(siteRechargeWayRequestVO);
    }

    @Override
    public ResponseVO<List<SiteRechargeWayResponseVO>> selectBySort(SiteRechargeWayRequestVO siteRechargeWayRequestVO) {
        return siteRechargeWayService.selectBySort(siteRechargeWayRequestVO);
    }

    @Override
    public ResponseVO<Boolean> batchSaveSort(String userAccount, List<SortNewReqVO> sortNewReqVOS) {
        return siteRechargeWayService.batchSaveSort(userAccount,sortNewReqVOS);
    }

    @Override
    public ResponseVO<Boolean> saveVipGradeUseScope(SiteRechargeWayVipUseScopeVO siteRechargeWayVipUseScopeVO) {
        return siteRechargeWayService.saveVipGradeUseScope(siteRechargeWayVipUseScopeVO);
    }
}
