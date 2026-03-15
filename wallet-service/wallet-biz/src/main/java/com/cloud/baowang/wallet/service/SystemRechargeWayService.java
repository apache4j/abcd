package com.cloud.baowang.wallet.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.I18nMsgBindUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.user.api.api.vip.SiteVipOptionApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipOptionVO;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.enums.wallet.RechargeTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WayFeeTypeEnum;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.*;
import com.cloud.baowang.wallet.api.vo.userCoin.SystemRechargeWayVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.wallet.po.SiteRechargeWayPO;
import com.cloud.baowang.wallet.po.SystemRechargeTypePO;
import com.cloud.baowang.wallet.po.SystemRechargeWayPO;
import com.cloud.baowang.wallet.repositories.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Desciption: 充值方式
 * @Author: Ford
 * @Date: 2024/7/26 11:51
 * @Version: V1.0
 **/
@Service
@Slf4j
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SystemRechargeWayService extends ServiceImpl<SystemRechargeWayRepository, SystemRechargeWayPO> {

    @Resource
    private I18nApi i18nApi;

    @Resource
    private SystemRechargeChannelRepository channelRepository;

    @Resource
    private SiteRechargeWayRepository siteRechargeWayRepository;

    @Resource
    private SystemRechargeTypeRepository systemRechargeTypeRepository;

    @Resource
    private SiteRechargeChannelRepository siteRechargeChannelRepository;
    @Resource
    private UserCoinService userCoinService;


    @Resource
    private SystemRechargeWayService _this;

    @Resource
    private LanguageManagerApi languageManagerApi;
    @Qualifier("com.cloud.baowang.wallet.api.api.UserCoinApi")
    @Autowired
    private UserCoinApi userCoinApi;


    @Resource
    private  SiteVipOptionApi siteVipOptionApi;


    public ResponseVO<List<SystemRechargeWayRespVO>> selectAllValid() {
        List<SystemRechargeWayRespVO> systemRechargeWayPOList = _this.selectAll().getData();
        systemRechargeWayPOList = systemRechargeWayPOList.stream().filter(o -> o.getStatus().equals(EnableStatusEnum.ENABLE.getCode())).toList();
        return ResponseVO.success(systemRechargeWayPOList);
    }

    @Cacheable(value = CacheConstants.RECHARGE_WAY_CACHE, key = CacheConstants.LIST, sync = true)
    public ResponseVO<List<SystemRechargeWayRespVO>> selectAll() {
        LambdaQueryWrapper<SystemRechargeWayPO> lqw = new LambdaQueryWrapper<SystemRechargeWayPO>();
        List<SystemRechargeWayPO> systemRechargeWayPOList = this.baseMapper.selectList(lqw);
        List<SystemRechargeWayRespVO> resultLists = Lists.newArrayList();
        for (SystemRechargeWayPO systemRechargeWayPO : systemRechargeWayPOList) {
            SystemRechargeWayRespVO systemRechargeWayRespVO = new SystemRechargeWayRespVO();
            BeanUtils.copyProperties(systemRechargeWayPO, systemRechargeWayRespVO);
            resultLists.add(systemRechargeWayRespVO);
        }
        return ResponseVO.success(resultLists);
    }

    public ResponseVO<List<SystemRechargeWayRespVO>> selectBySort(SystemRechargeWayReqVO systemRechargeWayReqVO) {
        LambdaQueryWrapper<SystemRechargeWayPO> lqw = new LambdaQueryWrapper<SystemRechargeWayPO>();
        lqw.eq(SystemRechargeWayPO::getCurrencyCode, systemRechargeWayReqVO.getCurrencyCode());
        lqw.orderByAsc(SystemRechargeWayPO::getSortOrder);
        List<SystemRechargeWayPO> systemRechargeWayPOList = this.baseMapper.selectList(lqw);
        List<SystemRechargeWayRespVO> resultLists = Lists.newArrayList();
        for (SystemRechargeWayPO systemRechargeWayPO : systemRechargeWayPOList) {
            SystemRechargeWayRespVO systemRechargeWayRespVO = new SystemRechargeWayRespVO();
            BeanUtils.copyProperties(systemRechargeWayPO, systemRechargeWayRespVO);
            resultLists.add(systemRechargeWayRespVO);
        }
        return ResponseVO.success(resultLists);
    }


    public ResponseVO<Page<SystemRechargeWayRespVO>> selectPage(SystemRechargeWayReqVO systemRechargeWayReqVO) {
        Page<SystemRechargeWayPO> page = new Page<SystemRechargeWayPO>(systemRechargeWayReqVO.getPageNumber(), systemRechargeWayReqVO.getPageSize());
        LambdaQueryWrapper<SystemRechargeWayPO> lqw = new LambdaQueryWrapper<SystemRechargeWayPO>();
        if (StringUtils.hasText(systemRechargeWayReqVO.getCurrencyCode())) {
            lqw.eq(SystemRechargeWayPO::getCurrencyCode, systemRechargeWayReqVO.getCurrencyCode());
        }
        if (StringUtils.hasText(systemRechargeWayReqVO.getRechargeTypeId())) {
            lqw.like(SystemRechargeWayPO::getRechargeTypeId, systemRechargeWayReqVO.getRechargeTypeId());
        }
        if (StringUtils.hasText(systemRechargeWayReqVO.getRechargeWay())) {
            lqw.like(SystemRechargeWayPO::getRechargeWay, systemRechargeWayReqVO.getRechargeWay());
        }
        if (systemRechargeWayReqVO.getStatus() != null) {
            lqw.eq(SystemRechargeWayPO::getStatus, systemRechargeWayReqVO.getStatus());
        }
        lqw.eq(null != systemRechargeWayReqVO.getFeeType(), SystemRechargeWayPO::getFeeType, systemRechargeWayReqVO.getFeeType());
        lqw.orderByDesc(SystemRechargeWayPO::getUpdatedTime);
        IPage<SystemRechargeWayPO> systemRechargeWayIPage = this.baseMapper.selectPage(page, lqw);
        Page<SystemRechargeWayRespVO> systemRechargeWayRespVOPage = new Page<SystemRechargeWayRespVO>(systemRechargeWayReqVO.getPageNumber(), systemRechargeWayReqVO.getPageSize());
        systemRechargeWayRespVOPage.setTotal(systemRechargeWayIPage.getTotal());
        systemRechargeWayRespVOPage.setPages(systemRechargeWayIPage.getPages());
        List<SystemRechargeWayRespVO> resultLists = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(systemRechargeWayIPage.getRecords())) {
            List<String> rechargeTypeIds = systemRechargeWayIPage.getRecords().stream().map(SystemRechargeWayPO::getRechargeTypeId).toList();
            LambdaQueryWrapper<SystemRechargeTypePO> systemRechargeTypePOLambdaQueryWrapper = new LambdaQueryWrapper<>();
            systemRechargeTypePOLambdaQueryWrapper.in(SystemRechargeTypePO::getId, rechargeTypeIds);
            List<SystemRechargeTypePO> systemRechargeTypePOs = systemRechargeTypeRepository.selectList(systemRechargeTypePOLambdaQueryWrapper);

            for (SystemRechargeWayPO systemRechargeWayPO : systemRechargeWayIPage.getRecords()) {
                SystemRechargeWayRespVO systemRechargeWayRespVO = new SystemRechargeWayRespVO();
                BeanUtils.copyProperties(systemRechargeWayPO, systemRechargeWayRespVO);
                Optional<SystemRechargeTypePO> systemRechargeTypePOOptional = systemRechargeTypePOs.stream().filter(o -> o.getId().equals(systemRechargeWayRespVO.getRechargeTypeId())).findFirst();
                systemRechargeTypePOOptional.ifPresent(systemRechargeTypePO -> systemRechargeWayRespVO.setRechargeTypeI18(systemRechargeTypePO.getRechargeTypeI18()));
                resultLists.add(systemRechargeWayRespVO);
            }
        }
        systemRechargeWayRespVOPage.setRecords(resultLists);
        return ResponseVO.success(systemRechargeWayRespVOPage);
    }

    public ResponseVO<SystemRechargeWayDetailRespVO> info(IdReqVO idReqVO) {
        LambdaQueryWrapper<SystemRechargeWayPO> lqw = new LambdaQueryWrapper<SystemRechargeWayPO>();
        lqw.eq(SystemRechargeWayPO::getId, idReqVO.getId());
        SystemRechargeWayPO systemRechargeWayPO = this.baseMapper.selectOne(lqw);
        if (systemRechargeWayPO != null) {
            SystemRechargeWayDetailRespVO systemRechargeWayDetailRespVO = new SystemRechargeWayDetailRespVO();
            BeanUtils.copyProperties(systemRechargeWayPO, systemRechargeWayDetailRespVO);
            systemRechargeWayDetailRespVO.setFeeType(String.valueOf(systemRechargeWayPO.getFeeType()));
            return ResponseVO.success(systemRechargeWayDetailRespVO);
        }
        return ResponseVO.success();
    }

    public ResponseVO<SystemRechargeWayDetailRespVO> getInfoById(IdReqVO idReqVO) {
        LambdaQueryWrapper<SystemRechargeWayPO> lqw = new LambdaQueryWrapper<SystemRechargeWayPO>();
        lqw.eq(SystemRechargeWayPO::getId, idReqVO.getId());
        SystemRechargeWayPO systemRechargeWayPO = this.baseMapper.selectOne(lqw);
        if (systemRechargeWayPO != null) {
            SystemRechargeWayDetailRespVO systemRechargeWayDetailRespVO = new SystemRechargeWayDetailRespVO();
            BeanUtils.copyProperties(systemRechargeWayPO, systemRechargeWayDetailRespVO);
            return ResponseVO.success(systemRechargeWayDetailRespVO);
        }
        return ResponseVO.success();
    }

    @CacheEvict(value = CacheConstants.RECHARGE_WAY_CACHE, key = CacheConstants.LIST)
    public ResponseVO<Void> insert(SystemRechargeWayNewReqVO systemRechargeWayNewReqVO) {
        String quickAmountStr = systemRechargeWayNewReqVO.getQuickAmount();
        if (StringUtils.hasText(quickAmountStr)) {
            String[] quickAmountArray = quickAmountStr.split(",");
            for (int i = 0; i < quickAmountArray.length - 1; i++) {
                String quickAmount = quickAmountArray[i];
                if (!NumberUtil.isNumber(quickAmount)) {
                    //非数字格式
                    throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
                }
                BigDecimal quickAmountDecimal = new BigDecimal(quickAmount);
                if (quickAmountDecimal.compareTo(BigDecimal.ZERO) <= 0) {
                    //数字小于等于0
                    throw new BaowangDefaultException(ConstantsCode.PARAM_NOT_VALID);
                }
            }
        }
        checkParam(systemRechargeWayNewReqVO.getFeeType(), systemRechargeWayNewReqVO.getWayFee(), systemRechargeWayNewReqVO.getWayFeeFixedAmount());
        //必须传全部语言
        List<LanguageManagerListVO> languageManagerListVOS = languageManagerApi.list().getData();
        Set<String> reqLangSet = systemRechargeWayNewReqVO.getRechargeWayI18List().stream().map(I18nMsgFrontVO::getLanguage).collect(Collectors.toSet());
        if (languageManagerListVOS.size() != reqLangSet.size()) {
            return ResponseVO.fail(ResultCode.LANG_NOT_FULL);
        }
        if (!checkWayUnique(systemRechargeWayNewReqVO.getCurrencyCode(), systemRechargeWayNewReqVO.getRechargeTypeId(), systemRechargeWayNewReqVO.getRechargeWay(), null, systemRechargeWayNewReqVO.getNetworkType())) {
            return ResponseVO.fail(ResultCode.DATA_IS_EXIST);
        }

        LambdaQueryWrapper<SystemRechargeTypePO> lambdaQueryWrapper = Wrappers.lambdaQuery(SystemRechargeTypePO.class);
        lambdaQueryWrapper.eq(SystemRechargeTypePO::getId, systemRechargeWayNewReqVO.getRechargeTypeId());
        SystemRechargeTypePO systemRechargeTypePO = systemRechargeTypeRepository.selectOne(lambdaQueryWrapper);
        //加密货币校验协议
        checkNetworkType(systemRechargeTypePO.getRechargeCode(), systemRechargeWayNewReqVO.getNetworkType());
        String rechargeWayI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.RECHARGE_WAY.getCode());
        SystemRechargeWayPO systemRechargeWayPO = new SystemRechargeWayPO();
        BeanUtils.copyProperties(systemRechargeWayNewReqVO, systemRechargeWayPO);
        systemRechargeWayPO.setRechargeTypeCode(systemRechargeTypePO.getRechargeCode());
        systemRechargeWayPO.setStatus(EnableStatusEnum.ENABLE.getCode());
        systemRechargeWayPO.setCreator(systemRechargeWayNewReqVO.getOperatorUserNo());
        systemRechargeWayPO.setUpdater(systemRechargeWayNewReqVO.getOperatorUserNo());
        systemRechargeWayPO.setCreatedTime(System.currentTimeMillis());
        systemRechargeWayPO.setUpdatedTime(System.currentTimeMillis());
        systemRechargeWayPO.setRechargeWayI18(rechargeWayI18Code);
        this.baseMapper.insert(systemRechargeWayPO);
        //保存到多语言
        i18nApi.insert(I18nMsgBindUtil.bind(rechargeWayI18Code, systemRechargeWayNewReqVO.getRechargeWayI18List()));
        return ResponseVO.success();
    }

    private void checkNetworkType(String typeCode, String networkType) {
        if (StringUtils.hasText(typeCode) && RechargeTypeEnum.CRYPTO_CURRENCY.getCode().equals(typeCode)) {
            if (!StringUtils.hasText(networkType)) {
                throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
            }
        }
    }

    private void checkParam(Integer feeType, BigDecimal percentageAmount, BigDecimal feeFixedAmount) {
        if (null == feeType) {
            throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
        } else {
            if (WayFeeTypeEnum.PERCENTAGE.getCode().equals(feeType)) {
                if (null == percentageAmount) {
                    throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
                }

            } else if (WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(feeType)) {
                if (null == feeFixedAmount) {
                    throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
                }
            } else if (WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(feeType)) {
                if (null == percentageAmount || null == feeFixedAmount) {
                    throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
                }
            } else {
                throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
            }
        }
    }

    /**
     * 唯一性校验 币种+类型+方式
     */
    private boolean checkWayUnique(String currencyCode, String rechargeTypeId, String rechargeWay, String id, String networkType) {
        if (StringUtils.hasText(networkType)) {
            LambdaQueryWrapper<SystemRechargeWayPO> lqw = new LambdaQueryWrapper<SystemRechargeWayPO>();
            lqw.eq(SystemRechargeWayPO::getCurrencyCode, currencyCode);
            lqw.eq(SystemRechargeWayPO::getRechargeTypeId, rechargeTypeId);
            lqw.eq(SystemRechargeWayPO::getNetworkType, networkType);
            lqw.ne(null != id, SystemRechargeWayPO::getId, id);
            SystemRechargeWayPO systemRechargeWaySameNetwork = this.baseMapper.selectOne(lqw);
            if (systemRechargeWaySameNetwork != null) {
                log.info("币种:{},网络:{}已存在", currencyCode, networkType);
                throw new BaowangDefaultException(ConstantsCode.DATA_IS_EXIST);
            }
        }
        LambdaQueryWrapper<SystemRechargeWayPO> lqw = new LambdaQueryWrapper<SystemRechargeWayPO>();
        lqw.eq(SystemRechargeWayPO::getCurrencyCode, currencyCode);
        lqw.eq(SystemRechargeWayPO::getRechargeTypeId, rechargeTypeId);
        lqw.eq(SystemRechargeWayPO::getRechargeWay, rechargeWay);
        lqw.ne(null != id, SystemRechargeWayPO::getId, id);
        List<SystemRechargeWayPO> wayPOS = this.baseMapper.selectList(lqw);
        return wayPOS.isEmpty();
    }

    @CacheEvict(value = CacheConstants.RECHARGE_WAY_CACHE, key = CacheConstants.LIST)
    public ResponseVO<Void> updateByInfo(SystemRechargeWayUpdateReqVO systemRechargeWayUpdateReqVO) {
        checkParam(systemRechargeWayUpdateReqVO.getFeeType(), systemRechargeWayUpdateReqVO.getWayFee(), systemRechargeWayUpdateReqVO.getWayFeeFixedAmount());
        LambdaQueryWrapper<SystemRechargeWayPO> lqw = new LambdaQueryWrapper<SystemRechargeWayPO>();
        lqw.eq(SystemRechargeWayPO::getId, systemRechargeWayUpdateReqVO.getId());
        SystemRechargeWayPO systemRechargeWayPOOld = this.baseMapper.selectOne(lqw);
        if (systemRechargeWayPOOld != null) {
            if (!checkWayUnique(systemRechargeWayUpdateReqVO.getCurrencyCode(), systemRechargeWayUpdateReqVO.getRechargeTypeId(), systemRechargeWayUpdateReqVO.getRechargeWay(), systemRechargeWayUpdateReqVO.getId(), systemRechargeWayUpdateReqVO.getNetworkType())) {
                return ResponseVO.fail(ResultCode.DATA_IS_EXIST);
            }
            Integer oldFeeType = systemRechargeWayPOOld.getFeeType();
            Integer newFeeType = systemRechargeWayUpdateReqVO.getFeeType();
            LambdaQueryWrapper<SystemRechargeTypePO> lambdaQueryWrapper = Wrappers.lambdaQuery(SystemRechargeTypePO.class);
            lambdaQueryWrapper.eq(SystemRechargeTypePO::getId, systemRechargeWayUpdateReqVO.getRechargeTypeId());
            SystemRechargeTypePO systemRechargeTypePO = systemRechargeTypeRepository.selectOne(lambdaQueryWrapper);
            //加密货币校验协议
            checkNetworkType(systemRechargeTypePO.getRechargeCode(), systemRechargeWayUpdateReqVO.getNetworkType());

            BeanUtils.copyProperties(systemRechargeWayUpdateReqVO, systemRechargeWayPOOld);
            systemRechargeWayPOOld.setRechargeTypeCode(systemRechargeTypePO.getRechargeCode());
            systemRechargeWayPOOld.setUpdater(systemRechargeWayUpdateReqVO.getOperatorUserNo());
            systemRechargeWayPOOld.setUpdatedTime(System.currentTimeMillis());

            setEntity(systemRechargeWayPOOld, systemRechargeWayUpdateReqVO);
            this.baseMapper.updateById(systemRechargeWayPOOld);

            //如果修改费率类型，同步变更下发站点的类型 费率类型
            updateSiteRechargeWay(systemRechargeWayUpdateReqVO.getId(), oldFeeType, newFeeType,
                    systemRechargeWayUpdateReqVO.getWayFee(), systemRechargeWayUpdateReqVO.getWayFeeFixedAmount());

            //保存到多语言
            if (!CollectionUtils.isEmpty(systemRechargeWayUpdateReqVO.getRechargeWayI18List())) {
                i18nApi.update(I18nMsgBindUtil.bind(systemRechargeWayPOOld.getRechargeWayI18(), systemRechargeWayUpdateReqVO.getRechargeWayI18List()));
            }
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }

    private void setEntity(SystemRechargeWayPO systemRechargeWayPOOld, SystemRechargeWayUpdateReqVO vo) {
        if (WayFeeTypeEnum.PERCENTAGE.getCode().equals(vo.getFeeType())) {
            systemRechargeWayPOOld.setWayFee(vo.getWayFee());
            systemRechargeWayPOOld.setWayFeeFixedAmount(BigDecimal.ZERO);
        } else if (WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(vo.getFeeType())) {
            systemRechargeWayPOOld.setWayFeeFixedAmount(vo.getWayFeeFixedAmount());
            systemRechargeWayPOOld.setWayFee(BigDecimal.ZERO);
        } else if (WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(vo.getFeeType())) {
            systemRechargeWayPOOld.setWayFee(vo.getWayFee());
            systemRechargeWayPOOld.setWayFeeFixedAmount(vo.getWayFeeFixedAmount());
        }
    }

    private void updateSiteRechargeWay(String rechargeWayId, Integer oldFeeType, Integer newFeeType,
                                       BigDecimal percentageAmount, BigDecimal feeFixedAmount) {
        if (!newFeeType.equals(oldFeeType)) {

            LambdaUpdateWrapper<SiteRechargeWayPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SiteRechargeWayPO::getRechargeWayId, rechargeWayId);
            updateWrapper.set(SiteRechargeWayPO::getFeeType, newFeeType);
            if (WayFeeTypeEnum.PERCENTAGE.getCode().equals(oldFeeType)
                    && WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(newFeeType)) {
                //百分比变为固定金额
                updateWrapper.set(SiteRechargeWayPO::getWayFee, BigDecimal.ZERO);
                updateWrapper.set(SiteRechargeWayPO::getWayFeeFixedAmount, feeFixedAmount);

            } else if (WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(oldFeeType)
                    && WayFeeTypeEnum.PERCENTAGE.getCode().equals(newFeeType)) {
                //固定金额变为百分比
                updateWrapper.set(SiteRechargeWayPO::getWayFee, percentageAmount);
                updateWrapper.set(SiteRechargeWayPO::getWayFeeFixedAmount, BigDecimal.ZERO);
            } else if (WayFeeTypeEnum.PERCENTAGE.getCode().equals(oldFeeType)
                    && WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(newFeeType)) {
                //百分比变为  百分比+固定金额
                updateWrapper.set(SiteRechargeWayPO::getWayFeeFixedAmount, feeFixedAmount);

            } else if (WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(oldFeeType)
                    && WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(newFeeType)) {
                //固定金额变为  百分比+固定金额
                updateWrapper.set(SiteRechargeWayPO::getWayFee, percentageAmount);
            } else if (WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(oldFeeType)
                    && WayFeeTypeEnum.PERCENTAGE.getCode().equals(newFeeType)) {
                //百分比+固定金额 变百分比
                updateWrapper.set(SiteRechargeWayPO::getWayFeeFixedAmount, BigDecimal.ZERO);
            } else if (WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(oldFeeType)
                    && WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(newFeeType)) {
                //百分比+固定金额 固定基恩
                updateWrapper.set(SiteRechargeWayPO::getWayFee, BigDecimal.ZERO);
            }
            siteRechargeWayRepository.update(null, updateWrapper);
        }
    }

    @CacheEvict(value = CacheConstants.RECHARGE_WAY_CACHE, key = CacheConstants.LIST)
    public ResponseVO<Void> enableOrDisable(SystemRechargeWayStatusReqVO systemRechargeWayStatusReqVO) {
        LambdaQueryWrapper<SystemRechargeWayPO> lqw = new LambdaQueryWrapper<SystemRechargeWayPO>();
        lqw.eq(SystemRechargeWayPO::getId, systemRechargeWayStatusReqVO.getId());
        SystemRechargeWayPO systemRechargeWayPOOld = this.baseMapper.selectOne(lqw);
        if (systemRechargeWayPOOld != null) {
            if (Objects.equals(EnableStatusEnum.ENABLE.getCode(), systemRechargeWayPOOld.getStatus())) {
                systemRechargeWayPOOld.setStatus(EnableStatusEnum.DISABLE.getCode());
                LambdaUpdateWrapper<SiteRechargeWayPO> siteLqw = new LambdaUpdateWrapper<>();
                siteLqw.eq(SiteRechargeWayPO::getRechargeWayId, systemRechargeWayStatusReqVO.getId());
                siteLqw.set(SiteRechargeWayPO::getStatus, EnableStatusEnum.DISABLE.getCode());
                siteRechargeWayRepository.update(null, siteLqw);
            } else {
                systemRechargeWayPOOld.setStatus(EnableStatusEnum.ENABLE.getCode());
            }
            systemRechargeWayPOOld.setUpdatedTime(System.currentTimeMillis());
            systemRechargeWayPOOld.setUpdater(systemRechargeWayStatusReqVO.getOperatorUserNo());
            this.baseMapper.updateById(systemRechargeWayPOOld);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }


    @CacheEvict(value = CacheConstants.RECHARGE_WAY_CACHE, key = CacheConstants.LIST)
    public ResponseVO<Boolean> batchSave(String userAccount, List<SortNewReqVO> sortNewReqVOS) {
        List<SystemRechargeWayPO> batchLists = Lists.newArrayList();
        for (SortNewReqVO sortNewReqVO : sortNewReqVOS) {
            SystemRechargeWayPO systemRechargeWayPO = new SystemRechargeWayPO();
            systemRechargeWayPO.setId(sortNewReqVO.getId());
            systemRechargeWayPO.setSortOrder(sortNewReqVO.getSortOrder());
            systemRechargeWayPO.setUpdatedTime(System.currentTimeMillis());
            systemRechargeWayPO.setUpdater(userAccount);
            batchLists.add(systemRechargeWayPO);
        }
        this.updateBatchById(batchLists);
        return ResponseVO.success();
    }

    public List<RechargeWayListVO> agentRechargeWayList(RechargeWayRequestVO rechargeWayRequestVO) {
        List<RechargeWayListVO> rechargeWayListVOS = new ArrayList<>();
        //获取站点对应的提现方式信息

        List<SystemRechargeWayVO> rechargeWayPOS = siteRechargeWayRepository.selectFrontWayList(rechargeWayRequestVO);

        if (null == rechargeWayPOS || rechargeWayPOS.isEmpty()) {
            return rechargeWayListVOS;
        }
        //获取站点关联的通道
        Map<String, List<SiteSystemRechargeChannelRespVO>> channelGroup = getChannelGroup(rechargeWayRequestVO.getSiteCode());
        //设置充值范围
        List<RechargeWayListVO> rechargeWayListVOList = setMinMaxValue(rechargeWayPOS, channelGroup);

        return rechargeWayListVOList;
    }

    public List<RechargeWayListVO> rechargeWayList(RechargeWayRequestVO rechargeWayRequestVO) {

        List<RechargeWayListVO> rechargeWayListVOS = new ArrayList<>();
        //获取站点对应的提现方式信息

        // 区分大陆盘与国际盘
        List<SystemRechargeWayVO> rechargeWayPOS = siteRechargeWayRepository.selectFrontWayList(rechargeWayRequestVO);
        if (ObjectUtil.equals(SiteHandicapModeEnum.China.getCode(), rechargeWayRequestVO.getHandicapMode())
                && ObjectUtil.isNotEmpty(rechargeWayRequestVO.getVipGradeCode())) {
            // 国内盘
            rechargeWayPOS = getUserGradeCodeFrom(rechargeWayPOS, rechargeWayRequestVO.getVipGradeCode());

            SiteVipOptionVO siteVipOptionVO = siteVipOptionApi.getVipGradeInfoByCode(rechargeWayRequestVO.getSiteCode(),rechargeWayRequestVO.getVipGradeCode(),rechargeWayRequestVO.getMainCurrency());
            if(null != siteVipOptionVO ){
                for (SystemRechargeWayVO rechargeWayVO:rechargeWayPOS){
                    if(null != siteVipOptionVO  && StringUtils.hasText(siteVipOptionVO.getDepositAmountLimit())){
                        List<String> vipQuickAmounts = new ArrayList<>(Arrays.asList(siteVipOptionVO.getDepositAmountLimit().split(CommonConstant.COMMA)));
                        vipQuickAmounts = vipQuickAmounts.stream().filter(o -> o != null && !o.isEmpty()).collect(Collectors.toList());

                        List<String> wayQuickAmounts = new ArrayList<>(Arrays.asList(rechargeWayVO.getQuickAmount().split(CommonConstant.COMMA)));
                        wayQuickAmounts = wayQuickAmounts.stream().filter(o -> o != null && !o.isEmpty()).collect(Collectors.toList());
                        for (String quickAmount: vipQuickAmounts) {
                            if(!wayQuickAmounts.contains(quickAmount)){
                                wayQuickAmounts.add(quickAmount);
                            }
                        }
                        Collections.sort(wayQuickAmounts, new Comparator<String>() {
                            @Override
                            public int compare(String s1, String s2) {
                                return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
                            }
                        });
                        rechargeWayVO.setQuickAmount(String.join(CommonConstant.COMMA,wayQuickAmounts));
                    }

                }
            }
        }

        if (null == rechargeWayPOS || rechargeWayPOS.isEmpty()) {
            return rechargeWayListVOS;
        }

        //获取站点关联的通道
        Map<String, List<SiteSystemRechargeChannelRespVO>> channelGroup = getUserChannelGroup(rechargeWayRequestVO.getSiteCode(),rechargeWayRequestVO.getVipRank(),rechargeWayRequestVO.getVipGradeCode());
        //设置充值范围
        List<RechargeWayListVO> rechargeWayListVOList = setMinMaxValue(rechargeWayPOS, channelGroup);

        return rechargeWayListVOList;
    }


    /**
     * getVipGradeUseScope 是 0，1，2，3，4,5,6,7,8,9,10
     * vipGradeCode 是 其中的一个
     */
    private List<SystemRechargeWayVO> getUserGradeCodeFrom(List<SystemRechargeWayVO> rechargeWayPOS, Integer vipGradeCode) {
        List<SystemRechargeWayVO> resultList = new ArrayList<>();
        if (ObjectUtil.isEmpty(rechargeWayPOS) || vipGradeCode == null) {
            return resultList;
        }
        String userVipGradeCodeStr = String.valueOf(vipGradeCode);
        for (SystemRechargeWayVO vo : rechargeWayPOS) {
            String vipGradeCodeStr = vo.getVipGradeUseScope();
            if (ObjectUtil.isNotEmpty(vipGradeCodeStr)) {
                String[] arr = vipGradeCodeStr.split(CommonConstant.COMMA);
                if (Arrays.asList(arr).contains(userVipGradeCodeStr)) {
                    resultList.add(vo);
                }
            }
        }
        return resultList;
    }

    public SiteRechargeWayFeeVO calculateSiteRechargeWayFeeRate(String siteCode, String rechargeWayId, BigDecimal amount, String channelType) {
        LambdaQueryWrapper<SiteRechargeWayPO> siteLwq = new LambdaQueryWrapper<>();
        siteLwq.eq(SiteRechargeWayPO::getSiteCode, siteCode);
        siteLwq.eq(SiteRechargeWayPO::getStatus, CommonConstant.business_one);
        siteLwq.eq(SiteRechargeWayPO::getRechargeWayId, rechargeWayId);
        siteLwq.last("limit 1");
        SiteRechargeWayPO siteRechargeWayPO = siteRechargeWayRepository.selectOne(siteLwq);
        SiteRechargeWayFeeVO siteRechargeWayFeeVO = new SiteRechargeWayFeeVO();
        if (null == siteRechargeWayPO) {
            siteRechargeWayFeeVO.setWayFeeFixedAmount(BigDecimal.ZERO);
            siteRechargeWayFeeVO.setWayFeeAmount(BigDecimal.ZERO);
            siteRechargeWayFeeVO.setWayFee(BigDecimal.ZERO);
            return siteRechargeWayFeeVO;
        }
        BigDecimal wayFeeAmount = BigDecimal.ZERO;
        BigDecimal settlementFeeRate = null == siteRechargeWayPO.getWayFee() ? BigDecimal.ZERO : siteRechargeWayPO.getWayFee();
        Integer wayFeeType = siteRechargeWayPO.getFeeType();
        BigDecimal percentageAmount = amount.multiply(settlementFeeRate.divide(new BigDecimal("100"))).setScale(2, RoundingMode.DOWN);
        BigDecimal fixedAmount = null == siteRechargeWayPO.getWayFeeFixedAmount() ? BigDecimal.ZERO : siteRechargeWayPO.getWayFeeFixedAmount();
        if (WayFeeTypeEnum.PERCENTAGE.getCode().equals(siteRechargeWayPO.getFeeType())) {
            wayFeeAmount = percentageAmount;
        } else if (WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(siteRechargeWayPO.getFeeType())) {
            wayFeeAmount = fixedAmount;
        } else if (WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(siteRechargeWayPO.getFeeType())) {
            wayFeeAmount = percentageAmount.add(fixedAmount);
        }
        siteRechargeWayFeeVO.setWayFeePercentageAmount(percentageAmount);
        siteRechargeWayFeeVO.setFeeType(wayFeeType);
        siteRechargeWayFeeVO.setWayFeeFixedAmount(fixedAmount);
        siteRechargeWayFeeVO.setWayFee(settlementFeeRate);
        siteRechargeWayFeeVO.setWayFeeAmount(wayFeeAmount);


        return siteRechargeWayFeeVO;
    }
    public Map<String, List<SiteSystemRechargeChannelRespVO>> getChannelGroup(String siteCode) {

        List<SiteSystemRechargeChannelRespVO> list = siteRechargeChannelRepository.selectSiteRechargeChannelList(siteCode);
        Map<String, List<SiteSystemRechargeChannelRespVO>> channelGroup = list.stream()
                .collect(Collectors.groupingBy(SiteSystemRechargeChannelRespVO::getRechargeWayId));

        return channelGroup;
    }
    public Map<String, List<SiteSystemRechargeChannelRespVO>> getUserChannelGroup(String siteCode, Integer vipRank,Integer vipGradeCode) {

        List<SiteSystemRechargeChannelRespVO> list = siteRechargeChannelRepository.selectSiteRechargeChannelList(siteCode);
        List<SiteSystemRechargeChannelRespVO> systemRechargeChannelRespVOS = new ArrayList<>();

        if(!ObjectUtil.isEmpty(list)){
            for (SiteSystemRechargeChannelRespVO vo : list) {
                String vipRankUseScope = vo.getVipRankUseScope();
                String vipGradeCodeUseScope = vo.getVipGradeUseScope();
                if(SiteHandicapModeEnum.China.getCode().equals(CurrReqUtils.getHandicapMode())){
                    if (ObjectUtil.isNotEmpty(vipGradeCodeUseScope)) {
                        String[] arr = vipGradeCodeUseScope.split(CommonConstant.COMMA);
                        if (Arrays.asList(arr).contains(String.valueOf(vipGradeCode))) {
                            systemRechargeChannelRespVOS.add(vo);
                        }
                    }
                }else{
                    if (ObjectUtil.isNotEmpty(vipRankUseScope)) {
                        String[] arr = vipRankUseScope.split(CommonConstant.COMMA);
                        if (Arrays.asList(arr).contains(String.valueOf(vipRank))) {
                            systemRechargeChannelRespVOS.add(vo);
                        }
                    }
                }
            }
        }
        Map<String, List<SiteSystemRechargeChannelRespVO>> channelGroup = systemRechargeChannelRespVOS.stream()
                .collect(Collectors.groupingBy(SiteSystemRechargeChannelRespVO::getRechargeWayId));

        return channelGroup;
    }

    private List<RechargeWayListVO> setMinMaxValue(List<SystemRechargeWayVO> list, Map<String, List<SiteSystemRechargeChannelRespVO>> channelGroup) {

        List<RechargeWayListVO> rechargeWayListVOS = new ArrayList<>();
        //汇总统计充值方式
        for (SystemRechargeWayVO systemRechargeWayPO : list) {
            RechargeWayListVO rechargeWayListVO = new RechargeWayListVO();
            BeanUtils.copyProperties(systemRechargeWayPO, rechargeWayListVO);
            rechargeWayListVO.setRechargeWay(systemRechargeWayPO.getRechargeWayI18());
            List<SiteSystemRechargeChannelRespVO> channelPOS = channelGroup.get(systemRechargeWayPO.getId());
            if(RechargeTypeEnum.MANUAL_RECHARGE.getCode().equals(systemRechargeWayPO.getRechargeTypeCode())){
                rechargeWayListVOS.add(rechargeWayListVO);
            }else{
                if (null != channelPOS && !channelPOS.isEmpty()) {
                    rechargeWayListVO.setRechargeMin(channelPOS.stream().map(SiteSystemRechargeChannelRespVO::getRechargeMin).min(BigDecimal::compareTo).get());
                    rechargeWayListVO.setRechargeMax(channelPOS.stream().map(SiteSystemRechargeChannelRespVO::getRechargeMax).max(BigDecimal::compareTo).get());
                    rechargeWayListVOS.add(rechargeWayListVO);
                }
            }


        }
        return rechargeWayListVOS;
    }

    public RechargeConfigVO getRechargeConfig(RechargeConfigRequestVO vo, UserInfoVO userInfoVO) {


        //获取余额，充值方式最大最小值，充值费率
        RechargeConfigVO rechargeConfigVO = getUserRechargeConfigBySiteCode(userInfoVO.getSiteCode(), vo.getRechargeWayId(), userInfoVO.getVipRank(),userInfoVO.getVipGradeCode());
        //设置余额
        UserCoinQueryVO userCoinQueryVO = new UserCoinQueryVO();
        userCoinQueryVO.setUserId(vo.getUserId());
        userCoinQueryVO.setCurrencyCode(userInfoVO.getMainCurrency());
        UserCoinWalletVO userCoinWalletVO = userCoinService.getUserCenterCoin(userCoinQueryVO);
        rechargeConfigVO.setBalance(userCoinWalletVO.getCenterAmount());

        return rechargeConfigVO;
    }

    public RechargeConfigVO getRechargeConfigBySiteCode(String siteCode, String rechargeWayId) {
        LambdaQueryWrapper<SiteRechargeWayPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SiteRechargeWayPO::getSiteCode, siteCode);
        lqw.eq(SiteRechargeWayPO::getRechargeWayId, rechargeWayId);
        SiteRechargeWayPO siteRechargeWayPO = siteRechargeWayRepository.selectOne(lqw);
        RechargeConfigVO rechargeConfigVO = new RechargeConfigVO();

        if (null != siteRechargeWayPO) {
            //设置费率
            rechargeConfigVO.setFeeRate(siteRechargeWayPO.getWayFee());
        }
        //设置最大最小范围
        Map<String, List<SiteSystemRechargeChannelRespVO>> channelGroup = getChannelGroup(siteCode);
        List<SiteSystemRechargeChannelRespVO> channelPOS = channelGroup.get(rechargeWayId);
        if (null != channelPOS && !channelPOS.isEmpty()) {
            rechargeConfigVO.setRechargeMinAmount(channelPOS.stream().map(SiteSystemRechargeChannelRespVO::getRechargeMin).min(BigDecimal::compareTo).get());
            rechargeConfigVO.setRechargeMaxAmount(channelPOS.stream().map(SiteSystemRechargeChannelRespVO::getRechargeMax).max(BigDecimal::compareTo).get());
        }
        return rechargeConfigVO;
    }

    public RechargeConfigVO getUserRechargeConfigBySiteCode(String siteCode, String rechargeWayId, Integer vipRank,Integer vipGradeCode) {
        LambdaQueryWrapper<SiteRechargeWayPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SiteRechargeWayPO::getSiteCode, siteCode);
        lqw.eq(SiteRechargeWayPO::getRechargeWayId, rechargeWayId);
        SiteRechargeWayPO siteRechargeWayPO = siteRechargeWayRepository.selectOne(lqw);
        RechargeConfigVO rechargeConfigVO = new RechargeConfigVO();

        if (null != siteRechargeWayPO) {
            //设置费率
            rechargeConfigVO.setFeeRate(siteRechargeWayPO.getWayFee());
        }
        //设置最大最小范围
        Map<String, List<SiteSystemRechargeChannelRespVO>> channelGroup = getUserChannelGroup(siteCode, vipRank,vipGradeCode);
        List<SiteSystemRechargeChannelRespVO> channelPOS = channelGroup.get(rechargeWayId);
        if (null != channelPOS && !channelPOS.isEmpty()) {
            rechargeConfigVO.setRechargeMinAmount(channelPOS.stream().map(SiteSystemRechargeChannelRespVO::getRechargeMin).min(BigDecimal::compareTo).get());
            rechargeConfigVO.setRechargeMaxAmount(channelPOS.stream().map(SiteSystemRechargeChannelRespVO::getRechargeMax).max(BigDecimal::compareTo).get());
        }
        return rechargeConfigVO;
    }

    /**
     * 获取站点的存款方式列表下拉框,按名字分组 ，id放到一起(和启用状态无关)
     *
     * @param siteCode
     * @return
     */
    public List<CodeValueVO> getRechargeWayListBySiteCode(String siteCode) {
        //获取站点对应的提现方式信息
        LambdaQueryWrapper<SiteRechargeWayPO> siteLwq = new LambdaQueryWrapper<>();
        siteLwq.eq(SiteRechargeWayPO::getSiteCode, siteCode);
        //  siteLwq.eq(SiteRechargeWayPO::getStatus, CommonConstant.business_one);
        siteLwq.select(SiteRechargeWayPO::getRechargeWayId);
        List<Object> rechargeWayIds = siteRechargeWayRepository.selectObjs(siteLwq);
        List<CodeValueVO> codeValueVOS = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(rechargeWayIds)) {
            LambdaQueryWrapper<SystemRechargeWayPO> lqw = new LambdaQueryWrapper<>();
            //lqw.eq(SystemRechargeWayPO::getStatus, CommonConstant.business_one);
            lqw.orderByAsc(SystemRechargeWayPO::getSortOrder);
            lqw.in(SystemRechargeWayPO::getId, rechargeWayIds);
            List<SystemRechargeWayPO> rechargeWayPOS = this.baseMapper.selectList(lqw);
            if (CollectionUtil.isNotEmpty(rechargeWayPOS)) {
                Map<String, List<SystemRechargeWayPO>> wayGroup = rechargeWayPOS.stream()
                        .collect(Collectors.groupingBy(SystemRechargeWayPO::getRechargeWayI18));
                for (Map.Entry<String, List<SystemRechargeWayPO>> entry : wayGroup.entrySet()) {
                    String key = entry.getKey();
                    List<SystemRechargeWayPO> list = entry.getValue();
                    CodeValueVO codeValueVO = new CodeValueVO();
                    codeValueVO.setValue(key);
                    codeValueVO.setCode(list.stream().map(SystemRechargeWayPO::getId).collect(Collectors.joining(", ")));
                    codeValueVOS.add(codeValueVO);
                }
            }
        }
        return codeValueVOS;
    }


    public SystemRechargeWayDetailRespVO getRechargeWayByCurrencyAndNetworkType(String currencyCode, String networkType, String siteCode, String wayId) {

        LambdaQueryWrapper<SystemRechargeWayPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SystemRechargeWayPO::getCurrencyCode, currencyCode);
        lqw.eq(StringUtils.hasText(networkType), SystemRechargeWayPO::getNetworkType, networkType);
        lqw.eq(StringUtils.hasText(wayId), SystemRechargeWayPO::getId, wayId);
        lqw.eq(SystemRechargeWayPO::getStatus, CommonConstant.business_one);
        List<SystemRechargeWayPO> list = this.baseMapper.selectList(lqw);
        if (CollectionUtil.isNotEmpty(list)) {
            SystemRechargeWayPO systemRechargeWayPO = list.get(0);

            LambdaQueryWrapper<SiteRechargeWayPO> siteLwq = new LambdaQueryWrapper<>();
            siteLwq.eq(SiteRechargeWayPO::getSiteCode, siteCode);
            siteLwq.eq(SiteRechargeWayPO::getStatus, CommonConstant.business_one);
            siteLwq.eq(SiteRechargeWayPO::getRechargeWayId, systemRechargeWayPO.getId());
            List<SiteRechargeWayPO> rechargeWayPOS = siteRechargeWayRepository.selectList(siteLwq);
            if (CollectionUtil.isNotEmpty(rechargeWayPOS)) {
                return ConvertUtil.entityToModel(systemRechargeWayPO, SystemRechargeWayDetailRespVO.class);
            }

            return null;
        }

        return null;
    }
}
