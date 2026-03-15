package com.cloud.baowang.wallet.service;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.account.api.enums.activity.AccountActivityTemplateEnum;
import com.cloud.baowang.activity.api.enums.task.TaskReceiveStatusEnum;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.enums.ActivityReceiveStatusEnum;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinAddVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountMqVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.common.kafka.vo.UserWinLoseMqVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.site.rebate.UserVenueRebateApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateAuditQueryVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.enums.wallet.*;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyFromTransferVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyConvertRespVO;
import com.cloud.baowang.wallet.api.vo.userwallet.SiteRebateRewardRecordVO;
import com.cloud.baowang.wallet.po.SiteRebateRewardRecordPO;
import com.cloud.baowang.wallet.repositories.SiteRebateRewardRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SiteRebateRewardRecordService extends ServiceImpl<SiteRebateRewardRecordRepository, SiteRebateRewardRecordPO> {

    private SystemDictConfigApi systemDictConfigApi;

    private UserInfoApi userInfoApi;
    private final SiteCurrencyInfoService siteCurrencyInfoService;
    private final UserVenueRebateApi userVenueRebateApi;

    private final WalletUserCommonCoinService userCommonCoinService;


    @Async
    public void bachAddSiteRebateRewardRecordS(List<SiteRebateRewardRecordVO> data,String siteCode) {
        if (CollectionUtils.isEmpty(data)){
            return;
        }
        List<SiteRebateRewardRecordPO> batchData=new ArrayList<>();
        // 获取对应站点过期策略配置
        ResponseVO<List<SystemDictConfigRespVO>> responseVO = systemDictConfigApi
                .getListByCode(DictCodeConfigEnums.REBATE_BENEFIT_EXPIRATION_TIME.getCode());
        if(!responseVO.isOk() || null == responseVO.getData()){
            log.error("获取:{} 配置数据为空或者异常", DictCodeConfigEnums.REBATE_BENEFIT_EXPIRATION_TIME.getMsg());
        }
        List<SystemDictConfigRespVO> dictList = responseVO.getData().stream().filter(obj->
                siteCode.equals(obj.getSiteCode()) || CommonConstant.business_zero.toString()
                        .equals(obj.getSiteCode())).toList();
        Map<String, String> dictMap = dictList.stream().collect(Collectors
                .toMap(SystemDictConfigRespVO::getSiteCode, SystemDictConfigRespVO::getConfigParam));
        // 过期时间
        long expireMillSecond = dictMap.containsKey(siteCode) ? Long.parseLong(dictMap.get(siteCode))
                * 3600 * 1000 : Long.parseLong(dictMap
                .get(CommonConstant.business_zero_str)) * 3600 * 1000;
        long createTime= System.currentTimeMillis();
        long rebateInvalidTime=createTime+expireMillSecond;
        data.forEach(vo ->{
            SiteRebateRewardRecordPO po= new SiteRebateRewardRecordPO();
            BeanUtils.copyProperties(vo,po);
            po.setRebateNameI18nCode("LOOKUP_11642");
            po.setOpenStatus(ActivityReceiveStatusEnum.UN_RECEIVE.getCode());
            po.setInvalidTime(rebateInvalidTime);
            po.setCreatedTime(createTime);
            po.setUpdatedTime(createTime);
            po.setCurrencyCode(vo.getCurrencyCode());
            po.setSiteCode(siteCode);
            batchData.add(po);
        });
        this.saveBatch(batchData);
    }

    public Boolean rebateReward(String id){
        boolean lock = false;
        long current = System.currentTimeMillis();
        // 2. 查询
        LambdaUpdateWrapper<SiteRebateRewardRecordPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(SiteRebateRewardRecordPO::getId,id).eq(SiteRebateRewardRecordPO::getOpenStatus, TaskReceiveStatusEnum.ELIGIBLE.getCode());
        SiteRebateRewardRecordPO recordPO = this.getOne(lambdaUpdateWrapper);
        if (ObjectUtils.isEmpty(recordPO)) {
            return Boolean.FALSE;
        }
        RLock fairLock = RedisUtil.getFairLock(RedisConstants.REBATE_RECEIVE_LOCK_KEY + recordPO.getUserId());
        try {
            log.info("返水领取用户:{}", recordPO.getUserId());
            lock = fairLock.tryLock(RedisLockConstants.WAIT_TIME, RedisLockConstants.UNLOCK_TIME, TimeUnit.SECONDS);
            if (lock) {
                // 1. 获取用户最新登录信息 (IP 和设备号)
                UserInfoVO userInfoVO = userInfoApi.getByUserId(recordPO.getUserId());
                // 如果已经过期了，则需要更该状态
                if (recordPO.getInvalidTime() < current) {
                    // 更新状态变为已失效
                    updateRebateRecordExpired(recordPO);
                    return Boolean.FALSE;
                }
                //  获取用户信息，并更新用户钱包
                boolean flag = updateWallet(recordPO, ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
                if (flag) {
                    //更新状态已领取
                    updateRebateRecordSuccess(recordPO);
                    //  发送mq消息
                    handleSendWinLossMessage(recordPO, userInfoVO);
                } else {
                    throw new BaowangDefaultException(ResultCode.RECEIVE_FAIL_DESCRIPTION);
                }
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("反水领取发生异常", e);
            return Boolean.FALSE;
        } finally {
            if (lock && fairLock.isHeldByCurrentThread()) {
                fairLock.unlock();
            }
        }
    }

    public void rebateUserReward(String userId){
        boolean lock = false;
        long current = System.currentTimeMillis();
        // 2. 查询
        LambdaUpdateWrapper<SiteRebateRewardRecordPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(SiteRebateRewardRecordPO::getUserId,userId).eq(SiteRebateRewardRecordPO::getOpenStatus, TaskReceiveStatusEnum.ELIGIBLE.getCode());
        List<SiteRebateRewardRecordPO> recordPOs = this.list(lambdaUpdateWrapper);
        if (CollectionUtils.isEmpty(recordPOs)) {
            return ;
        }
        RLock fairLock = RedisUtil.getFairLock(RedisConstants.REBATE_RECEIVE_LOCK_KEY + userId);
        try {
            log.info("返水领取用户:{}", userId);
            lock = fairLock.tryLock(RedisLockConstants.WAIT_TIME, RedisLockConstants.UNLOCK_TIME, TimeUnit.SECONDS);
            if (lock) {
                for (SiteRebateRewardRecordPO recordPO:recordPOs){
                    // 1. 获取用户最新登录信息 (IP 和设备号)
                    UserInfoVO userInfoVO = userInfoApi.getByUserId(recordPO.getUserId());
                    // 如果已经过期了，则需要更该状态
                    if (recordPO.getInvalidTime() < current) {
                        // 更新状态
                        updateRebateRecordExpired(recordPO);
                        return ;
                    }
                    //  获取用户信息，并更新用户钱包
                    boolean flag = updateWallet(recordPO, ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
                    if (flag) {
                        //更新状态已领取
                        updateRebateRecordSuccess(recordPO);
                        //  发送mq消息
                        handleSendWinLossMessage(recordPO, userInfoVO);
                    }
                }
            }
        } catch (Exception e) {
            log.error("反水领取发生异常", e);
        } finally {
            if (lock && fairLock.isHeldByCurrentThread()) {
                fairLock.unlock();
            }
        }
    }


    private void updateRebateRecordExpired(SiteRebateRewardRecordPO recordPO) {
        //  更新领取状态和时间
        LambdaUpdateWrapper<SiteRebateRewardRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(SiteRebateRewardRecordPO::getOpenStatus, TaskReceiveStatusEnum.EXPIRED.getCode()).set(SiteRebateRewardRecordPO::getUpdatedTime, System.currentTimeMillis()).eq(SiteRebateRewardRecordPO::getId, recordPO.getId());
        // 执行更新操作
        this.getBaseMapper().update(null, updateWrapper);
    }

    private void updateRebateRecordSuccess(SiteRebateRewardRecordPO recordPO) {
        //  更新领取状态和时间
        long time=System.currentTimeMillis();
        LambdaUpdateWrapper<SiteRebateRewardRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(SiteRebateRewardRecordPO::getOpenStatus, TaskReceiveStatusEnum.CLAIMED.getCode())
                .set(SiteRebateRewardRecordPO::getUpdatedTime, time)
                .set(SiteRebateRewardRecordPO::getRewardTime, time)
                .eq(SiteRebateRewardRecordPO::getId, recordPO.getId());
        // 执行更新操作
        this.getBaseMapper().update(null, updateWrapper);
    }

    private boolean updateWallet(SiteRebateRewardRecordPO insert, WalletUserInfoVO userInfoVO) {
        // 更改成用户货币发放
        UserCoinAddVO userCoinAddVO  = new UserCoinAddVO();
        userCoinAddVO.setOrderNo(insert.getOrderNo());
        userCoinAddVO.setUserId(insert.getUserId());
        userCoinAddVO.setUserInfoVO(userInfoVO);
        userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.REBATE.getCode());
        userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.REBATE.getCode());
        userCoinAddVO.setCoinValue(insert.getRewardAmount());

        userCoinAddVO.setActivityFlag(AccountActivityTemplateEnum.REBATE.getCode());
        String remark = WalletEnum.BusinessCoinTypeEnum.REBATE.getName();
        userCoinAddVO.setRemark(remark + insert.getRewardAmount());
        boolean coinResult =true;
        CoinRecordResultVO recordResultVO = userCommonCoinService.userCommonCoinAdd(userCoinAddVO);
        //   默认是ture
        coinResult = recordResultVO.getResult();
        // 如果是false 判断是否是重复交易，如果交易重复，也设置为成功
        if (!coinResult) {
            if (recordResultVO.getResultStatus() == UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS) {
                coinResult = true;
            }
        }
        //增加打码量 = 只能是法币
        //默认大码倍数1
        BigDecimal runningWaterMultiple = BigDecimal.ONE;
        BigDecimal activityAmount = insert.getRewardAmount();
        //平台币转法币
        ResponseVO<SiteCurrencyConvertRespVO> siteCurrencyConvertRespVOResponseVO = siteCurrencyInfoService.transferToMainCurrency(PlatCurrencyFromTransferVO.builder().siteCode(userInfoVO.getSiteCode()).sourceAmt(insert.getRewardAmount()).targetCurrencyCode(userInfoVO.getMainCurrency()).build());
        if (siteCurrencyConvertRespVOResponseVO.isOk()) {
            activityAmount = siteCurrencyConvertRespVOResponseVO.getData().getTargetAmount();
            // 使用截取
            log.info("领取返水,增加打码量:id{},user:{},转换前币种:{},金额:{},转化后币种:{},金额:{}", insert.getId(), insert.getUserId(), CommonConstant.PLAT_CURRENCY_CODE, insert.getRewardAmount(), siteCurrencyConvertRespVOResponseVO.getData().getTargetCurrencyCode(), activityAmount);
        }

        if (runningWaterMultiple == null) {
            log.info("打码倍数为空,配置错误");
            return false;
        }
        if (activityAmount == null) {
            log.info("奖励金额为空,配置错误");
            return false;
        }

        //流水倍数 * 打码量 = 所需打码量
        BigDecimal typingAmount = AmountUtils.multiply(activityAmount, runningWaterMultiple);
        UserTypingAmountRequestVO userTypingAmountRequestVO = new UserTypingAmountRequestVO();
        userTypingAmountRequestVO.setTypingAmount(typingAmount);
        userTypingAmountRequestVO.setOrderNo(insert.getOrderNo());
        userTypingAmountRequestVO.setUserAccount(userInfoVO.getUserAccount());
        userTypingAmountRequestVO.setType(TypingAmountEnum.ADD.getCode());
        userTypingAmountRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        userTypingAmountRequestVO.setUserAccount(userInfoVO.getUserAccount());
        userTypingAmountRequestVO.setUserId(userInfoVO.getUserId());
        userTypingAmountRequestVO.setAdjustType(TypingAmountAdjustTypeEnum.REBATE.getCode());
        UserTypingAmountMqVO userTypingAmountMqVO = UserTypingAmountMqVO.builder().userTypingAmountRequestVOList(List.of(userTypingAmountRequestVO)).build();
        log.info("领取返水打码量:{}", JSONObject.toJSONString(userTypingAmountMqVO));
        KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC, userTypingAmountMqVO);
        return coinResult;
    }

    /**
     * 处理并发送会员每日盈亏消息到 Kafka 队列。
     * @param siteActivityOrderRecordPO 包含订单记录信息的对象
     * @param userInfoVO                包含用户信息的对象
     */
    private void handleSendWinLossMessage(SiteRebateRewardRecordPO siteActivityOrderRecordPO, UserInfoVO userInfoVO) {
        //增加返水回掉
        UserRebateAuditQueryVO vo=new UserRebateAuditQueryVO();
        vo.setUserId(userInfoVO.getUserId());
        vo.setOrderNo(siteActivityOrderRecordPO.getOrderNo());
        vo.setSiteCode(userInfoVO.getSiteCode());
        vo.setReceiveTime(siteActivityOrderRecordPO.getRewardTime());
        userVenueRebateApi.onUserRebateReceived(vo);

        UserWinLoseMqVO userWinLoseMqVO = new UserWinLoseMqVO();
        userWinLoseMqVO.setOrderId(siteActivityOrderRecordPO.getOrderNo());
        userWinLoseMqVO.setUserId(siteActivityOrderRecordPO.getUserId());
        userWinLoseMqVO.setAgentId(userInfoVO.getSuperAgentId());
        userWinLoseMqVO.setDayHourMillis(System.currentTimeMillis());
        // 任务发放的是平台币
        userWinLoseMqVO.setCurrency(userInfoVO.getMainCurrency());
        userWinLoseMqVO.setPlatformFlag(false);
        userWinLoseMqVO.setRebateAmount(siteActivityOrderRecordPO.getRewardAmount());
        userWinLoseMqVO.setBizCode(CommonConstant.business_eight);
        userWinLoseMqVO.setSiteCode(userInfoVO.getSiteCode());
        KafkaUtil.send(TopicsConstants.USER_WIN_LOSE_CHANNEL, userWinLoseMqVO);
    }

}
