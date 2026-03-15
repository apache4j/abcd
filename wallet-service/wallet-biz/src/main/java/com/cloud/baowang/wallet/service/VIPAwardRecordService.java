package com.cloud.baowang.wallet.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.api.ActivityFinanceApi;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountMqVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.common.kafka.vo.UserWinLoseMqVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.enums.ActivityReceiveStatusEnum;
import com.cloud.baowang.user.api.enums.ReceiveStatusEnum;
import com.cloud.baowang.user.api.vo.vip.SiteVIPBenefitVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipSportVo;
import com.cloud.baowang.user.api.vo.vip.VIPChangeRequestVO;
import com.cloud.baowang.wallet.api.enums.ReceiveTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.*;
import com.cloud.baowang.wallet.api.vo.rebate.OrderRebateRequestVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserAwardRecordVO;
import com.cloud.baowang.wallet.po.SiteRebateRewardRecordPO;
import com.cloud.baowang.wallet.po.VIPAwardRecordPO;
import com.cloud.baowang.wallet.repositories.VIPAwardRecordRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author : 小智
 * @Date : 14/6/24 10:13 PM
 * @Version : 1.0
 */
@Service
@Transactional
@Data
@Slf4j
public class VIPAwardRecordService extends ServiceImpl<VIPAwardRecordRepository,
        VIPAwardRecordPO> {

    private final VIPAwardRecordRepository vipAwardRecordRepository;

    private final UserInfoApi userInfoApi;


    private final VipRankApi vipRankApi;
    private final SiteRebateRewardRecordService siteRebateRewardRecordService;

    private final SiteCurrencyInfoService siteCurrencyInfoService;
    private final ActivityFinanceApi activityFinanceApi;

    private final WalletUserCommonCoinService walletUserCommonCoinService;

    private final WalletUserCommonPlatformCoinService walletUserCommonPlatformCoinService;

    public void recordUserRebate(List<OrderRebateRequestVO> rebateRequestVOList) {
        try {
            // 周,月奖励记录表
            long currentTime = System.currentTimeMillis();
            List<VIPAwardRecordPO> list = Lists.newArrayList();
            rebateRequestVOList.forEach(obj -> {
                VIPAwardRecordPO po = VIPAwardRecordPO.builder().agentAccount(obj.getAgentAccount())
                        .accountType(obj.getUserInfoVO().getAccountType()).userAccount(obj.getUserAccount())
                        .userId(obj.getUserInfoVO().getUserId()).siteCode(obj.getUserInfoVO().getSiteCode())
                        .receiveType(ReceiveTypeEnum.MANUAL_RECEIVE.getCode())
                        .vipRankCode(obj.getVipRankCode()).currency(obj.getMainCurrency())
                        .vipGradeCode(obj.getUserInfoVO().getVipGradeCode()).orderId(obj.getOrderId())
                        .awardAmount(obj.getRebateAmount()).awardType(obj.getFlag().toString())
                        .expiredTime(currentTime + obj.getExpireTime()).recordStartTime(obj.getRecordStartTime())
                        .recordEndTime(obj.getRecordEndTime())
                        .receiveStatus(ActivityReceiveStatusEnum.UN_RECEIVE.getCode()).build();
                po.setCreatedTime(currentTime);
                po.setUpdatedTime(currentTime);
                list.add(po);
            });
            this.saveBatch(list);
            // 触发账变
            //addCoin(rebateRequestVOList);
        } catch (Exception e) {
            log.error("record user rebate have error", e);
        }
    }

    public void vipUpgradeAward(Date upgradeStart, Date upgradeEnd) {
        VIPChangeRequestVO vo = new VIPChangeRequestVO();
        vo.setChangeType(BigDecimal.ZERO.intValue());
        vo.setChangeTimeStart(upgradeStart.getTime());
        vo.setChangeTimeEnd(upgradeEnd.getTime());
        Date thisDayStart = DateUtil.beginOfDay(new Date());
        Date thisDayEnd = DateUtil.endOfDay(new Date());
        List<String> list = vipAwardRecordRepository.selectUpgradeAward(vo,
                thisDayStart.getTime(), thisDayEnd.getTime());
        // 查询该等级下是否已经领取过VIP升级礼金
        List<String> receivedList = vipAwardRecordRepository.selectCanReceiveAccounts();
        // 剔除已经领取过的人
        list = removeReceived(list, receivedList);
        List<UserInfoVO> userInfoVOS = userInfoApi.getUserInfoListByAccounts(list);
        // 特殊代理将不发放该奖励
//        List<String> agentDiscounts = systemBusinessFeignResource
//                .getAllDiscountManager(VIPAwardEnum.UPGRADE_BONUS.getCode());
//        Set<String> agentAccounts = agentFeignResource.getAllUnderAgent(agentDiscounts);
//        if(ObjectUtil.isNotEmpty(userInfoVOS)){
//            userInfoVOS = userInfoVOS.stream().filter(obj -> !agentAccounts.contains(obj.getSuperAgentAccount())).toList();
//        }
        // 获取所有VIP下的权益信息
        List<SiteVIPBenefitVO> vipBenefitVOS = Lists.newArrayList();
        if (null != vipBenefitVOS) {
            Map<Integer, BigDecimal> map = vipBenefitVOS.stream()
                    .collect(Collectors.toMap(SiteVIPBenefitVO::getVipRankCode, SiteVIPBenefitVO::getUpgrade));
            // 触发账变并记录VIP奖励表
            sendAwardCoin(userInfoVOS, map, VIPAwardEnum.UPGRADE_BONUS.getCode(), "升级礼金", null, 2);
        }
    }

    private void sendAwardCoin(List<UserInfoVO> userInfoVOS, Map<Integer, BigDecimal> map, String awardType,
                               String remark, Long expiredTime, Integer vipIndex) {
        if (null != userInfoVOS) {
            List<VIPAwardRecordPO> vipAwardRecordPOList = Lists.newArrayList();
            for (UserInfoVO vo : userInfoVOS) {
                VIPAwardRecordPO po = new VIPAwardRecordPO();
                if (BigDecimal.ZERO.intValue() == vo.getVipGradeCode()) {
                    continue;
                }
                po.setAwardType(awardType);
                po.setAwardAmount(map.get(vo.getVipGradeCode()));
                po.setVipGradeCode(vo.getVipGradeCode());
                po.setOrderId("J" + SnowFlakeUtils.getSnowId());
                po.setReceiveStatus(ActivityReceiveStatusEnum.UN_RECEIVE.getCode());
                po.setUserAccount(vo.getUserAccount());
                po.setAccountType(String.valueOf(vo.getAccountType()));
                po.setAgentAccount(vo.getSuperAgentAccount());
                po.setCreatedTime(System.currentTimeMillis());
                po.setUpdatedTime(System.currentTimeMillis());
                po.setExpiredTime(expiredTime);
                vipAwardRecordPOList.add(po);
//                UserSysNoticeConfigAddVO userSysNoticeConfigAddVO = new UserSysNoticeConfigAddVO();
//                userSysNoticeConfigAddVO.setUserAccount(vo.getUserAccount());
//                userSysNoticeConfigAddVO.setNoticeType(BigDecimalConstants.FOUR.intValue());
//                userSysNoticeConfigAddVO.setNoticeTitle("会员" + remark + "发放通知");
//                userSysNoticeConfigAddVO.setBusinessLine(UserSysMessageEnum.VIP_BENEFIT.getCode());
//                userSysNoticeConfigAddVO.setMessageType(UserSysMessageEnum.VIP_BENEFIT
//                        .getMessageTypes()[vipIndex].getSubCode());
//                userSysNoticeConfigAddVO.setOperator("Player");
//                String content = "尊敬的" + vo.getUserAccount() + ": " +
//                        "您的" + remark + po.getAwardAmount() + "元, 已发放到您的账户, " +
//                        "请查看到账金额。" +
//                        "如有任何疑问, 可联系客服。";
//                userSysNoticeConfigAddVO.setMessageContent(content);
//                userFeignResource.addUserSysNoticeConfig(userSysNoticeConfigAddVO);
                // 上半月红包触发会员账变(这里不需要了，改为手动领取触发)
//                UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
//                userCoinAddVO.setUserAccount(vo.getUserAccount());
//                userCoinAddVO.setOrderNo(po.getOrderId());
//                userCoinAddVO.setCoinValue(po.getAwardAmount());
//                userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
//                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
//                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
//                userCoinAddVO.setRemark(remark);
//                userCoinService.addCoin(userCoinAddVO);
            }
            // VIP奖励记录
            this.saveBatch(vipAwardRecordPOList);
        }
    }

    private List<String> removeReceived(List<String> list, List<String> receivedList) {
        List<String> result = Lists.newArrayList();
        for (String s : list) {
            if (!receivedList.contains(s)) {
                result.add(s);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }

    /**
     * 一键领取会员奖励
     *
     * @param userId
     * @return
     */
    public boolean receiveUserAward(String userId) {
        // 会员信息
        try {
            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);

            // 会员VIP奖励
            List<VIPAwardRecordPO> awardRecordPOList = this.lambdaQuery().eq(VIPAwardRecordPO::getUserId, userId)
                    .eq(VIPAwardRecordPO::getReceiveStatus, ActivityReceiveStatusEnum.UN_RECEIVE.getCode())
                    .gt(VIPAwardRecordPO::getExpiredTime, System.currentTimeMillis())
                    .list();

            if(ObjectUtil.isEmpty(awardRecordPOList)){
                log.info("该会员id:{} 没有需要一键领取的VIP福利", userId);
                return false;
            }

            List<SiteCurrencyInfoRespVO> currencyInfoRespVOS = siteCurrencyInfoService.getBySiteCode(
                    userInfoVO.getSiteCode());
            Map<String, BigDecimal> currencyMap = currencyInfoRespVOS.stream().collect(Collectors
                    .toMap(SiteCurrencyInfoRespVO::getCurrencyCode, SiteCurrencyInfoRespVO::getFinalRate));
            ResponseVO<List<SiteVIPRankVO>> vipRankResponse = vipRankApi.getVipRankListBySiteCode(CurrReqUtils.getSiteCode());
            if(null == vipRankResponse || !vipRankResponse.isOk()){
                log.error("该会员id:{} 获取VIP段位信息配置异常", userId);
                return false;
            }
            List<SiteVIPRankVO> siteVIPRankVOList = vipRankResponse.getData();


            for (VIPAwardRecordPO award : awardRecordPOList) {
                long receiveTime = System.currentTimeMillis();

                VIPAwardEnum vipAwardEnum = VIPAwardEnum.nameOfCode(award.getAwardType());
                if (CommonConstant.PLAT_CURRENCY_CODE.equals(award.getCurrency())) {
                    // 平台币账变 打码量
                    UserPlatformCoinAddVO userCoinAddVO = new UserPlatformCoinAddVO();
                    userCoinAddVO.setUserId(userId);
                    userCoinAddVO.setCoinType(PlatformWalletEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
                    userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
                    userCoinAddVO.setOrderNo(award.getOrderId());
                    userCoinAddVO.setRemark(VIPAwardEnum.nameOfCode(award.getAwardType()).getName());
                    userCoinAddVO.setBusinessCoinType(PlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
                    userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
                    userCoinAddVO.setCoinValue(award.getAwardAmount());
                    userCoinAddVO.setActivityFlag(vipAwardEnum.getAccountCoinType());
                    CoinRecordResultVO coinRecordResultVO = walletUserCommonPlatformCoinService.userCommonPlatformCoin(userCoinAddVO);
                    if (coinRecordResultVO.getResult()) {
                        // 增加打码量
                        addTypeAmount(userInfoVO, award, siteVIPRankVOList, currencyMap);
                        LambdaUpdateWrapper<VIPAwardRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
                        updateWrapper.eq(VIPAwardRecordPO::getOrderId, award.getOrderId());
                        updateWrapper.eq(VIPAwardRecordPO::getReceiveStatus, ActivityReceiveStatusEnum.UN_RECEIVE.getCode());
                        updateWrapper.set(VIPAwardRecordPO::getReceiveStatus, ActivityReceiveStatusEnum.RECEIVE.getCode());
                        updateWrapper.set(VIPAwardRecordPO::getReceiveTime, System.currentTimeMillis());
                        this.update(null, updateWrapper);
                        log.info("平台币vip奖励发送会员盈亏消息{}", JSONObject.toJSONString(award));
                        handleSendWinLossMessage(award, userInfoVO, receiveTime);
                    }
                } else {
                    //主货币账变 打码量
                    UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
                    userCoinAddVO.setUserId(userId);
                    userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
                    userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
                    userCoinAddVO.setOrderNo(award.getOrderId());
                    userCoinAddVO.setRemark(VIPAwardEnum.nameOfCode(award.getAwardType()).getName());
                    userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
                    userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
                    userCoinAddVO.setCoinValue(award.getAwardAmount());

                    userCoinAddVO.setActivityFlag(vipAwardEnum.getAccountCoinType());
                    CoinRecordResultVO recordResultVO = walletUserCommonCoinService.userCommonCoinAdd(userCoinAddVO);
                    if (recordResultVO.getResult()) {
                        // 增加打码量
                        addTypeAmount(userInfoVO, award, siteVIPRankVOList, currencyMap);
                        LambdaUpdateWrapper<VIPAwardRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
                        updateWrapper.eq(VIPAwardRecordPO::getOrderId, award.getOrderId());
                        updateWrapper.eq(VIPAwardRecordPO::getReceiveStatus, ActivityReceiveStatusEnum.UN_RECEIVE.getCode());
                        updateWrapper.set(VIPAwardRecordPO::getReceiveStatus, ActivityReceiveStatusEnum.RECEIVE.getCode());
                        updateWrapper.set(VIPAwardRecordPO::getReceiveTime, receiveTime);
                        this.update(null, updateWrapper);
                        log.info("主货币vip奖励发送会员盈亏消息{}", JSONObject.toJSONString(award));
                        handleSendWinLossMessage(award, userInfoVO, receiveTime);
                    }
                }
                // currecy 是WTC走平台币 其他走主货币
            }
        } catch (Exception e) {
            log.error("一键领取VIP奖励发生异常, userId:{}", userId, e);
            return false;
        }
        return true;
    }

    /**
     * 任务领取处理并发送会员每日盈亏消息到 Kafka 队列。
     *
     *
     * @param viPAwardRecordPO 包含订单记录信息的对象
     * @param userInfoVO       包含用户信息的对象
     */
    private void handleSendWinLossMessage(VIPAwardRecordPO viPAwardRecordPO, UserInfoVO userInfoVO, long receiveTime) {
        UserWinLoseMqVO userWinLoseMqVO = new UserWinLoseMqVO();
        userWinLoseMqVO.setOrderId(viPAwardRecordPO.getOrderId());
        userWinLoseMqVO.setUserId(viPAwardRecordPO.getUserId());
        //userWinLoseMqVO.setUserAccount(viPAwardRecordPO.getUserAccount());
        userWinLoseMqVO.setAgentId(userInfoVO.getSuperAgentId());
        //userWinLoseMqVO.setAgentAccount(userInfoVO.getSuperAgentAccount());
        //long dayHourMillis = TimeZoneUtils.convertToUtcStartOfHour(System.currentTimeMillis());
        userWinLoseMqVO.setDayHourMillis(receiveTime);
        // 任务发放的是平台币
        //userWinLoseMqVO.setCurrency(viPAwardRecordPO.getCurrencyCode());
        userWinLoseMqVO.setActivityAmount(viPAwardRecordPO.getAwardAmount());
        userWinLoseMqVO.setVipBenefitAmount(viPAwardRecordPO.getAwardAmount());
        userWinLoseMqVO.setBizCode(CommonConstant.business_seven);
        userWinLoseMqVO.setSiteCode(userInfoVO.getSiteCode());
        // 根据货币code 进行判断
        if(CommonConstant.PLAT_CURRENCY_CODE.equals(viPAwardRecordPO.getCurrency())){
            userWinLoseMqVO.setPlatformFlag(true);
            userWinLoseMqVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        }else{
            userWinLoseMqVO.setPlatformFlag(false);
            userWinLoseMqVO.setCurrency(viPAwardRecordPO.getCurrency());
        }
        KafkaUtil.send(TopicsConstants.USER_WIN_LOSE_CHANNEL, userWinLoseMqVO);
    }


    private void addTypeAmount(UserInfoVO userInfoVO, VIPAwardRecordPO award,
           List<SiteVIPRankVO> siteVIPRankVOList, Map<String, BigDecimal> currencyMap) {
        // 添加打码量
        UserTypingAmountMqVO userTypingAmountMqVO = new UserTypingAmountMqVO();
        UserTypingAmountRequestVO userTypingAmount = new UserTypingAmountRequestVO();
        // 注单号
        String rebateOrderNo = "GG" + SnowFlakeUtils.getSnowId();
        userTypingAmount.setUserAccount(userInfoVO.getUserAccount());
        userTypingAmount.setUserId(userInfoVO.getUserId());
        userTypingAmount.setOrderNo(rebateOrderNo);
        userTypingAmount.setAdjustType(TypingAmountAdjustTypeEnum.VIP_BENEFITS.getCode());
        userTypingAmount.setRemark(TypingAmountAdjustTypeEnum.VIP_BENEFITS.getName());
        Integer vipRankCode = userInfoVO.getVipRank();
        // 打码量 返水金额乘以打码倍数
        BigDecimal typingAmount = BigDecimal.ZERO;
        // 周打码量倍数
        BigDecimal weekMultiple = siteVIPRankVOList.stream()
                .filter(obj-> Objects.equals(obj.getVipRankCode(), vipRankCode)).findFirst()
                .orElse(new SiteVIPRankVO()).getWeekAmountMultiple();
        if(null == weekMultiple){
            log.error("该会员:{} 周流水倍数无配置, 无法新增打码量", userInfoVO.getUserAccount());
            return;
        }
        // 月打码量倍数
        BigDecimal monthMultiple = siteVIPRankVOList.stream()
                .filter(obj-> Objects.equals(obj.getVipRankCode(), vipRankCode)).findFirst()
                .orElse(new SiteVIPRankVO()).getMonthAmountMultiple();
        if(null == monthMultiple){
            log.error("该会员:{} 月流水倍数无配置, 无法新增打码量", userInfoVO.getUserAccount());
            return;
        }
        // 周体育打码量倍数
        BigDecimal weekSportMultiple = BigDecimal.ZERO;
        SiteVIPRankVO vipRankVO = siteVIPRankVOList.stream()
                .filter(obj-> CollectionUtil.isNotEmpty(obj.getSportVos()))
                .filter(obj-> Objects.equals(obj.getVipRankCode(), vipRankCode)).findFirst().orElse(new SiteVIPRankVO());
        if(ObjectUtil.isNotEmpty(vipRankVO.getSportVos())){
            weekSportMultiple = vipRankVO.getSportVos().stream().findFirst().orElse(new SiteVipSportVo())
                    .getWeekSportMultiple();
        }

        if(null == weekSportMultiple || weekSportMultiple.equals(BigDecimal.ZERO)){
            log.error("该会员:{} 周体育倍数无配置, 无法新增打码量", userInfoVO.getUserAccount());
            return;
        }
        switch(VIPAwardEnum.nameOfCode(award.getAwardType())){
            case UPGRADE_BONUS -> typingAmount = AmountUtils.multiply(award.getAwardAmount(), currencyMap
                    .get(userInfoVO.getMainCurrency())).multiply(BigDecimal.ONE);
            case WEEK_BONUS -> typingAmount = AmountUtils.multiply(award.getAwardAmount(), weekMultiple);
            case MONTH_BONUS -> typingAmount = AmountUtils.multiply(award.getAwardAmount(), monthMultiple);
            case WEEK_SPORT_BONUS -> typingAmount = AmountUtils.multiply(AmountUtils.multiply(award.getAwardAmount(), currencyMap
                    .get(userInfoVO.getMainCurrency())), weekSportMultiple);
        }
        userTypingAmount.setTypingAmount(typingAmount);
        userTypingAmount.setType(TypingAmountEnum.ADD.getCode());
        // 发送打码量mq
        List<UserTypingAmountRequestVO> userTypingAmountRequestVOS = List.of(userTypingAmount);
        userTypingAmountMqVO.setUserTypingAmountRequestVOList(userTypingAmountRequestVOS);
        KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC, userTypingAmountMqVO);
    }

    public boolean receiveActiveAward(String id) {
        try {
            VIPAwardRecordPO award = this.getOne(new LambdaQueryWrapper<VIPAwardRecordPO>()
                    .eq(VIPAwardRecordPO::getId, id).gt(VIPAwardRecordPO::getExpiredTime, System.currentTimeMillis()));
            if(null == award){
               log.info("该id:{} VIP福利 为空或已过期", id);
               return false;
            }
            UserInfoVO userInfoVO = userInfoApi.getUserInfoByUserId(award.getUserId());
            CoinRecordResultVO coinRecordResultVO;
            List<SiteCurrencyInfoRespVO> currencyInfoRespVOS = siteCurrencyInfoService.getBySiteCode(
                    userInfoVO.getSiteCode());
            Map<String, BigDecimal> currencyMap = currencyInfoRespVOS.stream().collect(Collectors
                    .toMap(SiteCurrencyInfoRespVO::getCurrencyCode, SiteCurrencyInfoRespVO::getFinalRate));
            ResponseVO<List<SiteVIPRankVO>> vipRankResponse = vipRankApi.getVipRankListBySiteCode(CurrReqUtils.getSiteCode());
            if(null == vipRankResponse || !vipRankResponse.isOk()){
                log.error("该id:{} 获取VIP段位信息配置异常", id);
                return false;
            }
            List<SiteVIPRankVO> siteVIPRankVOList = vipRankResponse.getData();
            if(CommonConstant.PLAT_CURRENCY_CODE.equals(award.getCurrency())){
                UserPlatformCoinAddVO userCoinAddVO = new UserPlatformCoinAddVO();
                userCoinAddVO.setUserId(award.getUserId());
                userCoinAddVO.setCoinType(PlatformWalletEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
                userCoinAddVO.setCoinValue(award.getAwardAmount());
                userCoinAddVO.setOrderNo(award.getOrderId());
                userCoinAddVO.setRemark(VIPAwardEnum.nameOfCode(award.getAwardType()).getName());
                userCoinAddVO.setBusinessCoinType(PlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
                userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
                coinRecordResultVO = walletUserCommonPlatformCoinService.userCommonPlatformCoin(userCoinAddVO);
            }
            else {
                UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
                userCoinAddVO.setUserId(award.getUserId());
                userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
                userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
                userCoinAddVO.setCoinValue(award.getAwardAmount());
                userCoinAddVO.setOrderNo(award.getOrderId());
                userCoinAddVO.setRemark(VIPAwardEnum.nameOfCode(award.getAwardType()).getName());
                userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
                userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
                coinRecordResultVO = walletUserCommonCoinService.userCommonCoinAdd(userCoinAddVO);
            }
            if (null != coinRecordResultVO && coinRecordResultVO.getResult()) {
                Long receiveTime = System.currentTimeMillis();
                // 增加打码量
                addTypeAmount(userInfoVO, award, siteVIPRankVOList, currencyMap);
                LambdaUpdateWrapper<VIPAwardRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(VIPAwardRecordPO::getId, id);
                updateWrapper.eq(VIPAwardRecordPO::getReceiveStatus, ActivityReceiveStatusEnum.UN_RECEIVE.getCode());
                updateWrapper.set(VIPAwardRecordPO::getReceiveStatus, ActivityReceiveStatusEnum.RECEIVE.getCode());
                updateWrapper.set(VIPAwardRecordPO::getReceiveTime, receiveTime);
                // 发送会员盈亏消息(currency 是WTC走平台币，其他走主货币)
                handleSendWinLossMessage(award, userInfoVO, receiveTime);
                return this.update(null, updateWrapper);
            }
        } catch (Exception e) {
            log.error("领取某个活动id:{} 发生异常", id, e);
            return false;
        }
        return true;
    }


    private void addCoin(List<OrderRebateRequestVO> rebateRequestVOList) {
        List<SiteCurrencyInfoRespVO> currencyInfoRespVOS = siteCurrencyInfoService.getListBySiteCodes(
                rebateRequestVOList.stream().map(OrderRebateRequestVO::getSiteCode).toList());
        Map<String, List<SiteCurrencyInfoRespVO>> siteMap = currencyInfoRespVOS.stream()
                .collect(Collectors.groupingBy(SiteCurrencyInfoRespVO::getSiteCode));
        Map<String, Map<String, BigDecimal>> currencyMap = Maps.newHashMap();
        for (Map.Entry<String, List<SiteCurrencyInfoRespVO>> map : siteMap.entrySet()) {
            currencyMap.put(map.getKey(), map.getValue().stream().collect(Collectors
                    .toMap(SiteCurrencyInfoRespVO::getCurrencyCode, SiteCurrencyInfoRespVO::getFinalRate)));
        }
        rebateRequestVOList.forEach(obj -> {
            UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
            userCoinAddVO.setUserId(obj.getUserInfoVO().getUserId());
            userCoinAddVO.setOrderNo(obj.getOrderId());
            userCoinAddVO.setCoinValue(obj.getRebateAmount());
            userCoinAddVO.setCustomerCoinType(obj.getCustomerCoinType());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
            userCoinAddVO.setRemark(VIPAwardEnum.nameOfCode(String.valueOf(obj.getFlag())).getName());
            userCoinAddVO.setUserInfoVO(obj.getUserInfoVO());
            log.info("站点:{}, 会员:{}红包触发账变金额:{}", obj.getSiteCode(), obj.getUserAccount(), obj.getRebateAmount());
            if (obj.getRebateAmount().compareTo(BigDecimal.ZERO) > 0) {
                // 有返水金额才会触发账变

                if (walletUserCommonCoinService.userCommonCoinAdd(userCoinAddVO).getResult()) {
                    // 添加打码量
                    UserTypingAmountMqVO userTypingAmountMqVO = new UserTypingAmountMqVO();
                    UserTypingAmountRequestVO userTypingAmount = new UserTypingAmountRequestVO();
                    // 注单号
                    String rebateOrderNo = "GG" + SnowFlakeUtils.getSnowId();
                    userTypingAmount.setUserAccount(obj.getUserAccount());
                    userTypingAmount.setOrderNo(rebateOrderNo);
                    userTypingAmount.setAdjustType(TypingAmountAdjustTypeEnum.VIP_BENEFITS.getCode());
                    userTypingAmount.setRemark(TypingAmountAdjustTypeEnum.VIP_BENEFITS.getName());
                    // 打码量 返水金额乘以打码倍数
                    userTypingAmount.setTypingAmount(AmountUtils.multiply(obj.getRebateAmount(),
                            currencyMap.get(obj.getSiteCode()).get(obj.getMainCurrency())));
                    userTypingAmount.setType(TypingAmountEnum.ADD.getCode());
                    // 发送打码量mq
                    List<UserTypingAmountRequestVO> userTypingAmountRequestVOS = List.of(userTypingAmount);
                    userTypingAmountMqVO.setUserTypingAmountRequestVOList(userTypingAmountRequestVOS);
                    KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC, userTypingAmountMqVO);
                    // 有返水金额的才可以发送
//                    UserWinLoseMqVO mqVO = new UserWinLoseMqVO();
//                    mqVO.setUserAccount(obj.getUserAccount());
//                    mqVO.setRebateOrderNo(rebateOrderNo);
//                    mqVO.setDay(DateUtil.beginOfDay(DateUtil.yesterday()).getTime());
//                    mqVO.setAgentAccount(obj.getAgentAccount());
//                    mqVO.setBizCode(CommonConstant.business_six);
//                    mqVO.setRebateAmounts(obj.getRebateAmount());
//                    rabbitTemplate.convertAndSend(.USER_WIN_LOSE_CHANNEL, JSON.toJSONString(mqVO));
                    // 记录通知消息成功返水
//                    UserSysNoticeConfigAddVO userSysNoticeConfigAddVO = new UserSysNoticeConfigAddVO();
//                    userSysNoticeConfigAddVO.setUserAccount(obj.getUserAccount());
//                    userSysNoticeConfigAddVO.setNoticeType(BigDecimalConstants.FOUR.intValue());
//                    userSysNoticeConfigAddVO.setNoticeTitle("会员返水发放通知");
//                    userSysNoticeConfigAddVO.setBusinessLine(UserSysMessageEnum.VIP_BENEFIT.getCode());
//                    userSysNoticeConfigAddVO.setMessageType(UserSysMessageEnum.VIP_BENEFIT
//                            .getMessageTypes()[0].getSubCode());
//                    userSysNoticeConfigAddVO.setOperator("Player");
//                    String content = "尊敬的" + obj.getUserAccount() + ": " +
//                            "您的投注返水" + obj.getRebateAmount() + "元, 已发放到您的账户, " +
//                            "请查看到账金额。"  +
//                            "如有任何疑问, 可联系客服。";
//                    userSysNoticeConfigAddVO.setMessageContent(content);
//                    userFeignResource.addUserSysNoticeConfig(userSysNoticeConfigAddVO);
                    // 发送会员盈亏返水MQ
//                    List<UserRebateResultVO> userRebateResultVOList = userRebateRepository
//                            .selectRebateResult(beginDate.getTime(), endDate.getTime(), obj.getUserAccount());
                }
            }
        });
    }

    public boolean recordVIPAward(final List<UserAwardRecordVO> userAwardRecordVOList) {
        try {
            if (null != userAwardRecordVOList) {
                long currentTime = System.currentTimeMillis();
                List<VIPAwardRecordPO> vipAwardRecordPOList = Lists.newArrayList();
                for (UserAwardRecordVO vo : userAwardRecordVOList) {
                    VIPAwardRecordPO po = new VIPAwardRecordPO();
                    if (BigDecimal.ZERO.compareTo(vo.getAwardAmount()) >= 0) {
                        // VIP等级为0或者奖励为0都不发
                        continue;
                    }
                    BeanUtils.copyProperties(vo, po);
                    po.setOrderId("J" + SnowFlakeUtils.getSnowId());
                    po.setReceiveStatus(ActivityReceiveStatusEnum.UN_RECEIVE.getCode());
                    po.setAccountType(String.valueOf(vo.getAccountType()));
                    po.setExpiredTime(currentTime + vo.getExpiredTime());
                    po.setCreatedTime(currentTime);
                    po.setUpdatedTime(currentTime);
                    vipAwardRecordPOList.add(po);
                }
                this.saveBatch(vipAwardRecordPOList);
                // 批量
//                handleSendWinLossMessage(vipAwardRecordPOList);
            }
        } catch (Exception e) {
            log.error("record vip award record have error, userAwardList:{}", userAwardRecordVOList, e);
            return false;
        }
        return true;
    }

    /**
     * vip升级处理并发送会员每日盈亏消息到 Kafka 队列。
     *
     * @param vipAwardRecordPOList 包含订单记录信息的对象
     */
    private void handleSendWinLossMessage(List<VIPAwardRecordPO> vipAwardRecordPOList) {
        for (VIPAwardRecordPO vipAwardRecordPO : vipAwardRecordPOList) {

            UserWinLoseMqVO userWinLoseMqVO = new UserWinLoseMqVO();
            userWinLoseMqVO.setOrderId(vipAwardRecordPO.getOrderId());
            userWinLoseMqVO.setUserId(vipAwardRecordPO.getUserId());
            //userWinLoseMqVO.setUserAccount(siteActivityOrderRecordPO.getUserAccount());
            userWinLoseMqVO.setAgentId(vipAwardRecordPO.getAgentId());
            //userWinLoseMqVO.setAgentAccount(userInfoVO.getSuperAgentAccount());
            userWinLoseMqVO.setDayHourMillis(vipAwardRecordPO.getReceiveTime());
            // 任务发放的是平台币
            userWinLoseMqVO.setCurrency(vipAwardRecordPO.getCurrency());
            userWinLoseMqVO.setPlatformFlag(true);
            userWinLoseMqVO.setVipBenefitAmount(vipAwardRecordPO.getAwardAmount());
            userWinLoseMqVO.setBizCode(CommonConstant.business_seven);
            userWinLoseMqVO.setSiteCode(vipAwardRecordPO.getSiteCode());
            log.info("vip等级发放会员盈亏消息{}", JSONObject.toJSONString(userWinLoseMqVO));
            KafkaUtil.send(TopicsConstants.USER_WIN_LOSE_CHANNEL, userWinLoseMqVO);
        }
    }

    public void vipExpired() {
        Long starttime=System.currentTimeMillis()-2592000000L;
        List<VIPAwardRecordPO> awardRecordPOS = this.lambdaQuery()
                .eq(VIPAwardRecordPO::getReceiveStatus, ReceiveStatusEnum.NOT_RECEIVED.getCode())
                .ge(VIPAwardRecordPO::getExpiredTime,starttime)
                .le(VIPAwardRecordPO::getExpiredTime, System.currentTimeMillis()).list();
        log.info("vip统一过期活动待处理的订单号:{}", awardRecordPOS.stream().map(VIPAwardRecordPO::getOrderId).toList());
        awardRecordPOS.forEach(obj->{
            LambdaUpdateWrapper<VIPAwardRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(VIPAwardRecordPO::getId, obj.getId());
            updateWrapper.set(VIPAwardRecordPO::getReceiveStatus, ReceiveStatusEnum.EXPIRED.getCode());
            updateWrapper.set(VIPAwardRecordPO::getUpdatedTime, System.currentTimeMillis());
            this.update(null, updateWrapper);
        });
        log.info("vip统一过期活动update状态完成");

        List<SiteRebateRewardRecordPO> siteRebateRewardRecordPOs =siteRebateRewardRecordService.list(Wrappers.<SiteRebateRewardRecordPO>lambdaQuery().eq(SiteRebateRewardRecordPO::getOpenStatus, ReceiveStatusEnum.NOT_RECEIVED.getCode())
                .le(SiteRebateRewardRecordPO::getInvalidTime, System.currentTimeMillis()));
        log.info("返水统一过期活动待处理的订单号:{}", siteRebateRewardRecordPOs.stream().map(SiteRebateRewardRecordPO::getOrderNo).toList());
        siteRebateRewardRecordPOs.forEach(obj->{
            obj.setOpenStatus( ReceiveStatusEnum.EXPIRED.getCode());
            obj.setUpdatedTime(System.currentTimeMillis());
        });
        siteRebateRewardRecordService.saveOrUpdateBatch(siteRebateRewardRecordPOs);
        log.info("返水统一过期update状态完成");

        log.info("活动相关的处理时效的任务开始");
        activityFinanceApi.bachInvalidData();
        log.info("活动相关的处理时效的任务结束修改完成");
    }
}
