package com.cloud.baowang.agent.service;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.agent.api.enums.AgentCoinBalanceTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentStatusEnum;
import com.cloud.baowang.agent.api.enums.AgentTypeEnum;
import com.cloud.baowang.agent.api.enums.depositWithdrawal.AgentDepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.agent.api.enums.depositWithdrawal.AgentDepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinAddVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentCallbackDepositParamVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentCallbackWithdrawParamVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentVirtualCurrencyPayCallbackVO;
import com.cloud.baowang.agent.po.AgentCoinRecordPO;
import com.cloud.baowang.agent.po.AgentDepositWithdrawalPO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.repositories.AgentCoinRecordRepository;
import com.cloud.baowang.agent.repositories.AgentDepositWithdrawalRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.PayProcessStatusEnum;
import com.cloud.baowang.wallet.api.enums.wallet.ChannelTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.ChannelTypeEnums;
import com.cloud.baowang.wallet.api.enums.wallet.RechargeTypeEnum;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.pay.api.enums.VirtualCurrencyPayTypeEnum;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.exchange.ExchangeRateConfigApi;
import com.cloud.baowang.system.api.enums.exchange.ShowWayEnum;
import com.cloud.baowang.system.api.vo.exchange.RateCalculateRequestVO;
import com.cloud.baowang.wallet.api.api.HotWalletAddressApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.SystemRechargeChannelApi;
import com.cloud.baowang.wallet.api.api.SystemRechargeWayApi;
import com.cloud.baowang.wallet.api.vo.IdReqVO;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.recharge.HotWalletAddressVO;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyToTransferVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyConvertRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayDetailRespVO;
import com.cloud.baowang.wallet.api.vo.withdraw.RechargeSuccessVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayFeeVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AgentDepositWithdrawCallbackService {

    private final AgentDepositWithdrawalRepository agentDepositWithdrawalRepository;

    private final AgentInfoService agentInfoService;

    private final AgentDepositWithdrawHandleService agentDepositWithdrawHandleService;


    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final HotWalletAddressApi hotWalletAddressApi;

    private final SystemRechargeChannelApi systemRechargeChannelApi;

    private final SystemRechargeWayApi systemRechargeWayApi;

    private final ExchangeRateConfigApi exchangeRateConfigApi;

    private final SystemDictConfigApi systemDictConfigApi;

    private final AgentCoinRecordRepository agentCoinRecordRepository;

    /**
     * 代理出款回调处理
     *
     * @param callbackWithdrawParamVO
     * @return
     */
    @DistributedLock(name = RedisKeyTransUtil.AGENT_WITHDRAW_CALLBACK, unique = "#callbackWithdrawParamVO.orderNo", waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    public boolean agentWithdrawCallback(final AgentCallbackWithdrawParamVO callbackWithdrawParamVO) {
        String orderNo = callbackWithdrawParamVO.getOrderNo();
        log.info("三方代付订单号:{} 接收回调消息开始,消息体:{}", orderNo,
                callbackWithdrawParamVO);
        try {
            LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
            lqw.eq(AgentDepositWithdrawalPO::getOrderNo, orderNo);
            AgentDepositWithdrawalPO agentDepositWithdrawalPO = agentDepositWithdrawalRepository.selectOne(lqw);
            if (ObjectUtil.isEmpty(agentDepositWithdrawalPO)) {
                log.error("该笔订单:{},订单不存在", orderNo);
                return false;
            }
            if (!agentDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode())) {
                log.info("订单{} 状态不为处理中 {}", agentDepositWithdrawalPO.getOrderNo(), agentDepositWithdrawalPO.getStatus());
                return false;
            }
            if (ObjectUtil.isNotEmpty(agentDepositWithdrawalPO)) {
//                LambdaUpdateWrapper<agentDepositWithdrawalPO> updateWrapper = new LambdaUpdateWrapper<>();
                agentDepositWithdrawalPO.setPayTxId(callbackWithdrawParamVO.getPayId());
                agentDepositWithdrawalPO.setChannelCode(callbackWithdrawParamVO.getPayCode());
                agentDepositWithdrawalPO.setPayAuditTime(System.currentTimeMillis());
                if(ChannelTypeEnums.OFFLINE.getDesc().equals(agentDepositWithdrawalPO.getPayoutType())){
                    agentDepositWithdrawalPO.setWayFeeAmount(BigDecimal.ZERO);
                }
                Long updatedTime = agentDepositWithdrawalPO.getUpdatedTime();
                if (callbackWithdrawParamVO.getStatus() == ThirdPayOrderStatusEnum.Success.getCode()) {
                    agentDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
                    agentDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.SUCCESS.getCode());
                    agentDepositWithdrawalPO.setPayProcessStatus(PayProcessStatusEnum.SUCCESS.getCode());
                    agentDepositWithdrawalPO.setRechargeWithdrawTimeConsuming(System.currentTimeMillis()-updatedTime);
                    agentDepositWithdrawalPO.setUpdatedTime(System.currentTimeMillis());

                    expenses(agentDepositWithdrawalPO);
                } else if (callbackWithdrawParamVO.getStatus() == ThirdPayOrderStatusEnum.Fail.getCode()) {
                    agentDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.WITHDRAW_FAIL.getCode());
                    agentDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode());
                    agentDepositWithdrawalPO.setPayProcessStatus(PayProcessStatusEnum.ABNORMAL.getCode());
                    agentDepositWithdrawalPO.setUpdatedTime(System.currentTimeMillis());
                    unFreeze(agentDepositWithdrawalPO, callbackWithdrawParamVO.getRemark());
                } else if (callbackWithdrawParamVO.getStatus() == ThirdPayOrderStatusEnum.Pending.getCode()) {
                    if (StringUtils.isBlank(agentDepositWithdrawalPO.getPayProcessStatus())) {
                        agentDepositWithdrawalPO.setPayProcessStatus(PayProcessStatusEnum.GETTING.getCode());
                        agentDepositWithdrawalRepository.updateById(agentDepositWithdrawalPO);
                    }
                }
            } else {
                log.info("该笔订单:{},订单状态不是待处理状态", orderNo);
            }
        } catch (Exception e) {
            log.error("支付订单号:{}, 用户账号:{}, 三方支付id:{},三方支付code:{} 发生异常", orderNo,
                    callbackWithdrawParamVO.getAgentAccount(), callbackWithdrawParamVO.getPayId(), callbackWithdrawParamVO.getPayCode(), e.getMessage());
            return false;
        }
        return true;
    }

    private Boolean unFreeze(AgentDepositWithdrawalPO agentDepositWithdrawalPO, String remark) {
        AgentCoinAddVO agentCoinAddVO = new AgentCoinAddVO();
        agentCoinAddVO.setAgentAccount(agentDepositWithdrawalPO.getAgentAccount());
        agentCoinAddVO.setAgentWalletType(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        agentCoinAddVO.setOrderNo(agentDepositWithdrawalPO.getOrderNo());
        agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum.AGENT_WITHDRAWAL_FAIL.getCode());
        agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.WITHDRAWAL.getCode());
        agentCoinAddVO.setBalanceType(AgentCoinBalanceTypeEnum.UN_FREEZE.getCode());
        agentCoinAddVO.setCoinValue(agentDepositWithdrawalPO.getArriveAmount());
        agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_WITHDRAWAL.getCode());
        agentCoinAddVO.setCoinTime(agentDepositWithdrawalPO.getUpdatedTime());
        agentCoinAddVO.setRemark(remark);
        AgentInfoVO agentInfoVO = agentInfoService.getByAgentAccountSite(agentDepositWithdrawalPO.getSiteCode(),agentDepositWithdrawalPO.getAgentAccount());
        agentCoinAddVO.setAgentInfo(agentInfoVO);
        agentDepositWithdrawHandleService.withdrawFail(agentDepositWithdrawalPO,null,agentCoinAddVO);
        return true;
    }

    private Boolean expenses(AgentDepositWithdrawalPO agentDepositWithdrawalPO) {
        AgentCoinAddVO agentCoinAddVO = new AgentCoinAddVO();
        agentCoinAddVO.setAgentAccount(agentDepositWithdrawalPO.getAgentAccount());
        agentCoinAddVO.setAgentWalletType(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
        agentCoinAddVO.setOrderNo(agentDepositWithdrawalPO.getOrderNo());
        agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum.AGENT_WITHDRAWAL.getCode());
        agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.WITHDRAWAL.getCode());
        agentCoinAddVO.setBalanceType(AgentCoinBalanceTypeEnum.EXPENSES.getCode());
        agentCoinAddVO.setCoinValue(agentDepositWithdrawalPO.getArriveAmount());
        agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_WITHDRAWAL.getCode());
        agentCoinAddVO.setWithdrawFlag(CommonConstant.business_one);
        agentCoinAddVO.setCoinTime(agentDepositWithdrawalPO.getUpdatedTime());
        agentCoinAddVO.setToThridCode(agentDepositWithdrawalPO.getDepositWithdrawChannelCode());
        agentCoinAddVO.setThirdOrderNo(agentDepositWithdrawalPO.getPayTxId());
        agentCoinAddVO.setRemark("代理提款");
        AgentInfoVO agentInfoVO = agentInfoService.getByAgentAccountSite(agentDepositWithdrawalPO.getSiteCode(),agentDepositWithdrawalPO.getAgentAccount());
        agentCoinAddVO.setAgentInfo(agentInfoVO);
        agentDepositWithdrawHandleService.withdrawSuccess(agentDepositWithdrawalPO,agentCoinAddVO);
        return true;
    }


    /**
     * 代理充值回调处理
     *
     * @param callbackDepositParamVO
     * @return
     */
    @DistributedLock(name = RedisKeyTransUtil.AGENT_DEPOSIT, unique = "#callbackDepositParamVO.orderNo", waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    public boolean depositCallback(AgentCallbackDepositParamVO callbackDepositParamVO) {
        String orderNo = callbackDepositParamVO.getOrderNo();
        log.info("代理三方充值支付订单号:{} 接收回调消息开始,消息体:{}", orderNo, callbackDepositParamVO);
        String agentAccount = callbackDepositParamVO.getAgentAccount();
        BigDecimal amount = callbackDepositParamVO.getAmount();
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
        lqw.eq(AgentDepositWithdrawalPO::getOrderNo, orderNo);
        AgentDepositWithdrawalPO agentDepositWithdrawalPO = agentDepositWithdrawalRepository.selectOne(lqw);
        if (ObjectUtil.isEmpty(agentDepositWithdrawalPO)) {
            log.error("该笔订单:{},订单不存在", orderNo);
            return false;
        }
        if (!agentDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode())) {
            log.info("订单{} 状态不为处理中 {}", agentDepositWithdrawalPO.getOrderNo(), agentDepositWithdrawalPO.getStatus());
            return false;
        }
        BigDecimal callbackAmount = callbackDepositParamVO.getAmount();
        if(!ChannelTypeEnum.SITE_CUSTOM.getCode().equals(agentDepositWithdrawalPO.getDepositWithdrawChannelType())){
            if(CurrencyEnum.KVND.getCode().equals(agentDepositWithdrawalPO.getCurrencyCode())){
                callbackAmount = callbackAmount.divide(new BigDecimal(1000));
            }
            if (agentDepositWithdrawalPO.getApplyAmount().compareTo(callbackAmount) != 0) {
                log.error("订单:{},回调金额{}与申请金额不一致", orderNo,callbackAmount,agentDepositWithdrawalPO.getApplyAmount());
                return false;
            }
        }else{
            if (callbackDepositParamVO.getStatus() == ThirdPayOrderStatusEnum.Success.getCode()) {
                if (agentDepositWithdrawalPO.getApplyAmount().compareTo(callbackAmount) != 0) {
                    agentDepositWithdrawalPO.setApplyAmount(callbackAmount);
                    //获取站点充值方式费率配置
                    SiteRechargeWayFeeVO siteRechargeWayFee = systemRechargeWayApi.calculateSiteRechargeWayFeeRate(agentDepositWithdrawalPO.getSiteCode(), agentDepositWithdrawalPO.getDepositWithdrawWayId(), agentDepositWithdrawalPO.getArriveAmount(), agentDepositWithdrawalPO.getDepositWithdrawChannelType());
                    if (RechargeTypeEnum.CRYPTO_CURRENCY.getCode().equals(agentDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                        agentDepositWithdrawalPO.setTradeCurrencyAmount(callbackDepositParamVO.getTradeCurrencyAmount());
                        BigDecimal targetAmount = AmountUtils.divide(callbackDepositParamVO.getTradeCurrencyAmount(), agentDepositWithdrawalPO.getPlatformCurrencyExchangeRate());
                        agentDepositWithdrawalPO.setArriveAmount(targetAmount);
                    } else {
                        BigDecimal targetAmount = AmountUtils.divide(callbackAmount, agentDepositWithdrawalPO.getPlatformCurrencyExchangeRate());
                        agentDepositWithdrawalPO.setArriveAmount(targetAmount);
                        agentDepositWithdrawalPO.setTradeCurrencyAmount(callbackAmount);
                    }
                    agentDepositWithdrawalPO.setFeeType(siteRechargeWayFee.getFeeType());
                    agentDepositWithdrawalPO.setWayFeeAmount(BigDecimal.ZERO);
                    agentDepositWithdrawalPO.setSettlementFeeRate(siteRechargeWayFee.getWayFee());
                    agentDepositWithdrawalPO.setSettlementFeePercentageAmount(siteRechargeWayFee.getWayFeePercentageAmount());
                    agentDepositWithdrawalPO.setSettlementFeeFixedAmount(siteRechargeWayFee.getWayFeeFixedAmount());
                    agentDepositWithdrawalPO.setSettlementFeeAmount(siteRechargeWayFee.getWayFeeAmount());
                }
            }
        }

        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(agentDepositWithdrawalPO.getAgentId());
        if(null == agentInfoPO){
            log.info("未找到该代理{}",agentDepositWithdrawalPO.getAgentId());
            return false;
        }
        if(agentInfoPO.getAgentType().equals(Integer.parseInt(AgentTypeEnum.TEST.getCode()))){
            log.info("该代理{} 为测试",agentDepositWithdrawalPO.getAgentId());
            return false;
        }
        //校验代理账号状态
        if(agentInfoPO.getStatus().contains(AgentStatusEnum.DEPOSIT_WITHDRAWAL_LOCK.getCode())){
            log.info("该代理{} 已被充提锁定",agentDepositWithdrawalPO.getAgentId());
            return false;
        }
        SystemRechargeWayDetailRespVO systemRechargeWayDetailRespVO = systemRechargeWayApi.getRechargeWayByCurrencyAndNetworkType(agentDepositWithdrawalPO.getCurrencyCode()
                ,"",agentDepositWithdrawalPO.getSiteCode(),agentDepositWithdrawalPO.getDepositWithdrawWayId());
        if(null == systemRechargeWayDetailRespVO || !CommonConstant.business_one.equals(systemRechargeWayDetailRespVO.getStatus())){
            log.error("充值方式{},已被禁用", agentDepositWithdrawalPO.getDepositWithdrawWayId());
            return false;
        }
        IdVO idVO = new IdVO();
        idVO.setId(agentDepositWithdrawalPO.getDepositWithdrawChannelId());
        SystemRechargeChannelBaseVO systemRechargeChannelBaseVO = systemRechargeChannelApi.getChannelInfoByChannelId(agentDepositWithdrawalPO.getCurrencyCode(),
                systemRechargeWayDetailRespVO.getId(),
                agentDepositWithdrawalPO.getSiteCode(),agentDepositWithdrawalPO.getDepositWithdrawChannelId());
        if(null == systemRechargeChannelBaseVO || !CommonConstant.business_one.equals(systemRechargeChannelBaseVO.getStatus())){
            log.error("充值通道{},已被禁用", agentDepositWithdrawalPO.getDepositWithdrawChannelId());
            return false;
        }

        if (StringUtils.isNotBlank(callbackDepositParamVO.getPayId())) {
            agentDepositWithdrawalPO.setPayTxId(callbackDepositParamVO.getPayId());
        }
        agentDepositWithdrawalPO.setPayAuditTime(System.currentTimeMillis());
        Long updatedTime = agentDepositWithdrawalPO.getUpdatedTime();
        if (callbackDepositParamVO.getStatus() == ThirdPayOrderStatusEnum.Success.getCode()) {
            agentDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
            agentDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.SUCCESS.getCode());
            agentDepositWithdrawalPO.setPayProcessStatus(PayProcessStatusEnum.SUCCESS.getCode());
            agentDepositWithdrawalPO.setRechargeWithdrawTimeConsuming(System.currentTimeMillis()-updatedTime);
            agentDepositWithdrawalPO.setUpdatedTime(System.currentTimeMillis());
//            agentDepositWithdrawalPO.setArriveAmount(amount);
            handleDepositSuccess(agentDepositWithdrawalPO,ConvertUtil.entityToModel(agentInfoPO,AgentInfoVO.class), callbackDepositParamVO.getRemark(),false);
        } else if (callbackDepositParamVO.getStatus() == ThirdPayOrderStatusEnum.Fail.getCode()) {
            agentDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.FAIL.getCode());
            agentDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode());
            agentDepositWithdrawalPO.setPayProcessStatus(PayProcessStatusEnum.ABNORMAL.getCode());
            agentDepositWithdrawalPO.setUpdatedTime(System.currentTimeMillis());
            agentDepositWithdrawalRepository.updateById(agentDepositWithdrawalPO);
            //发送 失败mq
            List<String> userIds = new ArrayList<>();
            userIds.add(agentDepositWithdrawalPO.getAgentId());
            RechargeSuccessVO rechargeSuccessVO = new RechargeSuccessVO();
            rechargeSuccessVO.setOrderNo(agentDepositWithdrawalPO.getOrderNo());
            rechargeSuccessVO.setCustomerStatus(agentDepositWithdrawalPO.getCustomerStatus());
            rechargeSuccessVO.setUpdatedTime(agentDepositWithdrawalPO.getUpdatedTime());
            agentDepositWithdrawHandleService.sendWebSocketMessage(agentDepositWithdrawalPO.getSiteCode(),userIds,rechargeSuccessVO);
        } else if (callbackDepositParamVO.getStatus() == ThirdPayOrderStatusEnum.Pending.getCode()) {
            if (StringUtils.isBlank(agentDepositWithdrawalPO.getPayProcessStatus())) {
                agentDepositWithdrawalPO.setPayProcessStatus(PayProcessStatusEnum.GETTING.getCode());
                agentDepositWithdrawalRepository.updateById(agentDepositWithdrawalPO);
            }
        }
        return true;
    }

    /**
     * 处理充值成功 实际到账
     *
     * @param agentDepositWithdrawalPO
     * @param remark                  账变记录备注
     */
    public void handleDepositSuccess(AgentDepositWithdrawalPO agentDepositWithdrawalPO,AgentInfoVO agentInfoVO, String remark,boolean isNew) {

        AgentCoinAddVO agentCoinAddVO = new AgentCoinAddVO();
        agentCoinAddVO.setAgentId(agentInfoVO.getAgentId());
        agentCoinAddVO.setAgentWalletType(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.QUOTA_WALLET.getCode());
        agentCoinAddVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        agentCoinAddVO.setAgentAccount(agentDepositWithdrawalPO.getAgentAccount());
        agentCoinAddVO.setOrderNo(agentDepositWithdrawalPO.getOrderNo());
        agentCoinAddVO.setCoinType(AgentCoinRecordTypeEnum.AgentCoinTypeEnum.AGENT_DEPOSIT.getCode());
        agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.DEPOSIT.getCode());
        agentCoinAddVO.setBalanceType(AgentCoinBalanceTypeEnum.INCOME.getCode());
        agentCoinAddVO.setCoinValue(agentDepositWithdrawalPO.getArriveAmount());
        agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_DEPOSIT.getCode());
        agentCoinAddVO.setRemark(agentDepositWithdrawalPO.getPayTxId());
        agentCoinAddVO.setAgentInfo(agentInfoVO);
        agentCoinAddVO.setCoinTime(agentDepositWithdrawalPO.getUpdatedTime());
        agentCoinAddVO.setToThridCode(agentDepositWithdrawalPO.getDepositWithdrawChannelCode());
        agentCoinAddVO.setThirdOrderNo(agentDepositWithdrawalPO.getPayTxId());
        // 统一更新插入充值成功的相关信息
        agentDepositWithdrawHandleService.depositSuccess(agentDepositWithdrawalPO, agentCoinAddVO,isNew);

    }

    @DistributedLock(name = RedisKeyTransUtil.AGENT_DEPOSIT, unique = "#vo.userAddress", waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    public Boolean virtualCurrencyDepositCallback(AgentVirtualCurrencyPayCallbackVO vo) {
        //todo update 创建订单，添加账变
        log.info("虚拟币充值成功通知,消息体:{}", vo);
        String agentId = vo.getOwnerUserId();
        String address = vo.getUserAddress();
        /*SystemDictConfigRespVO systemDictConfigRespVO=  systemDictConfigApi.getByCode(DictCodeConfigEnums.USDT_MIN_THRESHOLD.getCode(),"").getData();
        if(null != systemDictConfigRespVO && StringUtils.isNotBlank(systemDictConfigRespVO.getConfigParam())){
            if(vo.getTradeAmount().compareTo(new BigDecimal(systemDictConfigRespVO.getConfigParam())) < 0){
                log.error("充值金额{},小于系统配置金额{}", vo.getTradeAmount(), systemDictConfigRespVO.getConfigParam());
                return false;
            }
        }*/
        HotWalletAddressVO hotWalletAddressVO = hotWalletAddressApi.queryHotWalletAddress(address);
        if (null == hotWalletAddressVO) {
            log.error("充值地址未找到,充值通知返回的代理id{},热钱包地址", vo.getOwnerUserId(), address);
            return false;
        }
        LambdaQueryWrapper<AgentDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
        lqw.eq(AgentDepositWithdrawalPO::getAgentId, vo.getOwnerUserId());
        lqw.eq(AgentDepositWithdrawalPO::getPayTxId, vo.getTradeHash());
        AgentDepositWithdrawalPO agentDepositWithdrawalPOquery = agentDepositWithdrawalRepository.selectOne(lqw);
        if (ObjectUtil.isNotEmpty(agentDepositWithdrawalPOquery)) {
            log.error("该笔订单已处理,代理id:{},热钱包地址", vo.getOwnerUserId());
            return false;
        }
        LambdaQueryWrapper<AgentCoinRecordPO> agentCoinRecordLqw = new LambdaQueryWrapper<>();
        agentCoinRecordLqw.eq(AgentCoinRecordPO::getRemark,vo.getTradeHash());
        AgentCoinRecordPO agentCoinRecordPO = agentCoinRecordRepository.selectOne(agentCoinRecordLqw);

        if (ObjectUtil.isNotEmpty(agentCoinRecordPO)) {
            log.error("该笔订单产生账变,代理id:{},热钱包地址", vo.getOwnerUserId());
            return false;
        }

        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(vo.getOwnerUserId());
        if (null == agentInfoPO) {
            log.error("充值通知未找到该代理{}", vo.getOwnerUserId());
            return false;
        }
        if(agentInfoPO.getAgentType().equals(Integer.parseInt(AgentTypeEnum.TEST.getCode()))){
            log.error("充值通知该代理{}类型不能充值", vo.getOwnerUserId());
            return false;
        }
        //校验代理账号状态
        if(agentInfoPO.getStatus().contains(AgentStatusEnum.DEPOSIT_WITHDRAWAL_LOCK.getCode())){
            log.error("充值通知该代理{}状态充提限制", vo.getOwnerUserId());
            return false;
        }
        String currencyCode = hotWalletAddressVO.getCurrencyCode();

        SystemRechargeWayDetailRespVO systemRechargeWayDetailRespVO = systemRechargeWayApi.getRechargeWayByCurrencyAndNetworkType(currencyCode,hotWalletAddressVO.getNetworkType()
                ,hotWalletAddressVO.getSiteCode(),"");
        if(null == systemRechargeWayDetailRespVO || !CommonConstant.business_one.equals(systemRechargeWayDetailRespVO.getStatus())){
            log.info("充值通知未找到对应的充值方式");
            return false;
        }

        SystemRechargeChannelBaseVO systemRechargeChannelBaseVO = systemRechargeChannelApi.getChannelInfoByCurrencyAndWayId(currencyCode,systemRechargeWayDetailRespVO.getId(),
                hotWalletAddressVO.getSiteCode(),"");
        if(null == systemRechargeChannelBaseVO || !CommonConstant.business_one.equals(systemRechargeChannelBaseVO.getStatus())){
            log.info("充值通知未找到对应的充值通道");
            return false;
        }
        String rechargeWayId = systemRechargeChannelBaseVO.getRechargeWayId();
        IdReqVO idReqVO = new IdReqVO();
        idReqVO.setId(rechargeWayId);
        //总控汇率
        BigDecimal wtcUsdExchangeRate =  siteCurrencyInfoApi.getCurrencyFinalRate(CommonConstant.business_zero_str, CurrencyEnum.USD.getCode());

        AgentDepositWithdrawalPO agentDepositWithdrawalPO = new AgentDepositWithdrawalPO();
        agentDepositWithdrawalPO.setCoinCode(vo.getCoinCode());
        agentDepositWithdrawalPO.setDepositWithdrawTypeId(systemRechargeWayDetailRespVO.getRechargeTypeId());
        agentDepositWithdrawalPO.setDepositWithdrawTypeCode(systemRechargeWayDetailRespVO.getRechargeTypeCode());
        agentDepositWithdrawalPO.setDepositWithdrawWayId(systemRechargeWayDetailRespVO.getId());
        agentDepositWithdrawalPO.setDepositWithdrawWay(systemRechargeWayDetailRespVO.getRechargeWayI18());
        agentDepositWithdrawalPO.setDepositWithdrawChannelCode(systemRechargeChannelBaseVO.getChannelCode());
        agentDepositWithdrawalPO.setDepositWithdrawChannelId(systemRechargeChannelBaseVO.getId());
        agentDepositWithdrawalPO.setDepositWithdrawChannelType(systemRechargeChannelBaseVO.getChannelType());
        agentDepositWithdrawalPO.setDepositWithdrawChannelName(systemRechargeChannelBaseVO.getChannelName());
        agentDepositWithdrawalPO.setWtcUsdExchangeRate(wtcUsdExchangeRate);
        BigDecimal tradeAmount = vo.getTradeAmount();
        //获取主货币汇率
        RateCalculateRequestVO exchangeRateRequestVO = new RateCalculateRequestVO();
        exchangeRateRequestVO.setSiteCode(agentInfoPO.getSiteCode());
        exchangeRateRequestVO.setCurrencyCode(currencyCode);
        exchangeRateRequestVO.setShowWay(ShowWayEnum.RECHARGE.getCode());
        BigDecimal currencyExchangeRate = exchangeRateConfigApi.getLatestRate(exchangeRateRequestVO);
        BigDecimal applyAmount = tradeAmount.multiply(currencyExchangeRate).setScale(4,RoundingMode.DOWN);

        PlatCurrencyToTransferVO platCurrencyToTransferVO = new PlatCurrencyToTransferVO();
        platCurrencyToTransferVO.setSiteCode(agentInfoPO.getSiteCode());
        platCurrencyToTransferVO.setSourceCurrencyCode(vo.getCoinCode());
        platCurrencyToTransferVO.setSourceAmt(tradeAmount);
        SiteCurrencyConvertRespVO responseVO = siteCurrencyInfoApi.transferToPlat(platCurrencyToTransferVO).getData();
        BigDecimal exchangeRate = responseVO.getTransferRate();
        BigDecimal arriveAmount = responseVO.getTargetAmount();

        AgentInfoVO agentInfoVO = ConvertUtil.entityToModel(agentInfoPO, AgentInfoVO.class);
        String orderNo = "CK" + currencyCode + DateUtils.dateToyyyyMMddHHmmss(new Date()) + SnowFlakeUtils.getRandomZm();

        //获取站点充值方式费率配置
        SiteRechargeWayFeeVO siteRechargeWayFee = systemRechargeWayApi.calculateSiteRechargeWayFeeRate(agentInfoPO.getSiteCode(),systemRechargeWayDetailRespVO.getId(),applyAmount,systemRechargeChannelBaseVO.getChannelType());
        //计算手续费
        BigDecimal settlementFeeAmount = siteRechargeWayFee.getWayFeeAmount();

        if(VirtualCurrencyPayTypeEnum.MINI_RECHARGE.getCode().equals(vo.getOrderType())){
            agentDepositWithdrawalPO.setCombinedRecharge(CommonConstant.business_one);
        }
        agentDepositWithdrawalPO.setAgentId(agentInfoVO.getAgentId());
        agentDepositWithdrawalPO.setAgentAccount(agentInfoVO.getAgentAccount());
        agentDepositWithdrawalPO.setSiteCode(agentInfoVO.getSiteCode());
        agentDepositWithdrawalPO.setLevel(agentInfoVO.getLevel());
        agentDepositWithdrawalPO.setParentId(agentInfoVO.getParentId());
        agentDepositWithdrawalPO.setPath(agentInfoVO.getPath());
        agentDepositWithdrawalPO.setAccountBranch(vo.getNetworkType());
        agentDepositWithdrawalPO.setAccountType(vo.getChainType());
        agentDepositWithdrawalPO.setDepositWithdrawAddress(vo.getUserAddress());
        agentDepositWithdrawalPO.setCurrencyCode(currencyCode);
        agentDepositWithdrawalPO.setDepositWithdrawName(agentInfoVO.getName());
        agentDepositWithdrawalPO.setApplyAmount(applyAmount);
        agentDepositWithdrawalPO.setWayFeeAmount(siteRechargeWayFee.getWayFeeAmount());
        agentDepositWithdrawalPO.setFeeType(siteRechargeWayFee.getFeeType());
        agentDepositWithdrawalPO.setSettlementFeePercentageAmount(siteRechargeWayFee.getWayFeePercentageAmount());
        agentDepositWithdrawalPO.setSettlementFeeFixedAmount(siteRechargeWayFee.getWayFeeFixedAmount());
        agentDepositWithdrawalPO.setSettlementFeeRate(siteRechargeWayFee.getWayFee());
        agentDepositWithdrawalPO.setSettlementFeeAmount(settlementFeeAmount);
        agentDepositWithdrawalPO.setPayTxId(vo.getTradeHash());
        agentDepositWithdrawalPO.setTradeCurrencyAmount(vo.getTradeAmount());
        agentDepositWithdrawalPO.setArriveAmount(arriveAmount);
        agentDepositWithdrawalPO.setExchangeRate(currencyExchangeRate);
        agentDepositWithdrawalPO.setPlatformCurrencyExchangeRate(exchangeRate);
        agentDepositWithdrawalPO.setOrderNo(orderNo);
        agentDepositWithdrawalPO.setType(AgentDepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
        agentDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        agentDepositWithdrawalPO.setCustomerStatus(AgentDepositWithdrawalOrderCustomerStatusEnum.SUCCESS.getCode());
        Long currentTime = System.currentTimeMillis();
        agentDepositWithdrawalPO.setPayAuditTime(currentTime);
        agentDepositWithdrawalPO.setUpdatedTime(currentTime);
        agentDepositWithdrawalPO.setCreatedTime(currentTime);
        agentDepositWithdrawalPO.setRechargeWithdrawTimeConsuming(System.currentTimeMillis()-vo.getTradeTime());
        handleDepositSuccess(agentDepositWithdrawalPO,ConvertUtil.entityToModel(agentInfoPO,AgentInfoVO.class), "代理充值",true);
        return true;
    }
}
