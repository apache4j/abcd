package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SystemRechargeWayApi;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeConfigVO;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeWayListVO;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeWayRequestVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteSystemRechargeChannelRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayDetailRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayStatusReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayUpdateReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayFeeVO;
import com.cloud.baowang.wallet.service.SystemRechargeWayService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 09:45
 * @Version: V1.0
 **/
@RestController
@Validated
@Slf4j
public class SystemRechargeWayImpl implements SystemRechargeWayApi {
    @Resource
    private SystemRechargeWayService systemRechargeWayService;


    @Override
    public ResponseVO<List<SystemRechargeWayRespVO>> selectAllValid() {
        return systemRechargeWayService.selectAllValid();
    }

    @Override
    public ResponseVO<List<SystemRechargeWayRespVO>> selectAll() {
        return systemRechargeWayService.selectAll();
    }

    @Override
    public ResponseVO<Page<SystemRechargeWayRespVO>> selectPage(SystemRechargeWayReqVO systemRechargeWayReqVO) {
        return systemRechargeWayService.selectPage(systemRechargeWayReqVO);
    }

    @Override
    public ResponseVO<List<SystemRechargeWayRespVO>> selectBySort(SystemRechargeWayReqVO systemRechargeWayReqVO) {
        return systemRechargeWayService.selectBySort(systemRechargeWayReqVO);
    }

    @Override
    public ResponseVO<Void> insert(SystemRechargeWayNewReqVO systemRechargeWayNewReqVO) {
        return systemRechargeWayService.insert(systemRechargeWayNewReqVO);
    }

    @Override
    public ResponseVO<Void> update(SystemRechargeWayUpdateReqVO systemRechargeWayUpdateReqVO) {
        return systemRechargeWayService.updateByInfo(systemRechargeWayUpdateReqVO);
    }

    @Override
    public ResponseVO<Void> enableOrDisable(SystemRechargeWayStatusReqVO systemRechargeWayStatusReqVO) {
        return systemRechargeWayService.enableOrDisable(systemRechargeWayStatusReqVO);
    }

    @Override
    public List<SystemRechargeWayRespVO> queryRechargeWayList() {
        return ConvertUtil.entityListToModelList(systemRechargeWayService.list(),
                SystemRechargeWayRespVO.class);
    }

    @Override
    public List<RechargeWayListVO> rechargeWayList(RechargeWayRequestVO rechargeWayRequestVO) {
        return systemRechargeWayService.rechargeWayList(rechargeWayRequestVO);
    }
    @Override
    public List<RechargeWayListVO> agentRechargeWayList(RechargeWayRequestVO rechargeWayRequestVO) {
        return systemRechargeWayService.agentRechargeWayList(rechargeWayRequestVO);
    }
    @Override
    public Map<String, List<SiteSystemRechargeChannelRespVO>> getChannelGroup(String siteCode){
        return systemRechargeWayService.getChannelGroup(siteCode);
    }


    @Override
    public ResponseVO<SystemRechargeWayDetailRespVO> info(IdReqVO idReqVO) {
        return systemRechargeWayService.info(idReqVO);
    }

    @Override
    public ResponseVO<SystemRechargeWayDetailRespVO> getInfoById(IdReqVO idReqVO) {
        return systemRechargeWayService.getInfoById(idReqVO);
    }

    @Override
    public ResponseVO<Boolean> batchSave(String userAccount, List<SortNewReqVO> sortNewReqVOS) {
        return systemRechargeWayService.batchSave(userAccount,sortNewReqVOS);
    }

    @Override
    public RechargeConfigVO getRechargeConfigBySiteCode(String siteCode, String rechargeWayId ) {
        return systemRechargeWayService.getRechargeConfigBySiteCode(siteCode,rechargeWayId);
    }
    @Override
    public List<CodeValueVO> getRechargeWayListBySiteCode(String siteCode){
        return systemRechargeWayService.getRechargeWayListBySiteCode(siteCode);
    }

    @Override
    public SystemRechargeWayDetailRespVO getRechargeWayByCurrencyAndNetworkType(String currencyCode,String networkType,String siteCode,String wayId){
        return systemRechargeWayService.getRechargeWayByCurrencyAndNetworkType(currencyCode,networkType,siteCode,wayId);
    }


    @Override
    public SiteRechargeWayFeeVO calculateSiteRechargeWayFeeRate(String siteCode, String rechargeWayId,BigDecimal amount,String channelType){
        return systemRechargeWayService.calculateSiteRechargeWayFeeRate(siteCode,rechargeWayId,amount,channelType);
    }
}
