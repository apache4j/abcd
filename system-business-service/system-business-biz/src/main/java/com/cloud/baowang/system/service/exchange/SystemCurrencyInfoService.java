package com.cloud.baowang.system.service.exchange;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.I18nMsgBindUtil;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.i18n.dto.I18NMessageDTO;
import com.cloud.baowang.system.api.vo.IdReqVO;
import com.cloud.baowang.system.api.vo.exchange.*;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.po.exchange.SystemCurrencyInfo;
import com.cloud.baowang.system.repositories.exchange.SystemCurrencyInfoRepository;
import com.cloud.baowang.system.service.language.LanguageManagerService;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.SystemRechargeTypeApi;
import com.cloud.baowang.wallet.api.api.SystemWithdrawTypeApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInitReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 11:45
 * @Version: V1.0
 **/
@Service
@Slf4j
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SystemCurrencyInfoService  extends ServiceImpl<SystemCurrencyInfoRepository, SystemCurrencyInfo> {

    @Resource
    private SystemCurrencyInfoRepository systemCurrencyInfoRepository;

    @Resource
    private SystemCurrencyInfoService _this;

    @Resource
    private I18nApi i18nApi;
    @Resource
    private  LanguageManagerService languageManagerService;

    @Resource
    private SystemRechargeTypeApi systemRechargeTypeApi;

    @Resource
    private SystemWithdrawTypeApi systemWithdrawTypeApi;

    @Resource
    private SiteCurrencyInfoApi siteCurrencyInfoApi;


    //@Cacheable(value = CacheConstants.CURRENCY_CACHE, key = CacheConstants.LIST, sync = true)
    public ResponseVO<List<SystemCurrencyInfoRespVO>> selectAll() {
        List<SystemCurrencyInfoRespVO> resultLists=RedisUtil.getList(CacheConstants.KEY_CURRENCY_CACHE);
        if(CollectionUtils.isEmpty(resultLists)){
            LambdaQueryWrapper<SystemCurrencyInfo> lqw = new LambdaQueryWrapper<SystemCurrencyInfo>();
            lqw.orderByAsc(SystemCurrencyInfo::getCreatedTime);
            List<SystemCurrencyInfo> systemCurrencyInfos=systemCurrencyInfoRepository.selectList(lqw);
            resultLists= Lists.newArrayList();
            for(SystemCurrencyInfo systemCurrencyInfo:systemCurrencyInfos){
                SystemCurrencyInfoRespVO systemCurrencyInfoRespVO=new SystemCurrencyInfoRespVO();
                BeanUtils.copyProperties(systemCurrencyInfo,systemCurrencyInfoRespVO);
                resultLists.add(systemCurrencyInfoRespVO);
            }
            RedisUtil.setList(CacheConstants.KEY_CURRENCY_CACHE,resultLists);
        }
        return ResponseVO.success(resultLists);
    }

    /**
     * 查询有效币种
     * @return
     */
    public ResponseVO<List<SystemCurrencyInfoRespVO>> selectAllValid() {
        List<SystemCurrencyInfoRespVO> systemCurrencyInfos = _this.selectAll().getData();
        systemCurrencyInfos=systemCurrencyInfos.stream().filter(o->o.getStatus().equals(EnableStatusEnum.ENABLE.getCode())).collect(Collectors.toUnmodifiableList());
        return ResponseVO.success(systemCurrencyInfos);
    }

    public ResponseVO<Page<SystemCurrencyInfoRespVO>> selectPage(SystemCurrencyInfoReqVO systemCurrencyInfoReqVO) {
        Page<SystemCurrencyInfo> page = new Page<SystemCurrencyInfo>(systemCurrencyInfoReqVO.getPageNumber(), systemCurrencyInfoReqVO.getPageSize());
        /*LambdaQueryWrapper<SystemCurrencyInfo> lqw = new LambdaQueryWrapper<SystemCurrencyInfo>();
        if(StringUtils.hasText(systemCurrencyInfoReqVO.getCurrencyCode())){
            lqw.eq(SystemCurrencyInfo::getCurrencyCode, systemCurrencyInfoReqVO.getCurrencyCode());
        }
        if(!CollectionUtils.isEmpty(systemCurrencyInfoReqVO.getCurrencyCodeList())){
            lqw.in(SystemCurrencyInfo::getCurrencyCode, systemCurrencyInfoReqVO.getCurrencyCodeList());
        }
        if(StringUtils.hasText(systemCurrencyInfoReqVO.getCurrencyName())){
            lqw.like(SystemCurrencyInfo::getCurrencyName, systemCurrencyInfoReqVO.getCurrencyName());
        }
        if(systemCurrencyInfoReqVO.getStatus()!=null){
            lqw.eq(SystemCurrencyInfo::getStatus, systemCurrencyInfoReqVO.getStatus());
        }
        lqw.orderByDesc(SystemCurrencyInfo::getCreatedTime);*/
        IPage<SystemCurrencyInfo> systemCurrencyInfoPage =  systemCurrencyInfoRepository.listPage(page,systemCurrencyInfoReqVO);
        Page<SystemCurrencyInfoRespVO> systemCurrencyInfoRespVOPage=new Page<SystemCurrencyInfoRespVO>(systemCurrencyInfoReqVO.getPageNumber(), systemCurrencyInfoReqVO.getPageSize());
        systemCurrencyInfoRespVOPage.setTotal(systemCurrencyInfoPage.getTotal());
        systemCurrencyInfoRespVOPage.setPages(systemCurrencyInfoPage.getPages());
        List<SystemCurrencyInfoRespVO> resultLists= Lists.newArrayList();
        for(SystemCurrencyInfo systemCurrencyInfo:systemCurrencyInfoPage.getRecords()){
            SystemCurrencyInfoRespVO systemCurrencyInfoRespVO=new SystemCurrencyInfoRespVO();
            BeanUtils.copyProperties(systemCurrencyInfo,systemCurrencyInfoRespVO);
            resultLists.add(systemCurrencyInfoRespVO);
        }
        systemCurrencyInfoRespVOPage.setRecords(resultLists);
        return ResponseVO.success(systemCurrencyInfoRespVOPage);
    }

    // @CacheEvict(value = CacheConstants.CURRENCY_CACHE, key = CacheConstants.LIST)
    public ResponseVO<Boolean> insert(SystemCurrencyInfoNewReqVO systemCurrencyInfoNewReqVO) {
        //必须传全部语言
        List<LanguageManagerListVO> languageManagerListVOS=languageManagerService.languageByList(systemCurrencyInfoNewReqVO.getSiteCode()).getData();
        Set<String> reqLangSet=systemCurrencyInfoNewReqVO.getCurrencyNameI18List().stream().map(o->o.getLanguage()).collect(Collectors.toSet());
        if(languageManagerListVOS.size()!=reqLangSet.size()){
            log.info("新增币种-本次请求中语言不全面");
            return ResponseVO.fail(ResultCode.LANG_NOT_FULL);
        }
     /*   Set<String> reqCurrencyNameSet=systemCurrencyInfoNewReqVO.getCurrencyNameI18List().stream().map(o->o.getMessage()).collect(Collectors.toSet());
        if(languageManagerListVOS.size()!=reqCurrencyNameSet.size()){
            log.info("新增币种-本次请求中币种名称存在重复值");
            return ResponseVO.fail(ResultCode.CURRENCY_NAME_REPEAT);
        }*/
        List<I18NMessageDTO> i18NMessageDTOS=i18nApi.getMessageLikeKey(I18MsgKeyEnum.CURRENCY_NAME.getCode()).getData();
        for(I18nMsgFrontVO i18nMsgFrontVO:systemCurrencyInfoNewReqVO.getCurrencyNameI18List()){
            Optional<I18NMessageDTO> i18Optional= i18NMessageDTOS.stream().filter(o->o.getLanguage().equals(i18nMsgFrontVO.getLanguage())&&o.getMessage().equals(i18nMsgFrontVO.getMessage())).findFirst();
            if(i18Optional.isPresent()){
                log.info("新增币种-本次请求中语言:{}币种名称:{}和历史名称对比,存在重复值",i18nMsgFrontVO.getLanguage(),i18nMsgFrontVO.getMessage());
                return ResponseVO.fail(ResultCode.CURRENCY_NAME_REPEAT);
            }
        }

        LambdaQueryWrapper<SystemCurrencyInfo> lqw = new LambdaQueryWrapper<SystemCurrencyInfo>();
        lqw.eq(SystemCurrencyInfo::getCurrencyCode, systemCurrencyInfoNewReqVO.getCurrencyCode());
        SystemCurrencyInfo systemCurrencyInfoOld= systemCurrencyInfoRepository.selectOne(lqw);
        if(systemCurrencyInfoOld==null){
            String currencyNameI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.CURRENCY_NAME.getCode());

            SystemCurrencyInfo systemCurrencyInfo=new SystemCurrencyInfo();
            BeanUtils.copyProperties(systemCurrencyInfoNewReqVO,systemCurrencyInfo);
            if(CommonConstant.USDT.equals(systemCurrencyInfo.getCurrencyCode())){
                systemCurrencyInfo.setCurrencyName(CommonConstant.USDT);
            }else {
                systemCurrencyInfo.setCurrencyName(CurrencyEnum.nameByCode(systemCurrencyInfo.getCurrencyCode()));
            }
            systemCurrencyInfo.setCurrencyNameI18(currencyNameI18Code);
            String currencyNameCn=systemCurrencyInfoNewReqVO.getCurrencyNameI18List().stream().filter(o->o.getLanguage().equals(LanguageEnum.ZH_CN.getLang())).findFirst().get().getLanguageName();
            systemCurrencyInfo.setCurrencyName(currencyNameCn);
            systemCurrencyInfo.setStatus(EnableStatusEnum.ENABLE.getCode());
            systemCurrencyInfo.setCreator(systemCurrencyInfoNewReqVO.getOperatorUserNo());
            systemCurrencyInfo.setUpdater(systemCurrencyInfoNewReqVO.getOperatorUserNo());
            systemCurrencyInfo.setCreatedTime(System.currentTimeMillis());
            systemCurrencyInfo.setUpdatedTime(System.currentTimeMillis());
            systemCurrencyInfoRepository.insert(systemCurrencyInfo);
            //更新缓存
            RedisUtil.deleteKey(CacheConstants.KEY_CURRENCY_CACHE);

            //保存到多语言
            i18nApi.insert(I18nMsgBindUtil.bind(currencyNameI18Code,systemCurrencyInfoNewReqVO.getCurrencyNameI18List()));
            //初始化充值类型
            systemRechargeTypeApi.init(systemCurrencyInfo.getCurrencyCode());
            // 初始化提币类型
            systemWithdrawTypeApi.init(systemCurrencyInfo.getCurrencyCode());

            List<String> currencyCodes=Lists.newArrayList();
            currencyCodes.add(systemCurrencyInfo.getCurrencyCode());
            //编辑币种
            siteCurrencyInfoApi.init(
                            SiteCurrencyInitReqVO.builder().siteCode(CommonConstant.ADMIN_CENTER_SITE_CODE)
                                    .operatorUserNo(systemCurrencyInfoNewReqVO.getOperatorUserNo())
                                    .finalRate(systemCurrencyInfo.getFinalRate())
                                    .currencyCodeLists(currencyCodes
                                    ).build())
                    .isOk();

            return ResponseVO.success(Boolean.TRUE);
        }
        return ResponseVO.fail(ResultCode.DATA_IS_EXIST);
    }

    // @CacheEvict(value = CacheConstants.CURRENCY_CACHE, key = CacheConstants.LIST)
    public ResponseVO<Boolean> updateInfo(SystemCurrencyInfoUpdateReqVO systemCurrencyInfoUpdateReqVO) {
        String siteCode=systemCurrencyInfoUpdateReqVO.getSiteCode();
        LambdaQueryWrapper<SystemCurrencyInfo> lqw = new LambdaQueryWrapper<SystemCurrencyInfo>();
        lqw.eq(SystemCurrencyInfo::getId, systemCurrencyInfoUpdateReqVO.getId());
        SystemCurrencyInfo systemCurrencyInfoOld= systemCurrencyInfoRepository.selectOne(lqw);
        if(systemCurrencyInfoOld!=null){
            lqw.clear();
            lqw.eq(SystemCurrencyInfo::getCurrencyCode, systemCurrencyInfoUpdateReqVO.getCurrencyCode());
            SystemCurrencyInfo systemCurrencyCodeDb= systemCurrencyInfoRepository.selectOne(lqw);
            if(!Objects.equals(systemCurrencyCodeDb.getId(), systemCurrencyInfoOld.getId())){
                log.info("修改币种-本次请求中币种代码历史数据中已存在,{}",systemCurrencyInfoUpdateReqVO.getCurrencyCode());
                return ResponseVO.fail(ResultCode.DATA_IS_EXIST);
            }
            //必须传全部语言
            List<LanguageManagerListVO> languageManagerListVOS=languageManagerService.languageByList(siteCode).getData();
            Set<String> reqLangSet=systemCurrencyInfoUpdateReqVO.getCurrencyNameI18List().stream().map(o->o.getLanguage()).collect(Collectors.toSet());
            if(languageManagerListVOS.size()!=reqLangSet.size()){
                log.info("修改币种-本次请求中语言不全面");
                return ResponseVO.fail(ResultCode.LANG_NOT_FULL);
            }
           /* Set<String> reqCurrencyNameSet=systemCurrencyInfoUpdateReqVO.getCurrencyNameI18List().stream().map(o->o.getMessage()).collect(Collectors.toSet());
            if(languageManagerListVOS.size()!=reqCurrencyNameSet.size()){
                log.info("修改币种-本次请求中币种名称存在重复值");
                return ResponseVO.fail(ResultCode.CURRENCY_NAME_REPEAT);
            }*/
            List<I18NMessageDTO> i18NMessageDTOS=i18nApi.getMessageLikeKey(I18MsgKeyEnum.CURRENCY_NAME.getCode()).getData();
            List<I18NMessageDTO> i18NMessageFilterDTOS=i18NMessageDTOS.stream().filter(o->!o.getMessageKey().equals(systemCurrencyInfoOld.getCurrencyNameI18())).collect(Collectors.toList());
            for(I18nMsgFrontVO i18nMsgFrontVO:systemCurrencyInfoUpdateReqVO.getCurrencyNameI18List()){
                Optional<I18NMessageDTO> i18Optional= i18NMessageFilterDTOS.stream().filter(o->o.getMessage().equals(i18nMsgFrontVO.getMessage())&&o.getLanguage().equals(i18nMsgFrontVO.getLanguage())).findFirst();
                if(i18Optional.isPresent()){
                    log.info("修改币种-本次请求中语言:{}币种名称:{}和历史名称对比,存在重复值",i18nMsgFrontVO.getLanguage(),i18nMsgFrontVO.getMessage());
                    return ResponseVO.fail(ResultCode.CURRENCY_NAME_REPEAT);
                }
            }
            SystemCurrencyInfo systemCurrencyInfo=new SystemCurrencyInfo();
            BeanUtils.copyProperties(systemCurrencyInfoUpdateReqVO,systemCurrencyInfo);
            String currencyNameCn=systemCurrencyInfoUpdateReqVO.getCurrencyNameI18List().stream().filter(o->o.getLanguage().equals(LanguageEnum.ZH_CN.getLang())).findFirst().get().getLanguageName();
            systemCurrencyInfo.setCurrencyName(currencyNameCn);
            systemCurrencyInfo.setId(systemCurrencyInfoOld.getId());
            systemCurrencyInfo.setUpdatedTime(System.currentTimeMillis());
            systemCurrencyInfo.setUpdater(systemCurrencyInfoUpdateReqVO.getOperatorUserNo());
            systemCurrencyInfoRepository.updateById(systemCurrencyInfo);
            //更新缓存
            RedisUtil.deleteKey(CacheConstants.KEY_CURRENCY_CACHE);
            //保存到多语言
            if(!CollectionUtils.isEmpty(systemCurrencyInfoUpdateReqVO.getCurrencyNameI18List())){
                i18nApi.update(I18nMsgBindUtil.bind(systemCurrencyInfoOld.getCurrencyNameI18(),systemCurrencyInfoUpdateReqVO.getCurrencyNameI18List()));
            }
            //初始化充值类型
            systemRechargeTypeApi.init(systemCurrencyInfo.getCurrencyCode());
            // 初始化提币类型
            systemWithdrawTypeApi.init(systemCurrencyInfo.getCurrencyCode());

            List<String> currencyCodes=Lists.newArrayList();
            currencyCodes.add(systemCurrencyInfo.getCurrencyCode());
            //编辑币种
            siteCurrencyInfoApi.init(
                            SiteCurrencyInitReqVO.builder().siteCode(CommonConstant.ADMIN_CENTER_SITE_CODE)
                                    .operatorUserNo(systemCurrencyInfoUpdateReqVO.getOperatorUserNo())
                                    .finalRate(systemCurrencyInfo.getFinalRate())
                                    .currencyCodeLists(currencyCodes
                                    ).build())
                    .isOk();

            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);

    }

    // @CacheEvict(value = CacheConstants.CURRENCY_CACHE, key = CacheConstants.LIST)
    public ResponseVO<Boolean> enableOrDisable(SystemCurrencyInfoStatusReqVO systemCurrencyInfoStatusReqVO) {
        LambdaQueryWrapper<SystemCurrencyInfo> lqw = new LambdaQueryWrapper<SystemCurrencyInfo>();
        lqw.eq(SystemCurrencyInfo::getId, systemCurrencyInfoStatusReqVO.getId());
        SystemCurrencyInfo systemCurrencyInfoOld= systemCurrencyInfoRepository.selectOne(lqw);
        if(systemCurrencyInfoOld!=null){
            if(Objects.equals(EnableStatusEnum.ENABLE.getCode(), systemCurrencyInfoOld.getStatus())){
                // 关闭所有站点下币种
                siteCurrencyInfoApi.disableCurrency(systemCurrencyInfoOld.getCurrencyCode(),systemCurrencyInfoStatusReqVO.getOperatorUserNo());

                systemCurrencyInfoOld.setStatus(EnableStatusEnum.DISABLE.getCode());
            }else {
                systemCurrencyInfoOld.setStatus(EnableStatusEnum.ENABLE.getCode());

                // 开启总站下币种
                siteCurrencyInfoApi.enableAdminCurrency(systemCurrencyInfoOld.getCurrencyCode());
            }
            systemCurrencyInfoOld.setUpdatedTime(System.currentTimeMillis());
            systemCurrencyInfoOld.setUpdater(systemCurrencyInfoStatusReqVO.getOperatorUserNo());
            systemCurrencyInfoRepository.updateById(systemCurrencyInfoOld);
            //更新缓存
            RedisUtil.deleteKey(CacheConstants.KEY_CURRENCY_CACHE);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }


    public ResponseVO<SystemCurrencyInfoDetailRespVO> info(IdReqVO idReqVO) {
        LambdaQueryWrapper<SystemCurrencyInfo> lqw = new LambdaQueryWrapper<SystemCurrencyInfo>();
        lqw.eq(SystemCurrencyInfo::getId, idReqVO.getId());
        SystemCurrencyInfo systemCurrencyInfoOld= systemCurrencyInfoRepository.selectOne(lqw);
        if(systemCurrencyInfoOld!=null){
            SystemCurrencyInfoDetailRespVO systemCurrencyInfoDetailRespVO=new SystemCurrencyInfoDetailRespVO();
            BeanUtils.copyProperties(systemCurrencyInfoOld,systemCurrencyInfoDetailRespVO);
            return ResponseVO.success(systemCurrencyInfoDetailRespVO);
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }

    public SystemCurrencyInfoDetailRespVO selectByCurrencyCode(String currencyCode) {
        LambdaQueryWrapper<SystemCurrencyInfo> lqw = new LambdaQueryWrapper<SystemCurrencyInfo>();
        lqw.eq(SystemCurrencyInfo::getCurrencyCode, currencyCode);
        SystemCurrencyInfo systemCurrencyInfoOld= systemCurrencyInfoRepository.selectOne(lqw);
        if(systemCurrencyInfoOld!=null){
            SystemCurrencyInfoDetailRespVO systemCurrencyInfoDetailRespVO=new SystemCurrencyInfoDetailRespVO();
            BeanUtils.copyProperties(systemCurrencyInfoOld,systemCurrencyInfoDetailRespVO);
            return systemCurrencyInfoDetailRespVO;
        }
        return null;
    }

    public ResponseVO<Boolean> batchSaveRate(List<SystemBatchRateReqVO> systemBatchRateReqVOS) {
        for(SystemBatchRateReqVO systemBatchRateReqVO:systemBatchRateReqVOS){
            LambdaUpdateWrapper<SystemCurrencyInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<SystemCurrencyInfo>();
            lambdaUpdateWrapper.set(SystemCurrencyInfo::getFinalRate,systemBatchRateReqVO.getFinalRate());
            lambdaUpdateWrapper.set(SystemCurrencyInfo::getUpdatedTime,System.currentTimeMillis());
            lambdaUpdateWrapper.set(SystemCurrencyInfo::getUpdater,systemBatchRateReqVO.getUpdater());
            lambdaUpdateWrapper.eq(SystemCurrencyInfo::getCurrencyCode,systemBatchRateReqVO.getCurrencyCode());
            this.baseMapper.update(null,lambdaUpdateWrapper);
        }
        return ResponseVO.success();
    }
}
