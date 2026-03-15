package com.cloud.baowang.wallet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.enums.SiteSecurityBalanceAccountEnums;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityChangeLogAllRespVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityChangeLogPageReqVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityChangeLogRespVO;
import com.cloud.baowang.wallet.po.SiteSecurityBalancePO;
import com.cloud.baowang.wallet.po.SiteSecurityChangeLogPO;
import com.cloud.baowang.wallet.repositories.SiteSecurityChangeLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Desciption: 站点保证金
 * @Author: Ford
 * @Date: 2025/6/27 17:31
 * @Version: V1.0
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class SiteSecurityChangeLogService extends ServiceImpl<SiteSecurityChangeLogRepository,SiteSecurityChangeLogPO> {


    public ResponseVO<SiteSecurityChangeLogAllRespVO> listPage(SiteSecurityChangeLogPageReqVO securityChangeLogPageReqVO) {
        Page<SiteSecurityChangeLogPO> page = new Page<SiteSecurityChangeLogPO>(securityChangeLogPageReqVO.getPageNumber(), securityChangeLogPageReqVO.getPageSize());
        LambdaQueryWrapper<SiteSecurityChangeLogPO> lqw = new LambdaQueryWrapper<SiteSecurityChangeLogPO>();
        if(securityChangeLogPageReqVO.getStartTime()!=null){
            lqw.ge(SiteSecurityChangeLogPO::getUpdatedTime, securityChangeLogPageReqVO.getStartTime());
        }
        if(securityChangeLogPageReqVO.getEndTime()!=null){
            lqw.le(SiteSecurityChangeLogPO::getUpdatedTime, securityChangeLogPageReqVO.getEndTime());
        }

        if(StringUtils.hasText(securityChangeLogPageReqVO.getSourceOrderNo())){
            lqw.eq(SiteSecurityChangeLogPO::getSourceOrderNo, securityChangeLogPageReqVO.getSourceOrderNo());
        }

        if(StringUtils.hasText(securityChangeLogPageReqVO.getBalanceAccount())){
            lqw.eq(SiteSecurityChangeLogPO::getBalanceAccount, securityChangeLogPageReqVO.getBalanceAccount());
        }


        if(StringUtils.hasText(securityChangeLogPageReqVO.getSiteCode())){
            lqw.eq(SiteSecurityChangeLogPO::getSiteCode, securityChangeLogPageReqVO.getSiteCode());
        }
        if(StringUtils.hasText(securityChangeLogPageReqVO.getSiteName())){
            lqw.likeRight(SiteSecurityChangeLogPO::getSiteName, securityChangeLogPageReqVO.getSiteName());
        }
        if(StringUtils.hasText(securityChangeLogPageReqVO.getCompany())){
            lqw.likeRight(SiteSecurityChangeLogPO::getCompany, securityChangeLogPageReqVO.getCompany());
        }
        if(securityChangeLogPageReqVO.getSiteType()!=null){
            lqw.eq(SiteSecurityChangeLogPO::getSiteType, securityChangeLogPageReqVO.getSiteType());
        }
        if(securityChangeLogPageReqVO.getMinAmount()!=null){
            lqw.ge(SiteSecurityChangeLogPO::getChangeAmount, securityChangeLogPageReqVO.getMinAmount());
        }
        if(securityChangeLogPageReqVO.getMaxAmount()!=null){
            lqw.le(SiteSecurityChangeLogPO::getChangeAmount, securityChangeLogPageReqVO.getMaxAmount());
        }
        if(securityChangeLogPageReqVO.getSourceCoinType()!=null){
            lqw.eq(SiteSecurityChangeLogPO::getSourceCoinType, securityChangeLogPageReqVO.getSourceCoinType());
        }
        if(securityChangeLogPageReqVO.getCoinType()!=null){
            lqw.eq(SiteSecurityChangeLogPO::getCoinType, securityChangeLogPageReqVO.getCoinType());
        }
        if(StringUtils.hasText(securityChangeLogPageReqVO.getAmountDirect())){
            lqw.eq(SiteSecurityChangeLogPO::getAmountDirect, securityChangeLogPageReqVO.getAmountDirect());
        }
        if(StringUtils.hasText(securityChangeLogPageReqVO.getUserName())){
            lqw.eq(SiteSecurityChangeLogPO::getUserName, securityChangeLogPageReqVO.getUserName());
        }
        if(StringUtils.hasText(securityChangeLogPageReqVO.getUserType())){
            lqw.eq(SiteSecurityChangeLogPO::getUserType, securityChangeLogPageReqVO.getUserType());
        }
        lqw.orderByDesc(SiteSecurityChangeLogPO::getId);
        if("asc".equals(securityChangeLogPageReqVO.getOrderType()) && "beforeAmount".equals(securityChangeLogPageReqVO.getOrderField())){
            lqw.orderByAsc(SiteSecurityChangeLogPO::getBeforeAmount);
        }
        if("desc".equals(securityChangeLogPageReqVO.getOrderType()) && "beforeAmount".equals(securityChangeLogPageReqVO.getOrderField())){
            lqw.orderByDesc(SiteSecurityChangeLogPO::getBeforeAmount);
        }
        if("asc".equals(securityChangeLogPageReqVO.getOrderType()) && "changeAmount".equals(securityChangeLogPageReqVO.getOrderField())){
            lqw.orderByAsc(SiteSecurityChangeLogPO::getChangeAmount);
        }
        if("desc".equals(securityChangeLogPageReqVO.getOrderType()) && "changeAmount".equals(securityChangeLogPageReqVO.getOrderField())){
            lqw.orderByDesc(SiteSecurityChangeLogPO::getChangeAmount);
        }
        if("asc".equals(securityChangeLogPageReqVO.getOrderType()) && "afterAmount".equals(securityChangeLogPageReqVO.getOrderField())){
            lqw.orderByAsc(SiteSecurityChangeLogPO::getAfterAmount);
        }
        if("desc".equals(securityChangeLogPageReqVO.getOrderType()) && "afterAmount".equals(securityChangeLogPageReqVO.getOrderField())){
            lqw.orderByDesc(SiteSecurityChangeLogPO::getAfterAmount);
        }

        IPage<SiteSecurityChangeLogPO> siteSecurityChangeLogPOPage =  this.baseMapper.selectPage(page,lqw);
        Page<SiteSecurityChangeLogRespVO> securityBalanceRespPage=new Page<SiteSecurityChangeLogRespVO>(securityChangeLogPageReqVO.getPageNumber(), securityChangeLogPageReqVO.getPageSize());
        securityBalanceRespPage.setTotal(siteSecurityChangeLogPOPage.getTotal());
        securityBalanceRespPage.setPages(siteSecurityChangeLogPOPage.getPages());
        List<SiteSecurityChangeLogRespVO> resultLists= Lists.newArrayList();
        SiteSecurityChangeLogRespVO currentPage=new SiteSecurityChangeLogRespVO();
        if(!CollectionUtils.isEmpty(siteSecurityChangeLogPOPage.getRecords())){
            for(SiteSecurityChangeLogPO siteSecurityBalancePO :siteSecurityChangeLogPOPage.getRecords()){
                SiteSecurityChangeLogRespVO siteSecurityChangeLogRespVO=new SiteSecurityChangeLogRespVO();
                BeanUtils.copyProperties(siteSecurityBalancePO,siteSecurityChangeLogRespVO);
                resultLists.add(siteSecurityChangeLogRespVO);
                currentPage.setCurrency(siteSecurityBalancePO.getCurrency());
                currentPage.addBeforeAmount(siteSecurityBalancePO.getBeforeAmount());
                currentPage.addChangeAmount(siteSecurityBalancePO.getChangeAmount());
                currentPage.addAfterAmount(siteSecurityBalancePO.getAfterAmount());
            }
            securityBalanceRespPage.setRecords(resultLists);
        }
        SiteSecurityChangeLogRespVO  totalPage=this.baseMapper.selectTotal(lqw);
        if(totalPage==null){
            totalPage=new SiteSecurityChangeLogRespVO();
        }
        SiteSecurityChangeLogAllRespVO siteSecurityChangeLogAllRespVO=new SiteSecurityChangeLogAllRespVO();
        siteSecurityChangeLogAllRespVO.setCurrentPage(currentPage);
        siteSecurityChangeLogAllRespVO.setTotalPage(totalPage);
        siteSecurityChangeLogAllRespVO.setSiteSecurityChangeLogRespVOPage(securityBalanceRespPage);
        return ResponseVO.success(siteSecurityChangeLogAllRespVO);
    }




}
