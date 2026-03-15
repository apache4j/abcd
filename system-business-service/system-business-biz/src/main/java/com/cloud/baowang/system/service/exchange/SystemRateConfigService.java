package com.cloud.baowang.system.service.exchange;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.HttpClientUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.enums.exchange.ExchangeRateAdjustWayEnum;
import com.cloud.baowang.system.api.enums.exchange.RateTypeEnum;
import com.cloud.baowang.system.api.enums.exchange.ShowWayEnum;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.exchange.CalculateRateReqVO;
import com.cloud.baowang.system.api.vo.exchange.RateCalculateRequestVO;
import com.cloud.baowang.system.api.vo.exchange.RateEditReqVO;
import com.cloud.baowang.system.api.vo.exchange.RateInitRequestVO;
import com.cloud.baowang.system.api.vo.exchange.RateReqVO;
import com.cloud.baowang.system.api.vo.exchange.RateResVO;
import com.cloud.baowang.system.po.exchange.CalculateRateVO;
import com.cloud.baowang.system.po.exchange.SystemCurrencyInfo;
import com.cloud.baowang.system.po.exchange.SystemRateConfigPO;
import com.cloud.baowang.system.repositories.exchange.SystemCurrencyInfoRepository;
import com.cloud.baowang.system.repositories.exchange.SystemRateConfigRepository;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/5/20 20:24
 * @Version: V1.0
 **/
@Service
@Slf4j
public class SystemRateConfigService extends ServiceImpl<SystemRateConfigRepository, SystemRateConfigPO> {

    @Resource
    private SystemRateConfigRepository systemRateConfigRepository;

    @Resource
    private SystemCurrencyInfoRepository systemCurrencyInfoRepository;

    @Resource
    private SystemDictConfigApi systemDictConfigApi;

    @Value("${rate.apikey.currency}")
    private String currencyApiKey;



    /**
     * 分页查询
     * @param rateReqVO 查询条件
     * @return 查询结果
     */
    public Page<RateResVO> selectPage(RateReqVO rateReqVO) {
        Page<SystemRateConfigPO> page = new Page<SystemRateConfigPO>(rateReqVO.getPageNumber(), rateReqVO.getPageSize());
        LambdaQueryWrapper<SystemRateConfigPO> lqw = new LambdaQueryWrapper<SystemRateConfigPO>();
        lqw.eq(SystemRateConfigPO::getSiteCode, rateReqVO.getSiteCode());
        if(StringUtils.hasText(rateReqVO.getRateType())){
            lqw.eq(SystemRateConfigPO::getRateType, rateReqVO.getRateType());
        }
       /* if(rateReqVO.getShowWayEnum()!=null && StringUtils.hasText(rateReqVO.getShowWayEnum().getCode())){
            lqw.eq(SystemRateConfigPO::getShowWay, rateReqVO.getShowWayEnum().getCode());
        }*/

      /*  if (StringUtils.hasText(rateReqVO.getCurrencyCode())) {
            LambdaQueryWrapper<SystemParamPO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SystemParamPO::getType, CommonConstant.COIN_CODE);
            lambdaQueryWrapper.like(SystemParamPO::getValue,rateReqVO.getCurrencyCode());
            List<SystemParamPO> coinList=systemParamRepository.selectList(lambdaQueryWrapper);
            if(!CollectionUtils.isEmpty(coinList)){
                List<String> currencyCodes=coinList.stream().map(SystemParamPO::getCode).collect(Collectors.toList());
                lqw.and(
                        lq->lq.like(SystemRateConfigPO::getCurrencyCode, rateReqVO.getCurrencyCode())
                        .or()
                        .in(SystemRateConfigPO::getCurrencyCode,currencyCodes)
                );
            }else {
                lqw.and(lq->lq.like(SystemRateConfigPO::getCurrencyCode, rateReqVO.getCurrencyCode()));
            }
        }*/
        if (StringUtils.hasText(rateReqVO.getCurrencyCode())) {
            lqw.eq(SystemRateConfigPO::getCurrencyCode, rateReqVO.getCurrencyCode());
        }
        if (StringUtils.hasText(rateReqVO.getAdjustWay())) {
            lqw.like(SystemRateConfigPO::getAdjustWay, rateReqVO.getAdjustWay());
        }
        lqw.orderByDesc(SystemRateConfigPO::getShowWay);
        //数据初始化
        RateInitRequestVO rateInitRequestVO=new RateInitRequestVO();
        rateInitRequestVO.setCurrencyCodeList(Lists.newArrayList(rateReqVO.getCurrencyCode()));
        rateInitRequestVO.setSiteCode(rateReqVO.getSiteCode());
        rateInitRequestVO.setSyncAdmin(false);
        initAdminData(rateInitRequestVO);
        Page<SystemRateConfigPO> systemRateConfigPOPage = systemRateConfigRepository.selectPage(page, lqw);
        if(CollectionUtils.isEmpty(systemRateConfigPOPage.getRecords())){
            systemRateConfigPOPage = systemRateConfigRepository.selectPage(page, lqw);
        }
        Page<RateResVO> encryptRateResVOPage = new Page<RateResVO>();
        BeanUtils.copyProperties(systemRateConfigPOPage, encryptRateResVOPage);
        if(!CollectionUtils.isEmpty(systemRateConfigPOPage.getRecords())){
            List<RateResVO> resVOList= Lists.newArrayList();
           /* LambdaQueryWrapper<SystemParamPO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SystemParamPO::getType, CommonConstant.COIN_CODE);
            List<SystemParamPO> coinList=systemParamRepository.selectList(lambdaQueryWrapper);*/
            List<SystemCurrencyInfo> systemCurrencyInfos=systemCurrencyInfoRepository.selectList(null);
            for(SystemRateConfigPO systemRateConfigPO:systemRateConfigPOPage.getRecords()){
                RateResVO rateResVO=new RateResVO();
                BeanUtils.copyProperties(systemRateConfigPO,rateResVO);
                /*if(StringUtils.hasText(rateResVO.getAdjustWay())){
                    rateResVO.setAdjustWayName(ExchangeRateAdjustWayEnum.getNameByCode(Integer.valueOf(rateResVO.getAdjustWay())));
                }*/
                Optional<SystemCurrencyInfo> systemCurrencyInfoOptional=systemCurrencyInfos.stream().filter(o->o.getCurrencyCode().equals(rateResVO.getCurrencyCode())).findFirst();
                if(systemCurrencyInfoOptional.isPresent()){
                    SystemCurrencyInfo systemCurrencyInfo=systemCurrencyInfoOptional.get();
                    rateResVO.setCurrencyName(systemCurrencyInfo.getCurrencyNameI18());
                    rateResVO.setCurrencySymbol(systemCurrencyInfo.getCurrencySymbol());
                }
                resVOList.add(rateResVO);
            }
            encryptRateResVOPage.setRecords(resVOList);
        }
        return encryptRateResVOPage;
    }


    /**
     * 总站根据币种初始化三方汇率
     * @param rateInitRequestVO  初始化参数
     */
    private void initAdminData(RateInitRequestVO rateInitRequestVO) {
        String siteCode=rateInitRequestVO.getSiteCode();
        if(!CommonConstant.ADMIN_CENTER_SITE_CODE.equals(siteCode)) {
            return;
        }
        boolean refreshFlag=false;
        List<String> currencyCodes =rateInitRequestVO.getCurrencyCodeList();
        for(String currencyCode:currencyCodes){
            for(ShowWayEnum showWayEnum:ShowWayEnum.values()){
                //总站汇率数据
                LambdaQueryWrapper<SystemRateConfigPO> lqwAdmin = new LambdaQueryWrapper<SystemRateConfigPO>();
                lqwAdmin.eq(SystemRateConfigPO::getCurrencyCode,currencyCode);
                lqwAdmin.eq(SystemRateConfigPO::getSiteCode,CommonConstant.ADMIN_CENTER_SITE_CODE);
                lqwAdmin.eq(SystemRateConfigPO::getShowWay,showWayEnum.getCode());
                lqwAdmin.eq(SystemRateConfigPO::getRateType,RateTypeEnum.CURRENCY.getCode());
                SystemRateConfigPO systemRateConfigPOAdmin=this.baseMapper.selectOne(lqwAdmin);
                SystemRateConfigPO systemRateConfigPO=new SystemRateConfigPO();
                systemRateConfigPO.setSiteCode(siteCode);
                systemRateConfigPO.setCurrencyCode(currencyCode);
                systemRateConfigPO.setShowWay(showWayEnum.getCode());
                systemRateConfigPO.setBaseCurrencyCode(CurrencyEnum.USD.getCode().concat(currencyCode));
                systemRateConfigPO.setRateType(RateTypeEnum.CURRENCY.getCode());
                systemRateConfigPO.setAdjustWay(ExchangeRateAdjustWayEnum.FIXED_VALUE.getCode().toString());
                systemRateConfigPO.setAdjustNum("0");
                systemRateConfigPO.setCreatedTime(System.currentTimeMillis());
                systemRateConfigPO.setUpdatedTime(System.currentTimeMillis());
                systemRateConfigPO.setCreator(CommonConstant.SUPER_ADMIN);
                systemRateConfigPO.setUpdater(CommonConstant.SUPER_ADMIN);
                //不存在总站汇率
                if(systemRateConfigPOAdmin==null){
                    log.info("总站汇率初始化:{}",systemRateConfigPO);
                    this.baseMapper.insert(systemRateConfigPO);
                    refreshFlag=true;
                }
            }
        }
        //更新三方汇率
        refreshExchangeActRate(refreshFlag);
    }

    /**
     * 根据币种初始化三方汇率
     * 总站默认等于三方汇率
     * 站点默认等于总站配置汇率
     * @param rateInitRequestVO  初始化参数
     */
    private void initSiteData(RateInitRequestVO rateInitRequestVO) {
        String siteCode=rateInitRequestVO.getSiteCode();
        if(CommonConstant.ADMIN_CENTER_SITE_CODE.equals(siteCode)) {
            return;
        }
        boolean refreshFlag=false;
        List<String> currencyCodes =rateInitRequestVO.getCurrencyCodeList();
        //设置和总站汇率一样
        boolean syncAdminFlag=rateInitRequestVO.isSyncAdmin();
        for(String currencyCode:currencyCodes){
            for(ShowWayEnum showWayEnum:ShowWayEnum.values()){
                //总站汇率数据
                LambdaQueryWrapper<SystemRateConfigPO> lqwAdmin = new LambdaQueryWrapper<SystemRateConfigPO>();
                lqwAdmin.eq(SystemRateConfigPO::getCurrencyCode,currencyCode);
                lqwAdmin.eq(SystemRateConfigPO::getSiteCode,CommonConstant.ADMIN_CENTER_SITE_CODE);
                lqwAdmin.eq(SystemRateConfigPO::getShowWay,showWayEnum.getCode());
                lqwAdmin.eq(SystemRateConfigPO::getRateType,RateTypeEnum.CURRENCY.getCode());
                SystemRateConfigPO systemRateConfigPOAdmin=this.baseMapper.selectOne(lqwAdmin);

                SystemRateConfigPO systemRateConfigPO=new SystemRateConfigPO();
                systemRateConfigPO.setSiteCode(siteCode);
                systemRateConfigPO.setCurrencyCode(currencyCode);
                systemRateConfigPO.setShowWay(showWayEnum.getCode());
                systemRateConfigPO.setBaseCurrencyCode(CurrencyEnum.USD.getCode().concat(currencyCode));
                systemRateConfigPO.setRateType(RateTypeEnum.CURRENCY.getCode());
                systemRateConfigPO.setAdjustWay(ExchangeRateAdjustWayEnum.FIXED_VALUE.getCode().toString());
                systemRateConfigPO.setAdjustNum("0");
                String operatorUserNo=CommonConstant.SUPER_ADMIN;
                if(rateInitRequestVO.isSiteFlag()){
                    operatorUserNo=rateInitRequestVO.getOperatorUserNo();
                }
                LambdaQueryWrapper<SystemRateConfigPO> lqw = new LambdaQueryWrapper<SystemRateConfigPO>();
                lqw.eq(SystemRateConfigPO::getCurrencyCode,currencyCode);
                lqw.eq(SystemRateConfigPO::getSiteCode,siteCode);
                lqw.eq(SystemRateConfigPO::getShowWay,systemRateConfigPO.getShowWay());
                lqw.eq(SystemRateConfigPO::getRateType,systemRateConfigPO.getRateType());
                SystemRateConfigPO systemRateConfigPOSite=this.baseMapper.selectOne(lqw);
                //复制总站数据
                if(systemRateConfigPOAdmin!=null){
                    systemRateConfigPO.setAdjustWay(systemRateConfigPOAdmin.getAdjustWay());
                    systemRateConfigPO.setAdjustNum(systemRateConfigPOAdmin.getAdjustNum());
                    systemRateConfigPO.setFinalRate(systemRateConfigPOAdmin.getFinalRate());
                }
                //不存在站点汇率
                if(systemRateConfigPOSite==null){
                    log.info("站点汇率初始化:{}",systemRateConfigPO);
                    systemRateConfigPO.setCreatedTime(System.currentTimeMillis());
                    systemRateConfigPO.setUpdatedTime(System.currentTimeMillis());
                    systemRateConfigPO.setCreator(operatorUserNo);
                    systemRateConfigPO.setUpdater(operatorUserNo);
                    this.baseMapper.insert(systemRateConfigPO);
                    refreshFlag=true;
                }else {
                    //已存在 并且需要同步
                    if(syncAdminFlag){
                        systemRateConfigPO.setUpdatedTime(System.currentTimeMillis());
                        systemRateConfigPO.setUpdater(operatorUserNo);
                        systemRateConfigPO.setId(systemRateConfigPOSite.getId());
                        log.info("站点汇率强制更新为总站:{}",systemRateConfigPO);
                        this.baseMapper.updateById(systemRateConfigPO);
                    }
                }
            }
        }
        //更新三方汇率
        refreshExchangeActRate(refreshFlag);
    }


    /**
     * 修改
     * @param rateEditReqVO 修改参数
     * @return 修改结果
     */
    public ResponseVO<String> edit(RateEditReqVO rateEditReqVO) {
        SystemRateConfigPO systemRateConfigPO=systemRateConfigRepository.selectById(rateEditReqVO.getId());
        if(systemRateConfigPO==null){
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        RateTypeEnum rateTypeEnum=RateTypeEnum.nameOfCode(systemRateConfigPO.getRateType());
        BigDecimal finalRate = calculatingRate(
                CalculateRateVO
                        .builder()
                        .lastThirdRate(systemRateConfigPO.getThirdRate())
                        .adjustWay(rateEditReqVO.getAdjustWay())
                        .adjustNum(rateEditReqVO.getAdjustNum())
                        .decimalLength(rateTypeEnum.getDecimalLength())
                        .build());
        systemRateConfigPO.setAdjustWay(rateEditReqVO.getAdjustWay());
        systemRateConfigPO.setAdjustNum(rateEditReqVO.getAdjustNum());
        systemRateConfigPO.setFinalRate(finalRate);
        systemRateConfigPO.setUpdater(rateEditReqVO.getUpdater());
        systemRateConfigPO.setUpdatedTime(System.currentTimeMillis());
        this.updateById(systemRateConfigPO);
        return ResponseVO.success();

    }

    public ResponseVO<String> calculatingRateReq(CalculateRateReqVO calculateRateReqVO) {
        SystemRateConfigPO systemRateConfigPO=systemRateConfigRepository.selectById(calculateRateReqVO.getId());
        if(systemRateConfigPO==null){
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        RateTypeEnum rateTypeEnum=RateTypeEnum.nameOfCode(systemRateConfigPO.getRateType());
        BigDecimal finalRate = calculatingRate(
                CalculateRateVO
                        .builder()
                        .lastThirdRate(systemRateConfigPO.getThirdRate())
                        .adjustWay(calculateRateReqVO.getAdjustWay())
                        .adjustNum(calculateRateReqVO.getAdjustNum())
                        .decimalLength(rateTypeEnum.getDecimalLength())
                        .build());
        return ResponseVO.success(finalRate.stripTrailingZeros().toString());
    }


    /**
     * 计算最终汇率
     * @param vo
     * @return
     */
    private BigDecimal calculatingRate(CalculateRateVO vo) {
        BigDecimal result;
        BigDecimal thirdRate=vo.getLastThirdRate();
        String adjustNum = vo.getAdjustNum();
        if(!StringUtils.hasText(adjustNum)){
            return thirdRate;
        }
        if(!StringUtils.hasText(vo.getAdjustWay())){
            return thirdRate;
        }
        if (ExchangeRateAdjustWayEnum.PERCENTAGE.getCode().toString().equals(vo.getAdjustWay())) {
            // 百分比调整
            checkPercentAdjustNum(adjustNum);
            BigDecimal adjustNumPoint = NumberUtil.div(adjustNum, "100", vo.getDecimalLength(), RoundingMode.DOWN);
            BigDecimal adjustNumAdd = NumberUtil.add(BigDecimal.ONE, adjustNumPoint);
            result = NumberUtil.mul(thirdRate, adjustNumAdd);
        } else {
            // 固定值调整
            checkFixedAdjustNum(adjustNum);
            result = NumberUtil.add(thirdRate, new BigDecimal(adjustNum));
        }
        result=result.setScale(vo.getDecimalLength(), RoundingMode.DOWN);
        return result;
    }


    private void checkPercentAdjustNum(String adjustNum) {
        try {
            Integer.parseInt(adjustNum);
        } catch (Exception e) {
            throw new BaowangDefaultException(ResultCode.PERCENT_ADJUST_HINT);
        }
        if (new BigDecimal(-50).compareTo(new BigDecimal(adjustNum)) > 0
                || new BigDecimal(adjustNum).compareTo(new BigDecimal(50)) > 0) {
            throw new BaowangDefaultException(ResultCode.PERCENT_ADJUST_HINT);
        }
    }


    private void checkFixedAdjustNum(String adjustNum) {
        try {
            Double.parseDouble(adjustNum);
        } catch (Exception e) {
            throw new BaowangDefaultException(ResultCode.FIXED_VALUE_ADJUST_HINT);
        }
        if (ConvertUtil.getDecimalPlace(adjustNum) > 2) {
            throw new BaowangDefaultException(ResultCode.FIXED_VALUE_ADJUST_HINT);
        }
        if (new BigDecimal(-1).compareTo(new BigDecimal(adjustNum)) > 0
                || new BigDecimal(adjustNum).compareTo(new BigDecimal(1)) > 0) {
            throw new BaowangDefaultException(ResultCode.FIXED_VALUE_ADJUST_HINT);
        }
    }

    public ResponseVO<String> refreshEncryptActRate(Boolean refreshNowFlag) {
        if(refreshNowFlag){
            refreshRateNow();
            return ResponseVO.success();
        }
        ResponseVO<SystemDictConfigRespVO>  systemDictConfigRespVOResponseVO=systemDictConfigApi.getByCode(DictCodeConfigEnums.CRYPTO_EXCHANGE_RATE_REFRESH_TIME.getCode(),CommonConstant.ADMIN_CENTER_SITE_CODE);
        if(systemDictConfigRespVOResponseVO.isOk()) {
            SystemDictConfigRespVO systemDictConfigRespVO = systemDictConfigRespVOResponseVO.getData();
            //配置的时间间隔 单位:小时
            Integer hourTime = Integer.valueOf(systemDictConfigRespVO.getConfigParam());
            LambdaQueryWrapper<SystemRateConfigPO> lqw = new LambdaQueryWrapper<SystemRateConfigPO>();
            lqw.eq(SystemRateConfigPO::getRateType, RateTypeEnum.ENCRYPT.getCode());
            lqw.last(" limit 1");
            SystemRateConfigPO systemRateConfigPOLatest = systemRateConfigRepository.selectOne(lqw);
            Long currentTime = System.currentTimeMillis();//当前时间
            Long nextTime = systemRateConfigPOLatest.getUpdatedTime() + hourTime * 3600 * 1000;//下次更新时间
            if (nextTime <= currentTime) {//下次更新时间晚于当前时间
                log.info("{}达到更新时间{},开始更新虚拟币汇率",currentTime,nextTime);
                refreshRateNow();
            }
        }
        return ResponseVO.success();
    }

    /**
     * 立马刷新汇率
     */
    public void refreshRateNow(){
        String rateUrl="https://api.binance.com/api/v3/ticker/24hr";
        String respText= HttpClientUtil.get(rateUrl);
        if(StringUtils.hasText(respText)){
            JSONArray respJsonArray=JSONArray.parseArray(respText);
            LambdaQueryWrapper<SystemRateConfigPO> lqw = new LambdaQueryWrapper<SystemRateConfigPO>();
            lqw.eq(SystemRateConfigPO::getRateType, RateTypeEnum.ENCRYPT.getCode());
            List<SystemRateConfigPO> systemRateConfigPOS=systemRateConfigRepository.selectList(lqw);
            List<SystemRateConfigPO> rechargeList=systemRateConfigPOS.stream().filter(o->o.getShowWay().equals(ShowWayEnum.RECHARGE.getCode())).collect(Collectors.toList());
            List<SystemRateConfigPO> withDrawList=systemRateConfigPOS.stream().filter(o->o.getShowWay().equals(ShowWayEnum.WITHDRAW.getCode())).collect(Collectors.toList());
            //调整充值汇率
            rateAdjust(rechargeList,respJsonArray,true,null);
            //调整提币汇率
            rateAdjust(withDrawList,respJsonArray,false,rechargeList);
        }
    }

    private void rateAdjust(List<SystemRateConfigPO> systemRateConfigPOS,JSONArray respJsonArray,boolean rechargeFlag,List<SystemRateConfigPO> rechargeList) {
        for(SystemRateConfigPO systemRateConfigPO:systemRateConfigPOS){
            String baseCurrencyCode=systemRateConfigPO.getBaseCurrencyCode();
            BigDecimal thirdRate=null;
            if(!rechargeFlag){
                SystemRateConfigPO rechargeRate=rechargeList.stream().filter(o->o.getCurrencyCode().equals(systemRateConfigPO.getCurrencyCode())).findFirst().get();
                thirdRate=getThirdRate(respJsonArray,rechargeRate.getBaseCurrencyCode());
                log.info("虚拟货币变化前 currencyCode:{},rechargeFlag:{},thirdRate:{}",baseCurrencyCode,rechargeFlag,thirdRate);
                if(thirdRate!=null) {
                    thirdRate = NumberUtil.div(1, thirdRate);
                }
                log.info("虚拟货币变化后 currencyCode:{},rechargeFlag:{},thirdRate:{}",baseCurrencyCode,rechargeFlag,thirdRate);
            }else {
                thirdRate=getThirdRate(respJsonArray,baseCurrencyCode);
                log.info("虚拟货币 currencyCode:{},rechargeFlag:{},thirdRate:{}",baseCurrencyCode,rechargeFlag,thirdRate);
            }
            if(thirdRate==null){
                continue;
            }
            RateTypeEnum rateTypeEnum=RateTypeEnum.nameOfCode(systemRateConfigPO.getRateType());
            thirdRate=thirdRate.setScale(rateTypeEnum.getDecimalLength(),RoundingMode.DOWN);
            systemRateConfigPO.setThirdRate(thirdRate);
            BigDecimal finalRate = calculatingRate(
                    CalculateRateVO
                            .builder()
                            .lastThirdRate(systemRateConfigPO.getThirdRate())
                            .adjustWay(systemRateConfigPO.getAdjustWay())
                            .adjustNum(systemRateConfigPO.getAdjustNum())
                            .decimalLength(rateTypeEnum.getDecimalLength())//虚拟币8位
                            .build());
            log.info("虚拟货币 币种:{},实时汇率:{},调整后汇率:{}",baseCurrencyCode,thirdRate,finalRate);
            systemRateConfigPO.setFinalRate(finalRate);
            systemRateConfigPO.setUpdater("XXL_JOB");
            systemRateConfigPO.setUpdatedTime(System.currentTimeMillis());
            systemRateConfigRepository.updateById(systemRateConfigPO);
        }
    }

    private BigDecimal getThirdRate(JSONArray respJsonArray,String baseCurrencyCode) {
        for (int i = 0; i <= respJsonArray.size() - 1; i++) {
            JSONObject respJson = respJsonArray.getJSONObject(i);
            String symbol = respJson.getString("symbol");
            if(symbol.equals(baseCurrencyCode)){
                return respJson.getBigDecimal("lastPrice");
            }
        }
        return null;
    }

    /**
     * 法币汇率刷新
     * @param refreshNowFlag  是否立马更新
     * @return
     */
    public ResponseVO<String> refreshExchangeActRate(Boolean refreshNowFlag) {
        if(refreshNowFlag){
            log.info("立即更新法币汇率");
            JSONObject rateJson=getCurrencyApiRateJson();
            refreshExchangeRate(rateJson);
        }else {
            ResponseVO<SystemDictConfigRespVO>  systemDictConfigRespVOResponseVO=systemDictConfigApi.getByCode(DictCodeConfigEnums.FIAT_EXCHANGE_RATE_REFRESH_TIME.getCode(),CommonConstant.ADMIN_CENTER_SITE_CODE);
            if(systemDictConfigRespVOResponseVO.isOk()){
                SystemDictConfigRespVO systemDictConfigRespVO=systemDictConfigRespVOResponseVO.getData();
                //配置的时间间隔 单位:小时
                Integer hourTime=Integer.valueOf(systemDictConfigRespVO.getConfigParam());
                LambdaQueryWrapper<SystemRateConfigPO> lqw = new LambdaQueryWrapper<SystemRateConfigPO>();
                lqw.eq(SystemRateConfigPO::getRateType, RateTypeEnum.CURRENCY.getCode());
                lqw.orderByDesc(SystemRateConfigPO::getThirdRateTime);
                lqw.last(" limit 1");
                SystemRateConfigPO systemRateConfigPOLatest=systemRateConfigRepository.selectOne(lqw);
                Long currentTime=System.currentTimeMillis();//当前时间
                Long nextTime=systemRateConfigPOLatest.getThirdRateTime()+hourTime*3600*1000;//下次更新时间
                log.info("当前时间:{},下次应该更新时间:{}",currentTime,nextTime);
                if(nextTime<currentTime){//下次更新时间晚于当前时间
                    //开始刷新法币汇率
                    log.info("{}达到更新时间{},开始更新法币汇率",currentTime,nextTime);
                    JSONObject rateJson=getCurrencyApiRateJson();
                    refreshExchangeRate(rateJson);
                }
            }
        }
        return ResponseVO.success();
    }


    /**
     * {
     *     "meta": {
     *         "last_updated_at": "2024-05-22T23:59:59Z"
     *     },
     *     "data": {
     *         "ADA": {
     *             "code": "ADA",
     *             "value": 2.067966833
     *         },
     *         "AED": {
     *             "code": "AED",
     *             "value": 3.6710703673
     *         }
     *     }
     *  }
     *  https://app.currencyapi.com/dashboard
     * @return
     */
    private JSONObject getCurrencyApiRateJson() {
        String rateUrl="https://api.currencyapi.com/v3/latest";
        Map<String,String> headMap=new HashMap<>();
        headMap.put("apikey",currencyApiKey);
        String respText=HttpClientUtil.doGet(rateUrl,null,headMap);
        if(StringUtils.hasText(respText)){
            JSONObject respJson=JSONObject.parseObject(respText);
            JSONObject rateJson=respJson.getJSONObject("data");
            if(rateJson!=null){
                JSONObject resultJson=new JSONObject();
                for (String key : rateJson.keySet()) {
                    resultJson.put(key,rateJson.getJSONObject(key).get("value"));
                }
                log.info("实时汇率:{}",resultJson);
                return resultJson;
            }
        }
        return null;
    }

    private JSONObject getExchangeRateJson() {
        String rateUrl="https://v6.exchangerate-api.com/v6/08020fe7dd62a23deddaa368/latest/USD";
        String respText=HttpClientUtil.get(rateUrl);
        if(StringUtils.hasText(respText)){
            JSONObject respJson=JSONObject.parseObject(respText);
            JSONObject rateJson=respJson.getJSONObject("conversion_rates");
            log.info("实时汇率:{}",rateJson);
            return rateJson;
        }
        return null;
    }


    /**
     * 刷新法币三方汇率
     * @param rateJson
     */
    public void refreshExchangeRate(JSONObject rateJson){
        if(rateJson==null){
            return;
        }
        //初始化 KVND
        //initData(CurrencyEnum.KVND.getCode());

        LambdaQueryWrapper<SystemRateConfigPO> lqw = new LambdaQueryWrapper<SystemRateConfigPO>();
        lqw.eq(SystemRateConfigPO::getRateType, RateTypeEnum.CURRENCY.getCode());
        List<SystemRateConfigPO> systemRateConfigPOS=systemRateConfigRepository.selectList(lqw);
        for(SystemRateConfigPO systemRateConfigPO:systemRateConfigPOS){
            String actCurrencyCode=systemRateConfigPO.getCurrencyCode();
            String currencyCode=systemRateConfigPO.getCurrencyCode();
            log.info("currencyCode:{}",currencyCode);
            //VND 币种汇率同步更新 KVND汇率
            if(CurrencyEnum.KVND.getCode().equals(actCurrencyCode)){
                currencyCode=CurrencyEnum.VND.getCode();
            }
            if(rateJson.containsKey(currencyCode)){
                BigDecimal thirdRate=rateJson.getBigDecimal(currencyCode);
                if(CurrencyEnum.KVND.getCode().equals(actCurrencyCode)){
                    thirdRate= AmountUtils.divide(thirdRate,new BigDecimal("1000"),4);
                }
                RateTypeEnum rateTypeEnum=RateTypeEnum.nameOfCode(systemRateConfigPO.getRateType());
                thirdRate=thirdRate.setScale(rateTypeEnum.getDecimalLength(),RoundingMode.DOWN);
                systemRateConfigPO.setThirdRate(thirdRate);
                BigDecimal finalRate = calculatingRate(
                        CalculateRateVO
                                .builder()
                                .lastThirdRate(systemRateConfigPO.getThirdRate())
                                .adjustWay(systemRateConfigPO.getAdjustWay())
                                .adjustNum(systemRateConfigPO.getAdjustNum())
                                .decimalLength(rateTypeEnum.getDecimalLength())//法币 2位
                                .build());
                log.info("币种:{},实时汇率:{},调整后汇率:{}",actCurrencyCode,thirdRate,finalRate);
                systemRateConfigPO.setFinalRate(finalRate);
               // systemRateConfigPO.setUpdater(CommonConstant.SUPER_ADMIN);
               // systemRateConfigPO.setUpdatedTime(System.currentTimeMillis());
                systemRateConfigPO.setThirdRateTime(System.currentTimeMillis());
                systemRateConfigRepository.updateById(systemRateConfigPO);
            }
        }
    }

    public BigDecimal getLatestRate(RateCalculateRequestVO vo) {
        LambdaQueryWrapper<SystemRateConfigPO> lqw = new LambdaQueryWrapper<>();
       // lqw.eq(SystemRateConfigPO::getRateType,vo.getRateType());
        lqw.eq(SystemRateConfigPO::getSiteCode,vo.getSiteCode());
        lqw.eq(SystemRateConfigPO::getCurrencyCode,vo.getCurrencyCode());
        /*if (vo.getStatus()!=null) {
            lqw.eq(SystemRateConfigPO::getStatus,vo.getStatus());
        }*/
        lqw.eq(SystemRateConfigPO::getShowWay,vo.getShowWay());
        lqw.eq(SystemRateConfigPO::getRateType,RateTypeEnum.CURRENCY.getCode());
        SystemRateConfigPO systemRateConfigPO = this.systemRateConfigRepository.selectOne(lqw);
        if(null != systemRateConfigPO){
            return systemRateConfigPO.getFinalRate();
        }
        return BigDecimal.ZERO;
    }


    /**
     * 站点汇率按照总站进行初始化
     * @param rateInitRequestVO
     */
    public void init(RateInitRequestVO rateInitRequestVO) {
        if(CollectionUtils.isEmpty(rateInitRequestVO.getCurrencyCodeList())){
            return;
        }
        //数据初始化
        initSiteData(rateInitRequestVO);
    }

}
