package com.cloud.baowang.wallet.service;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WayFeeTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WithDrawCollectEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.I18nMsgBindUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayFeeVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithDrawWayCollectFieldVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayAddVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayDetailResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayResponseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayStatusVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayUpdateVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawCollectInfoVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawWayListVO;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawWayRequestVO;
import com.cloud.baowang.wallet.po.SiteWithdrawChannelPO;
import com.cloud.baowang.wallet.po.SiteWithdrawWayPO;
import com.cloud.baowang.wallet.po.SystemWithdrawChannelPO;
import com.cloud.baowang.wallet.po.SystemWithdrawTypePO;
import com.cloud.baowang.wallet.po.SystemWithdrawWayPO;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalPO;
import com.cloud.baowang.wallet.repositories.SiteWithdrawChannelRepository;
import com.cloud.baowang.wallet.repositories.SiteWithdrawWayRepository;
import com.cloud.baowang.wallet.repositories.SystemWithdrawChannelRepository;
import com.cloud.baowang.wallet.repositories.SystemWithdrawTypeRepository;
import com.cloud.baowang.wallet.repositories.SystemWithdrawWayRepository;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: qiqi
 **/
@Service
@Slf4j
public class SystemWithdrawWayService extends ServiceImpl<SystemWithdrawWayRepository, SystemWithdrawWayPO> {
    @Resource
    private I18nApi i18nApi;


    @Resource
    private SiteWithdrawWayRepository siteWithdrawWayRepository;



    @Resource
    private SystemWithdrawTypeRepository systemWithdrawTypeRepository;

    @Resource
    private SiteWithdrawChannelRepository siteWithdrawChannelRepository;


    @Resource
    private SystemWithdrawChannelRepository systemWithdrawChannelRepository;

    @Resource
    private SystemDictConfigApi systemDictConfigApi;

    @Resource
    private UserDepositWithdrawalRepository userDepositWithdrawalRepository;



    public ResponseVO<Page<SystemWithdrawWayResponseVO>> selectPage(SystemWithdrawWayRequestVO withdrawWayRequestVO) {
        Page<SystemWithdrawWayPO> page = new Page<SystemWithdrawWayPO>(withdrawWayRequestVO.getPageNumber(), withdrawWayRequestVO.getPageSize());
        LambdaQueryWrapper<SystemWithdrawWayPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.hasText(withdrawWayRequestVO.getCurrencyCode()),SystemWithdrawWayPO::getCurrencyCode, withdrawWayRequestVO.getCurrencyCode());
        lqw.like(StringUtils.hasText(withdrawWayRequestVO.getWithdrawTypeId()),SystemWithdrawWayPO::getWithdrawTypeId, withdrawWayRequestVO.getWithdrawTypeId());
        lqw.like(StringUtils.hasText(withdrawWayRequestVO.getWithdrawWay()),SystemWithdrawWayPO::getWithdrawWay, withdrawWayRequestVO.getWithdrawWay());
        lqw.eq(null !=withdrawWayRequestVO.getStatus(), SystemWithdrawWayPO::getStatus, withdrawWayRequestVO.getStatus());
        lqw.eq(null != withdrawWayRequestVO.getFeeType(),SystemWithdrawWayPO::getFeeType,withdrawWayRequestVO.getFeeType());
        lqw.orderByDesc(SystemWithdrawWayPO::getUpdatedTime);
        IPage<SystemWithdrawWayPO> systemWithdrawWayIPage =  this.baseMapper.selectPage(page,lqw);
        Page<SystemWithdrawWayResponseVO> systemWithdrawWayRespVOPage=new Page<SystemWithdrawWayResponseVO>(withdrawWayRequestVO.getPageNumber(), withdrawWayRequestVO.getPageSize());
        systemWithdrawWayRespVOPage.setTotal(systemWithdrawWayIPage.getTotal());
        systemWithdrawWayRespVOPage.setPages(systemWithdrawWayIPage.getPages());
        List<SystemWithdrawWayResponseVO> resultLists= Lists.newArrayList();
        if(!CollectionUtils.isEmpty(systemWithdrawWayIPage.getRecords())){
            List<String> rechargeTypeIds=systemWithdrawWayIPage.getRecords().stream().map(o->o.getWithdrawTypeId()).collect(Collectors.toUnmodifiableList());
            LambdaQueryWrapper<SystemWithdrawTypePO> systemWithdrawTypePOLambdaQueryWrapper=new LambdaQueryWrapper<>();
            systemWithdrawTypePOLambdaQueryWrapper.in(SystemWithdrawTypePO::getId,rechargeTypeIds);
            List<SystemWithdrawTypePO> systemWithdrawTypePOs=systemWithdrawTypeRepository.selectList(systemWithdrawTypePOLambdaQueryWrapper);

            for(SystemWithdrawWayPO systemWithdrawWayPO :systemWithdrawWayIPage.getRecords()){
                SystemWithdrawWayResponseVO systemWithdrawWayRespVO=new SystemWithdrawWayResponseVO();
                BeanUtils.copyProperties(systemWithdrawWayPO,systemWithdrawWayRespVO);
                Optional<SystemWithdrawTypePO> systemWithdrawTypePOOptional=systemWithdrawTypePOs.stream().filter(o->o.getId().equals(systemWithdrawWayRespVO.getWithdrawTypeId())).findFirst();
                if(systemWithdrawTypePOOptional.isPresent()){
                    systemWithdrawWayRespVO.setWithdrawTypeI18(systemWithdrawTypePOOptional.get().getWithdrawTypeI18());
                }
                systemWithdrawWayRespVO.setCollectFieldVOS(JSONArray.parseArray(systemWithdrawWayPO.getCollectInfo(), SystemWithDrawWayCollectFieldVO.class));
                resultLists.add(systemWithdrawWayRespVO);
            }
        }
        systemWithdrawWayRespVOPage.setRecords(resultLists);
        return ResponseVO.success(systemWithdrawWayRespVOPage);
    }

    public ResponseVO<Void> insert(SystemWithdrawWayAddVO systemWithdrawWayNewReqVO) {
        String quickAmountStr=systemWithdrawWayNewReqVO.getQuickAmount();
        if(StringUtils.hasText(quickAmountStr)){
            String[] quickAmountArray=quickAmountStr.split(",");
            for(int i=0;i<quickAmountArray.length-1;i++){
                String quickAmount=quickAmountArray[i];
                if(!NumberUtil.isNumber(quickAmount)){
                    //非数字格式
                    throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
                }
                BigDecimal quickAmountDecimal=new BigDecimal(quickAmount);
                if(quickAmountDecimal.compareTo(BigDecimal.ZERO)<=0){
                    //数字小于等于0
                    throw new BaowangDefaultException(ConstantsCode.PARAM_NOT_VALID);
                }
            }
        }
        LambdaQueryWrapper<SystemWithdrawTypePO> lambdaQueryWrapper=Wrappers.lambdaQuery(SystemWithdrawTypePO.class);
        lambdaQueryWrapper.eq(SystemWithdrawTypePO::getId,systemWithdrawWayNewReqVO.getWithdrawTypeId());
        SystemWithdrawTypePO systemWithdrawTypePO=systemWithdrawTypeRepository.selectOne(lambdaQueryWrapper);
        if(systemWithdrawTypePO==null){
            throw new BaowangDefaultException(ResultCode.WITHDRAW_TYPE_NOT_EXISTS);
        }
        checkParam(systemWithdrawTypePO.getWithdrawTypeCode(),systemWithdrawWayNewReqVO.getCollectFieldVOS(),systemWithdrawWayNewReqVO.getFeeType(),systemWithdrawWayNewReqVO.getWayFee(),systemWithdrawWayNewReqVO.getWayFeeFixedAmount());
        if(!checkWayUnique(systemWithdrawWayNewReqVO.getCurrencyCode(),systemWithdrawWayNewReqVO.getWithdrawTypeId(),systemWithdrawWayNewReqVO.getWithdrawWay(),null,systemWithdrawWayNewReqVO.getNetworkType())){
            return ResponseVO.fail(ResultCode.DATA_IS_EXIST);
        }

        //加密货币校验协议
        checkNetworkType(systemWithdrawTypePO.getWithdrawTypeCode(),systemWithdrawWayNewReqVO.getNetworkType());

        String withdrawWayI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.WITHDRAW_WAY.getCode());
        SystemWithdrawWayPO systemWithdrawWayPO =new SystemWithdrawWayPO();
        BeanUtils.copyProperties(systemWithdrawWayNewReqVO, systemWithdrawWayPO);
        systemWithdrawWayPO.setWithdrawTypeCode(systemWithdrawTypePO.getWithdrawTypeCode());
        systemWithdrawWayPO.setNetworkType(systemWithdrawWayNewReqVO.getNetworkType());
        systemWithdrawWayPO.setCollectInfo(JSONArray.toJSONString(systemWithdrawWayNewReqVO.getCollectFieldVOS()));
        systemWithdrawWayPO.setStatus(EnableStatusEnum.ENABLE.getCode());
        systemWithdrawWayPO.setCreator(systemWithdrawWayNewReqVO.getOperatorUserNo());
        systemWithdrawWayPO.setUpdater(systemWithdrawWayNewReqVO.getOperatorUserNo());
        systemWithdrawWayPO.setCreatedTime(System.currentTimeMillis());
        systemWithdrawWayPO.setUpdatedTime(System.currentTimeMillis());
        systemWithdrawWayPO.setWithdrawWayI18(withdrawWayI18Code);
        this.baseMapper.insert(systemWithdrawWayPO);
        //保存到多语言
        i18nApi.insert(I18nMsgBindUtil.bind(withdrawWayI18Code,systemWithdrawWayNewReqVO.getWithdrawWayI18List()));
        return ResponseVO.success();
    }

    private void checkNetworkType(String typeCode,String networkType){
        if(StringUtils.hasText(typeCode) && WithdrawTypeEnum.CRYPTO_CURRENCY.getCode().equals(typeCode)){
            if(!StringUtils.hasText(networkType)){
                throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
            }
        }
    }


    //唯一性校验 币种+类型+方式
    private boolean checkWayUnique(String currencyCode,String withdrawTypeId,String withdrawWay,String id,String networkType) {
        if(StringUtils.hasText(networkType)){
            LambdaQueryWrapper<SystemWithdrawWayPO> lqw = new LambdaQueryWrapper<SystemWithdrawWayPO>();
            lqw.eq(SystemWithdrawWayPO::getCurrencyCode, currencyCode);
            lqw.eq(SystemWithdrawWayPO::getWithdrawTypeId, withdrawTypeId);
            lqw.eq(SystemWithdrawWayPO::getNetworkType, networkType);
            lqw.ne(null != id, SystemWithdrawWayPO::getId, id);
            SystemWithdrawWayPO systemWithdrawWayPOSameNetwork = this.baseMapper.selectOne(lqw);
            if(systemWithdrawWayPOSameNetwork!=null){
                log.info("币种:{},网络:{}已存在",currencyCode,networkType);
                throw new BaowangDefaultException(ConstantsCode.DATA_IS_EXIST);
            }
        }
        LambdaQueryWrapper<SystemWithdrawWayPO> lqw = new LambdaQueryWrapper<SystemWithdrawWayPO>();
        lqw.eq(SystemWithdrawWayPO::getCurrencyCode, currencyCode);
        lqw.eq(SystemWithdrawWayPO::getWithdrawTypeId, withdrawTypeId);
        lqw.eq(SystemWithdrawWayPO::getWithdrawWay,withdrawWay);
        lqw.ne(null != id, SystemWithdrawWayPO::getId, id);
        List<SystemWithdrawWayPO> wayPOS = this.baseMapper.selectList(lqw);
        return wayPOS.isEmpty();
    }
    private void checkParam(String withdrawTypeCode,List<SystemWithDrawWayCollectFieldVO> collectFieldVOS,Integer feeType,BigDecimal percentageAmount,BigDecimal feeFixedAmount){
        if(WithdrawTypeEnum.ELECTRONIC_WALLET.getCode().equals(withdrawTypeCode)){
            boolean addressNoContains  = collectFieldVOS.stream().anyMatch(vo -> vo.getFiledCode().contains(WithDrawCollectEnum.ADDRESS_NO.getType()));
            boolean accountContains  = collectFieldVOS.stream().anyMatch(vo -> vo.getFiledCode().contains(WithDrawCollectEnum.USER_ACCOUNT.getType()));
            if(addressNoContains && accountContains){
                throw new BaowangDefaultException(ConstantsCode.ELECTRONIC_WALLET_COLLECT_SELECT_ONE);
            }
        }
        if(null == feeType){
            throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
        }else{
            if(WayFeeTypeEnum.PERCENTAGE.getCode().equals(feeType)){
                if(null == percentageAmount){
                    throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
                }

            }else if(WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(feeType)){
                if(null == feeFixedAmount){
                    throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
                }
            }else if(WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(feeType)){
                if(null == percentageAmount || null == feeFixedAmount){
                    throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
                }
            }else{
                throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
            }
        }
    }

    public ResponseVO<Void> updateByInfo(SystemWithdrawWayUpdateVO systemWithdrawWayUpdateReqVO) {
        LambdaQueryWrapper<SystemWithdrawTypePO> lambdaQueryWrapper=Wrappers.lambdaQuery(SystemWithdrawTypePO.class);
        lambdaQueryWrapper.eq(SystemWithdrawTypePO::getId,systemWithdrawWayUpdateReqVO.getWithdrawTypeId());
        SystemWithdrawTypePO systemWithdrawTypePO=systemWithdrawTypeRepository.selectOne(lambdaQueryWrapper);
        checkParam(systemWithdrawTypePO.getWithdrawTypeCode(),systemWithdrawWayUpdateReqVO.getCollectFieldVOS(),systemWithdrawWayUpdateReqVO.getFeeType(),systemWithdrawWayUpdateReqVO.getWayFee(),systemWithdrawWayUpdateReqVO.getWayFeeFixedAmount());
        LambdaQueryWrapper<SystemWithdrawWayPO> lqw = new LambdaQueryWrapper<SystemWithdrawWayPO>();
        lqw.eq(SystemWithdrawWayPO::getId, systemWithdrawWayUpdateReqVO.getId());
        SystemWithdrawWayPO systemWithdrawWayPOOld = this.baseMapper.selectOne(lqw);
        if(systemWithdrawWayPOOld !=null){
            if(!checkWayUnique(systemWithdrawWayUpdateReqVO.getCurrencyCode(),systemWithdrawWayUpdateReqVO.getWithdrawTypeId(),systemWithdrawWayUpdateReqVO.getWithdrawWay(),systemWithdrawWayUpdateReqVO.getId(),systemWithdrawWayUpdateReqVO.getNetworkType())){
                return ResponseVO.fail(ResultCode.DATA_IS_EXIST);
            }
            Integer oldFeeType = systemWithdrawWayPOOld.getFeeType();
            Integer newFeeType = systemWithdrawWayUpdateReqVO.getFeeType();

            //加密货币校验协议
            checkNetworkType(systemWithdrawTypePO.getWithdrawTypeCode(),systemWithdrawWayUpdateReqVO.getNetworkType());
            BeanUtils.copyProperties(systemWithdrawWayUpdateReqVO, systemWithdrawWayPOOld);
            systemWithdrawWayPOOld.setNetworkType(systemWithdrawWayUpdateReqVO.getNetworkType());
            systemWithdrawWayPOOld.setCollectInfo(JSONArray.toJSONString(systemWithdrawWayUpdateReqVO.getCollectFieldVOS()));
            systemWithdrawWayPOOld.setUpdater(systemWithdrawWayUpdateReqVO.getOperatorUserNo());
            systemWithdrawWayPOOld.setUpdatedTime(System.currentTimeMillis());
            systemWithdrawWayPOOld.setWithdrawTypeCode(systemWithdrawTypePO.getWithdrawTypeCode());
            setEntity(systemWithdrawWayPOOld,systemWithdrawWayUpdateReqVO);
            this.baseMapper.updateById(systemWithdrawWayPOOld);
            //如果修改费率类型，同步变更下发站点的类型 费率类型
            updateSiteRechargeWay(systemWithdrawWayUpdateReqVO.getId(),oldFeeType,newFeeType,
                    systemWithdrawWayUpdateReqVO.getWayFee(),systemWithdrawWayUpdateReqVO.getWayFeeFixedAmount());
            //保存到多语言
            if(!CollectionUtils.isEmpty(systemWithdrawWayUpdateReqVO.getWithdrawWayI18List())){
                i18nApi.update(I18nMsgBindUtil.bind(systemWithdrawWayPOOld.getWithdrawWayI18(),systemWithdrawWayUpdateReqVO.getWithdrawWayI18List()));
            }
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }
    private void setEntity(SystemWithdrawWayPO systemWithdrawWayPOOld,SystemWithdrawWayUpdateVO vo){
        if(WayFeeTypeEnum.PERCENTAGE.getCode().equals(vo.getFeeType())){
            systemWithdrawWayPOOld.setWayFee(vo.getWayFee());
            systemWithdrawWayPOOld.setWayFeeFixedAmount(BigDecimal.ZERO);
        }else if(WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(vo.getFeeType())){
            systemWithdrawWayPOOld.setWayFeeFixedAmount(vo.getWayFeeFixedAmount());
            systemWithdrawWayPOOld.setWayFee(BigDecimal.ZERO);
        }else if(WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(vo.getFeeType())){
            systemWithdrawWayPOOld.setWayFee(vo.getWayFee());
            systemWithdrawWayPOOld.setWayFeeFixedAmount(vo.getWayFeeFixedAmount());
        }
    }

    private void updateSiteRechargeWay(String withdrawWayId,Integer oldFeeType,Integer newFeeType,
                                       BigDecimal percentageAmount,BigDecimal feeFixedAmount){
        //查询方式关联的站点授权信息
        LambdaQueryWrapper<SiteWithdrawWayPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SiteWithdrawWayPO::getWithdrawId,withdrawWayId);
        if(!newFeeType.equals(oldFeeType)){

            LambdaUpdateWrapper<SiteWithdrawWayPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SiteWithdrawWayPO::getWithdrawId,withdrawWayId);
            updateWrapper.set(SiteWithdrawWayPO::getFeeType,newFeeType);
            if(WayFeeTypeEnum.PERCENTAGE.getCode().equals(oldFeeType)
                    && WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(newFeeType)){
                //百分比变为固定金额
                updateWrapper.set(SiteWithdrawWayPO::getWayFee,BigDecimal.ZERO);
                updateWrapper.set(SiteWithdrawWayPO::getWayFeeFixedAmount,feeFixedAmount);

            }else if(WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(oldFeeType)
                    && WayFeeTypeEnum.PERCENTAGE.getCode().equals(newFeeType)){
                //固定金额变为百分比
                updateWrapper.set(SiteWithdrawWayPO::getWayFee,percentageAmount);
                updateWrapper.set(SiteWithdrawWayPO::getWayFeeFixedAmount,BigDecimal.ZERO);
            }else if(WayFeeTypeEnum.PERCENTAGE.getCode().equals(oldFeeType)
                    && WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(newFeeType)){
                //百分比变为  百分比+固定金额
                updateWrapper.set(SiteWithdrawWayPO::getWayFeeFixedAmount,feeFixedAmount);

            }else if(WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(oldFeeType)
                    && WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(newFeeType)){
                //固定金额变为  百分比+固定金额
                updateWrapper.set(SiteWithdrawWayPO::getWayFee,percentageAmount);
            }else if(WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(oldFeeType)
                    && WayFeeTypeEnum.PERCENTAGE.getCode().equals(newFeeType)){
                //百分比+固定金额 变百分比
                updateWrapper.set(SiteWithdrawWayPO::getWayFeeFixedAmount,BigDecimal.ZERO);
            }else if(WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(oldFeeType)
                    && WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(newFeeType)){
                //百分比+固定金额 变固定金额
                updateWrapper.set(SiteWithdrawWayPO::getWayFee,BigDecimal.ZERO);
            }
            siteWithdrawWayRepository.update(null,updateWrapper);
        }
    }

    public ResponseVO<Void> enableOrDisable(SystemWithdrawWayStatusVO withdrawWayStatusVO) {
        LambdaQueryWrapper<SystemWithdrawWayPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SystemWithdrawWayPO::getId, withdrawWayStatusVO.getId());
        SystemWithdrawWayPO systemWithdrawWayPO= this.baseMapper.selectOne(lqw);
        if(systemWithdrawWayPO!=null){
            if(Objects.equals(EnableStatusEnum.ENABLE.getCode(), systemWithdrawWayPO.getStatus())){
                systemWithdrawWayPO.setStatus(EnableStatusEnum.DISABLE.getCode());
                LambdaUpdateWrapper<SiteWithdrawWayPO> siteLqw = new LambdaUpdateWrapper<>();
                siteLqw.eq(SiteWithdrawWayPO::getWithdrawId,withdrawWayStatusVO.getId());
                siteLqw.set(SiteWithdrawWayPO::getStatus,EnableStatusEnum.DISABLE.getCode());
                siteWithdrawWayRepository.update(null,siteLqw);
            }else {
                systemWithdrawWayPO.setStatus(EnableStatusEnum.ENABLE.getCode());
            }
            systemWithdrawWayPO.setUpdatedTime(System.currentTimeMillis());
            systemWithdrawWayPO.setUpdater(withdrawWayStatusVO.getOperatorUserNo());
            this.baseMapper.updateById(systemWithdrawWayPO);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }

    public ResponseVO<List<SystemWithdrawWayResponseVO>> selectAllValid() {
        LambdaQueryWrapper<SystemWithdrawWayPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SystemWithdrawWayPO::getStatus, EnableStatusEnum.ENABLE.getCode());
        List<SystemWithdrawWayPO> systemWithdrawWayList=this.baseMapper.selectList(lqw);
        List<SystemWithdrawWayResponseVO> resultLists= Lists.newArrayList();
        for(SystemWithdrawWayPO systemWithdrawWay:systemWithdrawWayList){
            SystemWithdrawWayResponseVO systemWithdrawWayRespVO=new SystemWithdrawWayResponseVO();
            BeanUtils.copyProperties(systemWithdrawWay,systemWithdrawWayRespVO);
            systemWithdrawWayRespVO.setCollectFieldVOS(JSONArray.parseArray(systemWithdrawWay.getCollectInfo(), SystemWithDrawWayCollectFieldVO.class));
            resultLists.add(systemWithdrawWayRespVO);
        }
        return ResponseVO.success(resultLists);
    }

    public ResponseVO<List<SystemWithdrawWayResponseVO>> selectAll() {
        LambdaQueryWrapper<SystemWithdrawWayPO> lqw = new LambdaQueryWrapper<>();
        List<SystemWithdrawWayPO> systemWithdrawWayList=this.baseMapper.selectList(lqw);
        List<SystemWithdrawWayResponseVO> resultLists= Lists.newArrayList();
        for(SystemWithdrawWayPO systemWithdrawWay:systemWithdrawWayList){
            SystemWithdrawWayResponseVO systemWithdrawWayRespVO=new SystemWithdrawWayResponseVO();
            BeanUtils.copyProperties(systemWithdrawWay,systemWithdrawWayRespVO);
            systemWithdrawWayRespVO.setCollectFieldVOS(JSONArray.parseArray(systemWithdrawWay.getCollectInfo(), SystemWithDrawWayCollectFieldVO.class));
            resultLists.add(systemWithdrawWayRespVO);
        }
        return ResponseVO.success(resultLists);
    }


    public List<WithdrawWayListVO> agentWithdrawWayList(WithdrawWayRequestVO withdrawWayRequestVO) {
        List<WithdrawWayListVO> withdrawWayListVOS= new ArrayList<>();
        List<SystemWithdrawWayPO> withdrawWayPOS = this.siteWithdrawWayRepository.selectFrontWayList(withdrawWayRequestVO);
        if(null == withdrawWayPOS || withdrawWayPOS.isEmpty()){
            return withdrawWayListVOS;
        }
        for (SystemWithdrawWayPO systemWithdrawWayPO:withdrawWayPOS) {
            WithdrawWayListVO withdrawWayListVO = new WithdrawWayListVO();
            BeanUtils.copyProperties(systemWithdrawWayPO,withdrawWayListVO);
            withdrawWayListVO.setWithdrawWay(systemWithdrawWayPO.getWithdrawWayI18());
            withdrawWayListVOS.add(withdrawWayListVO);

        }
        return withdrawWayListVOS;
    }

    public List<WithdrawWayListVO> withdrawWayList(WithdrawWayRequestVO withdrawWayRequestVO) {
        List<WithdrawWayListVO> withdrawWayListVOS= new ArrayList<>();
        List<SystemWithdrawWayPO> withdrawWayPOS = this.siteWithdrawWayRepository.selectFrontWayList(withdrawWayRequestVO);
        if(null == withdrawWayPOS || withdrawWayPOS.isEmpty()){
            return withdrawWayListVOS;
        }
        //如果是大陆盘，并且首笔提款仅限法币开关开启 只返回银行卡
        if(SiteHandicapModeEnum.China.getCode().equals(CurrReqUtils.getHandicapMode())){
            ResponseVO<SystemDictConfigRespVO> responseVO = systemDictConfigApi.getByCode(DictCodeConfigEnums.FIRST_WITHDRAW_ONLY_FIAT_CURRENCY.getCode(),withdrawWayRequestVO.getSiteCode());
            if(null != responseVO.getData() && YesOrNoEnum.YES.getCode().equals(responseVO.getData().getConfigParam())){

                LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
                lqw.eq(UserDepositWithdrawalPO::getUserId,CurrReqUtils.getOneId());
                lqw.eq(UserDepositWithdrawalPO::getType, DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
                lqw.eq(UserDepositWithdrawalPO::getDepositWithdrawTypeCode,WithdrawTypeEnum.BANK_CARD.getCode());
                lqw.eq(UserDepositWithdrawalPO::getStatus, DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
                List<UserDepositWithdrawalPO> userDepositWithdrawalPOS = userDepositWithdrawalRepository.selectList(lqw);
                if(userDepositWithdrawalPOS.size() == 0){
                    withdrawWayPOS = withdrawWayPOS.stream().filter(obj -> obj.getWithdrawTypeCode()
                            .equals(WithdrawTypeEnum.BANK_CARD.getCode())).collect(Collectors.toList());
                }
            }
        }
        for (SystemWithdrawWayPO systemWithdrawWayPO:withdrawWayPOS) {
            WithdrawWayListVO withdrawWayListVO = new WithdrawWayListVO();
            BeanUtils.copyProperties(systemWithdrawWayPO,withdrawWayListVO);
            withdrawWayListVO.setWithdrawWay(systemWithdrawWayPO.getWithdrawWayI18());
            withdrawWayListVOS.add(withdrawWayListVO);

        }
        return withdrawWayListVOS;
    }



    public ResponseVO<SystemWithdrawWayDetailResponseVO> info(IdReqVO idReqVO) {
        LambdaQueryWrapper<SystemWithdrawWayPO> lqw = new LambdaQueryWrapper<SystemWithdrawWayPO>();
        lqw.eq(SystemWithdrawWayPO::getId, idReqVO.getId());
        SystemWithdrawWayPO systemWithdrawWayPOOld = this.baseMapper.selectOne(lqw);
        if(systemWithdrawWayPOOld!=null){
            SystemWithdrawWayDetailResponseVO systemWithdrawWayDetailResponseVO=new SystemWithdrawWayDetailResponseVO();
            BeanUtils.copyProperties(systemWithdrawWayPOOld,systemWithdrawWayDetailResponseVO);
            systemWithdrawWayDetailResponseVO.setFeeType(String.valueOf(systemWithdrawWayPOOld.getFeeType()));
            systemWithdrawWayDetailResponseVO.setCollectFieldVOS(JSONArray.parseArray(systemWithdrawWayPOOld.getCollectInfo(), SystemWithDrawWayCollectFieldVO.class));
            return ResponseVO.success(systemWithdrawWayDetailResponseVO);
        }
        return ResponseVO.success();
    }

    public ResponseVO<SystemWithdrawWayDetailResponseVO> getInfoById(IdReqVO idReqVO) {
        LambdaQueryWrapper<SystemWithdrawWayPO> lqw = new LambdaQueryWrapper<SystemWithdrawWayPO>();
        lqw.eq(SystemWithdrawWayPO::getId, idReqVO.getId());
        SystemWithdrawWayPO systemWithdrawWayPOOld = this.baseMapper.selectOne(lqw);
        if(systemWithdrawWayPOOld!=null){
            SystemWithdrawWayDetailResponseVO systemWithdrawWayDetailResponseVO=new SystemWithdrawWayDetailResponseVO();
            BeanUtils.copyProperties(systemWithdrawWayPOOld,systemWithdrawWayDetailResponseVO);
            systemWithdrawWayDetailResponseVO.setCollectFieldVOS(JSONArray.parseArray(systemWithdrawWayPOOld.getCollectInfo(), SystemWithDrawWayCollectFieldVO.class));
            return ResponseVO.success(systemWithdrawWayDetailResponseVO);
        }
        return ResponseVO.success();
    }

    public ResponseVO<List<SystemWithdrawWayResponseVO>> selectBySort(SystemWithdrawWayRequestVO systemWithdrawWayRequestVO) {
        LambdaQueryWrapper<SystemWithdrawWayPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SystemWithdrawWayPO::getCurrencyCode,systemWithdrawWayRequestVO.getCurrencyCode());
        lqw.orderByAsc(SystemWithdrawWayPO::getSortOrder);
        List<SystemWithdrawWayPO> systemWithdrawWayList=this.baseMapper.selectList(lqw);
        List<SystemWithdrawWayResponseVO> resultLists= Lists.newArrayList();
        for(SystemWithdrawWayPO systemWithdrawWay:systemWithdrawWayList){
            SystemWithdrawWayResponseVO systemWithdrawWayRespVO=new SystemWithdrawWayResponseVO();
            BeanUtils.copyProperties(systemWithdrawWay,systemWithdrawWayRespVO);
            systemWithdrawWayRespVO.setCollectFieldVOS(JSONArray.parseArray(systemWithdrawWay.getCollectInfo(), SystemWithDrawWayCollectFieldVO.class));
            resultLists.add(systemWithdrawWayRespVO);
        }
        return ResponseVO.success(resultLists);
    }

    public ResponseVO<Boolean> batchSave(String userAccount, List<SortNewReqVO> sortNewReqVOS) {
        List<SystemWithdrawWayPO> batchLists = Lists.newArrayList();
        for (SortNewReqVO sortNewReqVO : sortNewReqVOS) {
            SystemWithdrawWayPO systemWithdrawWayPO = new SystemWithdrawWayPO();
            systemWithdrawWayPO.setId(sortNewReqVO.getId());
            systemWithdrawWayPO.setSortOrder(sortNewReqVO.getSortOrder());
            systemWithdrawWayPO.setUpdatedTime(System.currentTimeMillis());
            systemWithdrawWayPO.setUpdater(userAccount);
            batchLists.add(systemWithdrawWayPO);
        }
        this.updateBatchById(batchLists);
        return ResponseVO.success();
    }


    public SiteWithdrawWayFeeVO calculateSiteWithdrawWayFeeRate(String siteCode, String withdrawWayId, BigDecimal amount){
        LambdaQueryWrapper<SiteWithdrawWayPO> siteLwq = new LambdaQueryWrapper<>();
        siteLwq.eq(SiteWithdrawWayPO::getSiteCode, siteCode);
        siteLwq.eq(SiteWithdrawWayPO::getStatus, CommonConstant.business_one);
        siteLwq.eq(SiteWithdrawWayPO::getWithdrawId,withdrawWayId);
        siteLwq.last("limit 1");
        SiteWithdrawWayPO siteWithdrawWayPO = siteWithdrawWayRepository.selectOne(siteLwq);
        SiteWithdrawWayFeeVO siteWithdrawWayFeeVO = new SiteWithdrawWayFeeVO();
        if(null == siteWithdrawWayPO){
            siteWithdrawWayFeeVO.setWayFeeFixedAmount(BigDecimal.ZERO);
            siteWithdrawWayFeeVO.setWayFee(BigDecimal.ZERO);
            siteWithdrawWayFeeVO.setWayFeeAmount(BigDecimal.ZERO);
            siteWithdrawWayFeeVO.setWayFeePercentageAmount(BigDecimal.ZERO);
            return siteWithdrawWayFeeVO;
        }
        BigDecimal wayFeeAmount = BigDecimal.ZERO;
        BigDecimal  settlementFeeRate = null == siteWithdrawWayPO.getWayFee()?BigDecimal.ZERO: siteWithdrawWayPO.getWayFee();
        Integer wayFeeType = siteWithdrawWayPO.getFeeType();
        BigDecimal percentageAmount = amount.multiply(settlementFeeRate.divide(new BigDecimal("100"))).setScale(2, RoundingMode.DOWN);
        BigDecimal fixedAmount = null == siteWithdrawWayPO.getWayFeeFixedAmount()?BigDecimal.ZERO:siteWithdrawWayPO.getWayFeeFixedAmount();
        if(WayFeeTypeEnum.PERCENTAGE.getCode().equals(siteWithdrawWayPO.getFeeType())){
            wayFeeAmount = percentageAmount;
        }else if (WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(siteWithdrawWayPO.getFeeType())){
            wayFeeAmount = fixedAmount;
        }else if(WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(siteWithdrawWayPO.getFeeType())){
            wayFeeAmount = percentageAmount.add(fixedAmount);
        }
        siteWithdrawWayFeeVO.setWayFeePercentageAmount(percentageAmount);
        siteWithdrawWayFeeVO.setFeeType(wayFeeType);
        siteWithdrawWayFeeVO.setWayFeeFixedAmount(fixedAmount);
        siteWithdrawWayFeeVO.setWayFee(settlementFeeRate);
        siteWithdrawWayFeeVO.setWayFeeAmount(wayFeeAmount);

        return siteWithdrawWayFeeVO;
    }

    public Map<String, List<SystemWithdrawChannelResponseVO>> getChannelGroup(String siteCode, String vipRank) {
        LambdaQueryWrapper<SiteWithdrawChannelPO> siteChannelLqw = new LambdaQueryWrapper<>();
        siteChannelLqw.eq(SiteWithdrawChannelPO::getSiteCode, siteCode);
        siteChannelLqw.eq(SiteWithdrawChannelPO::getStatus, CommonConstant.business_one);
        siteChannelLqw.select(SiteWithdrawChannelPO::getChannelId);
        List<Object> siteChannelIds = siteWithdrawChannelRepository.selectObjs(siteChannelLqw);
        Map<String, List<SystemWithdrawChannelResponseVO>> channelGroup = new HashMap<>();
        if (null != siteChannelIds && !siteChannelIds.isEmpty()) {
            LambdaQueryWrapper<SystemWithdrawChannelPO> channelLqw = new LambdaQueryWrapper<>();
            channelLqw.eq(SystemWithdrawChannelPO::getStatus, CommonConstant.business_one);
            channelLqw.like(null != vipRank, SystemWithdrawChannelPO::getUseScope, vipRank);
            channelLqw.in(SystemWithdrawChannelPO::getId, siteChannelIds);
            List<SystemWithdrawChannelPO> channelPOList = systemWithdrawChannelRepository.selectList(channelLqw);
            List<SystemWithdrawChannelResponseVO> list = ConvertUtil.entityListToModelList(channelPOList, SystemWithdrawChannelResponseVO.class);
            channelGroup = list.stream()
                    .collect(Collectors.groupingBy(SystemWithdrawChannelResponseVO::getWithdrawWayId));
        }
        return channelGroup;
    }

    public List<WithdrawCollectInfoVO> getWithdrawCollectInfoList(String siteCode,String withdrawTypeCode,String currencyCode) {
       List<String> collectInfos = this.siteWithdrawWayRepository.getWithdrawCollectInfoList(siteCode,withdrawTypeCode,currencyCode);
       Map<String,WithdrawCollectInfoVO> map = new HashMap<>();
        for (String collectInfo:collectInfos) {
            List<WithdrawCollectInfoVO> list = JSONArray.parseArray(collectInfo, WithdrawCollectInfoVO.class);
            Map<String,WithdrawCollectInfoVO> detailMap =  list.stream().collect(Collectors
                    .toMap(WithdrawCollectInfoVO::getFiledCode, Function.identity(), (k1, k2) -> k2));

            map.putAll(detailMap);
        }
        List<WithdrawCollectInfoVO> withdrawCollectInfoVOS  = map.entrySet().stream().map(m ->m.getValue()).collect(Collectors.toList());

        return withdrawCollectInfoVOS;

    }
}
