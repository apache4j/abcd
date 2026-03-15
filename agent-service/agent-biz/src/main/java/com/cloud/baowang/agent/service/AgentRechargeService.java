package com.cloud.baowang.agent.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentStatusEnum;
import com.cloud.baowang.agent.api.enums.AgentTypeEnum;
import com.cloud.baowang.agent.api.enums.depositWithdrawal.AgentDepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.agent.api.enums.depositWithdrawal.AgentDepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentManualUpDownDetailVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentSuperTransferDetailVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentTradeRecordDetailRequestVO;
import com.cloud.baowang.agent.api.vo.recharge.*;
import com.cloud.baowang.agent.po.AgentDepositWithdrawalPO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.AgentManualUpDownRecordPO;
import com.cloud.baowang.agent.po.AgentTransferRecordPO;
import com.cloud.baowang.agent.repositories.AgentDepositWithdrawalRepository;
import com.cloud.baowang.agent.repositories.AgentManualUpDownRecordRepository;
import com.cloud.baowang.agent.repositories.AgentTransferRecordRepository;
import com.cloud.baowang.agent.util.MinioFileService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.exchange.ExchangeRateConfigApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.enums.exchange.ShowWayEnum;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.exchange.RateCalculateRequestVO;
import com.cloud.baowang.wallet.api.api.HotWalletAddressApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.SiteRechargeWayApi;
import com.cloud.baowang.wallet.api.api.SystemRechargeWayApi;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.wallet.ChannelTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.OwnerUserTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.RechargeTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TradeRecordTypeEnum;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AgentRechargeService extends ServiceImpl<AgentDepositWithdrawalRepository, AgentDepositWithdrawalPO> {



    private final HotWalletAddressApi hotWalletAddressApi;


    private AgentDepositWithdrawalRepository agentDepositWithdrawalRepository;

    private final AgentDepositWithdrawHandleService agentDepositWithdrawHandleService;

    private final SystemRechargeWayApi systemRechargeWayApi;

    private final AgentInfoService agentInfoService;


    private final ExchangeRateConfigApi exchangeRateConfigApi;

    private final MinioFileService minioFileService;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final AgentManualUpDownRecordRepository agentManualUpDownRecordRepository;

    private final AgentTransferRecordRepository agentTransferRecordRepository;

    private final SystemDictConfigApi systemDictConfigApi;

    private final SiteRechargeWayApi siteRechargeWayApi;



    @DistributedLock(name = RedisKeyTransUtil.AGENT_DEPOSIT, unique = "#agentRechargeReqVo.agentId", waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    public ResponseVO<AgentOrderNoVO> agentRecharge(AgentRechargeReqVO agentRechargeReqVo) {
        String agentId = agentRechargeReqVo.getAgentId();
        BigDecimal depositAmount = agentRechargeReqVo.getAmount();
        if(null == depositAmount){
            throw new BaowangDefaultException(ResultCode.AMOUNT_IS_NULL);
        }
        if(depositAmount.compareTo(BigDecimal.ZERO) <= 0){
            throw new BaowangDefaultException(ResultCode.DEPOSIT_AMOUNT_NOT_LE_ZERO);
        }
        IdReqVO idReqVO = new IdReqVO();
        idReqVO.setId(agentRechargeReqVo.getDepositWayId());
        SystemRechargeWayDetailRespVO systemRechargeWayDetailRespVO = systemRechargeWayApi.getInfoById(idReqVO).getData();
        if (null == systemRechargeWayDetailRespVO ) {
            throw new BaowangDefaultException(ResultCode.RECHARGE_WAY_NOT_EXIST);
        }
        if (systemRechargeWayDetailRespVO.getStatus().equals(EnableStatusEnum.DISABLE.getCode())) {
            throw new BaowangDefaultException(ResultCode.RECHARGE_WAY_DISABLE);
        }

        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(agentId);
        SiteRechargeWayVO siteRechargeWayVO = siteRechargeWayApi.queryRechargeWay(agentInfoPO.getSiteCode(),agentRechargeReqVo.getDepositWayId());
        if (null == siteRechargeWayVO ) {
            throw new BaowangDefaultException(ResultCode.RECHARGE_WAY_NOT_EXIST);
        }
        if (siteRechargeWayVO.getStatus().equals(EnableStatusEnum.DISABLE.getCode())) {
            throw new BaowangDefaultException(ResultCode.RECHARGE_WAY_DISABLE);
        }
        if(RechargeTypeEnum.BANK_CARD.getCode().equals(systemRechargeWayDetailRespVO.getRechargeTypeCode())
                && StringUtils.isBlank(agentRechargeReqVo.getDepositName())){
            throw new BaowangDefaultException(ResultCode.DEPOSIT_USER_NAME_IS_NULL);
        }

        //检查代理类型
        if(agentInfoPO.getAgentType().equals(Integer.parseInt(AgentTypeEnum.TEST.getCode()))){
            throw new BaowangDefaultException(ResultCode.CURRENT_ACCOUNT_NOT_DEPOSIT);
        }
        //校验代理账号状态
        if(agentInfoPO.getStatus().contains(AgentStatusEnum.DEPOSIT_WITHDRAWAL_LOCK.getCode())){
            throw new BaowangDefaultException(ResultCode.USER_LOGIN_LOCK);
        }
        //校验是否有三笔充值中订单
        checkThreeHandleOrder(agentId,agentInfoPO.getSiteCode());

        //二小时内存在5笔失败订单，一小时内不能再次充值
        checkContinueFiveFailOrder(agentId);


        AgentOrderNoVO orderNoVO = new AgentOrderNoVO();
        pay(orderNoVO,agentRechargeReqVo,agentInfoPO,systemRechargeWayDetailRespVO);


        return ResponseVO.success(orderNoVO);
    }
    private void checkContinueFiveFailOrder(String agentId){
        //如果存在暂停充值KEY 返回充值限制
        String rechargeLimitKey = "recharge::limit::" + agentId;
        if(RedisUtil.isKeyExist(rechargeLimitKey)){
            throw new BaowangDefaultException(ResultCode.RECHARGE_LIMIT);
        }
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
        Long startTime = DateUtils.addHour(System.currentTimeMillis(),-2);
        lqw.ge(AgentDepositWithdrawalPO::getCreatedTime,startTime);
        lqw.eq(AgentDepositWithdrawalPO::getAgentId,agentId);
        lqw.orderByDesc(AgentDepositWithdrawalPO::getUpdatedTime);
        lqw.eq(AgentDepositWithdrawalPO::getType, AgentDepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
        List<AgentDepositWithdrawalPO> agentDepositWithdrawalPO = agentDepositWithdrawalRepository.selectList(lqw);
        if(CollectionUtil.isNotEmpty(agentDepositWithdrawalPO) && agentDepositWithdrawalPO.size() >= CommonConstant.business_five){
            boolean flag = true;
            for (int i = 0;i<CommonConstant.business_five;i++ ){
                AgentDepositWithdrawalPO userDepositWithdrawalPO = agentDepositWithdrawalPO.get(i);
                if(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode().equals(userDepositWithdrawalPO.getStatus())
                        || AgentDepositWithdrawalOrderCustomerStatusEnum.PENDING.getCode().equals(userDepositWithdrawalPO.getCustomerStatus())){
                    flag = false;
                    break;
                }
            }
            if(flag){
                RedisUtil.setValue(rechargeLimitKey, rechargeLimitKey, 3600L, TimeUnit.SECONDS);
                throw new BaowangDefaultException(ResultCode.RECHARGE_LIMIT);
            }
        }
    }

    private void checkThreeHandleOrder(String agentId,String siteCode){
        List<String> statusList = List.of(DepositWithdrawalOrderStatusEnum.FIRST_WAIT.getCode(),
                DepositWithdrawalOrderStatusEnum.FIRST_AUDIT.getCode(),DepositWithdrawalOrderStatusEnum.ORDER_WAIT.getCode(),
                DepositWithdrawalOrderStatusEnum.ORDER_AUDIT.getCode(),DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
        lqw.eq(AgentDepositWithdrawalPO::getAgentId,agentId);
        lqw.eq(AgentDepositWithdrawalPO::getType, AgentDepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
        lqw.in(AgentDepositWithdrawalPO::getStatus,statusList);
        List<AgentDepositWithdrawalPO> agentDepositWithdrawalPOS = agentDepositWithdrawalRepository.selectList(lqw);
        SystemDictConfigRespVO systemDictConfigRespVO=  systemDictConfigApi.getByCode(DictCodeConfigEnums.MAX_ORDER_COUNT_IN_PROCESS.getCode(),siteCode).getData();
        int num = Integer.parseInt(systemDictConfigRespVO.getConfigParam());
        if(null != agentDepositWithdrawalPOS && agentDepositWithdrawalPOS.size() >= num){
            throw new BaowangDefaultException(systemDictConfigRespVO.getHintInfo());
        }
    }

    private void pay(AgentOrderNoVO orderNoVO, AgentRechargeReqVO agentRechargeReqVo, AgentInfoPO agentInfoPO,SystemRechargeWayDetailRespVO systemRechargeWayDetailRespVO){
        //获取充值方式，通道等信息
        String depositWayId = agentRechargeReqVo.getDepositWayId();



        BigDecimal amount = agentRechargeReqVo.getAmount();
        List<SiteSystemRechargeChannelRespVO>  filterChannelList =  checkRechargeChannel(depositWayId,agentInfoPO.getSiteCode(),amount);

        PlatCurrencyToTransferVO platCurrencyToTransferVO = new PlatCurrencyToTransferVO();
        platCurrencyToTransferVO.setSiteCode(agentInfoPO.getSiteCode());
        platCurrencyToTransferVO.setSourceCurrencyCode(systemRechargeWayDetailRespVO.getCurrencyCode());
        platCurrencyToTransferVO.setSourceAmt(amount);
        SiteCurrencyConvertRespVO responseVO = siteCurrencyInfoApi.transferToPlat(platCurrencyToTransferVO).getData();
        BigDecimal exchangeRate = responseVO.getTransferRate();
        amount = responseVO.getTargetAmount();
        //总控汇率
        BigDecimal wtcUsdExchangeRate =  siteCurrencyInfoApi.getCurrencyFinalRate(CommonConstant.business_zero_str, CurrencyEnum.USD.getCode());

        SiteSystemRechargeChannelRespVO systemRechargeChannelPO = new SiteSystemRechargeChannelRespVO();
        if(RechargeTypeEnum.CRYPTO_CURRENCY.getCode().equals(systemRechargeWayDetailRespVO.getRechargeTypeCode())){
            List<SiteSystemRechargeChannelRespVO> channelRespVOS = filterChannelList.stream()
                    .filter(p -> ChannelTypeEnum.SITE_CUSTOM.getCode().equals(p.getChannelType()))
                    .collect(Collectors.toList());
            if(null == channelRespVOS || channelRespVOS.isEmpty()){
                throw new BaowangDefaultException(ResultCode.NO_RECHARGE_CHANNEL_AVAILABLE);
            }
            systemRechargeChannelPO = channelRespVOS.get(0);
        }else {
            //获取上一次充值的订单
            AgentDepositWithdrawalPO lastAgentDepositWithdrawalPO = agentDepositWithdrawalRepository.selectLastRechargeOrderByAgentId(agentRechargeReqVo.getAgentId());

            if (null == lastAgentDepositWithdrawalPO) {
                //获取顺位第一的通道
                systemRechargeChannelPO = filterChannelList.get(0);
            } else {
                //如果上一笔订单为成功 ， 则取上次成功的通道 ,失败 顺延往下取，下面没有了 取第一顺位
                String lastChannelId = lastAgentDepositWithdrawalPO.getDepositWithdrawChannelId();
                SiteSystemRechargeChannelRespVO lastChannelPo = filterChannelList.stream()
                        .filter(p -> p.getId().equals(lastChannelId))
                        .findFirst()
                        .orElse(null);
                if (null == lastChannelPo) {
                    systemRechargeChannelPO = filterChannelList.get(0);
                } else {
                    if (DepositWithdrawalOrderStatusEnum.SUCCEED.getCode().equals(lastAgentDepositWithdrawalPO.getStatus())) {
                        systemRechargeChannelPO = lastChannelPo;
                    } else if (DepositWithdrawalOrderStatusEnum.FAIL.getCode().equals(lastAgentDepositWithdrawalPO.getStatus())) {
                        int num = filterChannelList.indexOf(lastChannelPo);
                        if (filterChannelList.size() > num + 1) {
                            systemRechargeChannelPO = filterChannelList.get(num + 1);
                        } else {
                            systemRechargeChannelPO = filterChannelList.get(0);
                        }
                    } else {
                        systemRechargeChannelPO = filterChannelList.get(0);
                    }
                }
            }
        }
        //获取主货币汇率
        RateCalculateRequestVO exchangeRateRequestVO = new RateCalculateRequestVO();
        exchangeRateRequestVO.setSiteCode(agentInfoPO.getSiteCode());
        exchangeRateRequestVO.setCurrencyCode(systemRechargeWayDetailRespVO.getCurrencyCode());
        exchangeRateRequestVO.setShowWay(ShowWayEnum.RECHARGE.getCode());
        BigDecimal currencyExchangeRate = exchangeRateConfigApi.getLatestRate(exchangeRateRequestVO);
        BigDecimal tradeCurrencyAmount = agentRechargeReqVo.getAmount();
        AgentDepositWithdrawalPO agentDepositWithdrawalPO = new AgentDepositWithdrawalPO();
        if(RechargeTypeEnum.CRYPTO_CURRENCY.getCode().equals(systemRechargeWayDetailRespVO.getRechargeTypeCode())){
            tradeCurrencyAmount = agentRechargeReqVo.getAmount().divide(currencyExchangeRate,2,RoundingMode.DOWN);
            agentDepositWithdrawalPO.setCoinCode(CurrencyEnum.USDT.getCode());
            PlatCurrencyToTransferVO platCurrencyToTransferVO1 = new PlatCurrencyToTransferVO();
            //修改为获取总控的汇率
            platCurrencyToTransferVO1.setSiteCode(CommonConstant.ADMIN_CENTER_SITE_CODE);
            platCurrencyToTransferVO1.setSourceCurrencyCode(CurrencyEnum.USDT.getCode());
            platCurrencyToTransferVO1.setSourceAmt(tradeCurrencyAmount);
            SiteCurrencyConvertRespVO responseVO1 = siteCurrencyInfoApi.transferToPlat(platCurrencyToTransferVO1).getData();

            BigDecimal arriveAmount = responseVO1.getTargetAmount();
            exchangeRate = responseVO1.getTransferRate();
            amount = arriveAmount;
            agentDepositWithdrawalPO.setExchangeRate(currencyExchangeRate);
        }else{
            agentDepositWithdrawalPO.setCoinCode(systemRechargeWayDetailRespVO.getCurrencyCode());
            agentDepositWithdrawalPO.setExchangeRate(exchangeRate);
        }



        //获取站点充值方式费率配置
        SiteRechargeWayFeeVO siteRechargeWayFee = systemRechargeWayApi.calculateSiteRechargeWayFeeRate(agentInfoPO.getSiteCode(),depositWayId,agentRechargeReqVo.getAmount(),systemRechargeChannelPO.getChannelType());

        BigDecimal settlementFeeAmount = siteRechargeWayFee.getWayFeeAmount();

        if(ChannelTypeEnum.SITE_CUSTOM.getCode().equals(systemRechargeChannelPO.getChannelType())){
            agentDepositWithdrawalPO.setWayFeeAmount(BigDecimal.ZERO);
        }
        String orderNo = "CK"+systemRechargeWayDetailRespVO.getCurrencyCode()+ DateUtils.dateToyyyyMMddHHmmss(new Date())+ SnowFlakeUtils.getRandomZm();

        agentDepositWithdrawalPO.setSiteCode(agentRechargeReqVo.getSiteCode());
        agentDepositWithdrawalPO.setAgentId(agentRechargeReqVo.getAgentId());
        agentDepositWithdrawalPO.setLevel(agentInfoPO.getLevel());
        agentDepositWithdrawalPO.setParentId(agentInfoPO.getParentId());
        agentDepositWithdrawalPO.setPath(agentInfoPO.getPath());
        agentDepositWithdrawalPO.setAgentAccount(agentRechargeReqVo.getAgentAccount());
        agentDepositWithdrawalPO.setApplyIp(agentRechargeReqVo.getApplyIp());
        agentDepositWithdrawalPO.setDeviceType(agentRechargeReqVo.getDeviceType());
        agentDepositWithdrawalPO.setApplyAmount(agentRechargeReqVo.getAmount());
        agentDepositWithdrawalPO.setTradeCurrencyAmount(tradeCurrencyAmount);
        agentDepositWithdrawalPO.setArriveAmount(amount);
        agentDepositWithdrawalPO.setPlatformCurrencyExchangeRate(exchangeRate);
        agentDepositWithdrawalPO.setWtcUsdExchangeRate(wtcUsdExchangeRate);
        agentDepositWithdrawalPO.setFeeType(siteRechargeWayFee.getFeeType());
        agentDepositWithdrawalPO.setSettlementFeeRate(siteRechargeWayFee.getWayFee());
        agentDepositWithdrawalPO.setSettlementFeePercentageAmount(siteRechargeWayFee.getWayFeePercentageAmount());
        agentDepositWithdrawalPO.setSettlementFeeFixedAmount(siteRechargeWayFee.getWayFeeFixedAmount());
        agentDepositWithdrawalPO.setSettlementFeeAmount(settlementFeeAmount);

        agentDepositWithdrawalPO.setCurrencyCode(systemRechargeWayDetailRespVO.getCurrencyCode());
        agentDepositWithdrawalPO.setDepositWithdrawTypeCode(systemRechargeWayDetailRespVO.getRechargeTypeCode());
        agentDepositWithdrawalPO.setDepositWithdrawTypeId(systemRechargeWayDetailRespVO.getRechargeTypeId());
        agentDepositWithdrawalPO.setDepositWithdrawWayId(agentRechargeReqVo.getDepositWayId());
        agentDepositWithdrawalPO.setDepositWithdrawWay(systemRechargeWayDetailRespVO.getRechargeWayI18());
        agentDepositWithdrawalPO.setAccountBranch(systemRechargeWayDetailRespVO.getNetworkType());
        agentDepositWithdrawalPO.setDepositWithdrawChannelId(systemRechargeChannelPO.getId());
        agentDepositWithdrawalPO.setDepositWithdrawChannelCode(systemRechargeChannelPO.getChannelCode());
        agentDepositWithdrawalPO.setDepositWithdrawChannelName(systemRechargeChannelPO.getChannelName());
        agentDepositWithdrawalPO.setDepositWithdrawChannelType(systemRechargeChannelPO.getChannelType());
        if(ChannelTypeEnum.SITE_CUSTOM.getCode().equals(systemRechargeChannelPO.getChannelType())){
            agentDepositWithdrawalPO.setRecvUserName(systemRechargeChannelPO.getRecvUserName());
            agentDepositWithdrawalPO.setRecvBankBranch(systemRechargeChannelPO.getRecvBankBranch());
            agentDepositWithdrawalPO.setRecvBankCode(systemRechargeChannelPO.getRecvBankCode());
            agentDepositWithdrawalPO.setRecvBankName(systemRechargeChannelPO.getRecvBankName());
            agentDepositWithdrawalPO.setRecvBankAccount(systemRechargeChannelPO.getRecvBankAccount());
            agentDepositWithdrawalPO.setRecvQrCode(systemRechargeChannelPO.getRecvQrCode());
            agentDepositWithdrawalPO.setDepositWithdrawAddress(systemRechargeChannelPO.getRecvBankCard());
        }
        agentDepositWithdrawalPO.setOrderNo(orderNo);
        agentDepositWithdrawalPO.setDepositWithdrawName(agentRechargeReqVo.getDepositName());
        agentDepositWithdrawalPO.setType(AgentDepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
        agentDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode());
        agentDepositWithdrawalPO.setCustomerStatus(AgentDepositWithdrawalOrderCustomerStatusEnum.PENDING.getCode());
        agentDepositWithdrawalPO.setCreatedTime(System.currentTimeMillis());
        agentDepositWithdrawalPO.setUpdatedTime(System.currentTimeMillis());
        agentDepositWithdrawalPO.setDeviceNo(agentRechargeReqVo.getDeviceNo());
        String paymentUrl = agentDepositWithdrawHandleService.depositApplySuccess(agentRechargeReqVo,agentDepositWithdrawalPO,systemRechargeChannelPO);
        orderNoVO.setThirdIsUrl(isURL(paymentUrl)?CommonConstant.business_one:CommonConstant.business_zero);
        orderNoVO.setThirdPayUrl(paymentUrl);
        orderNoVO.setChannelType(systemRechargeChannelPO.getChannelType());
        orderNoVO.setOrderNo(orderNo);

    }
    public boolean isURL(String str) {
        try {
            new URL(str);
            return true;
        } catch (java.net.MalformedURLException e) {
            return false;
        }
    }

    private  List<SiteSystemRechargeChannelRespVO> checkRechargeChannel(String depositWayId,String siteCode,BigDecimal amount){
        Map<String, List<SiteSystemRechargeChannelRespVO>> channelGroup =  systemRechargeWayApi.getChannelGroup(siteCode);
        List<SiteSystemRechargeChannelRespVO> channelPOS = channelGroup.get(depositWayId);
        if(null == channelPOS || channelPOS.isEmpty()){
            throw new BaowangDefaultException(ResultCode.NO_RECHARGE_CHANNEL_AVAILABLE);
        }
        BigDecimal rechargeMin = channelPOS.stream().map(SiteSystemRechargeChannelRespVO::getRechargeMin).min(BigDecimal::compareTo).get();
        BigDecimal rechargeMax = channelPOS.stream().map(SiteSystemRechargeChannelRespVO::getRechargeMax).max(BigDecimal::compareTo).get();

        if(amount.compareTo(rechargeMin) < 0){
            throw new BaowangDefaultException(ResultCode.NO_RECHARGE_CHANNEL_AVAILABLE);
        }
        if(amount.compareTo(rechargeMax) > 0){
            throw new BaowangDefaultException(ResultCode.NO_RECHARGE_CHANNEL_AVAILABLE);
        }
        List<SiteSystemRechargeChannelRespVO> filterChannelList = channelPOS.stream()
                .filter(p -> amount.compareTo(p.getRechargeMin())>=0  && amount.compareTo(p.getRechargeMax()) <=0)
                .collect(Collectors.toList());
        if(null == filterChannelList || filterChannelList.isEmpty()){
            throw new BaowangDefaultException(ResultCode.NO_RECHARGE_CHANNEL_AVAILABLE);
        }
        return filterChannelList;
    }

    private AgentDepositOrderDetailVO setDepositOrderDetail(AgentDepositWithdrawalPO agentDepositWithdrawalPO,AgentDepositOrderDetailVO agentDepositOrderDetailVO){
        BeanUtils.copyProperties(agentDepositWithdrawalPO,agentDepositOrderDetailVO);

        agentDepositOrderDetailVO.setAccountName(agentDepositWithdrawalPO.getDepositWithdrawName());
        agentDepositOrderDetailVO.setAccountAddress(agentDepositWithdrawalPO.getDepositWithdrawAddress());
        agentDepositOrderDetailVO.setExchangeRate(agentDepositWithdrawalPO.getExchangeRate());
        agentDepositOrderDetailVO.setTradeCurrencyAmount(agentDepositWithdrawalPO.getTradeCurrencyAmount());
        agentDepositOrderDetailVO.setCurrencyCode(agentDepositWithdrawalPO.getCurrencyCode());
        agentDepositOrderDetailVO.setNetworkType(agentDepositWithdrawalPO.getAccountBranch());
        agentDepositOrderDetailVO.setCoinCode(agentDepositWithdrawalPO.getCoinCode());
        agentDepositOrderDetailVO.setFeeRate(BigDecimal.ZERO);
        agentDepositOrderDetailVO.setFeeAmount(BigDecimal.ZERO);
        agentDepositOrderDetailVO.setPlatformExchangeRate(agentDepositWithdrawalPO.getPlatformCurrencyExchangeRate());
        agentDepositOrderDetailVO.setPlatformCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        agentDepositOrderDetailVO.setThirdPayUrl(agentDepositWithdrawalPO.getPayThirdUrl());
        if(StringUtils.isNotBlank(agentDepositWithdrawalPO.getFileKey())){
                agentDepositOrderDetailVO.setThirdPayUrl(agentDepositWithdrawalPO.getFileKey());
        }
        if(StringUtils.isNotBlank(agentDepositOrderDetailVO.getCashFlowFile())){
            String  domain = minioFileService.getMinioDomain();
            agentDepositOrderDetailVO.setVoucherFlag(CommonConstant.business_one);
//            userDepositOrderDetailVO.setCashFlowFile(userDepositWithdrawalPO.getCashFlowFile());
            String[] cashFlowFileArr = agentDepositOrderDetailVO.getCashFlowFile().split(",");
            List<String> cashFlowFileList = Arrays.asList(agentDepositOrderDetailVO.getCashFlowFile().split(",")).stream()
                    .map(s -> domain+"/"+s )
                    .toList();
            agentDepositOrderDetailVO.setCashFlowFileList(cashFlowFileList);
            agentDepositOrderDetailVO.setCashFlowFile(String.join(",",cashFlowFileList));

        }else{
            agentDepositOrderDetailVO.setVoucherFlag(CommonConstant.business_zero);
        }
        SystemDictConfigRespVO systemDictConfigRespVO=  systemDictConfigApi.getByCode(DictCodeConfigEnums.CLIENT_ORDER_TIMEOUT.getCode(),"").getData();
        Long time = Long.parseLong(systemDictConfigRespVO.getConfigParam())*60*1000;
        Long currentTime = System.currentTimeMillis();
        Long expiredTime = agentDepositWithdrawalPO.getCreatedTime()+ time;
        Long  remindTime = (expiredTime-currentTime)/1000;
        agentDepositOrderDetailVO.setRemindTime(remindTime < 0?0:remindTime);
        return agentDepositOrderDetailVO;
    }

    public AgentDepositOrderDetailVO depositOrderDetail(AgentOrderNoVO orderNoVO) {
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentDepositWithdrawalPO::getOrderNo,orderNoVO.getOrderNo());
        AgentDepositWithdrawalPO agentDepositWithdrawalPO = agentDepositWithdrawalRepository.selectOne(lqw);
        AgentDepositOrderDetailVO agentDepositOrderDetailVO = new AgentDepositOrderDetailVO();
        setDepositOrderDetail(agentDepositWithdrawalPO,agentDepositOrderDetailVO);
        return agentDepositOrderDetailVO;
    }

    public int  uploadVoucher(AgentDepositOrderFileVO depositOrderFileVO) {
       LambdaUpdateWrapper<AgentDepositWithdrawalPO> updateWrapper = new LambdaUpdateWrapper();
       updateWrapper.eq(AgentDepositWithdrawalPO::getOrderNo,depositOrderFileVO.getOrderNo());
       updateWrapper.set(AgentDepositWithdrawalPO::getCashFlowFile,depositOrderFileVO.getCashFlowFile());
       updateWrapper.set(AgentDepositWithdrawalPO::getCashFlowRemark,depositOrderFileVO.getCashFlowRemark());
        updateWrapper.set(AgentDepositWithdrawalPO::getPayTxId,depositOrderFileVO.getOrderHash());
       return  agentDepositWithdrawalRepository.update(null,updateWrapper);
   }
   public Integer cancelDepositOrder(AgentOrderNoVO orderNoVO) {
       LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
       lqw.eq(AgentDepositWithdrawalPO::getOrderNo,orderNoVO.getOrderNo());
       AgentDepositWithdrawalPO userDepositWithdrawalPO = agentDepositWithdrawalRepository.selectOne(lqw);
       if(!DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode().equals(userDepositWithdrawalPO.getStatus())){
           throw new BaowangDefaultException(ResultCode.CURRENT_STATUS_NOT_CANCEL);
       }
       userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.APPLICANT_CANCEL.getCode());
       userDepositWithdrawalPO.setCustomerStatus(AgentDepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode());

       return agentDepositWithdrawalRepository.updateById(userDepositWithdrawalPO);




   }

    public void urgeOrder(AgentOrderNoVO vo) {
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentDepositWithdrawalPO::getOrderNo,vo.getOrderNo());
        AgentDepositWithdrawalPO userDepositWithdrawalPO = agentDepositWithdrawalRepository.selectOne(lqw);
        userDepositWithdrawalPO.setUrgeOrder(CommonConstant.business_one);
        agentDepositWithdrawalRepository.updateById(userDepositWithdrawalPO);
    }

    public ResponseVO<AgentRechargeConfigVO> getRechargeConfig(AgentRechargeConfigRequestVO vo) {
        String rechargeWayId = vo.getRechargeWayId();
        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(vo.getAgentId());
        //检查代理类型
       /* if(!agentInfoPO.getAgentType().equals(String.valueOf(AgentTypeEnum.FORMAL.getCode()))
            && !agentInfoPO.getAgentType().equals(String.valueOf(AgentTypeEnum.COOPERATE.getCode()))){
            throw new BaowangDefaultException(ResultCode.CURRENT_ACCOUNT_NOT_DEPOSIT);
        }
        //校验代理账号状态
        if(agentInfoPO.getStatus().contains(AgentStatusEnum.DEPOSIT_WITHDRAWAL_LOCK.getCode())){
            throw new BaowangDefaultException(ResultCode.USER_LOGIN_LOCK);
        }*/

        Map<String, List<SiteSystemRechargeChannelRespVO>> channelGroup =  systemRechargeWayApi.getChannelGroup(vo.getSiteCode());
        List<SiteSystemRechargeChannelRespVO> channelPOS = channelGroup.get(vo.getRechargeWayId());
        if(null == channelPOS || channelPOS.isEmpty()){
            throw new BaowangDefaultException(ResultCode.NO_RECHARGE_CHANNEL_AVAILABLE);
        }
        RechargeConfigVO wayRechargeConfigVO = systemRechargeWayApi.getRechargeConfigBySiteCode(vo.getSiteCode(),rechargeWayId);
        AgentRechargeConfigVO agentRechargeConfigVO = ConvertUtil.entityToModel(wayRechargeConfigVO,AgentRechargeConfigVO.class);

        //充值不收手续费
        agentRechargeConfigVO.setFeeRate(BigDecimal.ZERO);

        IdReqVO idReqVO =  new IdReqVO();
        idReqVO.setId(rechargeWayId);
        SystemRechargeWayDetailRespVO systemRechargeWayDetailRespVO = systemRechargeWayApi.getInfoById(idReqVO).getData();
        //获取汇率
        RateCalculateRequestVO exchangeRateRequestVO = new RateCalculateRequestVO();
        exchangeRateRequestVO.setCurrencyCode(systemRechargeWayDetailRespVO.getCurrencyCode());
        exchangeRateRequestVO.setShowWay(ShowWayEnum.RECHARGE.getCode());
        exchangeRateRequestVO.setSiteCode(agentInfoPO.getSiteCode());
        BigDecimal exchangeRate = exchangeRateConfigApi.getLatestRate(exchangeRateRequestVO);
        agentRechargeConfigVO.setExchangeRate(exchangeRate);

        BigDecimal platformExchangeRate = siteCurrencyInfoApi.getCurrencyFinalRate(agentInfoPO.getSiteCode(), systemRechargeWayDetailRespVO.getCurrencyCode());
        if(RechargeTypeEnum.CRYPTO_CURRENCY.getCode().equals(systemRechargeWayDetailRespVO.getRechargeTypeCode())){
            platformExchangeRate = siteCurrencyInfoApi.getCurrencyFinalRate(agentInfoPO.getSiteCode(), systemRechargeWayDetailRespVO.getCurrencyCode());
        }
        //获取平台币汇率
        agentRechargeConfigVO.setPlatformExchangeRate(platformExchangeRate);

        agentRechargeConfigVO.setHaveThreeHandingOrder(CommonConstant.business_zero);
        //查询是否有n条充值中的订单
//        checkThreeHandleOrder(agentInfoPO.getAgentId(), agentInfoPO.getSiteCode());

        agentRechargeConfigVO.setQuickAmount(systemRechargeWayDetailRespVO.getQuickAmount());
        agentRechargeConfigVO.setCurrencyCode(systemRechargeWayDetailRespVO.getCurrencyCode());
        agentRechargeConfigVO.setRechargeWayId(rechargeWayId);
        agentRechargeConfigVO.setRechargeWay(systemRechargeWayDetailRespVO.getRechargeWayI18());
        //获取加密货币热钱包地址
        if(RechargeTypeEnum.CRYPTO_CURRENCY.getCode().equals(systemRechargeWayDetailRespVO.getRechargeTypeCode())){
            List<SiteSystemRechargeChannelRespVO> channelRespVOS = channelPOS.stream()
                    .filter(p -> ChannelTypeEnum.SITE_CUSTOM.getCode().equals(p.getChannelType()))
                    .collect(Collectors.toList());
            if(!channelRespVOS.isEmpty()){
                agentRechargeConfigVO.setChannelType(ChannelTypeEnum.SITE_CUSTOM.getCode());
            }else{
                GenHotWalletAddressReqVO genHotWalletAddressReqVO = new GenHotWalletAddressReqVO();
                genHotWalletAddressReqVO.setSiteCode(vo.getSiteCode());
                genHotWalletAddressReqVO.setOneId(agentInfoPO.getAgentId());
                genHotWalletAddressReqVO.setOneAccount(agentInfoPO.getAgentAccount());
                genHotWalletAddressReqVO.setNetworkType(systemRechargeWayDetailRespVO.getNetworkType());
                genHotWalletAddressReqVO.setOwnerUserType(OwnerUserTypeEnum.AGENT.getCode());
                genHotWalletAddressReqVO.setCurrencyCode(CurrencyEnum.USDT.getCode());
                ResponseVO<String> responseVO = hotWalletAddressApi.getHotWalletAddress(genHotWalletAddressReqVO);
                agentRechargeConfigVO.setAddress(responseVO.getData());
                agentRechargeConfigVO.setChannelType(ChannelTypeEnum.THIRD.getCode());
            }



        }
        String remindKey = "recharge::noRemind::" + vo.getAgentId()+"::"+systemRechargeWayDetailRespVO.getNetworkType()+"::"+systemRechargeWayDetailRespVO.getCurrencyCode();
        if (RedisUtil.isKeyExist(remindKey)) {
            agentRechargeConfigVO.setIsRemind(CommonConstant.business_zero);
        }else {
            agentRechargeConfigVO.setIsRemind(CommonConstant.business_one);
        }
        return ResponseVO.success(agentRechargeConfigVO);
    }

    public Page<ClientAgentRechargeRecordResponseVO> clientAgentRechargeRecorder(ClientAgentRechargeRecordRequestVO vo) {
        Page<AgentDepositWithdrawalPO> page = new Page<>(vo.getPageNumber(),vo.getPageSize());
        Page<ClientAgentRechargeRecordResponseVO> agentRechargeRecordResponseVOPage =  agentDepositWithdrawalRepository.rechargeRecordList(page,vo);

        return agentRechargeRecordResponseVOPage;
    }

    public AgentRechargeRecordDetailResponseVO clientAgentRechargeRecordDetail(AgentTradeRecordDetailRequestVO vo) {
        AgentRechargeRecordDetailResponseVO agentRechargeRecordDetailResponseVO = new AgentRechargeRecordDetailResponseVO();

        if(TradeRecordTypeEnum.MANUAL_UP.getCode().equals(vo.getTradeWayType())
                || TradeRecordTypeEnum.MANUAL_DOWN.getCode().equals(vo.getTradeWayType())){
            AgentManualUpDownDetailVO agentManualUpDownDetailVO = getManualUpDetail(vo.getOrderNo());
            agentManualUpDownDetailVO.setTradeWayType(vo.getTradeWayType());
            agentManualUpDownDetailVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
            agentRechargeRecordDetailResponseVO.setManualUpDownDetailVO(agentManualUpDownDetailVO);
        }else if(TradeRecordTypeEnum.SUPERIOR_TRANSFER.getCode().equals(vo.getTradeWayType())){
            AgentSuperTransferDetailVO agentSuperTransferDetailVO = getSuperTransferDetail(vo.getOrderNo());
            agentSuperTransferDetailVO.setTradeWayType(vo.getTradeWayType());
            agentSuperTransferDetailVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
            agentRechargeRecordDetailResponseVO.setSuperTransferDetailVO(agentSuperTransferDetailVO);
        }
        return agentRechargeRecordDetailResponseVO;

    }

    private AgentSuperTransferDetailVO getSuperTransferDetail(String orderNo){
        LambdaQueryWrapper<AgentTransferRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentTransferRecordPO::getOrderNo,orderNo);
        AgentTransferRecordPO agentTransferRecordPO = agentTransferRecordRepository.selectOne(lqw);
        AgentSuperTransferDetailVO agentSuperTransferDetailVO = new AgentSuperTransferDetailVO();
        agentSuperTransferDetailVO.setOrderNo(agentTransferRecordPO.getOrderNo());
        agentSuperTransferDetailVO.setSuperAgentAccount(agentTransferRecordPO.getAgentAccount());
        agentSuperTransferDetailVO.setArriveAmount(agentTransferRecordPO.getTransferAmount());
        agentSuperTransferDetailVO.setUpdatedTime(agentTransferRecordPO.getTransferTime());
        agentSuperTransferDetailVO.setCustomerStatus(CommonConstant.business_one_str);
        return agentSuperTransferDetailVO;
    }
    private AgentManualUpDownDetailVO getManualUpDetail(String orderNo){
        LambdaQueryWrapper<AgentManualUpDownRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentManualUpDownRecordPO::getOrderNo,orderNo);
        AgentManualUpDownRecordPO agentManualUpDownRecordPO = agentManualUpDownRecordRepository.selectOne(lqw);
        AgentManualUpDownDetailVO agentManualUpDownDetailVO = new AgentManualUpDownDetailVO();
        agentManualUpDownDetailVO.setOrderNo(agentManualUpDownRecordPO.getOrderNo());
        agentManualUpDownDetailVO.setCustomerStatus(CommonConstant.business_one_str);

        agentManualUpDownDetailVO.setUpdatedTime(agentManualUpDownRecordPO.getUpdatedTime());
        agentManualUpDownDetailVO.setArriveAmount(agentManualUpDownRecordPO.getAdjustAmount());
        return agentManualUpDownDetailVO;
    }
}
