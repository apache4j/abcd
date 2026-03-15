package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SystemWithdrawWayApi;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayFeeVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayAddVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayDetailResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayStatusVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayUpdateVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawConfigRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawConfigVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawTypeListVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawWayListVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawWayRequestVO;
import com.cloud.baowang.wallet.service.SystemWithdrawWayService;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author: qiqi
 **/
@RestController
@Validated
@Slf4j
@AllArgsConstructor
public class SystemWithdrawWayImpl implements SystemWithdrawWayApi {

    private final SystemWithdrawWayService withdrawWayService;

    @Override
    public ResponseVO<Page<SystemWithdrawWayResponseVO>> selectPage(SystemWithdrawWayRequestVO withdrawWayRequestVO) {
        return withdrawWayService.selectPage(withdrawWayRequestVO);
    }

    @Override
    public ResponseVO<Void> insert(SystemWithdrawWayAddVO withdrawWayAddVO) {
        return withdrawWayService.insert(withdrawWayAddVO);
    }

    @Override
    public ResponseVO<Void> update(SystemWithdrawWayUpdateVO withdrawWayUpdateVO) {
        return withdrawWayService.updateByInfo(withdrawWayUpdateVO);
    }

    @Override
    public ResponseVO<Void> enableOrDisable(SystemWithdrawWayStatusVO withdrawWayStatusVO) {
        return withdrawWayService.enableOrDisable(withdrawWayStatusVO);
    }

    @Override
    public ResponseVO<List<SystemWithdrawWayResponseVO>> selectAllValid() {
        return withdrawWayService.selectAllValid();
    }

    @Override
    public ResponseVO<List<SystemWithdrawWayResponseVO>> selectAll() {
        return withdrawWayService.selectAll();
    }

    @Override
    public List<SystemWithdrawWayResponseVO> queryWithdrawWayList() {
        return ConvertUtil.entityListToModelList(withdrawWayService.list(),
                SystemWithdrawWayResponseVO.class);
    }
    @Override
    public List<WithdrawWayListVO> withdrawWayList(WithdrawWayRequestVO withdrawWayRequestVO) {
        return withdrawWayService.withdrawWayList(withdrawWayRequestVO);
    }

    @Override
    public List<WithdrawWayListVO> agentWithdrawWayList(WithdrawWayRequestVO withdrawWayRequestVO) {
        return withdrawWayService.agentWithdrawWayList(withdrawWayRequestVO);
    }


    @Override
    public ResponseVO<SystemWithdrawWayDetailResponseVO> info(IdReqVO idReqVO) {
        return withdrawWayService.info(idReqVO);
    }

    @Override
    public ResponseVO<SystemWithdrawWayDetailResponseVO> getInfoById(IdReqVO idReqVO) {
        return withdrawWayService.getInfoById(idReqVO);
    }

    @Override
    public ResponseVO<List<SystemWithdrawWayResponseVO>> selectBySort(SystemWithdrawWayRequestVO systemWithdrawWayRequestVO) {
        return withdrawWayService.selectBySort(systemWithdrawWayRequestVO);
    }

    @Override
    public ResponseVO<Boolean> batchSave(String userAccount, List<SortNewReqVO> sortNewReqVOS) {
        return withdrawWayService.batchSave(userAccount,sortNewReqVOS);
    }

    @Override
    public SiteWithdrawWayFeeVO calculateSiteWithdrawWayFeeRate(String siteCode, String withdrawWayId, BigDecimal amount){
        return withdrawWayService.calculateSiteWithdrawWayFeeRate(siteCode,withdrawWayId,amount);
    }

    @Override
    public Map<String, List<SystemWithdrawChannelResponseVO>> getChannelGroup(String siteCode, String vipRank) {
        return withdrawWayService.getChannelGroup(siteCode,vipRank);
    }
}
