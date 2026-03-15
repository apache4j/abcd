package com.cloud.baowang.wallet.consumer;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.UserAccountListVO;
import com.cloud.baowang.wallet.api.vo.mq.UserGamePayoutMqVO;
import com.cloud.baowang.wallet.api.vo.mq.UserGamePayoutVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.po.UserCoinRecordPO;
import com.cloud.baowang.wallet.service.UserCoinRecordService;
import com.cloud.baowang.wallet.service.UserCoinService;
import com.cloud.baowang.wallet.service.WalletUserCommonCoinService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class ThirdGamePayoutConsumer {

    private final UserInfoApi userInfoApi;
    private final UserCoinRecordService userCoinRecordService;

    private final WalletUserCommonCoinService userCommonCoinService;

    @KafkaListener(topics = TopicsConstants.THIRD_GAME_PAYOUT_TOPIC, groupId = GroupConstants.THIRD_GAME_PAYOUT_GROUP)
    public void thirdGamePayoutMessage(UserGamePayoutMqVO userGamePayoutMqVO, Acknowledgment ackItem) {
        long start = System.currentTimeMillis();
        log.info("收到三方游戏派彩消息 the msg: {} by kafka", JSONObject.toJSONString(userGamePayoutMqVO));
        try {
            List<UserGamePayoutVO> userRecordPayoutVOList = userGamePayoutMqVO.getUserRecordPayoutVOList();
            if (CollectionUtil.isEmpty(userRecordPayoutVOList)) {
                ackItem.acknowledge();
                return;
            }
            List<String> userIds = userRecordPayoutVOList.stream().map(UserGamePayoutVO::getUserId).distinct().toList();
            UserAccountListVO userIdListVO = new UserAccountListVO();
            userIdListVO.setAccountList(userIds);
            List<UserInfoVO> userInfoByAccountList = userInfoApi.getUserInfoByUserIdsList(userIdListVO);
            Map<String, UserInfoVO> account2Info = userInfoByAccountList.stream().collect(Collectors.toMap(UserInfoVO::getUserId, e -> e));
            Map<Integer, List<UserGamePayoutVO>> orderClassify2VOs = userRecordPayoutVOList.stream().collect(Collectors.groupingBy(UserGamePayoutVO::getOrderClassify));

            List<UserGamePayoutVO> userGamePayoutVOS = orderClassify2VOs.get(ClassifyEnum.SETTLED.getCode());
            if (CollectionUtil.isNotEmpty(userGamePayoutVOS)) {
                // 结算
                List<String> unDealSettleNo = userGamePayoutVOS.stream().filter(e -> e.getThirdOrderId() != null).map(UserGamePayoutVO::getThirdOrderId).toList();
                if (CollectionUtil.isNotEmpty(unDealSettleNo)) {
                    List<UserCoinRecordPO> settleCount = userCoinRecordService.list(Wrappers.<UserCoinRecordPO>lambdaQuery().select(UserCoinRecordPO::getOrderNo).in(UserCoinRecordPO::getOrderNo, unDealSettleNo).eq(UserCoinRecordPO::getCoinType, WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode()));
                    List<String> settledNo = settleCount.stream().map(UserCoinRecordPO::getOrderNo).toList();
                    if (CollectionUtil.isNotEmpty(settledNo)) {
                        userGamePayoutVOS.removeIf(e -> settledNo.contains(e.getThirdOrderId()));
                    }
                }
                if (CollectionUtil.isNotEmpty(userGamePayoutVOS)) {
                    for (UserGamePayoutVO payoutVO : userGamePayoutVOS) {
                        UserInfoVO userInfoVO = account2Info.get(payoutVO.getUserId());
                        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
                        userCoinAddVO.setUserId(userInfoVO.getUserId());
                        userCoinAddVO.setOrderNo(payoutVO.getThirdOrderId());
                        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
                        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
                        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
                        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.GAME_PAYOUT.getCode());
                        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.GAME_PAYOUT.getCode());
                        userCoinAddVO.setCoinValue(payoutVO.getPayoutAmount().abs());
                        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
                        userCoinAddVO.setRemark(payoutVO.getVenueCode());
                        CoinRecordResultVO coinRecordResultVO = userCommonCoinService.userCommonCoinAdd(userCoinAddVO);
                        UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
                        switch (resultStatus) {
                            case SUCCESS, AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> {
                            }
                            case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
                                log.error("收到三方游戏派彩消息,场馆代码:{} third game payout fail,req:{} error:{}", payoutVO.getVenueCode(), payoutVO, resultStatus);
                            }
                        }
                    }
                }
            }

            List<UserGamePayoutVO> userGameCancelVOS = orderClassify2VOs.get(ClassifyEnum.CANCEL.getCode());
            if (CollectionUtil.isNotEmpty(userGameCancelVOS)) {
                // 取消
                List<String> unDealSettleNo = userGameCancelVOS.stream().map(UserGamePayoutVO::getThirdOrderId).filter(Objects::nonNull).toList();
                if (CollectionUtil.isNotEmpty(unDealSettleNo)) {
                    List<UserCoinRecordPO> settleCount = userCoinRecordService.list(Wrappers.<UserCoinRecordPO>lambdaQuery().select(UserCoinRecordPO::getOrderNo).in(UserCoinRecordPO::getOrderNo, unDealSettleNo).eq(UserCoinRecordPO::getCoinType,WalletEnum.CoinTypeEnum.CANCEL_BET.getCode()));
                    List<String> settledNo = settleCount.stream().map(UserCoinRecordPO::getOrderNo).toList();
                    if (CollectionUtil.isNotEmpty(settledNo)) {
                        userGameCancelVOS.removeIf(e -> settledNo.contains(e.getThirdOrderId()));
                    }
                }
                if (CollectionUtil.isNotEmpty(userGameCancelVOS)) {
                    for (UserGamePayoutVO payoutVO : userGameCancelVOS) {
                        UserInfoVO userInfoVO = account2Info.get(payoutVO.getUserId());
                        UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
                        String balanceType = CoinBalanceTypeEnum.INCOME.getCode();
                        String coinType = WalletEnum.CoinTypeEnum.CANCEL_BET.getCode();
                        String businessType = WalletEnum.BusinessCoinTypeEnum.GAME_BET.getCode();
                        BigDecimal amount = payoutVO.getPayoutAmount();
                        // 是否已结算
                        UserCoinRecordPO settleUserCoinRec = userCoinRecordService.getOne(Wrappers.<UserCoinRecordPO>lambdaQuery().eq(UserCoinRecordPO::getOrderNo, payoutVO.getThirdOrderId()).eq(UserCoinRecordPO::getCoinType,WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode()));
                        if (settleUserCoinRec != null) {
                            // 已结算
                            String originBalanceType = settleUserCoinRec.getBalanceType();
                            if (CoinBalanceTypeEnum.INCOME.getCode().equals(originBalanceType)) {
                                balanceType = CoinBalanceTypeEnum.EXPENSES.getCode();
                            }
                            amount = settleUserCoinRec.getCoinAmount().abs();
                            coinType = WalletEnum.CoinTypeEnum.CANCEL_GAME_PAYOUT.getCode();
                            businessType = WalletEnum.BusinessCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode();

                        }
                        userCoinAddVO.setUserId(userInfoVO.getUserId());
                        userCoinAddVO.setOrderNo(payoutVO.getThirdOrderId());
                        userCoinAddVO.setCurrency(userInfoVO.getMainCurrency());
                        userCoinAddVO.setBalanceType(balanceType);
                        userCoinAddVO.setCoinType(coinType);
                        userCoinAddVO.setBusinessCoinType(businessType);
                        userCoinAddVO.setCustomerCoinType(WalletEnum.CustomerCoinTypeEnum.CANCEL_GAME_PAYOUT.getCode());
                        userCoinAddVO.setCoinValue(amount);
                        userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
                        userCoinAddVO.setRemark(payoutVO.getVenueCode());

                        CoinRecordResultVO coinRecordResultVO = userCommonCoinService.userCommonCoinAdd(userCoinAddVO);
                        UpdateBalanceStatusEnums resultStatus = coinRecordResultVO.getResultStatus();
                        switch (resultStatus) {
                            case SUCCESS, AMOUNT_LESS_ZERO, REPEAT_TRANSACTIONS -> {
                            }
                            case INSUFFICIENT_BALANCE, WALLET_NOT_EXIST, FAIL -> {
                                log.error("收到三方游戏派彩消息,{} third game payout fail,req:{} error:{}", payoutVO.getVenueCode(), payoutVO, resultStatus);
                            }
                        }
                    }
                }

            }

        } catch (Exception e) {
            log.error("三方游戏派彩MQ 执行错误,消息id：{}", userGamePayoutMqVO.getMsgId(), e);
            // throw new BaowangDefaultException("三方游戏派彩MQ执行错误e");
        }finally {
           log.info("收到三方游戏派彩消息 the msg: {} 整体耗时:{}毫秒", userGamePayoutMqVO,(System.currentTimeMillis() - start));
           ackItem.acknowledge();
        }
    }
}
