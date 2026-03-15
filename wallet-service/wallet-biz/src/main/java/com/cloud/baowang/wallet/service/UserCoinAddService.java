package com.cloud.baowang.wallet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.usercoin.FreezeFlagEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.wallet.api.vo.withdraw.NoticeBalanceChangesVO;
import com.cloud.baowang.wallet.po.UserCoinPO;
import com.cloud.baowang.wallet.po.UserCoinRecordPO;
import com.cloud.baowang.wallet.repositories.UserCoinRecordRepository;
import com.cloud.baowang.wallet.repositories.UserCoinRepository;
import com.cloud.baowang.websocket.api.constants.WsMessageConstant;
import com.cloud.baowang.websocket.api.enums.ClientTypeEnum;
import com.cloud.baowang.websocket.api.enums.WSSubscribeEnum;
import com.cloud.baowang.websocket.api.vo.WSBaseResp;
import com.cloud.baowang.websocket.api.vo.WsMessageMqVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserCoinAddService {

    private final UserCoinRecordRepository userCoinRecordRepository;

    private final UserCoinRepository userCoinRepository;


    @DistributedLock(name = RedisKeyTransUtil.ADD_COIN_LOCK_KEY, unique = "#userCoinAddVO.userId", fair = true, waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public CoinRecordResultVO userCoinAdd(UserCoinAddVO userCoinAddVO, CoinRecordResultVO coinRecordResultVO) {
            LambdaQueryWrapper<UserCoinRecordPO> ucrlqw = new LambdaQueryWrapper<>();
            ucrlqw.eq(UserCoinRecordPO::getOrderNo, userCoinAddVO.getOrderNo());
            ucrlqw.eq(UserCoinRecordPO::getCoinType, userCoinAddVO.getCoinType());
            ucrlqw.eq(UserCoinRecordPO::getBalanceType, userCoinAddVO.getBalanceType());
            List<UserCoinRecordPO> userCoinRecordPOList = userCoinRecordRepository.selectList(ucrlqw);
            Long coinRecordTime = System.currentTimeMillis();
            if(null != userCoinAddVO.getCoinTime()){
                coinRecordTime = userCoinAddVO.getCoinTime();
            }
            Integer coinNum = CommonConstant.business_one;
            if (!userCoinRecordPOList.isEmpty()) {
                if(!VenueEnum.V8.getVenueCode().equals(userCoinAddVO.getVenueCode())
                        && !VenueEnum.DG2.getVenueCode().equals(userCoinAddVO.getVenueCode())
                        && !WalletEnum.CoinTypeEnum.RECALCULATE_GAME_PAYOUT.getCode().equals(userCoinAddVO.getCoinType())){
                    coinRecordResultVO.setResult(false);
                    coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS);
                    log.info("订单编号为{}的订单已添加账变", userCoinAddVO.getOrderNo());
                    return coinRecordResultVO;
                }
                coinNum = userCoinRecordPOList.size()+1;
            }
            BigDecimal coinValue = userCoinAddVO.getCoinValue().setScale(4, RoundingMode.DOWN);
    //            String userAccount = userCoinAddVO.getUserAccount();
            WalletUserInfoVO userInfoVO = userCoinAddVO.getUserInfoVO();
            //账变记录
            UserCoinRecordPO userCoinRecordPO = new UserCoinRecordPO();
            userCoinRecordPO.setId(SnowFlakeUtils.getSnowIdBySelfCenterId(userInfoVO.getSiteCode()));
            userCoinRecordPO.setSiteCode(userInfoVO.getSiteCode());
            userCoinRecordPO.setCurrency(userInfoVO.getMainCurrency());
            userCoinRecordPO.setUserName(userInfoVO.getUserName());
            userCoinRecordPO.setUserAccount(userInfoVO.getUserAccount());
            userCoinRecordPO.setUserId(userInfoVO.getUserId());
            userCoinRecordPO.setVipGradeCode(userInfoVO.getVipGradeCode());
            userCoinRecordPO.setUserLabelId(userInfoVO.getUserLabelId());
            userCoinRecordPO.setVipRank(userInfoVO.getVipRank());
            userCoinRecordPO.setAccountStatus(String.valueOf(userInfoVO.getAccountStatus()));
            userCoinRecordPO.setAccountType(userInfoVO.getAccountType());
            userCoinRecordPO.setRiskControlLevelId(userInfoVO.getRiskLevelId());
            userCoinRecordPO.setRiskControlLevel(userInfoVO.getRiskLevel());
            userCoinRecordPO.setAgentId(userInfoVO.getSuperAgentId());
            userCoinRecordPO.setAgentName(userInfoVO.getSuperAgentAccount());
            userCoinRecordPO.setBusinessCoinType(userCoinAddVO.getBusinessCoinType());
            userCoinRecordPO.setCoinType(userCoinAddVO.getCoinType());
            userCoinRecordPO.setCustomerCoinType(userCoinAddVO.getCustomerCoinType());
            userCoinRecordPO.setBalanceType(userCoinAddVO.getBalanceType());
            userCoinRecordPO.setOrderNo(userCoinAddVO.getOrderNo());
            userCoinRecordPO.setCoinValue(coinValue);
            userCoinRecordPO.setCoinNum(coinNum);
            userCoinRecordPO.setCreatedTime(coinRecordTime);
            userCoinRecordPO.setRemark(userCoinAddVO.getRemark());
            userCoinRecordPO.setDescInfo(userCoinAddVO.getDescInfo());
            LambdaQueryWrapper<UserCoinPO> lqw = new LambdaQueryWrapper<>();
            lqw.eq(UserCoinPO::getUserId,userCoinAddVO.getUserId());
            UserCoinPO userCoinPO = userCoinRepository.selectOne(lqw);
            if (null == userCoinPO) {
                if (!CoinBalanceTypeEnum.INCOME.getCode().equals(userCoinAddVO.getBalanceType())) {
                    log.info("账变失败会员{},无钱包信息，不能进行当前操作{}", userCoinAddVO.getUserId()
                            , userCoinAddVO.getBalanceType());
                    coinRecordResultVO.setResult(false);
                    coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.WALLET_NOT_EXIST);
                    return coinRecordResultVO;
                }
                userCoinRecordPO.setCoinFrom(BigDecimal.ZERO);
                userCoinRecordPO.setCoinTo(coinValue);
                userCoinRecordPO.setCoinAmount(coinValue);
                userCoinPO = new UserCoinPO();
                userCoinPO.setSiteCode(userInfoVO.getSiteCode());
                userCoinPO.setUserAccount(userInfoVO.getUserAccount());
                userCoinPO.setUserId(userInfoVO.getUserId());
                userCoinPO.setCurrency(userInfoVO.getMainCurrency());
                userCoinPO.setTotalAmount(coinValue);
                userCoinPO.setFreezeAmount(BigDecimal.ZERO);
                userCoinPO.setAvailableAmount(coinValue);
                userCoinPO.setIsBringInVenue("1");
                userCoinPO.setCreator(userInfoVO.getId());
                userCoinPO.setCreatedTime(coinRecordTime);
                userCoinPO.setUpdatedTime(coinRecordTime);
                userCoinRepository.insert(userCoinPO);
                coinRecordResultVO.setCoinAfterBalance(userCoinPO.getAvailableAmount());
                coinRecordResultVO.setCoinRecordTime(coinRecordTime);
            } else {
               /* if (CoinBalanceTypeEnum.EXPENSES.getCode().equals(userCoinAddVO.getBalanceType())) {
                    if (!CommonConstant.business_one.equals(userCoinAddVO.getWithdrawFlag()) &&
                            userCoinPO.getAvailableAmount().compareTo(userCoinAddVO.getCoinValue()) < 0) {
                        log.info("账变失败会员{},可用余额{},小于账变金额{}",userCoinAddVO.getUserAccount()
                                ,userCoinPO.getAvailableAmount(),userCoinAddVO.getCoinValue());
                        coinRecordResultVO.setResult(false);
                        coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.INSUFFICIENT_BALANCE);
                        return coinRecordResultVO;
                    }
                }*/
                userCoinRecordPO.setCoinFrom(userCoinPO.getAvailableAmount());
                userCoinRecordPO.setCoinAmount(coinValue);
                if (CoinBalanceTypeEnum.INCOME.getCode().equals(userCoinAddVO.getBalanceType())) {
                    userCoinPO.setTotalAmount(userCoinPO.getTotalAmount().add(coinValue));
                    userCoinPO.setAvailableAmount(userCoinPO.getAvailableAmount().add(coinValue));
                } else if (CoinBalanceTypeEnum.EXPENSES.getCode().equals(userCoinAddVO.getBalanceType())) {
                    if (FreezeFlagEnum.UNFREEZE.getCode().equals(userCoinAddVO.getFreezeFlag())) {
                        //取款申请时已冻结金额，可用余额已扣除，所以这里只扣除总金额和冻结金额
                        userCoinPO.setTotalAmount(userCoinPO.getTotalAmount().subtract(coinValue));
                        userCoinPO.setFreezeAmount(userCoinPO.getFreezeAmount().subtract(coinValue));
                    } else {
                        BigDecimal totalAmount = userCoinPO.getTotalAmount().subtract(coinValue);
                        BigDecimal availableAmount = userCoinPO.getAvailableAmount().subtract(coinValue);
                        userCoinPO.setTotalAmount(totalAmount);
                        userCoinPO.setAvailableAmount(availableAmount);
                    }

                } else if (CoinBalanceTypeEnum.FREEZE.getCode().equals(userCoinAddVO.getBalanceType())) {
                    userCoinPO.setAvailableAmount(userCoinPO.getAvailableAmount().subtract(coinValue));
                    userCoinPO.setFreezeAmount(userCoinPO.getFreezeAmount().add(coinValue));
                } else if (CoinBalanceTypeEnum.UN_FREEZE.getCode().equals(userCoinAddVO.getBalanceType())) {
                    userCoinPO.setAvailableAmount(userCoinPO.getAvailableAmount().add(coinValue));
                    userCoinPO.setFreezeAmount(userCoinPO.getFreezeAmount().subtract(coinValue));
                }

                userCoinRecordPO.setCoinTo(userCoinPO.getAvailableAmount());
                userCoinRepository.updateById(userCoinPO);
                coinRecordResultVO.setCoinAfterBalance(userCoinPO.getAvailableAmount());
                coinRecordResultVO.setCoinRecordTime(coinRecordTime);
            }
            userCoinPO.setUpdatedTime(System.currentTimeMillis());
            userCoinRecordRepository.insert(userCoinRecordPO);
            coinRecordResultVO.setId(userCoinRecordPO.getId());
            log.info("会员{}账变金额{}成功,订单编号{}", userCoinAddVO.getUserId(), coinValue, userCoinAddVO.getOrderNo());

            return coinRecordResultVO;
    }

    @Async
    public void sendNoticeBalanceChanges(WalletUserInfoVO userInfoVO, CoinRecordResultVO coinRecordResultVO){
        log.info("异步发送会员{},余额{}通知开始", userInfoVO.getUserId(),coinRecordResultVO.getCoinAfterBalance());
        try{
            //余额变化通知
            WsMessageMqVO messageMqVO = new WsMessageMqVO();
            messageMqVO.setSiteCode(userInfoVO.getSiteCode());
            messageMqVO.setUidList(Lists.newArrayList(userInfoVO.getUserId()));
            messageMqVO.setClientTypeEnum(ClientTypeEnum.CLIENT);
            BigDecimal coinAfterBalance = coinRecordResultVO.getCoinAfterBalance().compareTo(BigDecimal.ZERO) <0?BigDecimal.ZERO:coinRecordResultVO.getCoinAfterBalance();
            messageMqVO.setMessage(new WSBaseResp<>(WSSubscribeEnum.NOTICE_BALANCE_CHANGES.getTopic(),
                    ResponseVO.success(new NoticeBalanceChangesVO(coinAfterBalance))));
            KafkaUtil.send(WsMessageConstant.WS_MESSAGE_BROADCAST_TOPIC, messageMqVO);
        }catch (Exception e){
            log.info("异步发送会员{},余额{}通知失败，失败信息{}", userInfoVO.getUserId(),coinRecordResultVO.getCoinAfterBalance(),e.getMessage());
        }
        log.info("异步发送会员{},余额{}通知结束", userInfoVO.getUserId(),coinRecordResultVO.getCoinAfterBalance());
    }
}
