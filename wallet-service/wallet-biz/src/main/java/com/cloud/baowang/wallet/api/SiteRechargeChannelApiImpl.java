package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SiteRechargeChannelApi;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeChannelReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChangeVipUseScopeVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChannelBatchReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChannelRecvInfoVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChannelReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChannelResVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChannelRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeChannelStatusReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.service.SiteRechargeChannelService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Desciption: 充值通道
 * @Author: Ford
 * @Date: 2024/7/27 17:33
 * @Version: V1.0
 **/
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class SiteRechargeChannelApiImpl implements SiteRechargeChannelApi {
    @Resource
    private SiteRechargeChannelService siteRechargeChannelService;

    @Override
    public ResponseVO<Void> batchSave(List<SiteRechargeChannelBatchReqVO> siteRechargeChannelBatchReqVO,String siteCode,Integer handicapMode) {
        return siteRechargeChannelService.batchSave(siteRechargeChannelBatchReqVO,siteCode,handicapMode);
    }

    @Override
    public ResponseVO<Void> enableOrDisable(SiteRechargeChannelStatusReqVO siteRechargeChannelStatusReqVO) {
        return siteRechargeChannelService.enableOrDisable(siteRechargeChannelStatusReqVO);
    }

    @Override
    public ResponseVO<SiteRechargeChannelResVO> queryPlatformAuthorize(RechargeChannelReqVO reqVO) {
        return siteRechargeChannelService.queryPlatformAuthorize(reqVO);
    }

    @Override
    public ResponseVO<Page<SiteRechargeChannelRespVO>> selectRechargePage(SiteRechargeChannelReqVO siteRechargeChannelReqVO) {
        return siteRechargeChannelService.selectRechargePage(siteRechargeChannelReqVO);
    }

    @Override
    public ResponseVO<List<SiteRechargeChannelRespVO>> selectBySort(SiteRechargeChannelReqVO siteRechargeChannelReqVO) {
        return siteRechargeChannelService.selectBySort(siteRechargeChannelReqVO);
    }

    @Override
    public ResponseVO<Boolean> batchSaveSort(String account, List<SortNewReqVO> sortNewReqVOS) {
        return siteRechargeChannelService.batchSaveSort(account,sortNewReqVOS);
    }

    @Override
    public ResponseVO<Boolean> saveReceiveInfo(SiteRechargeChannelRecvInfoVO siteRechargeChannelRecvInfoVO) {
        return siteRechargeChannelService.saveReceiveInfo(siteRechargeChannelRecvInfoVO);
    }

    @Override
    public ResponseVO<Boolean> saveVipGradeUseScope(SiteRechargeChangeVipUseScopeVO siteRechargeChangeVipUseScopeVO) {
        return siteRechargeChannelService.saveVipGradeUseScope(siteRechargeChangeVipUseScopeVO);
    }
}
