package com.cloud.baowang.account.consumer;

import com.cloud.baowang.account.api.enums.*;
import com.cloud.baowang.account.api.vo.AccountAgentCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountBusinessUserReqVO;
import com.cloud.baowang.account.api.vo.AccountUserCoinAddReqVO;
import com.cloud.baowang.account.api.vo.AccountUserPlatformCoinAddReqVO;
import com.cloud.baowang.account.service.account.AccountTransfer;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.vo.*;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Component
@AllArgsConstructor
public class UserCoinConsumer {
    private final AccountTransfer accountTransfer;

    @KafkaListener(topics = TopicsConstants.ACCOUNT_USER_COIN_TOPIC, groupId = GroupConstants.ACCOUNT_USER_COIN_TOPIC_GROUP)
    @DistributedLock(name = RedisKeyTransUtil.ADD_COIN_LOCK_KEY, unique = "#reqVO.userId", fair = true, waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void userTransfer(AccountUserCoinRequestMqVO reqVO, Acknowledgment ackItem){
        log.error("userTransfer:{}",reqVO);
        try {
            //账务系统变更
            AccountBusinessUserReqVO accountUserReqVO = new AccountBusinessUserReqVO();
            accountUserReqVO.setAccountName(reqVO.getUserAccount());
            accountUserReqVO.setSourceAccountNo(reqVO.getUserId());
            accountUserReqVO.setSiteCode(reqVO.getSiteCode());
            accountUserReqVO.setCode(reqVO.getCoinType());
            accountUserReqVO.setUserType(SourceAccountTypeEnums.MEMBER.getType());
            accountUserReqVO.setBalanceType(reqVO.getBalanceType());
            accountUserReqVO.setCurrencyCode(reqVO.getCurrencyCode());
            accountUserReqVO.setAccountStatus(reqVO.getAccountStatus());
            accountUserReqVO.setInnerOrderNo(reqVO.getInnerOrderNo());
            accountUserReqVO.setThirdOrderNo(reqVO.getThirdOrderNo());
            accountUserReqVO.setToThridCode(reqVO.getToThirdCode());
            accountUserReqVO.setWalletAmount(BigDecimal.ZERO);
            accountUserReqVO.setCoinTime(reqVO.getCoinTime());
            accountUserReqVO.setCoinValue(reqVO.getCoinValue());
            accountUserReqVO.setFinalRate(reqVO.getFinalRate());
            if (AccountBalanceTypeEnum.FREEZE.getCode().equals(reqVO.getBalanceType()) ||
                    AccountBalanceTypeEnum.UN_FREEZE.getCode().equals(reqVO.getBalanceType())){
                accountUserReqVO.setBalanceType(AccountBalanceTypeEnum.EXPENSES.getCode());
            }
            if (AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_WITHDRAWAL.getCode().equals(
                    reqVO.getBusinessCoinType())&& Objects.nonNull(reqVO.getFreezeFlag())){
                accountUserReqVO.setBussinessFlag(String.valueOf(reqVO.getFreezeFlag()));
            }
            if ((AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode().equals(
                    reqVO.getBusinessCoinType())||
                    AccountUserCoinEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode().equals(
                            reqVO.getBusinessCoinType())) && Objects.nonNull(reqVO.getActivityFlag())){
                accountUserReqVO.setBussinessFlag(String.valueOf(reqVO.getActivityFlag()));
            }
            accountTransfer.singleTransfer(accountUserReqVO);
        } catch (Exception e) {
            log.error("gameTransfer发生异常: {}", reqVO , e);
        }finally {
            ackItem.acknowledge();
        }
    }


    @KafkaListener(topics = TopicsConstants.ACCOUNT_USER_PLATFROM_COIN_TOPIC, groupId = GroupConstants.ACCOUNT_USER_PLATFROM_COIN_TOPIC_GROUP)
    @DistributedLock(name = RedisKeyTransUtil.ADD_COIN_LOCK_KEY, unique = "#reqVO.userId", fair = true, waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
   public void platfromTransfer(AccountPlatfromCoinRequestMqVO reqVO, Acknowledgment ackItem){
        log.error("platfromTransfer:{}",reqVO);
        try {
            //账务系统变更
            AccountBusinessUserReqVO accountUserReqVO = new AccountBusinessUserReqVO();
            accountUserReqVO.setAccountName(reqVO.getUserAccount());
            accountUserReqVO.setSourceAccountNo(reqVO.getUserId());
            accountUserReqVO.setSiteCode(reqVO.getSiteCode());
            accountUserReqVO.setCode(reqVO.getCoinType());
            accountUserReqVO.setUserType(SourceAccountTypeEnums.PLATFORM.getType());
            accountUserReqVO.setBalanceType(reqVO.getBalanceType());
            accountUserReqVO.setCurrencyCode(reqVO.getCurrencyCode());
            accountUserReqVO.setAccountStatus(reqVO.getAccountStatus());
            accountUserReqVO.setInnerOrderNo(reqVO.getInnerOrderNo());
            accountUserReqVO.setThirdOrderNo(reqVO.getThirdOrderNo());
            accountUserReqVO.setToThridCode(reqVO.getToThirdCode());
            accountUserReqVO.setWalletAmount(BigDecimal.ZERO);
            accountUserReqVO.setCoinTime(reqVO.getCoinTime());
            accountUserReqVO.setCoinValue(reqVO.getCoinValue());
            accountUserReqVO.setFinalRate(reqVO.getFinalRate());
            if ((AccountPlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode().equals(
                    reqVO.getBusinessCoinType())||
                    AccountPlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode().equals(
                            reqVO.getBusinessCoinType())) && Objects.nonNull(reqVO.getActivityFlag())){
                accountUserReqVO.setBussinessFlag(String.valueOf(reqVO.getActivityFlag()));
            }
            accountTransfer.singleTransfer(accountUserReqVO);
        } catch (Exception e) {
            log.error("gameTransfer发生异常: {}", reqVO , e);
        }finally {
            ackItem.acknowledge();
        }
    }


    @KafkaListener(topics = TopicsConstants.ACCOUNT_AGENT_COIN_TOPIC, groupId = GroupConstants.ACCOUNT_AGENT_COIN_TOPIC_GROUP)
    @DistributedLock(name = RedisKeyTransUtil.ADD_COIN_LOCK_KEY, unique = "#reqVO.agentId", fair = true, waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void AgentTransfer(AccountAgentCoinRequestMqVO reqVO, Acknowledgment ackItem){
        log.error("AgentTransfer:{}",reqVO);
        try {
            //账务系统变更
            AccountBusinessUserReqVO accountUserReqVO = new AccountBusinessUserReqVO();

            accountUserReqVO.setAccountName(reqVO.getAgentAccount());
            accountUserReqVO.setSourceAccountNo(reqVO.getAgentId());
            accountUserReqVO.setSiteCode(reqVO.getSiteCode());
            accountUserReqVO.setCode(reqVO.getCoinType());
            accountUserReqVO.setUserType(SourceAccountTypeEnums.AGENT.getType());
            accountUserReqVO.setBalanceType(reqVO.getBalanceType());
            accountUserReqVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
            accountUserReqVO.setAccountStatus(reqVO.getStatus());
            accountUserReqVO.setInnerOrderNo(reqVO.getInnerOrderNo());
            accountUserReqVO.setThirdOrderNo(reqVO.getThirdOrderNo());
            accountUserReqVO.setToThridCode(reqVO.getToThirdCode());
            accountUserReqVO.setWalletAmount(BigDecimal.ZERO);
            accountUserReqVO.setCoinTime(reqVO.getCoinTime());
            accountUserReqVO.setCoinValue(reqVO.getCoinValue());
            accountUserReqVO.setFinalRate(reqVO.getFinalRate());
            if (AccountBalanceTypeEnum.FREEZE.getCode().equals(reqVO.getBalanceType()) ||
                    AccountBalanceTypeEnum.UN_FREEZE.getCode().equals(reqVO.getBalanceType())){
                accountUserReqVO.setBalanceType(AccountBalanceTypeEnum.EXPENSES.getCode());
            }
            if (AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_WITHDRAWAL.getCode().equals(
                    reqVO.getBusinessCoinType())&& Objects.nonNull(reqVO.getFreezeFlag())){
                accountUserReqVO.setBussinessFlag(String.valueOf(reqVO.getFreezeFlag()));
            }
            if (AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_TRANSFER.getCode().equals(reqVO.getBusinessCoinType())
                    || AccountAgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.DEPOSIT_OF_SUBORDINATES.getCode().equals(reqVO.getBusinessCoinType())){
                accountUserReqVO.setBussinessFlag(reqVO.getAgentWalletType());
            }
            accountTransfer.singleTransfer(accountUserReqVO);
        } catch (Exception e) {
            log.error("gameTransfer发生异常: {}", reqVO , e);
        }finally {
            ackItem.acknowledge();
        }
    }

    //游戏流量
    @KafkaListener(topics = TopicsConstants.ACCOUNT_GAME_TOPIC, groupId = GroupConstants.ACCOUNT_GAME_TOPIC_GROUP)
    @DistributedLock(name = RedisKeyTransUtil.ADD_COIN_LOCK_KEY, unique = "#reqVO.userId", fair = true, waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void gameTransfer(AccountUserCoinRequestMqVO reqVO, Acknowledgment ackItem){
        log.error("gameTransfer:{}",reqVO);
        try {
            //账务系统变更
            AccountBusinessUserReqVO accountUserReqVO = new AccountBusinessUserReqVO();
            accountUserReqVO.setAccountName(reqVO.getUserAccount());
            accountUserReqVO.setSourceAccountNo(reqVO.getUserId());
            accountUserReqVO.setSiteCode(reqVO.getSiteCode());
            accountUserReqVO.setCode(reqVO.getAccountCoinType());
            accountUserReqVO.setBalanceType(reqVO.getBalanceType());
            accountUserReqVO.setCurrencyCode(reqVO.getCurrencyCode());
            accountUserReqVO.setAccountStatus(reqVO.getAccountStatus());
            accountUserReqVO.setInnerOrderNo(reqVO.getInnerOrderNo());
            accountUserReqVO.setThirdOrderNo(reqVO.getThirdOrderNo());
            accountUserReqVO.setToThridCode(reqVO.getToThirdCode());
            accountUserReqVO.setWalletAmount(BigDecimal.ZERO);
            accountUserReqVO.setCoinTime(reqVO.getCoinTime());
            accountUserReqVO.setCoinValue(reqVO.getCoinValue());
            accountUserReqVO.setFinalRate(reqVO.getFinalRate());
            accountUserReqVO.setBussinessFlag(String.valueOf(reqVO.getFreezeFlag()));
            accountTransfer.gameTransfer(accountUserReqVO);
        } catch (Exception e) {
            log.error("gameTransfer发生异常: {}", reqVO , e);
        }finally {
            ackItem.acknowledge();
        }
    }

    //拉单发送消息
    @KafkaListener(topics = TopicsConstants.CLEAN_ACCOUNT_VENUE_BET_AMOUNT_TOPIC, groupId = GroupConstants.CLEAN_ACCOUNT_VENUE_BET_AMOUNT_TOPIC_GROUP)
    public void cleanHandlerList(AccountFlowMqVO vo, Acknowledgment ackItem) {
        if (vo == null) {
            log.error("批量清理场馆账户余额投注注单内容:{}",vo);
            return;
        }
        try {
            List<AccountRequestMqVO> accountRequestList = vo.getAccountRequestList();
            Map<String, List<AccountRequestMqVO>> flowMap = accountRequestList.stream()
                    .collect(Collectors.groupingBy(AccountRequestMqVO::getUserId));
            for (Map.Entry<String, List<AccountRequestMqVO>> map : flowMap.entrySet()) {
                log.info("投注用户注单内容:{}  ,参数 :{}", map.getKey(), map.getValue());
                accountTransfer.batchCleanAccountCoin(map.getValue());
            }
        } catch (Exception e) {
            log.error("清理三方场馆场馆账户表发生异常: {}", vo , e);
        }finally {
            ackItem.acknowledge();
        }
    }

}
