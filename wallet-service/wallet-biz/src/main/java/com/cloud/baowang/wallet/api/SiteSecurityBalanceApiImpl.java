package com.cloud.baowang.wallet.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.SiteSecurityBalanceApi;
import com.cloud.baowang.wallet.api.enums.SiteSecurityReviewEnums;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityApplyReqVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceChangeRecordReqVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceInitReqVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceOverdrawAmountReqVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalancePageReqVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceRespVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceThresholdAmountReqVO;
import com.cloud.baowang.wallet.po.SiteSecurityBalancePO;
import com.cloud.baowang.wallet.service.SiteSecurityAdjustReviewService;
import com.cloud.baowang.wallet.service.SiteSecurityBalanceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption: 站点保证金相关
 * @Author: Ford
 * @Date: 2025/6/27 17:29
 * @Version: V1.0
 **/
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class SiteSecurityBalanceApiImpl implements SiteSecurityBalanceApi {
    private SiteSecurityBalanceService siteSecurityBalanceService;

    private final SiteSecurityAdjustReviewService siteSecurityAdjustReviewService;
    @Override
    public ResponseVO<Void> init(SiteSecurityBalanceInitReqVO securityBalanceInitReqVO) {
        return siteSecurityBalanceService.init(securityBalanceInitReqVO);
    }

    @Override
    public ResponseVO<Page<SiteSecurityBalanceRespVO>> listPage(SiteSecurityBalancePageReqVO siteSecurityBalancePageReqVO) {
        return siteSecurityBalanceService.listPage(siteSecurityBalancePageReqVO);
    }

    @Override
    public ResponseVO<Void> adminSetThresholdAmount(SiteSecurityBalanceThresholdAmountReqVO siteSecurityBalanceThresholdAmountReqVO) {
        return siteSecurityBalanceService.adminSetThresholdAmount(siteSecurityBalanceThresholdAmountReqVO);
    }

    /**
     * 设置透支金额
     * @param siteSecurityBalanceThresholdAmountReqVO
     * @return
     */
    @Override
    public ResponseVO<Void> adminSetOverdrawAmount(SiteSecurityBalanceOverdrawAmountReqVO siteSecurityBalanceThresholdAmountReqVO) {
        SiteSecurityBalancePO securityBalancePO=siteSecurityBalanceService.selectBySiteCode(siteSecurityBalanceThresholdAmountReqVO.getSiteCode());
        SiteSecurityApplyReqVO siteSecurityApplyReqVO=new SiteSecurityApplyReqVO();
        siteSecurityApplyReqVO.setSiteCode(siteSecurityBalanceThresholdAmountReqVO.getSiteCode());
        siteSecurityApplyReqVO.setAdjustType(siteSecurityBalanceThresholdAmountReqVO.getAdjustType());
        siteSecurityApplyReqVO.setRemark(siteSecurityBalanceThresholdAmountReqVO.getRemark());
        siteSecurityApplyReqVO.setSiteName(securityBalancePO.getSiteName());
        siteSecurityApplyReqVO.setOperatorUserNo(siteSecurityBalanceThresholdAmountReqVO.getUpdateUser());
        siteSecurityApplyReqVO.setAdjustAmount(siteSecurityBalanceThresholdAmountReqVO.getAdjustAmount());
        siteSecurityApplyReqVO.setCurrency(securityBalancePO.getCurrency());
        log.info("设置透支金额申请:{}",siteSecurityApplyReqVO);
        return siteSecurityAdjustReviewService.apply(siteSecurityApplyReqVO);
    }

    /**
     * 调整金额
     * @param siteSecurityApplyReqVO
     * @return
     */
    @Override
    public ResponseVO<Void> adjustAmount(SiteSecurityApplyReqVO siteSecurityApplyReqVO) {
        SiteSecurityBalancePO securityBalancePO=siteSecurityBalanceService.selectBySiteCode(siteSecurityApplyReqVO.getSiteCode());
        siteSecurityApplyReqVO.setSiteName(securityBalancePO.getSiteName());
        siteSecurityApplyReqVO.setCurrency(securityBalancePO.getCurrency());
        log.info("调整保证金金额申请:{}",siteSecurityApplyReqVO);
        return siteSecurityAdjustReviewService.apply(siteSecurityApplyReqVO);
    }


    @Override
    public ResponseVO<Void> afterDepositOrWithdraw(SiteSecurityBalanceChangeRecordReqVO vo) {
        return siteSecurityBalanceService.recordBalanceChangeLog(vo);
    }


    @Override
    public boolean isClosed(String siteCode) {
        return siteSecurityBalanceService.isClosed(siteCode);
    }

    @Override
    public SiteSecurityBalanceRespVO findBySiteCode(String siteCode) {
        SiteSecurityBalancePO siteSecurityBalancePO = siteSecurityBalanceService.selectBySiteCode(siteCode);
        if(siteSecurityBalancePO==null){
            return null;
        }
        SiteSecurityBalanceRespVO siteSecurityBalanceRespVO=new SiteSecurityBalanceRespVO();
        BeanUtils.copyProperties(siteSecurityBalancePO,siteSecurityBalanceRespVO);
        return siteSecurityBalanceRespVO;
    }
}
