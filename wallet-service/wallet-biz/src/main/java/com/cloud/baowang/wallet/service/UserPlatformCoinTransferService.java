package com.cloud.baowang.wallet.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserWinLoseMqVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.PlatformCoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.PlatformWalletEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userwallet.UserPlatformTransferVO;
import com.cloud.baowang.wallet.po.UserPlatformCoinPO;
import com.cloud.baowang.wallet.po.UserPlatformTransferRecordPO;
import com.cloud.baowang.wallet.repositories.UserPlatformCoinRepository;
import com.cloud.baowang.wallet.repositories.UserPlatformTransferRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author qiqi
 */

@Service
@Slf4j
@AllArgsConstructor
public class UserPlatformCoinTransferService extends ServiceImpl<UserPlatformCoinRepository, UserPlatformCoinPO> {


    private final SiteCurrencyInfoService siteCurrencyInfoService;

    private final UserInfoApi userInfoApi;

    private final UserPlatformTransferRecordRepository userPlatformTransferRecordRepository;

    private final WalletUserCommonPlatformCoinService walletUserCommonPlatformCoinService;




    /**
     * 平台币兑换
     *
     * @param userPlatformTransferVO 转换参数
     * @return
     */
    @DistributedLock(name = RedisKeyTransUtil.TRANSFER_PLATFORM_COIN_LOCK_KEY, unique = "#userPlatformTransferVO.userId", fair = true, waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public ResponseVO<String> transfer(UserPlatformTransferVO userPlatformTransferVO) {
        log.info("平台币转换开始:{}",userPlatformTransferVO);
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userPlatformTransferVO.getUserId());
        String siteCode = userPlatformTransferVO.getSiteCode();
        String currencyCode = userInfoVO.getMainCurrency();
        BigDecimal finalRate = siteCurrencyInfoService.getCurrencyFinalRate(siteCode, currencyCode);
        if (finalRate == null) {
            log.info("平台币转{}汇率没有配置,不能进行转换", currencyCode);
            return ResponseVO.fail(ResultCode.RATE_NOT_CONFIG);
        }
        UserPlatformCoinAddVO userPlatformCoinAddVO = new UserPlatformCoinAddVO();
        userPlatformCoinAddVO.setUserId(userPlatformTransferVO.getUserId());
        String orderNo = OrderUtil.getOrderNoSss("P");
        userPlatformCoinAddVO.setOrderNo(orderNo);
        BigDecimal transferAmount = userPlatformTransferVO.getTransferAmount();
        userPlatformCoinAddVO.setCoinValue(transferAmount);
        userPlatformCoinAddVO.setBusinessCoinType(PlatformWalletEnum.BusinessCoinTypeEnum.PLATFORM_CONVERSION.getCode());
        userPlatformCoinAddVO.setCoinType(PlatformWalletEnum.CoinTypeEnum.PLATFORM_CONVERSION.getCode());
        userPlatformCoinAddVO.setBalanceType(PlatformCoinBalanceTypeEnum.EXPENSES.getCode());
        userPlatformCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));

        //转换后金额
        BigDecimal targetAmount = AmountUtils.multiply(transferAmount, finalRate,4);
        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
        userCoinAddVO.setUserId(userInfoVO.getUserId());
        userCoinAddVO.setOrderNo(orderNo);
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.PLATFORM_CONVERSION.getCode());
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.PLATFORM_CONVERSION.getCode());
        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.PLATFORM_CONVERSION.getCode());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setCurrency(currencyCode);
        userCoinAddVO.setCoinValue(targetAmount);
        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));

        CoinRecordResultVO coinRecordResultVO = walletUserCommonPlatformCoinService.wtcToMainCurrency(userPlatformCoinAddVO,userCoinAddVO);
        //调用平台币账变
        /*CoinRecordResultVO coinRecordResultVO = this.addPlatformCoin(userPlatformCoinAddVO);
        if (!coinRecordResultVO.getResult()) {
            log.info("调用平台币帐变失败:{}", coinRecordResultVO);
            if (Objects.equals(coinRecordResultVO.getResultStatus().getCode(), UpdateBalanceStatusEnums.INSUFFICIENT_BALANCE.getCode())) {
                return ResponseVO.fail(ResultCode.INSUFFICIENT_BALANCE);
            } else {
                return ResponseVO.fail(ResultCode.TRANSFER_ERROR);
            }
        }

        //调用法币帐变

        CoinRecordResultVO userCoinRecordResultVO = userCoinService.addCoin(userCoinAddVO);
        */
        if (!coinRecordResultVO.getResult()) {
            log.info("调用平台币帐变失败:{}", coinRecordResultVO);
            if (Objects.equals(coinRecordResultVO.getResultStatus().getCode(), UpdateBalanceStatusEnums.INSUFFICIENT_BALANCE.getCode())) {
                return ResponseVO.fail(ResultCode.INSUFFICIENT_BALANCE);
            } else {
                return ResponseVO.fail(ResultCode.TRANSFER_ERROR);
            }
        }
        //记录 平台币兑换记录
        UserPlatformTransferRecordPO userPlatformTransferRecordPO = new UserPlatformTransferRecordPO();
        userPlatformTransferRecordPO.setUserId(userInfoVO.getUserId());
        userPlatformTransferRecordPO.setUserAccount(userInfoVO.getUserAccount());
        userPlatformTransferRecordPO.setAgentId(userInfoVO.getSuperAgentId());
        userPlatformTransferRecordPO.setAgentAccount(userInfoVO.getSuperAgentAccount());
        userPlatformTransferRecordPO.setTransferAmount(transferAmount);
        userPlatformTransferRecordPO.setTransferRate(finalRate);
        userPlatformTransferRecordPO.setTargetAmount(targetAmount);
        userPlatformTransferRecordPO.setSiteCode(siteCode);
        userPlatformTransferRecordPO.setOrderNo(orderNo);
        userPlatformTransferRecordPO.setOrderTime(System.currentTimeMillis());
        userPlatformTransferRecordPO.setPlatCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        userPlatformTransferRecordPO.setTargetCurrencyCode(currencyCode);
        userPlatformTransferRecordRepository.insert(userPlatformTransferRecordPO);
        // 平台币兑换为主货币，统计到会员盈亏报表
        handleSendWinLossMessage(userPlatformTransferRecordPO, userInfoVO);
        log.info("平台币转换结束:{}",userPlatformTransferVO);
        return ResponseVO.success();
    }

    /**
     * 处理并发送会员每日盈亏消息到 Kafka 队列。
     *
     * @param siteActivityOrderRecordPO 包含订单记录信息的对象
     * @param userInfoVO                包含用户信息的对象
     */
    private void handleSendWinLossMessage(UserPlatformTransferRecordPO siteActivityOrderRecordPO, UserInfoVO userInfoVO) {
        UserWinLoseMqVO userWinLoseMqVO = new UserWinLoseMqVO();
        userWinLoseMqVO.setOrderId(siteActivityOrderRecordPO.getOrderNo());
        userWinLoseMqVO.setUserId(siteActivityOrderRecordPO.getUserId());
        userWinLoseMqVO.setAgentId(userInfoVO.getSuperAgentId());
        userWinLoseMqVO.setDayHourMillis(System.currentTimeMillis());
        // 任务发放的是主货币币
        userWinLoseMqVO.setCurrency(siteActivityOrderRecordPO.getTargetCurrencyCode());
        userWinLoseMqVO.setPlatformFlag(false);
        //userWinLoseMqVO.setActivityAmount(siteActivityOrderRecordPO.getTransferAmount());
        // 类型是已使用优惠
        userWinLoseMqVO.setBizCode(CommonConstant.business_six);
        userWinLoseMqVO.setSiteCode(userInfoVO.getSiteCode());
        userWinLoseMqVO.setAlreadyUseAmount(siteActivityOrderRecordPO.getTargetAmount());
        KafkaUtil.send(TopicsConstants.USER_WIN_LOSE_CHANNEL, userWinLoseMqVO);
    }



}
