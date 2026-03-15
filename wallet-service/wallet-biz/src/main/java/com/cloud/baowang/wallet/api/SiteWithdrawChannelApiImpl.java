package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SiteWithdrawChannelApi;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.*;
import com.cloud.baowang.wallet.service.SiteWithdrawChannelService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Desciption: 提现通道
 * @Author: qiqi
 **/
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class SiteWithdrawChannelApiImpl implements SiteWithdrawChannelApi {
    @Resource
    private SiteWithdrawChannelService siteWithdrawChannelService;

    @Override
    public ResponseVO<Void> batchSave(List<SiteWithdrawChannelBatchRequestVO> siteWithdrawChannelBatchReqVO,String siteCode) {
        return siteWithdrawChannelService.batchSave(siteWithdrawChannelBatchReqVO,siteCode);
    }

    @Override
    public ResponseVO<Void> enableOrDisable(SiteWithdrawChannelStatusRequestVO siteWithdrawChannelStatusReqVO) {
        return siteWithdrawChannelService.enableOrDisable(siteWithdrawChannelStatusReqVO);
    }

    @Override
    public ResponseVO<List<SystemWithdrawChannelResponseVO>> queryByCond(SiteWithdrawChannelReqVO siteWithdrawChannelReqVO) {
        return siteWithdrawChannelService.queryByCond(siteWithdrawChannelReqVO);
    }

    @Override
    public ResponseVO<SiteWithdrawChannelResVO> queryWithdrawPlatformAuthorize(WithdrawChannelReqVO reqVO) {
        return siteWithdrawChannelService.queryWithdrawPlatformAuthorize(reqVO);
    }

    @Override
    public List<SiteWithdrawChannelVO> getListBySiteCodeAndWayId(String siteCode, String depositWithdrawWayId) {
        return siteWithdrawChannelService.getListBySiteCodeAndWayId(siteCode,depositWithdrawWayId);
    }

    @Override
    public ResponseVO<Page<SiteWithdrawChannelResponseVO>> selectWithdrawPage(SiteWithdrawChannelRequestVO siteWithdrawChannelReqVO) {
        return siteWithdrawChannelService.selectWithdrawPage(siteWithdrawChannelReqVO);
    }

    @Override
    public ResponseVO<List<SiteWithdrawChannelResponseVO>> selectBySort(SiteWithdrawChannelRequestVO siteWithdrawChannelReqVO) {
        return siteWithdrawChannelService.selectBySort(siteWithdrawChannelReqVO);
    }

    @Override
    public ResponseVO<Boolean> batchSaveSort(String account, List<SortNewReqVO> sortNewReqVOS) {
        return siteWithdrawChannelService.batchSaveSort(account,sortNewReqVOS);
    }
}
