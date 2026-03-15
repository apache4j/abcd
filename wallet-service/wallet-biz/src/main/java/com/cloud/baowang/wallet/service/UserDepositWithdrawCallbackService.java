package com.cloud.baowang.wallet.service;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.FreezeFlagEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.PayProcessStatusEnum;
import com.cloud.baowang.wallet.api.enums.wallet.ChannelTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.ChannelTypeEnums;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.RechargeTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.VirtualCurrencyPayCallbackVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.pay.api.enums.VirtualCurrencyPayTypeEnum;
import com.cloud.baowang.system.api.api.exchange.ExchangeRateConfigApi;
import com.cloud.baowang.system.api.enums.exchange.ShowWayEnum;
import com.cloud.baowang.system.api.vo.exchange.RateCalculateRequestVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayDetailRespVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.CallbackDepositParamVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.CallbackWithdrawParamVO;
import com.cloud.baowang.wallet.api.vo.withdraw.RechargeSuccessVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayFeeVO;
import com.cloud.baowang.wallet.po.HotWalletAddressPO;
import com.cloud.baowang.wallet.po.SystemRechargeWayPO;
import com.cloud.baowang.wallet.po.UserCoinRecordPO;
import com.cloud.baowang.wallet.po.UserDepositWithdrawalPO;
import com.cloud.baowang.wallet.repositories.HotWalletAddressRepository;
import com.cloud.baowang.wallet.repositories.SiteRechargeWayRepository;
import com.cloud.baowang.wallet.repositories.UserCoinRecordRepository;
import com.cloud.baowang.wallet.repositories.UserDepositWithdrawalRepository;
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
public class UserDepositWithdrawCallbackService {

    private final UserDepositWithdrawalRepository userDepositWithdrawalRepository;

    private final UserInfoApi userInfoApi;

    private final UserDepositWithdrawHandleService userDepositWithdrawHandleService;

    private final HotWalletAddressRepository hotWalletAddressRepository;

    private final ExchangeRateConfigApi exchangeRateConfigApi;

    private final SystemRechargeWayService rechargeWayService;

    private final SiteRechargeWayRepository siteRechargeWayRepository;

    private final SystemRechargeChannelService rechargeChannelService;

    private final UserCoinRecordRepository userCoinRecordRepository;

    private final RechargeAdsService rechargeAdsService;
    /**
     * 出款回调处理
     *
     * @param callbackWithdrawParamVO
     * @return
     */
    @DistributedLock(name = RedisKeyTransUtil.USER_WITHDRAW_CALLBACK, unique = "#callbackWithdrawParamVO.orderNo", waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    public boolean userWithdrawCallback(final CallbackWithdrawParamVO callbackWithdrawParamVO) {
        String orderNo = callbackWithdrawParamVO.getOrderNo();
        log.info("三方代付订单号:{} 接收回调消息开始,消息体:{}", orderNo,
                callbackWithdrawParamVO);
        try {
            LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
            lqw.eq(UserDepositWithdrawalPO::getOrderNo, orderNo);
            UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectOne(lqw);
            if (ObjectUtil.isEmpty(userDepositWithdrawalPO)) {
                log.error("该笔订单:{},订单不存在", orderNo);
                return false;
            }
            if (!userDepositWithdrawalPO.getType().equals(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode())) {
                log.info("取款订单:{},该笔订单不为取款订单，订单类型{}", userDepositWithdrawalPO.getOrderNo(), userDepositWithdrawalPO.getType());
                return false;
            }
            if (!userDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode())) {
                log.info("取款订单{} 状态不为处理中 {}", userDepositWithdrawalPO.getOrderNo(), userDepositWithdrawalPO.getStatus());
                return false;
            }
            if (ObjectUtil.isNotEmpty(userDepositWithdrawalPO)) {
                userDepositWithdrawalPO.setPayTxId(callbackWithdrawParamVO.getPayId());
                userDepositWithdrawalPO.setPayAuditTime(System.currentTimeMillis());
                Long updatedTime = userDepositWithdrawalPO.getUpdatedTime();
                if(ChannelTypeEnums.OFFLINE.getDesc().equals(userDepositWithdrawalPO.getPayoutType())){
                    userDepositWithdrawalPO.setSettlementFeeAmount(BigDecimal.ZERO);
                }
                if (callbackWithdrawParamVO.getStatus().equals(ThirdPayOrderStatusEnum.Success.getCode()) ) {
                    userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
                    userDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.SUCCESS.getCode());
                    userDepositWithdrawalPO.setPayProcessStatus(PayProcessStatusEnum.SUCCESS.getCode());
//                    userDepositWithdrawalPO.setArriveAmount(callbackWithdrawParamVO.getAmount());
                    userDepositWithdrawalPO.setRechargeWithdrawTimeConsuming(System.currentTimeMillis()-updatedTime);
                    userDepositWithdrawalPO.setUpdatedTime(System.currentTimeMillis());

                    expenses(userDepositWithdrawalPO);
                } else if (callbackWithdrawParamVO.getStatus().equals(ThirdPayOrderStatusEnum.Fail.getCode())) {
                    userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.WITHDRAW_FAIL.getCode());
                    userDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode());
                    userDepositWithdrawalPO.setPayProcessStatus(PayProcessStatusEnum.ABNORMAL.getCode());
                    userDepositWithdrawalPO.setUpdatedTime(System.currentTimeMillis());
                    unFreeze(userDepositWithdrawalPO, callbackWithdrawParamVO.getRemark());
                } else if (callbackWithdrawParamVO.getStatus().equals(ThirdPayOrderStatusEnum.Pending.getCode()) ) {
                    if (StringUtils.isBlank(userDepositWithdrawalPO.getPayProcessStatus())) {
                        userDepositWithdrawalPO.setPayProcessStatus(PayProcessStatusEnum.GETTING.getCode());
                        userDepositWithdrawalRepository.updateById(userDepositWithdrawalPO);
                    }
                }
            } else {
                log.info("该笔订单:{},订单状态不是待处理状态", orderNo);
            }
        } catch (Exception e) {
            log.error("支付订单号:{}, 用户账号:{}, 三方支付id:{},三方支付code:{} 发生异常", orderNo,
                    callbackWithdrawParamVO.getUserAccount(), callbackWithdrawParamVO.getPayId(), callbackWithdrawParamVO.getPayCode(), e.getMessage());
            return false;
        }
        return true;
    }

    private Boolean unFreeze(UserDepositWithdrawalPO userDepositWithdrawalPO, String remark) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setUserId(userDepositWithdrawalPO.getUserId());
        userCoinAddVO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_WITHDRAWAL_FAIL.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.WITHDRAWAL.getCode());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.UN_FREEZE.getCode());
        userCoinAddVO.setCoinValue(userDepositWithdrawalPO.getApplyAmount());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_WITHDRAWAL.getCode());
        userCoinAddVO.setCoinTime(userDepositWithdrawalPO.getUpdatedTime());
        userCoinAddVO.setRemark(remark);
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userDepositWithdrawalPO.getUserId());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userDepositWithdrawHandleService.withdrawFail(userDepositWithdrawalPO, null, userCoinAddVO);
        return true;
    }

    private Boolean expenses(UserDepositWithdrawalPO userDepositWithdrawalPO) {
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setUserId(userDepositWithdrawalPO.getUserId());
        userCoinAddVO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_WITHDRAWAL.getCode());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.EXPENSES.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.WITHDRAWAL.getCode());
        userCoinAddVO.setCoinValue(userDepositWithdrawalPO.getApplyAmount());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_WITHDRAWAL.getCode());
        userCoinAddVO.setFreezeFlag(FreezeFlagEnum.UNFREEZE.getCode());
        userCoinAddVO.setCoinTime(userDepositWithdrawalPO.getUpdatedTime());
        userCoinAddVO.setToThridCode(userDepositWithdrawalPO.getDepositWithdrawChannelCode());
        userCoinAddVO.setThirdOrderNo(userDepositWithdrawalPO.getPayTxId());
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userDepositWithdrawalPO.getUserId());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userCoinAddVO.setRemark("会员提款");
        userDepositWithdrawHandleService.withdrawSuccess(userInfoVO,userDepositWithdrawalPO, userCoinAddVO);
        return true;
    }


    /**
     * 充值回调处理
     *
     * @param callbackDepositParamVO
     * @return
     */
    @DistributedLock(name = RedisKeyTransUtil.USER_DEPOSIT_CALLBACK, unique = "#callbackDepositParamVO.orderNo", waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    public boolean depositCallback(CallbackDepositParamVO callbackDepositParamVO) {
        String orderNo = callbackDepositParamVO.getOrderNo();
        log.info("三方充值支付订单号:{} 接收回调消息开始,消息体:{}", orderNo, callbackDepositParamVO);
        BigDecimal amount = callbackDepositParamVO.getAmount();
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
        lqw.eq(UserDepositWithdrawalPO::getOrderNo, orderNo);
        UserDepositWithdrawalPO userDepositWithdrawalPO = userDepositWithdrawalRepository.selectOne(lqw);
        if (ObjectUtil.isEmpty(userDepositWithdrawalPO)) {
            log.error("充值该笔订单:{},订单不存在", orderNo);
            return false;
        }
        if (!userDepositWithdrawalPO.getType().equals(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode())) {
            log.info("充值订单:{},该笔订单不为充值订单，订单类型{}", userDepositWithdrawalPO.getOrderNo(), userDepositWithdrawalPO.getType());
            return false;
        }
        if (!userDepositWithdrawalPO.getStatus().equals(DepositWithdrawalOrderStatusEnum.HANDLE_ING.getCode())) {
            log.info("充值订单{} 状态不为处理中 {}", userDepositWithdrawalPO.getOrderNo(), userDepositWithdrawalPO.getStatus());
            return false;
        }
        BigDecimal callbackAmount = callbackDepositParamVO.getAmount();

        if(!ChannelTypeEnum.SITE_CUSTOM.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawChannelType())){
            if(CurrencyEnum.KVND.getCode().equals(userDepositWithdrawalPO.getCurrencyCode())){
                callbackAmount = callbackAmount.divide(new BigDecimal(1000));
            }
            if (userDepositWithdrawalPO.getApplyAmount().compareTo(callbackAmount) != 0) {
                log.info("订单:{},回调金额{},与申请金额:{}不一致", orderNo,callbackAmount,userDepositWithdrawalPO.getApplyAmount());
                return false;
            }
        }else{
            if (callbackDepositParamVO.getStatus() == ThirdPayOrderStatusEnum.Success.getCode()) {
                if (userDepositWithdrawalPO.getApplyAmount().compareTo(callbackAmount) != 0) {
                    userDepositWithdrawalPO.setArriveAmount(callbackDepositParamVO.getAmount());
                    userDepositWithdrawalPO.setApplyAmount(callbackDepositParamVO.getAmount());
                    //到账金额与申请金额不一致，需重算结算手续费 获取站点充值方式费率配置
                    SiteRechargeWayFeeVO siteRechargeWayFee = rechargeWayService.calculateSiteRechargeWayFeeRate(userDepositWithdrawalPO.getSiteCode(), userDepositWithdrawalPO.getDepositWithdrawWayId(), userDepositWithdrawalPO.getArriveAmount(), userDepositWithdrawalPO.getDepositWithdrawChannelType());
                    userDepositWithdrawalPO.setSettlementFeeRate(siteRechargeWayFee.getWayFee());
                    userDepositWithdrawalPO.setWayFeeType(siteRechargeWayFee.getFeeType());
                    userDepositWithdrawalPO.setSettlementFeePercentageAmount(siteRechargeWayFee.getWayFeePercentageAmount());
                    userDepositWithdrawalPO.setSettlementFeeFixedAmount(siteRechargeWayFee.getWayFeeFixedAmount());
                    userDepositWithdrawalPO.setWayFeeAmount(BigDecimal.ZERO);
                    userDepositWithdrawalPO.setSettlementFeeAmount(siteRechargeWayFee.getWayFeeAmount());
                    if (RechargeTypeEnum.CRYPTO_CURRENCY.getCode().equals(userDepositWithdrawalPO.getDepositWithdrawTypeCode())) {
                        userDepositWithdrawalPO.setTradeCurrencyAmount(callbackDepositParamVO.getTradeCurrencyAmount());
                    } else {
                        userDepositWithdrawalPO.setTradeCurrencyAmount(callbackDepositParamVO.getAmount());
                    }
                }
            }

        }

        String userId = userDepositWithdrawalPO.getUserId();
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userDepositWithdrawalPO.getUserId());
        if(null == userInfoVO){
            log.error("充值通知未找到该用户{}", userId);
            return false;
        }
        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.PAY_LOCK.getCode())) {
            log.error("会员id{},被限制充提", userId);
            return false;
        }
        SystemRechargeWayDetailRespVO systemRechargeWayPO = rechargeWayService.getRechargeWayByCurrencyAndNetworkType(userDepositWithdrawalPO.getCurrencyCode(),
                "",userDepositWithdrawalPO.getSiteCode(),userDepositWithdrawalPO.getDepositWithdrawWayId());
        if(null == systemRechargeWayPO || !CommonConstant.business_one.equals(systemRechargeWayPO.getStatus())){
            log.error("充值方式{},已被禁用", userDepositWithdrawalPO.getDepositWithdrawWayId());
            return false;
        }
        SystemRechargeChannelBaseVO systemRechargeChannelPO = rechargeChannelService.getChannelInfoByChannelId(userDepositWithdrawalPO.getCurrencyCode(),
                userDepositWithdrawalPO.getDepositWithdrawWayId(),userDepositWithdrawalPO.getSiteCode(),userDepositWithdrawalPO.getDepositWithdrawChannelId());
//        SystemRechargeChannelPO systemRechargeChannelPO = rechargeChannelService.getById(userDepositWithdrawalPO.getDepositWithdrawChannelId());
        if(null == systemRechargeChannelPO || !CommonConstant.business_one.equals(systemRechargeChannelPO.getStatus())){
            log.error("充值通道{},已被禁用", userDepositWithdrawalPO.getDepositWithdrawChannelId());
            return false;
        }
        if (StringUtils.isNotBlank(callbackDepositParamVO.getPayId())) {
            userDepositWithdrawalPO.setPayTxId(callbackDepositParamVO.getPayId());
        }
        userDepositWithdrawalPO.setPayAuditTime(System.currentTimeMillis());
        Long updatedTime = userDepositWithdrawalPO.getUpdatedTime();
        userDepositWithdrawalPO.setUpdatedTime(System.currentTimeMillis());
        if (callbackDepositParamVO.getStatus() == ThirdPayOrderStatusEnum.Success.getCode()) {
            userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
            userDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.SUCCESS.getCode());
            userDepositWithdrawalPO.setPayProcessStatus(PayProcessStatusEnum.SUCCESS.getCode());
            userDepositWithdrawalPO.setRechargeWithdrawTimeConsuming(System.currentTimeMillis()-updatedTime);
            handleDepositSuccess(userDepositWithdrawalPO, "会员存款",false);

        } else if (callbackDepositParamVO.getStatus() == ThirdPayOrderStatusEnum.Fail.getCode()) {
            userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.FAIL.getCode());
            userDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.FAIL.getCode());
            userDepositWithdrawalPO.setPayProcessStatus(PayProcessStatusEnum.ABNORMAL.getCode());
            userDepositWithdrawalPO.setPayAuditRemark(callbackDepositParamVO.getRemark());
            userDepositWithdrawalRepository.updateById(userDepositWithdrawalPO);
            //发送 失败mq
            List<String> userIds = new ArrayList<>();
            userIds.add(userDepositWithdrawalPO.getUserId());
            RechargeSuccessVO rechargeSuccessVO = new RechargeSuccessVO();
            rechargeSuccessVO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
            rechargeSuccessVO.setCustomerStatus(userDepositWithdrawalPO.getCustomerStatus());
            rechargeSuccessVO.setUpdatedTime(userDepositWithdrawalPO.getUpdatedTime());
            userDepositWithdrawHandleService.sendWebSocketMessage(userDepositWithdrawalPO.getSiteCode(),userIds,rechargeSuccessVO);
        } else if (callbackDepositParamVO.getStatus() == ThirdPayOrderStatusEnum.Pending.getCode()) {
            if (StringUtils.isBlank(userDepositWithdrawalPO.getPayProcessStatus())) {
                userDepositWithdrawalPO.setPayProcessStatus(PayProcessStatusEnum.GETTING.getCode());
                userDepositWithdrawalRepository.updateById(userDepositWithdrawalPO);
            }
        }
        return true;
    }

    /**
     * 处理充值成功 实际到账
     *
     * @param userDepositWithdrawalPO
     * @param remark                  账变记录备注
     */
    public void handleDepositSuccess(UserDepositWithdrawalPO userDepositWithdrawalPO, String remark,boolean isNew) {

        // 中心钱包加额 + 账变记录 (实际到账金额)
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setUserId(userDepositWithdrawalPO.getUserId());
        userCoinAddVO.setCurrency(userDepositWithdrawalPO.getCurrencyCode());
        userCoinAddVO.setOrderNo(userDepositWithdrawalPO.getOrderNo());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_DEPOSIT.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_DEPOSIT.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.DEPOSIT.getCode());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCoinValue(userDepositWithdrawalPO.getArriveAmount());
        userCoinAddVO.setCoinTime(userDepositWithdrawalPO.getUpdatedTime());
        userCoinAddVO.setRemark(userDepositWithdrawalPO.getPayTxId());
        userCoinAddVO.setToThridCode(userDepositWithdrawalPO.getDepositWithdrawChannelCode());
        userCoinAddVO.setThirdOrderNo(userDepositWithdrawalPO.getPayTxId());
        // 实际到账金额 打码量
        UserTypingAmountRequestVO userTypingAmount = new UserTypingAmountRequestVO();
        userTypingAmount.setSiteCode(userDepositWithdrawalPO.getSiteCode());
        userTypingAmount.setUserId(userDepositWithdrawalPO.getUserId());
        userTypingAmount.setUserAccount(userDepositWithdrawalPO.getUserAccount());
        userTypingAmount.setTypingAmount(userDepositWithdrawalPO.getArriveAmount());
        userTypingAmount.setType(TypingAmountEnum.ADD.getCode());
        userTypingAmount.setOrderNo(userDepositWithdrawalPO.getOrderNo());
        userTypingAmount.setCurrencyCode(userDepositWithdrawalPO.getCurrencyCode());
        userTypingAmount.setAdjustType(TypingAmountAdjustTypeEnum.DEPOSIT.getCode());
        userTypingAmount.setRemark("会员存款-打码量");

        // 需要更新user_info的first_deposit_amount(首存金额)
        UserInfoVO userInfo = userInfoApi.getByUserId(userDepositWithdrawalPO.getUserId());
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfo, WalletUserInfoVO.class));
        if (null == userInfo) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }


        // 统一更新插入充值成功的 发送MQ相关信息
        userDepositWithdrawHandleService.depositSuccess(userInfo,
                userDepositWithdrawalPO,
                userCoinAddVO,
                userTypingAmount,isNew);



    }


    @DistributedLock(name = RedisKeyTransUtil.USER_DEPOSIT_CALLBACK, unique = "#vo.userAddress", waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    public Boolean virtualCurrencyDepositCallback(VirtualCurrencyPayCallbackVO vo) {
        //todo update 创建订单，添加账变
        log.info("虚拟币充值成功通知,消息体:{}",vo);
        String userId = vo.getOwnerUserId();
        String address = vo.getUserAddress();
        /*SystemDictConfigRespVO systemDictConfigRespVO=  systemDictConfigApi.getByCode(DictCodeConfigEnums.USDT_MIN_THRESHOLD.getCode(),"").getData();
        if(null != systemDictConfigRespVO && StringUtils.isNotBlank(systemDictConfigRespVO.getConfigParam())){
            if(vo.getTradeAmount().compareTo(new BigDecimal(systemDictConfigRespVO.getConfigParam())) < 0){
                log.error("充值金额{},小于系统配置金额{}", vo.getTradeAmount(), systemDictConfigRespVO.getConfigParam());
                return false;
            }
        }*/
        HotWalletAddressPO hotWalletAddressPO = hotWalletAddressRepository.selectOne(new LambdaQueryWrapper<HotWalletAddressPO>().eq(HotWalletAddressPO::getAddress,address));
        if(null == hotWalletAddressPO ){
            log.error("充值地址未找到,会员id:{},热钱包地址", vo.getOwnerUserId(),address);
            return false;
        }
        BigDecimal amount = vo.getTradeAmount();
        LambdaQueryWrapper<UserDepositWithdrawalPO> lqw = new LambdaQueryWrapper();
        lqw.eq(UserDepositWithdrawalPO::getUserId, userId);
        lqw.eq(UserDepositWithdrawalPO::getPayTxId,vo.getTradeHash());
        UserDepositWithdrawalPO userDepositWithdrawalPOquery = userDepositWithdrawalRepository.selectOne(lqw);
        if (ObjectUtil.isNotEmpty(userDepositWithdrawalPOquery)) {
            log.error("该笔订单已处理,会员id:{},热钱包地址", vo.getOwnerUserId(),address);
            return false;
        }
        LambdaQueryWrapper<UserCoinRecordPO> userCoinRecordLqw = new LambdaQueryWrapper<>();
        userCoinRecordLqw.eq(UserCoinRecordPO::getRemark,vo.getTradeHash());
        UserCoinRecordPO userCoinRecordPO = userCoinRecordRepository.selectOne(userCoinRecordLqw);
        if (ObjectUtil.isNotEmpty(userCoinRecordPO)) {
            log.error("该笔订单已产生账变,会员id:{},热钱包地址", vo.getOwnerUserId(),address);
            return false;
        }

        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        if(null == userInfoVO){
            log.error("充值通知未找到该用户{}", vo.getOwnerUserId());
            return false;
        }
        if (userInfoVO.getAccountStatus().contains(UserStatusEnum.PAY_LOCK.getCode())) {
            log.error("会员id{},被限制充提", vo.getOwnerUserId());
            return false;
        }
        String currencyCode = hotWalletAddressPO.getCurrencyCode();

        //获取币种，协议的充值方式 唯一

        SystemRechargeWayDetailRespVO systemRechargeWayDetailRespVO = rechargeWayService.
                getRechargeWayByCurrencyAndNetworkType(currencyCode,hotWalletAddressPO.getNetworkType(),
                        hotWalletAddressPO.getSiteCode(),"");
        if(null == systemRechargeWayDetailRespVO || !CommonConstant.business_one.equals(systemRechargeWayDetailRespVO.getStatus()) ){
            log.info("充值通知未找到对应的充值方式");
            return false;
        }

        SystemRechargeChannelBaseVO systemRechargeChannelPO = rechargeChannelService.getChannelInfoByCurrencyAneWayId(currencyCode,systemRechargeWayDetailRespVO.getId(),
                hotWalletAddressPO.getSiteCode(),"");
        if(null == systemRechargeChannelPO || !CommonConstant.business_one.equals(systemRechargeChannelPO.getStatus())){
            log.info("充值通知未找到对应的充值通道");
            return false;
        }
        RateCalculateRequestVO exchangeRateRequestVO = new RateCalculateRequestVO();
        exchangeRateRequestVO.setSiteCode(userInfoVO.getSiteCode());
        exchangeRateRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        exchangeRateRequestVO.setShowWay(ShowWayEnum.RECHARGE.getCode());
        BigDecimal exchangeRate = exchangeRateConfigApi.getLatestRate(exchangeRateRequestVO);
        if(exchangeRate!=null && exchangeRate.compareTo(BigDecimal.ZERO)!=0){
            amount = amount.multiply(exchangeRate).setScale(4, RoundingMode.DOWN);
        }
        //获取汇率总控
        RateCalculateRequestVO rateCalculateRequestVO = new RateCalculateRequestVO();
        rateCalculateRequestVO.setCurrencyCode(userInfoVO.getMainCurrency());
        rateCalculateRequestVO.setShowWay(ShowWayEnum.RECHARGE.getCode());
        rateCalculateRequestVO.setSiteCode(CommonConstant.business_zero_str);
        BigDecimal currencyExchangeRate = exchangeRateConfigApi.getLatestRate(rateCalculateRequestVO);
        //获取站点充值方式费率配置
        SiteRechargeWayFeeVO siteRechargeWayFee = rechargeWayService.calculateSiteRechargeWayFeeRate(userInfoVO.getSiteCode(),systemRechargeWayDetailRespVO.getId(),amount,systemRechargeChannelPO.getChannelType());
        //计算手续费
        BigDecimal settlementFeeAmount = siteRechargeWayFee.getWayFeeAmount();
        if(ChannelTypeEnum.SITE_CUSTOM.getCode().equals(systemRechargeChannelPO.getChannelType())){
            siteRechargeWayFee.setWayFeeAmount(BigDecimal.ZERO);
        }
        String rechargeWayId = systemRechargeChannelPO.getRechargeWayId();
        SystemRechargeWayPO systemRechargeWayPO = rechargeWayService.getById(rechargeWayId);
        UserDepositWithdrawalPO userDepositWithdrawalPO = new UserDepositWithdrawalPO();
        userDepositWithdrawalPO.setCurrencyUsdExchangeRate(currencyExchangeRate);
        userDepositWithdrawalPO.setCoinCode(vo.getCoinCode());
        userDepositWithdrawalPO.setDepositWithdrawTypeId(systemRechargeWayPO.getRechargeTypeId());
        userDepositWithdrawalPO.setDepositWithdrawTypeCode(systemRechargeWayPO.getRechargeTypeCode());
        userDepositWithdrawalPO.setDepositWithdrawWayId(systemRechargeWayPO.getId());
        userDepositWithdrawalPO.setDepositWithdrawWay(systemRechargeWayPO.getRechargeWayI18());
        userDepositWithdrawalPO.setDepositWithdrawChannelCode(systemRechargeChannelPO.getChannelCode());
        userDepositWithdrawalPO.setDepositWithdrawChannelName(systemRechargeChannelPO.getChannelName());
        userDepositWithdrawalPO.setDepositWithdrawChannelId(systemRechargeChannelPO.getId());
        userDepositWithdrawalPO.setDepositWithdrawChannelType(systemRechargeChannelPO.getChannelType());
        userDepositWithdrawalPO.setAgentId(userInfoVO.getSuperAgentId());
        userDepositWithdrawalPO.setAgentAccount(userInfoVO.getSuperAgentAccount());

        String orderNo = "CK"+userInfoVO.getMainCurrency()+ DateUtils.dateToyyyyMMddHHmmss(new Date())+SnowFlakeUtils.getRandomZm();
        userDepositWithdrawalPO.setCurrencyCode(currencyCode);
        if(VirtualCurrencyPayTypeEnum.MINI_RECHARGE.getCode().equals(vo.getOrderType())){
            userDepositWithdrawalPO.setCombinedRecharge(CommonConstant.business_one);
        }
        userDepositWithdrawalPO.setWayFeeType(siteRechargeWayFee.getFeeType());
        userDepositWithdrawalPO.setWayFeeAmount(siteRechargeWayFee.getWayFeeAmount());
        userDepositWithdrawalPO.setSettlementFeeRate(siteRechargeWayFee.getWayFee());
        userDepositWithdrawalPO.setSettlementFeePercentageAmount(siteRechargeWayFee.getWayFeePercentageAmount());
        userDepositWithdrawalPO.setSettlementFeeFixedAmount(siteRechargeWayFee.getWayFeeFixedAmount());
        userDepositWithdrawalPO.setSettlementFeeAmount(settlementFeeAmount);
        userDepositWithdrawalPO.setUserAccount(userInfoVO.getUserAccount());
        userDepositWithdrawalPO.setUserId(userId);
        userDepositWithdrawalPO.setSiteCode(userInfoVO.getSiteCode());
        userDepositWithdrawalPO.setAccountBranch(vo.getNetworkType());
        userDepositWithdrawalPO.setAccountType(vo.getChainType());
        userDepositWithdrawalPO.setDepositWithdrawAddress(vo.getUserAddress());
        userDepositWithdrawalPO.setDepositWithdrawName(userInfoVO.getUserName());
        userDepositWithdrawalPO.setApplyAmount(amount);
        userDepositWithdrawalPO.setPayTxId(vo.getTradeHash());
        userDepositWithdrawalPO.setTradeCurrencyAmount(vo.getTradeAmount());
        userDepositWithdrawalPO.setArriveAmount(amount);
        userDepositWithdrawalPO.setExchangeRate(exchangeRate);
        userDepositWithdrawalPO.setOrderNo(orderNo);
        userDepositWithdrawalPO.setType(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
        userDepositWithdrawalPO.setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        userDepositWithdrawalPO.setCustomerStatus(DepositWithdrawalOrderCustomerStatusEnum.SUCCESS.getCode());
        Long currentTime = System.currentTimeMillis();
        userDepositWithdrawalPO.setPayAuditTime(currentTime);
        userDepositWithdrawalPO.setUpdatedTime(currentTime);
        userDepositWithdrawalPO.setCreatedTime(currentTime);
        userDepositWithdrawalPO.setRechargeWithdrawTimeConsuming(currentTime-vo.getTradeTime());
        handleDepositSuccess(userDepositWithdrawalPO, "会员存款",true);
        return true;

    }
}
