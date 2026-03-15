package com.cloud.baowang.wallet.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.SystemConfigApi;
import com.cloud.baowang.system.api.api.exchange.SystemCurrencyInfoApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.exchange.SystemBatchRateReqVO;
import com.cloud.baowang.system.api.vo.exchange.SystemCurrencyInfoDetailRespVO;
import com.cloud.baowang.system.api.vo.exchange.SystemCurrencyInfoReqVO;
import com.cloud.baowang.system.api.vo.exchange.SystemCurrencyInfoRespVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.wallet.api.vo.SiteCurrencyDownBoxVO;
import com.cloud.baowang.wallet.api.vo.recharge.*;
import com.cloud.baowang.wallet.po.SiteCurrencyInfoPO;
import com.cloud.baowang.wallet.repositories.SiteCurrencyInfoRepository;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.cloud.baowang.common.core.constants.RedisConstants.SITE_CURRENCY;

/**
 * @Desciption: 站点币种
 * @Author: Ford
 * @Date: 2024/7/26 11:51
 * @Version: V1.0
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class SiteCurrencyInfoService extends ServiceImpl<SiteCurrencyInfoRepository, SiteCurrencyInfoPO> {

    private final SystemCurrencyInfoApi systemCurrencyInfoApi;
    private final SiteApi siteApi;
    private final SystemConfigApi systemConfigApi;


    /**
     * 缓存同步
     * @param siteCode
     * @return
     */
    public List<SiteCurrencyInfoPO> getListByDbCache(String siteCode){
        String redisKey=String.format(SITE_CURRENCY,siteCode);
        List<SiteCurrencyInfoPO> siteCurrencyInfoPOList =RedisUtil.getList(redisKey);
        if(CollectionUtils.isEmpty(siteCurrencyInfoPOList)){
            LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
            query.eq(SiteCurrencyInfoPO::getSiteCode, siteCode);
            siteCurrencyInfoPOList = this.list(query);
            RedisUtil.deleteKey(redisKey);
            RedisUtil.setList(redisKey,siteCurrencyInfoPOList);
        }
        //排序
        if(!CollectionUtils.isEmpty(siteCurrencyInfoPOList)){
            siteCurrencyInfoPOList = siteCurrencyInfoPOList.stream()
                    .sorted(Comparator.comparing(SiteCurrencyInfoPO::getSortOrder))
                    .collect(Collectors.toMap(
                            SiteCurrencyInfoPO::getCurrencyCode,
                            po -> po,
                            (existing, replacement) -> existing,
                            LinkedHashMap::new
                    ))
                    .values()
                    .stream()
                    .toList();
        }
        return siteCurrencyInfoPOList;
    }

    /**
     * 更新所有站点币种缓存
     */
    public  void refreshBySiteInfo(String siteCode){
                //更新币种缓存
        String redisKey=String.format(SITE_CURRENCY,siteCode);
        RedisUtil.deleteKey(redisKey);
    }

    /**
     * 更新所有站点币种缓存
     */
    public  void refreshAllSite(){
        ResponseVO<List<SiteVO>> resp = siteApi.getSiteList();
        if (resp.isOk()) {
            List<SiteVO> data = resp.getData();
            List<String> siteCodeList = data.stream().map(SiteVO::getSiteCode).toList();
            for (String siteCode : siteCodeList) {
                //更新币种缓存
                String redisKey=String.format(SITE_CURRENCY,siteCode);
                RedisUtil.deleteKey(redisKey);
            }
            //删除总站
            String redisKey=String.format(SITE_CURRENCY,CommonConstant.ADMIN_CENTER_SITE_CODE);
            RedisUtil.deleteKey(redisKey);
        }
        /*List<SiteCurrencyInfoPO> allSiteCurrencyLists=this.baseMapper.selectList(null);
        for(SiteCurrencyInfoPO siteCurrencyInfoPO:allSiteCurrencyLists){
            //更新币种缓存
            String redisKey=String.format(SITE_CURRENCY,siteCurrencyInfoPO.getSiteCode());
            RedisUtil.deleteKey(redisKey);
        }*/
    }

    /**
     * 更新所有站点币种缓存
     */
    public  void refreshOneSite(String siteCode){
        String redisKey=String.format(SITE_CURRENCY,siteCode);
        //更新币种缓存
        RedisUtil.deleteKey(redisKey);
    }

    /**
     * 保存汇率 删除缓存key
     * @param siteCurrencyBatchReqVO
     * @return
     */

    public ResponseVO<Boolean> batchSaveRate(SiteCurrencyBatchReqVO siteCurrencyBatchReqVO) {
        List<SiteCurrencyRateReqVO> siteCurrencyRateReqVOS = siteCurrencyBatchReqVO.getSiteCurrencyRateReqVOS();
        if (CollectionUtils.isEmpty(siteCurrencyRateReqVOS)) {
            return ResponseVO.success(Boolean.FALSE);
        }
        //我们这个平台币叫WTC，所有站点不支持更改
        //如果需要更改，走数据库，整个包网统一叫WTC，后面会发币
        if (StringUtils.hasText(siteCurrencyBatchReqVO.getPlatCurrencyName())) {
            siteCurrencyBatchReqVO.setPlatCurrencyName(siteCurrencyBatchReqVO.getPlatCurrencyName());
        } else {
            siteCurrencyBatchReqVO.setPlatCurrencyName(CommonConstant.PLAT_CURRENCY_NAME);
        }
        if (StringUtils.hasText(siteCurrencyBatchReqVO.getPlatCurrencySymbol())) {
            siteCurrencyBatchReqVO.setPlatCurrencySymbol(siteCurrencyBatchReqVO.getPlatCurrencySymbol());
        } else {
            siteCurrencyBatchReqVO.setPlatCurrencySymbol(Character.toString(CommonConstant.PLAT_FORM_SYMBOL));
        }
        //同步修改下当前站点对应的主币种信息
        siteApi.updPlatCurrency(siteCurrencyBatchReqVO.getSiteCode(),
                siteCurrencyBatchReqVO.getPlatCurrencyName(),
                siteCurrencyBatchReqVO.getPlatCurrencySymbol(),
                siteCurrencyBatchReqVO.getPlatCurrencyIcon()
        );
        // List<SiteCurrencyInfoPO> siteCurrencyInfoPOList = Lists.newArrayList();
        List<SystemBatchRateReqVO> systemBatchRateReqVOS=Lists.newArrayList();
        for (SiteCurrencyRateReqVO siteCurrencyRateReqVO : siteCurrencyRateReqVOS) {
            LambdaUpdateWrapper<SiteCurrencyInfoPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<SiteCurrencyInfoPO>();
            lambdaUpdateWrapper.set(SiteCurrencyInfoPO::getPlatCurrencyName, siteCurrencyBatchReqVO.getPlatCurrencyName());
            lambdaUpdateWrapper.set(SiteCurrencyInfoPO::getPlatCurrencySymbol, siteCurrencyBatchReqVO.getPlatCurrencySymbol());
            lambdaUpdateWrapper.set(SiteCurrencyInfoPO::getPlatCurrencyIcon, siteCurrencyBatchReqVO.getPlatCurrencyIcon());
            lambdaUpdateWrapper.set(SiteCurrencyInfoPO::getFinalRate, siteCurrencyRateReqVO.getFinalRate());
            lambdaUpdateWrapper.set(SiteCurrencyInfoPO::getUpdatedTime, System.currentTimeMillis());
            lambdaUpdateWrapper.set(SiteCurrencyInfoPO::getUpdater, siteCurrencyBatchReqVO.getOperatorUserNo());
            lambdaUpdateWrapper.eq(SiteCurrencyInfoPO::getCurrencyCode, siteCurrencyRateReqVO.getCurrencyCode());
            this.update(lambdaUpdateWrapper);
            SystemBatchRateReqVO systemBatchRateReqVO=new SystemBatchRateReqVO();
            systemBatchRateReqVO.setCurrencyCode(siteCurrencyRateReqVO.getCurrencyCode());
            systemBatchRateReqVO.setFinalRate(siteCurrencyRateReqVO.getFinalRate());
            systemBatchRateReqVO.setUpdater(siteCurrencyBatchReqVO.getOperatorUserNo());
            systemBatchRateReqVOS.add(systemBatchRateReqVO);
        }
        //删除所有站点Redis
        refreshAllSite();

        systemCurrencyInfoApi.batchSaveRate(systemBatchRateReqVOS);
        //修改当前站点 废弃
        // this.updateBatchById(siteCurrencyInfoPOList);
        //修改所有站点的汇率
        // this.baseMapper.updateBatchList(siteCurrencyInfoPOList);
        return ResponseVO.success(Boolean.TRUE);
    }



    public ResponseVO<Page<SiteCurrencyInfoRespVO>> selectPage(SiteCurrencyInfoReqVO siteCurrencyInfoReqVO) {
        LambdaQueryWrapper<SiteCurrencyInfoPO> lqw = new LambdaQueryWrapper<SiteCurrencyInfoPO>();
        lqw.eq(SiteCurrencyInfoPO::getSiteCode, siteCurrencyInfoReqVO.getSiteCode());
        if (siteCurrencyInfoReqVO.getStatus() != null) {
            lqw.eq(SiteCurrencyInfoPO::getStatus, siteCurrencyInfoReqVO.getStatus());
        }
        if (StringUtils.hasText(siteCurrencyInfoReqVO.getCurrencyCode())) {
            lqw.like(SiteCurrencyInfoPO::getCurrencyCode, siteCurrencyInfoReqVO.getCurrencyCode());
        }
        lqw.orderByAsc(SiteCurrencyInfoPO::getSortOrder);
        List<SiteCurrencyInfoPO> siteCurrencyInfoPOList = this.baseMapper.selectList(lqw);
        if (CollectionUtils.isEmpty(siteCurrencyInfoPOList)) {
            return ResponseVO.success(new Page<>(siteCurrencyInfoReqVO.getPageNumber(),siteCurrencyInfoReqVO.getPageSize()));
        }
        List<String> currencyCodeList = siteCurrencyInfoPOList.stream().map(o -> o.getCurrencyCode()).collect(Collectors.toUnmodifiableList());
        SystemCurrencyInfoReqVO systemCurrencyInfoReqVO = new SystemCurrencyInfoReqVO();
        systemCurrencyInfoReqVO.setCurrencyName(siteCurrencyInfoReqVO.getCurrencyName());
        systemCurrencyInfoReqVO.setCurrencyCodeList(currencyCodeList);
        systemCurrencyInfoReqVO.setLanguageCode(siteCurrencyInfoReqVO.getLanguageCode());
        systemCurrencyInfoReqVO.setPageNumber(siteCurrencyInfoReqVO.getPageNumber());
        systemCurrencyInfoReqVO.setPageSize(siteCurrencyInfoReqVO.getPageSize());
        ResponseVO<Page<SystemCurrencyInfoRespVO>> currencyInfoRespVO = systemCurrencyInfoApi.selectPage(systemCurrencyInfoReqVO);
        if (currencyInfoRespVO.isOk()) {
            Page<SystemCurrencyInfoRespVO> systemCurrencyInfoRespVOPage = currencyInfoRespVO.getData();
            Page<SiteCurrencyInfoRespVO> siteCurrencyInfoRespVOPage = new Page<>();
            BeanUtils.copyProperties(systemCurrencyInfoRespVOPage, siteCurrencyInfoRespVOPage);
            List<SiteCurrencyInfoRespVO> siteCurrencyInfoRespVOS = Lists.newArrayList();
            for (SystemCurrencyInfoRespVO systemCurrencyInfoRespVO : systemCurrencyInfoRespVOPage.getRecords()) {
                SiteCurrencyInfoRespVO siteCurrencyInfoRespVO = new SiteCurrencyInfoRespVO();
                BeanUtils.copyProperties(systemCurrencyInfoRespVO, siteCurrencyInfoRespVO);
                SiteCurrencyInfoPO siteCurrencyInfoPO = siteCurrencyInfoPOList.stream().filter(o -> o.getCurrencyCode().equals(systemCurrencyInfoRespVO.getCurrencyCode())).findFirst().get();
                siteCurrencyInfoRespVO.setStatus(siteCurrencyInfoPO.getStatus());
                siteCurrencyInfoRespVO.setPlatCurrencyCode(siteCurrencyInfoPO.getPlatCurrencyCode());
                siteCurrencyInfoRespVO.setPlatCurrencyName(siteCurrencyInfoPO.getPlatCurrencyName());
                siteCurrencyInfoRespVO.setPlatCurrencySymbol(siteCurrencyInfoPO.getPlatCurrencySymbol());
                siteCurrencyInfoRespVO.setPlatCurrencyIcon(siteCurrencyInfoPO.getPlatCurrencyIcon());
                siteCurrencyInfoRespVO.setId(siteCurrencyInfoPO.getId());
                siteCurrencyInfoRespVO.setFinalRate(siteCurrencyInfoPO.getFinalRate());
                siteCurrencyInfoRespVO.setSortOrder(siteCurrencyInfoPO.getSortOrder());
                siteCurrencyInfoRespVO.setSiteCode(siteCurrencyInfoPO.getSiteCode());
                siteCurrencyInfoRespVO.setCreator(siteCurrencyInfoPO.getCreator());
                siteCurrencyInfoRespVO.setCreatedTime(siteCurrencyInfoPO.getCreatedTime());
                siteCurrencyInfoRespVO.setUpdater(siteCurrencyInfoPO.getUpdater());
                siteCurrencyInfoRespVO.setUpdatedTime(siteCurrencyInfoPO.getUpdatedTime());
                siteCurrencyInfoRespVOS.add(siteCurrencyInfoRespVO);
            }
            siteCurrencyInfoRespVOS = siteCurrencyInfoRespVOS.stream().sorted(Comparator.comparing(SiteCurrencyInfoRespVO::getSortOrder)).collect(Collectors.toUnmodifiableList());
            siteCurrencyInfoRespVOPage.setRecords(siteCurrencyInfoRespVOS);
            return ResponseVO.success(siteCurrencyInfoRespVOPage);
        }
        return ResponseVO.success(new Page<>(siteCurrencyInfoReqVO.getPageNumber(),siteCurrencyInfoReqVO.getPageSize()));
    }

    public ResponseVO<List<SiteCurrencyInfoRespVO>> selectAllBySort(String siteCode) {
       /* LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
        query.eq(SiteCurrencyInfoPO::getSiteCode, siteCode);
        query.orderByAsc(SiteCurrencyInfoPO::getSortOrder);*/
        List<SiteCurrencyInfoPO> list = getListByDbCache(siteCode);
        if (CollectionUtil.isNotEmpty(list)) {
            List<SiteCurrencyInfoRespVO> result = BeanUtil.copyToList(list, SiteCurrencyInfoRespVO.class);
            setVo(result);
            return ResponseVO.success(result);
        }
        return ResponseVO.success(Lists.newArrayList());
    }




    public ResponseVO<Boolean> enableOrDisable(SiteCurrencyStatusReqVO siteCurrencyStatusReqVO) {
        LambdaQueryWrapper<SiteCurrencyInfoPO> lqw = new LambdaQueryWrapper<SiteCurrencyInfoPO>();
        lqw.eq(SiteCurrencyInfoPO::getId, siteCurrencyStatusReqVO.getId());
        SiteCurrencyInfoPO siteCurrencyInfoPOOld = this.baseMapper.selectOne(lqw);
        if (siteCurrencyInfoPOOld != null) {
            if (Objects.equals(EnableStatusEnum.ENABLE.getCode(), siteCurrencyInfoPOOld.getStatus())) {
                siteCurrencyInfoPOOld.setStatus(EnableStatusEnum.DISABLE.getCode());
            } else {
                SystemCurrencyInfoDetailRespVO systemCurrencyInfoDetailRespVO = systemCurrencyInfoApi.selectByCurrencyCode(siteCurrencyInfoPOOld.getCurrencyCode());
                if (systemCurrencyInfoDetailRespVO != null && Objects.equals(systemCurrencyInfoDetailRespVO.getStatus(), EnableStatusEnum.DISABLE.getCode())) {
                    log.info("总站币种:{}已经被禁用,不能操作", siteCurrencyInfoPOOld.getCurrencyCode());
                    return ResponseVO.fail(ResultCode.SYSTEM_CURRENCY_IS_DISABLE);
                }
                siteCurrencyInfoPOOld.setStatus(EnableStatusEnum.ENABLE.getCode());
            }
            siteCurrencyInfoPOOld.setUpdatedTime(System.currentTimeMillis());
            siteCurrencyInfoPOOld.setUpdater(siteCurrencyStatusReqVO.getOperatorUserNo());
            this.baseMapper.updateById(siteCurrencyInfoPOOld);
            //更新缓存
            refreshOneSite(siteCurrencyInfoPOOld.getSiteCode());
            return ResponseVO.success(Boolean.TRUE);
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> init(SiteCurrencyInitReqVO siteCurrencyBatchReqVO) {
        String operatorUserNoAdmin=siteCurrencyBatchReqVO.getOperatorUserNo();
        String siteCode=siteCurrencyBatchReqVO.getSiteCode();
        BigDecimal finalRate=siteCurrencyBatchReqVO.getFinalRate();
        log.info("站点币种初始化:{},操作人:{}",siteCurrencyBatchReqVO,operatorUserNoAdmin);
        String operatorUserNo=CommonConstant.SUPER_ADMIN;
        siteCurrencyBatchReqVO.setOperatorUserNo(operatorUserNo);
        List<String> reqCurrencyCodeLists = siteCurrencyBatchReqVO.getCurrencyCodeLists();
        if (CollectionUtils.isEmpty(reqCurrencyCodeLists)) {
            return ResponseVO.success(Boolean.TRUE);
        }

        //获取总站汇率 直接更新
        LambdaQueryWrapper<SiteCurrencyInfoPO> adminQueryWrapperList = new LambdaQueryWrapper<SiteCurrencyInfoPO>();
        adminQueryWrapperList.eq(SiteCurrencyInfoPO::getSiteCode, CommonConstant.ADMIN_CENTER_SITE_CODE);
        List<SiteCurrencyInfoPO> admiCurrencyInfoPOList = this.baseMapper.selectList(adminQueryWrapperList);

        String platCurrencyCode = CommonConstant.PLAT_CURRENCY_CODE;
        String platCurrencyName = CommonConstant.PLAT_CURRENCY_NAME;
        String platCurrencySymbol = Character.toString(CommonConstant.PLAT_FORM_SYMBOL);
        String platCurrencyIcon = "";// 图标
        if (!CollectionUtils.isEmpty(admiCurrencyInfoPOList)) {
            SiteCurrencyInfoPO adminCurrencyInfoPO = admiCurrencyInfoPOList.get(0);
            platCurrencyCode = adminCurrencyInfoPO.getPlatCurrencyCode();
            platCurrencyName = adminCurrencyInfoPO.getPlatCurrencyName();
            platCurrencySymbol = adminCurrencyInfoPO.getPlatCurrencySymbol();
            platCurrencyIcon = adminCurrencyInfoPO.getPlatCurrencyIcon();
        }

        //总站初始化
        if(CommonConstant.ADMIN_CENTER_SITE_CODE.equals(siteCode)){
            String currencyCode=siteCurrencyBatchReqVO.getCurrencyCodeLists().get(0);
            LambdaQueryWrapper<SiteCurrencyInfoPO> lambdaQueryWrapper = new LambdaQueryWrapper<SiteCurrencyInfoPO>();
            lambdaQueryWrapper.eq(SiteCurrencyInfoPO::getSiteCode, siteCode);
            lambdaQueryWrapper.eq(SiteCurrencyInfoPO::getCurrencyCode, currencyCode);
            long countNum= this.baseMapper.selectCount(lambdaQueryWrapper);
            if(countNum<=0){
                SiteCurrencyInfoPO siteCurrencyInfoPO = new SiteCurrencyInfoPO();
                siteCurrencyInfoPO.setSiteCode(siteCode);
                siteCurrencyInfoPO.setCurrencyCode(currencyCode);
                siteCurrencyInfoPO.setPlatCurrencyCode(platCurrencyCode);
                siteCurrencyInfoPO.setPlatCurrencyName(platCurrencyName);
                siteCurrencyInfoPO.setPlatCurrencySymbol(platCurrencySymbol);
                siteCurrencyInfoPO.setPlatCurrencyIcon(platCurrencyIcon);
                siteCurrencyInfoPO.setStatus(EnableStatusEnum.ENABLE.getCode());
                siteCurrencyInfoPO.setSortOrder(1);
                siteCurrencyInfoPO.setCreator(operatorUserNoAdmin);
                siteCurrencyInfoPO.setCreatedTime(System.currentTimeMillis());
                siteCurrencyInfoPO.setFinalRate(finalRate);
                siteCurrencyInfoPO.setUpdater(operatorUserNoAdmin);
                siteCurrencyInfoPO.setUpdatedTime(System.currentTimeMillis());
                this.baseMapper.insert(siteCurrencyInfoPO);
            }else{
                //更新平台币最新汇率
                LambdaUpdateWrapper<SiteCurrencyInfoPO> lambdaUpdateWrapper=new LambdaUpdateWrapper<SiteCurrencyInfoPO>();
                lambdaUpdateWrapper.set(SiteCurrencyInfoPO::getFinalRate,finalRate);
                lambdaUpdateWrapper.set(SiteCurrencyInfoPO::getUpdater,operatorUserNoAdmin);
                lambdaUpdateWrapper.set(SiteCurrencyInfoPO::getUpdatedTime,System.currentTimeMillis());
                lambdaUpdateWrapper.eq(SiteCurrencyInfoPO::getCurrencyCode, siteCurrencyBatchReqVO.getCurrencyCodeLists().get(0));
                this.baseMapper.update(null,lambdaUpdateWrapper);
            }
            //刷新当前站点缓存
            refreshAllSite();
            return    ResponseVO.success(Boolean.TRUE);
        }

        //站点初始化
        List<SiteCurrencyInfoPO> batchLists = Lists.newArrayList();
        List<SystemCurrencyInfoRespVO> systemCurrencyInfoRespVOS = systemCurrencyInfoApi.selectAll().getData();
        //获取当前站id 直接更新
        LambdaQueryWrapper<SiteCurrencyInfoPO> lambdaQueryWrapperList = new LambdaQueryWrapper<SiteCurrencyInfoPO>();
        lambdaQueryWrapperList.eq(SiteCurrencyInfoPO::getSiteCode, siteCurrencyBatchReqVO.getSiteCode());
        List<SiteCurrencyInfoPO> siteCurrencyInfoPOList = this.baseMapper.selectList(lambdaQueryWrapperList);

        //币种初始化
        for (String currencyCode : reqCurrencyCodeLists) {
            Optional<SystemCurrencyInfoRespVO> systemCurrencyInfoRespVOOptional = systemCurrencyInfoRespVOS.stream().filter(o -> o.getCurrencyCode().equals(currencyCode)).findFirst();
            if (systemCurrencyInfoRespVOOptional.isEmpty()) {
                log.info("站点币种初始化,币种:{},在币种列表里不存在",currencyCode);
                continue;
            }
            SiteCurrencyInfoPO siteCurrencyInfoPO = new SiteCurrencyInfoPO();
            siteCurrencyInfoPO.setSiteCode(siteCurrencyBatchReqVO.getSiteCode());
            siteCurrencyInfoPO.setCurrencyCode(currencyCode);
            siteCurrencyInfoPO.setPlatCurrencyCode(platCurrencyCode);
            siteCurrencyInfoPO.setPlatCurrencyName(platCurrencyName);
            siteCurrencyInfoPO.setPlatCurrencySymbol(platCurrencySymbol);
            siteCurrencyInfoPO.setPlatCurrencyIcon(platCurrencyIcon);
            siteCurrencyInfoPO.setStatus(EnableStatusEnum.ENABLE.getCode());
            siteCurrencyInfoPO.setSortOrder(1);
            siteCurrencyInfoPO.setCreator(siteCurrencyBatchReqVO.getOperatorUserNo());
            siteCurrencyInfoPO.setCreatedTime(System.currentTimeMillis());

            if (!CollectionUtils.isEmpty(admiCurrencyInfoPOList)) {
                Optional<SiteCurrencyInfoPO> adminCurrencyInfoPOOptional = admiCurrencyInfoPOList.stream().filter(o -> o.getCurrencyCode().equals(currencyCode)).findFirst();
                if (adminCurrencyInfoPOOptional.isPresent()) {
                    //同步总站最新汇率
                    SiteCurrencyInfoPO adminCurrencyPo = adminCurrencyInfoPOOptional.get();
                    siteCurrencyInfoPO.setFinalRate(adminCurrencyPo.getFinalRate());
                    siteCurrencyInfoPO.setStatus(adminCurrencyPo.getStatus());
                    siteCurrencyInfoPO.setUpdater(siteCurrencyBatchReqVO.getOperatorUserNo());
                    siteCurrencyInfoPO.setUpdatedTime(System.currentTimeMillis());
                }
            }
            if (!CollectionUtils.isEmpty(siteCurrencyInfoPOList)) {
                Optional<SiteCurrencyInfoPO> siteCurrencyInfoPOOptional = siteCurrencyInfoPOList.stream().filter(o -> o.getCurrencyCode().equals(currencyCode)).findFirst();
                //保留之前编辑的ID
                if (siteCurrencyInfoPOOptional.isPresent()) {
                    SiteCurrencyInfoPO currentSiteCurrencyPo = siteCurrencyInfoPOOptional.get();
                    siteCurrencyInfoPO.setId(currentSiteCurrencyPo.getId());
                    siteCurrencyInfoPO.setStatus(currentSiteCurrencyPo.getStatus());
                }
            }
            batchLists.add(siteCurrencyInfoPO);
            log.info("站点币种初始化,待更新数据:{}",batchLists);
        }
        //站点币种初始化
        if (!CollectionUtils.isEmpty(batchLists)) {
            log.info("站点币种初始化,更新数据:{}",batchLists);
            this.saveOrUpdateBatch(batchLists);
            if (!siteCurrencyBatchReqVO.getSiteCode().equals(CommonConstant.ADMIN_CENTER_SITE_CODE)) {
                //不勾选 禁用
                LambdaUpdateWrapper<SiteCurrencyInfoPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<SiteCurrencyInfoPO>();
                lambdaUpdateWrapper.set(SiteCurrencyInfoPO::getStatus, EnableStatusEnum.DISABLE.getCode());
                lambdaUpdateWrapper.set(SiteCurrencyInfoPO::getUpdatedTime, System.currentTimeMillis());
                lambdaUpdateWrapper.set(SiteCurrencyInfoPO::getUpdater, siteCurrencyBatchReqVO.getOperatorUserNo());
                lambdaUpdateWrapper.eq(SiteCurrencyInfoPO::getSiteCode, siteCurrencyBatchReqVO.getSiteCode());
                lambdaUpdateWrapper.notIn(SiteCurrencyInfoPO::getCurrencyCode, reqCurrencyCodeLists);
                log.info("站点币种初始化,更新站点其他币种为禁用:{}",reqCurrencyCodeLists);
                this.update(lambdaUpdateWrapper);
                //刷新当前站点缓存
                refreshBySiteInfo(siteCurrencyBatchReqVO.getSiteCode());
            }
        }
        return ResponseVO.success(Boolean.TRUE);
    }

    public ResponseVO<Boolean> batchSave(String currentUserAccount, List<SortNewReqVO> siteCurrencyInfoSortNewReqVOS) {
        List<SiteCurrencyInfoPO> batchLists = Lists.newArrayList();
        for (SortNewReqVO siteCurrencyInfoSortNewReqVO : siteCurrencyInfoSortNewReqVOS) {
            SiteCurrencyInfoPO siteCurrencyInfoPO = new SiteCurrencyInfoPO();
            siteCurrencyInfoPO.setId(siteCurrencyInfoSortNewReqVO.getId());
            siteCurrencyInfoPO.setSortOrder(siteCurrencyInfoSortNewReqVO.getSortOrder());
            siteCurrencyInfoPO.setUpdatedTime(System.currentTimeMillis());
            siteCurrencyInfoPO.setUpdater(currentUserAccount);
            batchLists.add(siteCurrencyInfoPO);
        }
        this.updateBatchById(batchLists);
        //刷新所有站点缓存
        refreshAllSite();
        return ResponseVO.success(Boolean.TRUE);
    }

    //查询平台币信息 和汇率无关
    public ResponseVO<SiteCurrencyInfoRespVO> findPlatCurrencyNameBySiteCode(String siteCode) {
        LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
        query.eq(SiteCurrencyInfoPO::getSiteCode, siteCode)
                .eq(SiteCurrencyInfoPO::getStatus, EnableStatusEnum.ENABLE.getCode())
                .last(" limit 1 ")
        ;
        SiteCurrencyInfoPO siteCurrencyInfoPO = this.baseMapper.selectOne(query);
        if (siteCurrencyInfoPO != null) {
            SiteCurrencyInfoRespVO siteCurrencyInfoRespVO = new SiteCurrencyInfoRespVO();
            siteCurrencyInfoRespVO.setPlatCurrencyCode(siteCurrencyInfoPO.getPlatCurrencyCode());
            siteCurrencyInfoRespVO.setPlatCurrencyName(siteCurrencyInfoPO.getPlatCurrencyName());
            siteCurrencyInfoRespVO.setPlatCurrencySymbol(siteCurrencyInfoPO.getPlatCurrencySymbol());
            siteCurrencyInfoRespVO.setPlatCurrencyIcon(siteCurrencyInfoPO.getPlatCurrencyIcon());
            return ResponseVO.success(siteCurrencyInfoRespVO);
        }
        return ResponseVO.success();
    }

    /**
     * 获取当前站点下全部的币种信息
     *
     * @param siteCode 站点code
     * @return 币种列表，包含多语言
     */
    public List<SiteCurrencyInfoRespVO> getBySiteCode(String siteCode) {
        List<SiteCurrencyInfoPO> siteCurrencyInfoPOList=getListByDbCache(siteCode);
        if (CollectionUtil.isNotEmpty(siteCurrencyInfoPOList)) {
            List<SiteCurrencyInfoRespVO> result = BeanUtil.copyToList(siteCurrencyInfoPOList, SiteCurrencyInfoRespVO.class);
            setVo(result);
            return result;
        }
        return new ArrayList<>();
    }


    /**
     * 获取当前站点下全部已启用的币种信息
     *
     * @param siteCode 站点code
     * @return 币种列表，包含多语言
     */
    public List<SiteCurrencyInfoRespVO> getValidBySiteCode(String siteCode) {
        List<SiteCurrencyInfoPO> siteCurrencyInfoPOList=getListByDbCache(siteCode);
        if (!CollectionUtils.isEmpty(siteCurrencyInfoPOList)) {
            siteCurrencyInfoPOList=siteCurrencyInfoPOList.stream().filter(o->o.getStatus().equals(EnableStatusEnum.ENABLE.getCode())).toList();
           //log.info("siteCurrencyInfoPOList:{}",siteCurrencyInfoPOList);
            if(!CollectionUtils.isEmpty(siteCurrencyInfoPOList)){
               siteCurrencyInfoPOList.stream().sorted(Comparator.comparing(SiteCurrencyInfoPO::getSortOrder));
               List<SiteCurrencyInfoRespVO> result = BeanUtil.copyToList(siteCurrencyInfoPOList, SiteCurrencyInfoRespVO.class);
               setVo(result);
               return result;
           }
        }
        return new ArrayList<>();
    }


    /**
     * 获取当前站点下全部已启用的币种信息
     * 包含平台币
     *
     * @param siteCode 站点code
     * @return 币种列表，包含多语言
     */
    public List<CodeValueVO> getCurrencyList(String siteCode) {
        LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
        query.eq(SiteCurrencyInfoPO::getSiteCode, siteCode);
        query.orderByAsc(SiteCurrencyInfoPO::getSortOrder);
        List<SiteCurrencyInfoPO> list = this.list(query);
        if (CollectionUtil.isNotEmpty(list)) {
            List<SiteCurrencyInfoRespVO> siteCurrencyInfoRespVOS = BeanUtil.copyToList(list, SiteCurrencyInfoRespVO.class);
            setVo(siteCurrencyInfoRespVOS);
            List<CodeValueVO> codeValueVOS = Lists.newArrayList();
            String platCurrencyCode = CommonConstant.PLAT_CURRENCY_CODE;
            String platCurrencyName = CommonConstant.PLAT_CURRENCY_NAME;
            for (SiteCurrencyInfoRespVO siteCurrencyInfoRespVO : siteCurrencyInfoRespVOS) {
                CodeValueVO codeValueVO = new CodeValueVO();
                codeValueVO.setCode(siteCurrencyInfoRespVO.getCurrencyCode());
                codeValueVO.setValue(siteCurrencyInfoRespVO.getCurrencyNameI18());
                platCurrencyCode = siteCurrencyInfoRespVO.getPlatCurrencyCode();
                platCurrencyName = siteCurrencyInfoRespVO.getPlatCurrencyName();
                codeValueVOS.add(codeValueVO);
            }
            CodeValueVO codeValueVO = new CodeValueVO();
            codeValueVO.setCode(platCurrencyCode);
            codeValueVO.setValue(platCurrencyName);
            codeValueVOS.add(0, codeValueVO);
            return codeValueVOS;
        }
        return new ArrayList<>();
    }

    /**
     * 获取当前站点下全部已启用的币种信息
     * 不包含平台币
     *
     * @param siteCode 站点code
     * @return 币种列表，包含多语言
     */
    public List<CodeValueVO> getCurrencyListNo(String siteCode) {
        LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
        query.eq(SiteCurrencyInfoPO::getSiteCode, siteCode);
        query.orderByAsc(SiteCurrencyInfoPO::getSortOrder);
        List<SiteCurrencyInfoPO> list = this.list(query);
        if (CollectionUtil.isNotEmpty(list)) {
            List<SiteCurrencyInfoRespVO> siteCurrencyInfoRespVOS = BeanUtil.copyToList(list, SiteCurrencyInfoRespVO.class);
            setVo(siteCurrencyInfoRespVOS);
            List<CodeValueVO> codeValueVOS = Lists.newArrayList();
            for (SiteCurrencyInfoRespVO siteCurrencyInfoRespVO : siteCurrencyInfoRespVOS) {
                CodeValueVO codeValueVO = new CodeValueVO();
                codeValueVO.setCode(siteCurrencyInfoRespVO.getCurrencyCode());
                codeValueVO.setValue(siteCurrencyInfoRespVO.getCurrencyNameI18());
                codeValueVOS.add(codeValueVO);
            }
            return codeValueVOS;
        }
        return new ArrayList<>();
    }


    public List<CodeValueVO> getCurrencyDownBox(String siteCode) {
       /* LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
        query.eq(SiteCurrencyInfoPO::getSiteCode, siteCode);
        List<SiteCurrencyInfoPO> list = this.list(query);*/
        List<SiteCurrencyInfoPO> list =getListByDbCache(siteCode);
        if (CollectionUtil.isNotEmpty(list)) {
            List<SiteCurrencyInfoRespVO> siteCurrencyInfoRespVOS = BeanUtil.copyToList(list, SiteCurrencyInfoRespVO.class);
            setVo(siteCurrencyInfoRespVOS);
            List<CodeValueVO> codeValueVOS = Lists.newArrayList();
            String platCurrencyCode = CommonConstant.PLAT_CURRENCY_CODE;
            String platCurrencyName = CommonConstant.PLAT_CURRENCY_NAME;
            for (SiteCurrencyInfoRespVO siteCurrencyInfoRespVO : siteCurrencyInfoRespVOS) {
                CodeValueVO codeValueVO = new CodeValueVO();
                codeValueVO.setType(siteCurrencyInfoRespVO.getCurrencyCode());
                codeValueVO.setCode(siteCurrencyInfoRespVO.getCurrencyCode());
                codeValueVO.setValue(siteCurrencyInfoRespVO.getCurrencyCode());
                platCurrencyCode = siteCurrencyInfoRespVO.getPlatCurrencyCode();
                platCurrencyName = siteCurrencyInfoRespVO.getPlatCurrencyName();
                codeValueVOS.add(codeValueVO);
            }
            CodeValueVO codeValueVO = new CodeValueVO();
            codeValueVO.setCode(platCurrencyCode);
            codeValueVO.setValue(platCurrencyName);
            codeValueVO.setType(platCurrencyName);
            if (LanguageEnum.ZH_CN.getLang().equals(CurrReqUtils.getLanguage())) {
                codeValueVO.setType("全部转换为" + platCurrencyName);
            }
            codeValueVOS.add(0, codeValueVO);
            return codeValueVOS;
        }
        return new ArrayList<>();
    }


    /**
     * 关联币种，获取i18数据
     *
     * @param result
     */
    private void setVo(List<SiteCurrencyInfoRespVO> result) {
        ResponseVO<List<SystemCurrencyInfoRespVO>> systemCurrencyInfoResp = systemCurrencyInfoApi.selectAll();
        String domain = systemConfigApi.queryMinioDomain().getData();
        Map<String, SystemCurrencyInfoRespVO> map = new HashMap<>();
        if (systemCurrencyInfoResp.isOk()) {
            List<SystemCurrencyInfoRespVO> data = systemCurrencyInfoResp.getData();
            map = data.stream().collect(Collectors.toMap(SystemCurrencyInfoRespVO::getCurrencyCode, currencyInfo -> currencyInfo));
        }
        for (SiteCurrencyInfoRespVO vo : result) {
            if (map.containsKey(vo.getCurrencyCode())) {
                SystemCurrencyInfoRespVO systemInfoVo = map.get(vo.getCurrencyCode());
                vo.setCurrencyIcon(systemInfoVo.getCurrencyIcon());
                vo.setCurrencyIconFileUrl(domain + "/" + systemInfoVo.getCurrencyIcon());
                vo.setCurrencyDecimal(systemInfoVo.getCurrencyDecimal());
                vo.setCurrencySymbol(systemInfoVo.getCurrencySymbol());
                vo.setCurrencyName(systemInfoVo.getCurrencyName());
                vo.setPlatCurrencyIconFileUrl( domain+ "/" +vo.getPlatCurrencyIcon());
                vo.setCurrencyNameI18(systemInfoVo.getCurrencyNameI18());
            }
        }
    }

    /**
     * 批量获取站点所有对应币种信息
     *
     * @param siteCodeList siteCodes
     * @return 币种列表
     */
    public List<SiteCurrencyInfoRespVO> getListBySiteCodes(List<String> siteCodeList) {
        List<SiteCurrencyInfoRespVO> resultList=Lists.newArrayList();
        List<SiteCurrencyInfoPO> siteCurrencyInfoPOList =Lists.newArrayList();
        for(String siteCode:siteCodeList){
            List<SiteCurrencyInfoPO> list =getListByDbCache(siteCode);
            siteCurrencyInfoPOList.addAll(list);
        }
        if (CollectionUtil.isNotEmpty(siteCurrencyInfoPOList)) {
            List<SiteCurrencyInfoRespVO> result = BeanUtil.copyToList(siteCurrencyInfoPOList, SiteCurrencyInfoRespVO.class);
            setVo(result);
            resultList.addAll(result);
        }
        return resultList;
    }

    /**
     * 平台币兑换为法币
     *
     * @param platCurrencyTransferVO 转换参数
     * @return 转换后金额
     */
    public ResponseVO<BigDecimal> transferPlatToMainCurrency(PlatCurrencyFromTransferVO platCurrencyTransferVO) {
       /* LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
        query.eq(SiteCurrencyInfoPO::getSiteCode, platCurrencyTransferVO.getSiteCode())
                .eq(SiteCurrencyInfoPO::getPlatCurrencyCode, CommonConstant.PLAT_CURRENCY_CODE)
                .eq(SiteCurrencyInfoPO::getCurrencyCode, platCurrencyTransferVO.getTargetCurrencyCode())*/
        //币种禁用不影响 汇率转换
        // .eq(SiteCurrencyInfoPO::getStatus, EnableStatusEnum.ENABLE.getCode())
        /*SiteCurrencyInfoPO siteCurrencyInfoPO = this.baseMapper.selectOne(query);
        if (siteCurrencyInfoPO == null) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }*/
        Map<String, BigDecimal> currencyRateMap=getAllFinalRate(platCurrencyTransferVO.getSiteCode());
        String currencyCode=platCurrencyTransferVO.getTargetCurrencyCode();
        if(currencyRateMap.containsKey(currencyCode)){
            BigDecimal finalRate = currencyRateMap.get(currencyCode);
            BigDecimal targetAmount = AmountUtils.multiply(platCurrencyTransferVO.getSourceAmt(), finalRate);
            return ResponseVO.success(targetAmount);
        }else {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
    }

    /**
     * 平台币兑换为法币
     *
     * @param platCurrencyTransferVO 转换参数
     * @return SiteCurrencyConvertRespVO 转换后金额
     */
    public ResponseVO<SiteCurrencyConvertRespVO> transferToMainCurrency(PlatCurrencyFromTransferVO platCurrencyTransferVO) {
       /* LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
        query.eq(SiteCurrencyInfoPO::getSiteCode, platCurrencyTransferVO.getSiteCode())
                .eq(SiteCurrencyInfoPO::getPlatCurrencyCode, CommonConstant.PLAT_CURRENCY_CODE)
                .eq(SiteCurrencyInfoPO::getCurrencyCode, platCurrencyTransferVO.getTargetCurrencyCode())
        //币种禁用不影响 汇率转换
        // .eq(SiteCurrencyInfoPO::getStatus, EnableStatusEnum.ENABLE.getCode())
        ;
        SiteCurrencyInfoPO siteCurrencyInfoPO = this.baseMapper.selectOne(query);
        if (siteCurrencyInfoPO == null) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }*/
        Map<String, BigDecimal> currencyRateMap=getAllFinalRate(platCurrencyTransferVO.getSiteCode());
        String currencyCode=platCurrencyTransferVO.getTargetCurrencyCode();
        if(currencyRateMap.containsKey(currencyCode)){
            BigDecimal finalRate = currencyRateMap.get(currencyCode);
            BigDecimal targetAmount = AmountUtils.multiply(platCurrencyTransferVO.getSourceAmt(), finalRate);
            SiteCurrencyConvertRespVO siteCurrencyConvertRespVO = new SiteCurrencyConvertRespVO();
            siteCurrencyConvertRespVO.setSourceCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
            siteCurrencyConvertRespVO.setSourceAmount(platCurrencyTransferVO.getSourceAmt());
            siteCurrencyConvertRespVO.setTargetCurrencyCode(platCurrencyTransferVO.getTargetCurrencyCode());
            siteCurrencyConvertRespVO.setTargetAmount(targetAmount);
            siteCurrencyConvertRespVO.setTransferRate(finalRate);
            return ResponseVO.success(siteCurrencyConvertRespVO);
        }else {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

    }

    /**
     * 法币转换为平台币
     *
     * @param platCurrencyToTransferVO 请求参数
     * @return 转换后金额
     */
    public ResponseVO<BigDecimal> transferMainCurrencyToPlat(PlatCurrencyToTransferVO platCurrencyToTransferVO) {
        /*LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
        query.eq(SiteCurrencyInfoPO::getSiteCode, platCurrencyToTransferVO.getSiteCode())
                .eq(SiteCurrencyInfoPO::getPlatCurrencyCode, CommonConstant.PLAT_CURRENCY_CODE)
                .eq(SiteCurrencyInfoPO::getCurrencyCode, platCurrencyToTransferVO.getSourceCurrencyCode())
        //币种禁用不影响 汇率转换
        //  .eq(SiteCurrencyInfoPO::getStatus, EnableStatusEnum.ENABLE.getCode())
        ;
        SiteCurrencyInfoPO siteCurrencyInfoPO = this.baseMapper.selectOne(query);
        if (siteCurrencyInfoPO == null) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }*/
        Map<String, BigDecimal> currencyRateMap=getAllFinalRate(platCurrencyToTransferVO.getSiteCode());
        String currencyCode=platCurrencyToTransferVO.getSourceCurrencyCode();
        if(currencyRateMap.containsKey(currencyCode)){
            BigDecimal finalRate = currencyRateMap.get(currencyCode);
            BigDecimal targetAmount = AmountUtils.divide(platCurrencyToTransferVO.getSourceAmt(), finalRate);
            return ResponseVO.success(targetAmount);
        }else {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
    }

    /**
     * 法币转换为平台币
     *
     * @param platCurrencyToTransferVO 请求参数
     * @return 转换后金额
     */
    public ResponseVO<SiteCurrencyConvertRespVO> transferToPlat(PlatCurrencyToTransferVO platCurrencyToTransferVO) {
        /*LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
        query.eq(SiteCurrencyInfoPO::getSiteCode, platCurrencyToTransferVO.getSiteCode())
                .eq(SiteCurrencyInfoPO::getPlatCurrencyCode, CommonConstant.PLAT_CURRENCY_CODE)
                .eq(SiteCurrencyInfoPO::getCurrencyCode, platCurrencyToTransferVO.getSourceCurrencyCode())
        //币种禁用不影响 汇率转换
        // .eq(SiteCurrencyInfoPO::getStatus, EnableStatusEnum.ENABLE.getCode())
        ;
        SiteCurrencyInfoPO siteCurrencyInfoPO = this.baseMapper.selectOne(query);
        if (siteCurrencyInfoPO == null) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }*/
        Map<String, BigDecimal> currencyRateMap=getAllFinalRate(platCurrencyToTransferVO.getSiteCode());
        String currencyCode=platCurrencyToTransferVO.getSourceCurrencyCode();
        if(currencyRateMap.containsKey(currencyCode)){
            BigDecimal finalRate = currencyRateMap.get(currencyCode);
            BigDecimal targetAmount = AmountUtils.divide(platCurrencyToTransferVO.getSourceAmt(), finalRate);
            SiteCurrencyConvertRespVO siteCurrencyConvertRespVO = new SiteCurrencyConvertRespVO();
            siteCurrencyConvertRespVO.setSourceCurrencyCode(platCurrencyToTransferVO.getSourceCurrencyCode());
            siteCurrencyConvertRespVO.setSourceAmount(platCurrencyToTransferVO.getSourceAmt());
            siteCurrencyConvertRespVO.setTargetCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
            siteCurrencyConvertRespVO.setTargetAmount(targetAmount);
            siteCurrencyConvertRespVO.setTransferRate(finalRate);
            return ResponseVO.success(siteCurrencyConvertRespVO);
        }else {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
    }





    /**
     * 获取币种对应的平台汇率
     *
     * @param siteCode 站点编码
     * @return 汇率
     */
    //添加缓存
    public Map<String, BigDecimal> getAllFinalRate(String siteCode) {
        List<SiteCurrencyInfoPO> siteCurrencyInfoPOList =getListByDbCache(siteCode);
        Map<String, BigDecimal> currencyMap = Maps.newHashMap();
        if (CollectionUtil.isNotEmpty(siteCurrencyInfoPOList)) {
            siteCurrencyInfoPOList.stream().filter(o->o.getFinalRate()!=null).forEach(o -> {
                currencyMap.put(o.getCurrencyCode(), o.getFinalRate());
            });
            currencyMap.put(siteCurrencyInfoPOList.get(0).getPlatCurrencyCode(), BigDecimal.ONE);
            return currencyMap;
        }
        return Map.of();

    }

    public Map<String, Map<String, BigDecimal>> getAllSiteFinalRate(List<String> siteCodes) {
        Map<String, Map<String, BigDecimal>> resultMap = Maps.newHashMap();
        LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
        query.isNotNull(SiteCurrencyInfoPO::getFinalRate);
        query.in(SiteCurrencyInfoPO::getSiteCode, siteCodes);
        query.orderByAsc(SiteCurrencyInfoPO::getSortOrder);
        List<SiteCurrencyInfoPO> siteCurrencyInfoPOList=this.baseMapper.selectList(query);
        if (CollectionUtil.isNotEmpty(siteCurrencyInfoPOList)) {
            Map<String, List<SiteCurrencyInfoPO>> groupBySiteCode = siteCurrencyInfoPOList.stream()
                    .collect(Collectors.groupingBy(SiteCurrencyInfoPO::getSiteCode));

            groupBySiteCode.keySet().forEach(e ->{
                List<SiteCurrencyInfoPO> data=groupBySiteCode.get(e);
                Map<String, BigDecimal> currencyMap = data.stream()
                        .collect(Collectors.toMap(
                                SiteCurrencyInfoPO::getCurrencyCode,
                                SiteCurrencyInfoPO::getFinalRate
                        ));
                currencyMap.put(siteCurrencyInfoPOList.get(0).getPlatCurrencyCode(), BigDecimal.ONE);
                resultMap.put(e,currencyMap);
            });
        }
        return resultMap;

    }

    /**
     * 获取币种对应的平台汇率
     *
     * @return 汇率
     */
    public Map<String, Map<String, BigDecimal>> getAllPlateFinalRate() {
        LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
        List<SiteCurrencyInfoPO> list = this.list(query);
        Map<String, Map<String, BigDecimal>> currencyMap = Maps.newHashMap();
        if (CollectionUtil.isNotEmpty(list)) {
            currencyMap = list.stream()
                    .filter(o -> null != o.getFinalRate())
                    .collect(Collectors.groupingBy(SiteCurrencyInfoPO::getSiteCode,
                            Collectors.toMap(SiteCurrencyInfoPO::getCurrencyCode,
                                    SiteCurrencyInfoPO::getFinalRate)));
            return currencyMap;
        }
        return Map.of();

    }

    public BigDecimal getCurrencyFinalRate(String siteCode, String currencyCode) {

        List<SiteCurrencyInfoPO> siteCurrencyInfoPOList =getListByDbCache(siteCode);
        Optional<SiteCurrencyInfoPO> siteCurrencyInfoPOOptional=siteCurrencyInfoPOList.stream().filter(o->o.getCurrencyCode().equals(currencyCode)).findFirst();
        if(siteCurrencyInfoPOOptional.isPresent()){
            return siteCurrencyInfoPOOptional.get().getFinalRate();
        }
        return null;
       /* LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
        query.eq(SiteCurrencyInfoPO::getSiteCode, siteCode);
        query.eq(SiteCurrencyInfoPO::getCurrencyCode, currencyCode);
        query.last(" limit 1");*/
        /*SiteCurrencyInfoPO siteCurrencyInfoPO = this.baseMapper.selectOne(query);
        if (ObjectUtil.isNotEmpty(siteCurrencyInfoPO)) {
            return siteCurrencyInfoPO.getFinalRate();
        }
        return null;*/
    }

    public Map<String, List<SiteCurrencyInfoRespVO>> getCurrencyBySiteCodes(List<String> siteCodes) {
       // LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
      //  query.in(SiteCurrencyInfoPO::getSiteCode, siteCodes);
        // List<SiteCurrencyInfoPO> list = this.list(query);
        List<SiteCurrencyInfoPO> resultList=Lists.newArrayList();
        for(String siteCode:siteCodes){
            List<SiteCurrencyInfoPO> siteCurrencyInfoPOList = this.getListByDbCache(siteCode);
            resultList.addAll(siteCurrencyInfoPOList);
        }
        List<SiteCurrencyInfoRespVO> vos = BeanUtil.copyToList(resultList, SiteCurrencyInfoRespVO.class);
        if (CollectionUtil.isNotEmpty(vos)) {
            return vos.stream().collect(Collectors.groupingBy(SiteCurrencyInfoRespVO::getSiteCode));
        }
        return new HashMap<>();
    }


    /**
     * 获取指定站点下的所有汇率
     *
     * @param siteCodeList 站点列表
     * @return 站点, 币种, 汇率
     */
    public Map<String, Map<String, BigDecimal>> getAllFinalRateBySiteList(List<String> siteCodeList) {
        /*LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
        query.in(SiteCurrencyInfoPO::getSiteCode, siteCodeList);
        List<SiteCurrencyInfoPO> list = this.list(query);
        Map<String, Map<String, BigDecimal>> resultMap = Maps.newHashMap();
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(o -> {
                if (resultMap.containsKey(o.getSiteCode())) {
                    Map<String, BigDecimal> currencyMap = resultMap.get(o.getSiteCode());
                    currencyMap.put(o.getCurrencyCode(), o.getFinalRate());
                    resultMap.put(o.getSiteCode(), currencyMap);
                } else {
                    Map<String, BigDecimal> currencyMap = Maps.newHashMap();
                    currencyMap.put(o.getCurrencyCode(), o.getFinalRate());
                    resultMap.put(o.getSiteCode(), currencyMap);
                }
            });
            return resultMap;
        }
        return Map.of();*/
        Map<String, Map<String, BigDecimal>> resultSiteMap = new HashMap<>();
        for(String siteCode:siteCodeList){
            Map<String, BigDecimal> siteRateMap=getAllFinalRate(siteCode);
            resultSiteMap.put(siteCode,siteRateMap);
        }
        return resultSiteMap;
    }

    /**
     * 总站禁用 站点自动禁用
     *
     * @param currencyCode 币种
     */
    public void disableCurrency(String currencyCode,String operatorUserNo) {
        if (!StringUtils.hasText(currencyCode)) {
            return;
        }
        log.info("总站币种:{}禁用,自动将所有站点币种禁用", currencyCode);
        LambdaUpdateWrapper<SiteCurrencyInfoPO> lqw = new LambdaUpdateWrapper<SiteCurrencyInfoPO>();
        lqw.set(SiteCurrencyInfoPO::getStatus, EnableStatusEnum.DISABLE.getCode());
        lqw.set(SiteCurrencyInfoPO::getUpdatedTime, System.currentTimeMillis());
        lqw.set(SiteCurrencyInfoPO::getUpdater, CommonConstant.SUPER_ADMIN);
        lqw.eq(SiteCurrencyInfoPO::getCurrencyCode, currencyCode);
        this.update(lqw);

        LambdaQueryWrapper<SiteCurrencyInfoPO> lambdaQueryWrapper = new LambdaQueryWrapper<SiteCurrencyInfoPO>();
        lambdaQueryWrapper.eq(SiteCurrencyInfoPO::getCurrencyCode, currencyCode);
        List<SiteCurrencyInfoPO> allSiteCurrencyLists=this.baseMapper.selectList(lambdaQueryWrapper);
        for(SiteCurrencyInfoPO siteCurrencyInfoPO:allSiteCurrencyLists){
            //更新币种缓存
            String redisKey=String.format(SITE_CURRENCY,siteCurrencyInfoPO.getSiteCode());
            RedisUtil.deleteKey(redisKey);
        }
    }


    public ResponseVO<List<SiteCurrencyDownBoxVO>> getSiteCurrencyDownBox(String siteCode) {
        List<SiteCurrencyDownBoxVO> result = new ArrayList<>();
        int yesCode = Integer.parseInt(YesOrNoEnum.YES.getCode());
        int noCode = Integer.parseInt(YesOrNoEnum.NO.getCode());
        //获取总台全部币种(包含启用禁用)
        ResponseVO<List<SystemCurrencyInfoRespVO>> resp = systemCurrencyInfoApi.selectAll();
        List<SystemCurrencyInfoRespVO> data = resp.getData();
        if (resp.isOk() && CollectionUtil.isNotEmpty(data)) {

            //新增站点,统计所有启用的币种列表
            if (org.apache.commons.lang3.StringUtils.isBlank(siteCode)) {
                //过滤掉所有禁用的币种
                data = data.stream().filter(item -> EnableStatusEnum.ENABLE.getCode().equals(item.getStatus())).toList();
                if (CollectionUtil.isNotEmpty(data)) {
                    data.forEach(obj -> result.add(SiteCurrencyDownBoxVO.builder()
                            .code(obj.getCurrencyCode()).value(obj.getCurrencyNameI18()).isChecked(noCode).build()));
                }
            } else {
                //编辑站点,获取到站点对应已选择的币种
                LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
                query.eq(SiteCurrencyInfoPO::getSiteCode, siteCode);
                List<SiteCurrencyInfoPO> siteCurrencyList = this.list(query);
                if (CollectionUtil.isNotEmpty(siteCurrencyList)) {
                    //站点已有data转map
                    Map<String, SiteCurrencyInfoPO> map = siteCurrencyList.stream()
                            .collect(Collectors.toMap(SiteCurrencyInfoPO::getCurrencyCode, currency -> currency));
                    for (SystemCurrencyInfoRespVO systemCurrencyData : data) {
                        SiteCurrencyDownBoxVO siteCurrencyDownBoxVO = new SiteCurrencyDownBoxVO();
                        if (map.containsKey(systemCurrencyData.getCurrencyCode())) {
                            //站点中包含了总台的币种,默认勾选上
                            siteCurrencyDownBoxVO.setCode(systemCurrencyData.getCurrencyCode());
                            siteCurrencyDownBoxVO.setValue(systemCurrencyData.getCurrencyNameI18());
                            siteCurrencyDownBoxVO.setIsChecked(yesCode);
                            result.add(siteCurrencyDownBoxVO);
                        } else {
                            //不包含的,判断下总台是禁用还是启用当前币种,禁用的不做展示
                            if (EnableStatusEnum.ENABLE.getCode().equals(systemCurrencyData.getStatus())) {
                                siteCurrencyDownBoxVO.setCode(systemCurrencyData.getCurrencyCode());
                                siteCurrencyDownBoxVO.setValue(systemCurrencyData.getCurrencyNameI18());
                                siteCurrencyDownBoxVO.setIsChecked(noCode);
                                result.add(siteCurrencyDownBoxVO);
                            }
                        }
                    }
                }
            }
        }


        return ResponseVO.success(result);
    }

    public SiteCurrencyInfoRespVO getByCurrencyCode(String siteCode, String currencyCode) {
       /* LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
        query.eq(SiteCurrencyInfoPO::getSiteCode, siteCode);
        query.eq(SiteCurrencyInfoPO::getCurrencyCode, currencyCode);
        SiteCurrencyInfoPO po = this.baseMapper.selectOne(query);
        return ConvertUtil.entityToModel(po, SiteCurrencyInfoRespVO.class);*/
        List<SiteCurrencyInfoPO> siteCurrencyInfoPOList=this.getListByDbCache(siteCode);
        Optional<SiteCurrencyInfoPO> siteCurrencyInfoPOOptional =siteCurrencyInfoPOList.stream().filter(o->o.getCurrencyCode().equals(currencyCode)).findFirst();
        return siteCurrencyInfoPOOptional.map(siteCurrencyInfoPO -> ConvertUtil.entityToModel(siteCurrencyInfoPO, SiteCurrencyInfoRespVO.class)).orElse(null);
    }

    public void enableAdminCurrency(String currencyCode) {
        if (!StringUtils.hasText(currencyCode)) {
            return;
        }
        log.info("总站币种:{}开启,自动开启总站下币种", currencyCode);
        LambdaUpdateWrapper<SiteCurrencyInfoPO> lqw = new LambdaUpdateWrapper<SiteCurrencyInfoPO>();
        lqw.set(SiteCurrencyInfoPO::getStatus, EnableStatusEnum.ENABLE.getCode());
        lqw.set(SiteCurrencyInfoPO::getUpdatedTime, System.currentTimeMillis());
        lqw.set(SiteCurrencyInfoPO::getUpdater, CommonConstant.SUPER_ADMIN);
        lqw.eq(SiteCurrencyInfoPO::getCurrencyCode, currencyCode);
        lqw.eq(SiteCurrencyInfoPO::getSiteCode, CommonConstant.ADMIN_CENTER_SITE_CODE);
        this.update(lqw);

        LambdaQueryWrapper<SiteCurrencyInfoPO> lambdaQueryWrapper = new LambdaQueryWrapper<SiteCurrencyInfoPO>();
        lambdaQueryWrapper.eq(SiteCurrencyInfoPO::getCurrencyCode, currencyCode);
        lambdaQueryWrapper.eq(SiteCurrencyInfoPO::getSiteCode, CommonConstant.ADMIN_CENTER_SITE_CODE);
        List<SiteCurrencyInfoPO> allSiteCurrencyLists=this.baseMapper.selectList(lambdaQueryWrapper);
        for(SiteCurrencyInfoPO siteCurrencyInfoPO:allSiteCurrencyLists){
            //更新币种缓存
            String redisKey=String.format(SITE_CURRENCY,siteCurrencyInfoPO.getSiteCode());
            RedisUtil.deleteKey(redisKey);
        }
    }
}
