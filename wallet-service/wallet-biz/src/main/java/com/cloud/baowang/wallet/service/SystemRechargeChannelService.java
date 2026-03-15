package com.cloud.baowang.wallet.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.wallet.ChannelTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.vip.VIPRankVO;
import com.cloud.baowang.wallet.api.vo.recharge.ChannelQueryReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelStatusReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelUpdateReqVO;
import com.cloud.baowang.wallet.po.SiteRechargeChannelPO;
import com.cloud.baowang.wallet.po.SystemRechargeChannelPO;
import com.cloud.baowang.wallet.po.SystemRechargeWayPO;
import com.cloud.baowang.wallet.repositories.SiteRechargeChannelRepository;
import com.cloud.baowang.wallet.repositories.SystemRechargeChannelRepository;
import com.cloud.baowang.wallet.repositories.SystemRechargeWayRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 11:51
 * @Version: V1.0
 **/
@Service
@Slf4j
@AllArgsConstructor
public class SystemRechargeChannelService extends ServiceImpl<SystemRechargeChannelRepository, SystemRechargeChannelPO> {

    private final SystemRechargeWayRepository systemRechargeWayRepository;
    private final SystemRechargeChannelRepository systemRechargeChannelRepository;

    private final SiteRechargeChannelRepository siteRechargeChannelRepository;


    private final VipRankApi vipRankApi;

    private final VipGradeApi vipGradeApi;

    public ResponseVO<Page<SystemRechargeChannelRespVO>> selectPage(SystemRechargeChannelReqVO systemRechargeChannelReqVO) {
        Page<SystemRechargeChannelPO> page = new Page<SystemRechargeChannelPO>(systemRechargeChannelReqVO.getPageNumber(), systemRechargeChannelReqVO.getPageSize());
        LambdaQueryWrapper<SystemRechargeChannelPO> lqw = new LambdaQueryWrapper<SystemRechargeChannelPO>();
        if(StringUtils.hasText(systemRechargeChannelReqVO.getCurrencyCode())){
            lqw.eq(SystemRechargeChannelPO::getCurrencyCode, systemRechargeChannelReqVO.getCurrencyCode());
        }
        if(StringUtils.hasText(systemRechargeChannelReqVO.getRechargeWayId())){
            lqw.eq(SystemRechargeChannelPO::getRechargeWayId, systemRechargeChannelReqVO.getRechargeWayId());
        }
        if(StringUtils.hasText(systemRechargeChannelReqVO.getChannelType())){
            lqw.eq(SystemRechargeChannelPO::getChannelType, systemRechargeChannelReqVO.getChannelType());
        }
        if(StringUtils.hasText(systemRechargeChannelReqVO.getChannelName())){
            lqw.like(SystemRechargeChannelPO::getChannelName, systemRechargeChannelReqVO.getChannelName());
        }
        if(systemRechargeChannelReqVO.getStatus()!=null){
            lqw.eq(SystemRechargeChannelPO::getStatus, systemRechargeChannelReqVO.getStatus());
        }
        lqw.orderByDesc(SystemRechargeChannelPO::getUpdatedTime);
        IPage<SystemRechargeChannelPO> systemRechargeChannelIPage =  this.baseMapper.selectPage(page,lqw);
        Page<SystemRechargeChannelRespVO> systemRechargeChannelRespVOPage=new Page<SystemRechargeChannelRespVO>(systemRechargeChannelReqVO.getPageNumber(), systemRechargeChannelReqVO.getPageSize());
        systemRechargeChannelRespVOPage.setTotal(systemRechargeChannelIPage.getTotal());
        systemRechargeChannelRespVOPage.setPages(systemRechargeChannelIPage.getPages());
        List<SystemRechargeChannelRespVO> resultLists= Lists.newArrayList();
        if(!CollectionUtils.isEmpty(systemRechargeChannelIPage.getRecords())){
            List<VIPRankVO> vipRankList = vipRankApi.getVipRankList();
            Map<Integer, String> vipRankMap = vipRankList.stream()
                    .collect(Collectors.toMap(VIPRankVO::getVipRankCode, VIPRankVO::getVipRankNameI18nCode));

            List<CodeValueNoI18VO> vipGradeTopTen =  vipGradeApi.getVipGradeTopTen();
            List<String> rechargeWayIds=systemRechargeChannelIPage.getRecords().stream().map(SystemRechargeChannelPO::getRechargeWayId).toList();
            LambdaQueryWrapper<SystemRechargeWayPO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(SystemRechargeWayPO::getId,rechargeWayIds);
            List<SystemRechargeWayPO> systemRechargeWayPOS=systemRechargeWayRepository.selectList(lambdaQueryWrapper);

            for(SystemRechargeChannelPO systemRechargeChannelPO :systemRechargeChannelIPage.getRecords()){
                SystemRechargeChannelRespVO systemRechargeChannelRespVO=new SystemRechargeChannelRespVO();
                BeanUtils.copyProperties(systemRechargeChannelPO,systemRechargeChannelRespVO);
                String useScope = systemRechargeChannelRespVO.getUseScope();

                if (org.apache.commons.lang3.StringUtils.isNotBlank(useScope)) {
                    List<String> scope = Arrays.asList(useScope.split(CommonConstant.COMMA));
                    String result = scope.stream()
                            .map(Integer::valueOf) // 将字符串转换为 Integer
                            .filter(vipRankMap::containsKey) // 过滤出存在于 map 中的键
                            .map(vipRankMap::get) // 获取对应的名称
                            .map(I18nMessageUtil::getI18NMessageInAdvice) // 获取国际化名称
                            .collect(Collectors.joining(",")); // 用逗号拼接
                    systemRechargeChannelRespVO.setUseScopeText(result);
                }
                String vipGradeUseScope = systemRechargeChannelRespVO.getVipGradeUseScope();
                Map<String, String> vipGradeMap = vipGradeTopTen.stream()
                        .collect(Collectors.toMap(CodeValueNoI18VO::getCode, CodeValueNoI18VO::getValue));
                if (org.apache.commons.lang3.StringUtils.isNotBlank(vipGradeUseScope)) {
                    List<String> scope = Arrays.asList(vipGradeUseScope.split(CommonConstant.COMMA));
                    String vipGradeUseScopeStr = scope.stream()
                            .filter(vipGradeMap::containsKey)
                            .map(vipGradeMap::get)
                            .collect(Collectors.joining(","));
                    systemRechargeChannelRespVO.setVipGradeUseScopeText(vipGradeUseScopeStr);
                }

                Optional<SystemRechargeWayPO> systemRechargeWayPOOptional=systemRechargeWayPOS.stream().filter(o->o.getId().equals(String.valueOf(systemRechargeChannelPO.getRechargeWayId()))).findFirst();
                systemRechargeWayPOOptional.ifPresent(systemRechargeWayPO -> systemRechargeChannelRespVO.setRechargeWayI18(systemRechargeWayPO.getRechargeWayI18()));
                resultLists.add(systemRechargeChannelRespVO);
            }
            systemRechargeChannelRespVOPage.setRecords(resultLists);
        }

        return ResponseVO.success(systemRechargeChannelRespVOPage);
    }

    public ResponseVO<List<SystemRechargeChannelRespVO>> selectBySort(SystemRechargeChannelReqVO systemRechargeChannelReqVO) {
        LambdaQueryWrapper<SystemRechargeChannelPO> lqw = new LambdaQueryWrapper<SystemRechargeChannelPO>();
        lqw.eq(SystemRechargeChannelPO::getCurrencyCode, systemRechargeChannelReqVO.getCurrencyCode());
        if(StringUtils.hasText(systemRechargeChannelReqVO.getRechargeWayId())){
            lqw.eq(SystemRechargeChannelPO::getRechargeWayId, systemRechargeChannelReqVO.getRechargeWayId());
        }
        lqw.orderByAsc(SystemRechargeChannelPO::getSortOrder);
        List<SystemRechargeChannelPO> systemRechargeChannelPOList =  this.baseMapper.selectList(lqw);
        List<SystemRechargeChannelRespVO> resultLists= Lists.newArrayList();
        if(!CollectionUtils.isEmpty(systemRechargeChannelPOList)){
            List<String> rechargeWayIds=systemRechargeChannelPOList.stream().map(SystemRechargeChannelPO::getRechargeWayId).toList();
            LambdaQueryWrapper<SystemRechargeWayPO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(SystemRechargeWayPO::getId,rechargeWayIds);
            List<SystemRechargeWayPO> systemRechargeWayPOS=systemRechargeWayRepository.selectList(lambdaQueryWrapper);
            for(SystemRechargeChannelPO systemRechargeChannelPO :systemRechargeChannelPOList){
                SystemRechargeChannelRespVO systemRechargeChannelRespVO=new SystemRechargeChannelRespVO();
                BeanUtils.copyProperties(systemRechargeChannelPO,systemRechargeChannelRespVO);
                Optional<SystemRechargeWayPO> systemRechargeWayPOOptional=systemRechargeWayPOS.stream().filter(o->o.getId().equals(String.valueOf(systemRechargeChannelPO.getRechargeWayId()))).findFirst();
                systemRechargeWayPOOptional.ifPresent(systemRechargeWayPO -> systemRechargeChannelRespVO.setRechargeWayI18(systemRechargeWayPO.getRechargeWayI18()));
                resultLists.add(systemRechargeChannelRespVO);
            }
        }
        return   ResponseVO.success(resultLists);

    }

    public ResponseVO<Void> insert(SystemRechargeChannelNewReqVO systemRechargeChannelNewReqVO) {
        if(ChannelTypeEnum.THIRD.getCode().equals(systemRechargeChannelNewReqVO.getChannelType())){
            if(!checkChannelCodeUnique(systemRechargeChannelNewReqVO.getChannelCode(),systemRechargeChannelNewReqVO.getRechargeWayId(),
                    systemRechargeChannelNewReqVO.getMerNo(),systemRechargeChannelNewReqVO.getChannelName(), null)){
                throw  new BaowangDefaultException(ResultCode.CHANNEL_CODE_IS_EXIST);
            }
            LambdaQueryWrapper<SystemRechargeChannelPO> lqw = new LambdaQueryWrapper<SystemRechargeChannelPO>();
            lqw.eq(SystemRechargeChannelPO::getCurrencyCode, systemRechargeChannelNewReqVO.getCurrencyCode());
            lqw.eq(SystemRechargeChannelPO::getChannelCode, systemRechargeChannelNewReqVO.getChannelCode());
           long countNum = this.baseMapper.selectCount(lqw);
           if(countNum>=1) {
               return ResponseVO.fail(ResultCode.DATA_IS_EXIST);
           }
        }
        LambdaQueryWrapper<SystemRechargeWayPO> lambdaQueryWrapper= Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(SystemRechargeWayPO::getId,systemRechargeChannelNewReqVO.getRechargeWayId());
        SystemRechargeWayPO systemRechargeWayPO=systemRechargeWayRepository.selectOne(lambdaQueryWrapper);
        if(systemRechargeWayPO==null){
            throw  new BaowangDefaultException(ResultCode.RECHARGE_WAY_IS_NOT_EXIST);
        }
        SystemRechargeChannelPO systemRechargeChannelPO =new SystemRechargeChannelPO();
        BeanUtils.copyProperties(systemRechargeChannelNewReqVO, systemRechargeChannelPO);
        String idNo= OrderUtil.createNumber(12);
        systemRechargeChannelPO.setRechargeChannelNo(idNo);
        systemRechargeChannelPO.setRechargeTypeId(systemRechargeWayPO.getRechargeTypeId());
        systemRechargeChannelPO.setRechargeTypeCode(systemRechargeWayPO.getRechargeTypeCode());
        systemRechargeChannelPO.setStatus(EnableStatusEnum.ENABLE.getCode());
        systemRechargeChannelPO.setAuthNum(0);
        systemRechargeChannelPO.setCreator(systemRechargeChannelNewReqVO.getOperatorUserNo());
        systemRechargeChannelPO.setUpdater(systemRechargeChannelNewReqVO.getOperatorUserNo());
        systemRechargeChannelPO.setCreatedTime(System.currentTimeMillis());
        systemRechargeChannelPO.setUpdatedTime(System.currentTimeMillis());
        this.baseMapper.insert(systemRechargeChannelPO);
        return ResponseVO.success();
    }

    private String genIdNo(String rechargeTypeCode) {
        LambdaQueryWrapper<SystemRechargeChannelPO> lqw = new LambdaQueryWrapper<SystemRechargeChannelPO>();
        lqw.eq(SystemRechargeChannelPO::getRechargeTypeCode, rechargeTypeCode);
        lqw.orderByDesc(SystemRechargeChannelPO::getCreatedTime);
        lqw.last("limit 0,1 for update");
        SystemRechargeChannelPO systemRechargeChannelPOOld = this.baseMapper.selectOne(lqw);
        String lastId=rechargeTypeCode+"000";
        if(systemRechargeChannelPOOld!=null&&StringUtils.hasText(systemRechargeChannelPOOld.getRechargeChannelNo())){
            lastId= systemRechargeChannelPOOld.getRechargeChannelNo();
        }
        String lastIdStr=lastId.replace(rechargeTypeCode,"");
        Integer newId=Integer.valueOf(lastIdStr).intValue()+1;
        return rechargeTypeCode.concat(String.format("%03d",newId));
    }

    /**
     * 渠道编码唯一校验
     * @param channelCode
     * @param id
     * @return
     */
    private boolean checkChannelCodeUnique(String channelCode,String rechargeWayId,String merNo, String channelName, String id) {
        LambdaQueryWrapper<SystemRechargeChannelPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SystemRechargeChannelPO::getChannelCode, channelCode);
        lqw.eq(SystemRechargeChannelPO::getChannelName, channelName);
        lqw.eq(SystemRechargeChannelPO::getRechargeWayId,rechargeWayId);
        lqw.eq(SystemRechargeChannelPO::getMerNo,merNo);
        lqw.ne(null != id, SystemRechargeChannelPO::getId, id);
        List<SystemRechargeChannelPO> channelPOS = this.baseMapper.selectList(lqw);
        return channelPOS.isEmpty();
    }

    public ResponseVO<Void> updateByInfo(SystemRechargeChannelUpdateReqVO systemRechargeChannelUpdateReqVO) {
        if(ChannelTypeEnum.THIRD.getCode().equals(systemRechargeChannelUpdateReqVO.getChannelType())){
            if(!checkChannelCodeUnique(systemRechargeChannelUpdateReqVO.getChannelCode(),systemRechargeChannelUpdateReqVO.getRechargeWayId(),
                    systemRechargeChannelUpdateReqVO.getMerNo(),systemRechargeChannelUpdateReqVO.getChannelName(), systemRechargeChannelUpdateReqVO.getId())){
                throw  new BaowangDefaultException(ResultCode.CHANNEL_CODE_IS_EXIST);
            }
        }
        LambdaQueryWrapper<SystemRechargeChannelPO> lqw = new LambdaQueryWrapper<SystemRechargeChannelPO>();
        lqw.eq(SystemRechargeChannelPO::getId, systemRechargeChannelUpdateReqVO.getId());
        SystemRechargeChannelPO systemRechargeChannelPOOld = this.baseMapper.selectOne(lqw);
        if(systemRechargeChannelPOOld==null){
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        BeanUtils.copyProperties(systemRechargeChannelUpdateReqVO, systemRechargeChannelPOOld);
        LambdaQueryWrapper<SystemRechargeWayPO> lambdaQueryWrapper= Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(SystemRechargeWayPO::getId,systemRechargeChannelUpdateReqVO.getRechargeWayId());
        SystemRechargeWayPO systemRechargeWayPO=systemRechargeWayRepository.selectOne(lambdaQueryWrapper);
        if(systemRechargeWayPO==null){
            throw  new BaowangDefaultException(ResultCode.RECHARGE_WAY_IS_NOT_EXIST);
        }
        systemRechargeChannelPOOld.setRechargeTypeId(systemRechargeWayPO.getRechargeTypeId());
        systemRechargeChannelPOOld.setRechargeTypeCode(systemRechargeWayPO.getRechargeTypeCode());
        systemRechargeChannelPOOld.setUpdater(systemRechargeChannelUpdateReqVO.getOperatorUserNo());
        systemRechargeChannelPOOld.setUpdatedTime(System.currentTimeMillis());
        this.baseMapper.updateById(systemRechargeChannelPOOld);
        return ResponseVO.success();
    }

    public ResponseVO<Void> enableOrDisable(SystemRechargeChannelStatusReqVO systemRechargeChannelStatusReqVO) {
        LambdaQueryWrapper<SystemRechargeChannelPO> lqw = new LambdaQueryWrapper<SystemRechargeChannelPO>();
        lqw.eq(SystemRechargeChannelPO::getId, systemRechargeChannelStatusReqVO.getId());
        SystemRechargeChannelPO systemRechargeChannelPOOld = this.baseMapper.selectOne(lqw);
        if(systemRechargeChannelPOOld !=null){
            if(Objects.equals(EnableStatusEnum.ENABLE.getCode(), systemRechargeChannelPOOld.getStatus())){
                systemRechargeChannelPOOld.setStatus(EnableStatusEnum.DISABLE.getCode());
                LambdaUpdateWrapper<SiteRechargeChannelPO> siteLqw = new LambdaUpdateWrapper<>();
                siteLqw.eq(SiteRechargeChannelPO::getChannelId,systemRechargeChannelStatusReqVO.getId());
                siteLqw.set(SiteRechargeChannelPO::getStatus,EnableStatusEnum.DISABLE.getCode());
                siteRechargeChannelRepository.update(null,siteLqw);
            }else {
                systemRechargeChannelPOOld.setStatus(EnableStatusEnum.ENABLE.getCode());
            }
            systemRechargeChannelPOOld.setUpdatedTime(System.currentTimeMillis());
            systemRechargeChannelPOOld.setUpdater(systemRechargeChannelStatusReqVO.getOperatorUserNo());
            this.baseMapper.updateById(systemRechargeChannelPOOld);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }

    /**
     * 增加授权数量
     * @param channelIds
     */
    public void addAuthNum(List<String> channelIds) {
        UpdateWrapper<SystemRechargeChannelPO> updateWrapper = new UpdateWrapper<SystemRechargeChannelPO>();
        updateWrapper.setSql(" auth_num = auth_num + 1");
        updateWrapper.in("id",channelIds);
        this.update(updateWrapper);
    }

    /**
     * 减少授权数量
     * @param channelIds
     */
    public void subAuthNum(List<String> channelIds) {
        UpdateWrapper<SystemRechargeChannelPO> updateWrapper = new UpdateWrapper<SystemRechargeChannelPO>();
        updateWrapper.setSql(" auth_num = auth_num - 1");
        updateWrapper.in("id",channelIds);
        this.update(updateWrapper);
    }

    public SystemRechargeChannelBaseVO getChannelInfo(ChannelQueryReqVO reqVO) {
        LambdaQueryWrapper<SystemRechargeChannelPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getChannelName()), SystemRechargeChannelPO::getChannelName, reqVO.getChannelName());
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getChannelCode()), SystemRechargeChannelPO::getChannelCode, reqVO.getChannelCode());
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getCurrencyCode()), SystemRechargeChannelPO::getCurrencyCode, reqVO.getCurrencyCode());
        queryWrapper.last("limit 1");
        SystemRechargeChannelPO po = systemRechargeChannelRepository.selectOne(queryWrapper);
        return ConvertUtil.entityToModel(po, SystemRechargeChannelBaseVO.class);
    }

    public SystemRechargeChannelBaseVO getChannelInfoByMerNo(String channelName, String merchantNo) {
        LambdaQueryWrapper<SystemRechargeChannelPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemRechargeChannelPO::getMerNo, merchantNo);
        queryWrapper.eq(SystemRechargeChannelPO::getChannelName, channelName);
        queryWrapper.last("limit 1");
        SystemRechargeChannelPO po = systemRechargeChannelRepository.selectOne(queryWrapper);
        return ConvertUtil.entityToModel(po, SystemRechargeChannelBaseVO.class);
    }

    public SystemRechargeChannelBaseVO getChannelInfoByCurrencyAneWayId(String currencyCode,String rechargeWayId,String siteCode,String channelId) {
        SystemRechargeChannelBaseVO systemRechargeChannelBaseVO = siteRechargeChannelRepository.getChannelInfoByCurrencyAneWayId(currencyCode,rechargeWayId,siteCode,channelId);
        return systemRechargeChannelBaseVO;
    }

    public SystemRechargeChannelBaseVO getChannelInfoByChannelId(String currencyCode,String rechargeWayId,String siteCode,String channelId) {
        SystemRechargeChannelBaseVO systemRechargeChannelBaseVO = siteRechargeChannelRepository.getChannelInfoByChannelId(currencyCode,rechargeWayId,siteCode,channelId);
        return systemRechargeChannelBaseVO;
    }


    public ResponseVO<Boolean> batchSave(String userAccount, List<SortNewReqVO> sortNewReqVOS) {
        List<SystemRechargeChannelPO> batchLists = Lists.newArrayList();
        for (SortNewReqVO sortNewReqVO : sortNewReqVOS) {
            SystemRechargeChannelPO systemRechargeChannelPO = new SystemRechargeChannelPO();
            systemRechargeChannelPO.setId(sortNewReqVO.getId());
            systemRechargeChannelPO.setSortOrder(sortNewReqVO.getSortOrder());
            //通道排序的目的就是为了保证权重 可视化编辑
            systemRechargeChannelPO.setWeight(sortNewReqVO.getSortOrder());
            systemRechargeChannelPO.setUpdatedTime(System.currentTimeMillis());
            systemRechargeChannelPO.setUpdater(userAccount);
            batchLists.add(systemRechargeChannelPO);
        }
        this.updateBatchById(batchLists);
        return ResponseVO.success();
    }

    public ResponseVO<List<SystemRechargeChannelRespVO>> selectAll() {
        LambdaQueryWrapper<SystemRechargeChannelPO> lqw = new LambdaQueryWrapper<SystemRechargeChannelPO>();
        lqw.orderByAsc(SystemRechargeChannelPO::getSortOrder);
        List<SystemRechargeChannelPO> systemRechargeChannelPOList =  this.baseMapper.selectList(lqw);
        List<SystemRechargeChannelRespVO> resultLists= Lists.newArrayList();
        if(!CollectionUtils.isEmpty(systemRechargeChannelPOList)){
            List<String> rechargeWayIds=systemRechargeChannelPOList.stream().map(SystemRechargeChannelPO::getRechargeWayId).toList();
            LambdaQueryWrapper<SystemRechargeWayPO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(SystemRechargeWayPO::getId,rechargeWayIds);
            List<SystemRechargeWayPO> systemRechargeWayPOS=systemRechargeWayRepository.selectList(lambdaQueryWrapper);
            List<VIPRankVO> vipRankList = vipRankApi.getVipRankList();
            Map<Integer, String> vipRankMap = vipRankList.stream()
                    .collect(Collectors.toMap(VIPRankVO::getVipRankCode, VIPRankVO::getVipRankNameI18nCode));

            for(SystemRechargeChannelPO systemRechargeChannelPO :systemRechargeChannelPOList){
                SystemRechargeChannelRespVO systemRechargeChannelRespVO=new SystemRechargeChannelRespVO();
                BeanUtils.copyProperties(systemRechargeChannelPO,systemRechargeChannelRespVO);
                String useScope = systemRechargeChannelRespVO.getUseScope();
                if (org.apache.commons.lang3.StringUtils.isNotBlank(useScope)) {
                    List<String> scope = Arrays.asList(useScope.split(CommonConstant.COMMA));
                    String result = scope.stream()
                            .map(Integer::valueOf) // 将字符串转换为 Integer
                            .filter(vipRankMap::containsKey) // 过滤出存在于 map 中的键
                            .map(vipRankMap::get) // 获取对应的名称
                            .map(I18nMessageUtil::getI18NMessageInAdvice) // 获取国际化名称
                            .collect(Collectors.joining(",")); // 用逗号拼接
                    systemRechargeChannelRespVO.setUseScopeText(result);
                }
                Optional<SystemRechargeWayPO> systemRechargeWayPOOptional=systemRechargeWayPOS.stream().filter(o->o.getId().equals(String.valueOf(systemRechargeChannelPO.getRechargeWayId()))).findFirst();
                systemRechargeWayPOOptional.ifPresent(systemRechargeWayPO -> systemRechargeChannelRespVO.setRechargeWayI18(systemRechargeWayPO.getRechargeWayI18()));
                resultLists.add(systemRechargeChannelRespVO);
            }
        }
        return   ResponseVO.success(resultLists);
    }
}
