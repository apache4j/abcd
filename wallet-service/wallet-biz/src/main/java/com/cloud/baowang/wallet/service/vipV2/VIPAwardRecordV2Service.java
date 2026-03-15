package com.cloud.baowang.wallet.service.vipV2;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.api.v2.ActivityFinanceV2Api;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountMqVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.common.kafka.vo.UserWinLoseMqVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.enums.ActivityReceiveStatusEnum;
import com.cloud.baowang.user.api.enums.ReceiveStatusEnum;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPBenefitVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.cloud.baowang.user.api.vo.vip.VIPChangeRequestVO;
import com.cloud.baowang.wallet.api.enums.ReceiveTypeEnum;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.wallet.*;
import com.cloud.baowang.wallet.api.vo.activityV2.UserAwardRecordV2ReqVO;
import com.cloud.baowang.wallet.api.vo.rebate.OrderRebateRequestVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinAddVO;
import com.cloud.baowang.wallet.api.vo.vipV2.UserAwardRecordV2VO;
import com.cloud.baowang.wallet.po.vipV2.VIPAwardRecordV2PO;
import com.cloud.baowang.wallet.repositories.vipV2.VIPAwardRecordV2Repository;
import com.cloud.baowang.wallet.service.SiteCurrencyInfoService;
import com.cloud.baowang.wallet.service.SiteRebateRewardRecordService;
import com.cloud.baowang.wallet.service.UserCoinService;
import com.cloud.baowang.wallet.service.WalletUserCommonCoinService;
import com.cloud.baowang.wallet.service.WalletUserCommonPlatformCoinService;
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
public class VIPAwardRecordV2Service extends ServiceImpl<VIPAwardRecordV2Repository,
        VIPAwardRecordV2PO> {
    private final UserCoinService userCoinService;

    private final VIPAwardRecordV2Repository vipAwardRecordV2Repository;

    private final UserInfoApi userInfoApi;


    private final VipRankApi vipRankApi;
    private final SiteRebateRewardRecordService siteRebateRewardRecordService;

    private final SiteCurrencyInfoService siteCurrencyInfoService;
    private final ActivityFinanceV2Api activityFinanceApi;

    private final WalletUserCommonCoinService walletUserCommonCoinService;

    private final WalletUserCommonPlatformCoinService walletUserCommonPlatformCoinService;

    public void recordUserRebate(List<OrderRebateRequestVO> rebateRequestVOList) {
        try {
            // 周,月奖励记录表
            long currentTime = System.currentTimeMillis();
            List<VIPAwardRecordV2PO> list = Lists.newArrayList();
            rebateRequestVOList.forEach(obj -> {
                VIPAwardRecordV2PO po = VIPAwardRecordV2PO.builder().agentAccount(obj.getAgentAccount())
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
        List<String> list = vipAwardRecordV2Repository.selectUpgradeAward(vo,
                thisDayStart.getTime(), thisDayEnd.getTime());
        // 查询该等级下是否已经领取过VIP升级礼金
        List<String> receivedList = vipAwardRecordV2Repository.selectCanReceiveAccounts();
        // 剔除已经领取过的人
        list = removeReceived(list, receivedList);
        List<UserInfoVO> userInfoVOS = userInfoApi.getUserInfoListByAccounts(list);
        // 特殊代理将不发放该奖励
//        List<String> agentDiscounts = systemBusinessFeignResource
//                .getAllDiscountManager(VIPAwardV2Enum.UPGRADE_BONUS.getCode());
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
            sendAwardCoin(userInfoVOS, map, VIPAwardV2Enum.UPGRADE_BONUS.getCode(), "升级礼金", null, 2);
        }
    }

    private void sendAwardCoin(List<UserInfoVO> userInfoVOS, Map<Integer, BigDecimal> map, String awardType,
                               String remark, Long expiredTime, Integer vipIndex) {
        if (null != userInfoVOS) {
            List<VIPAwardRecordV2PO> vipAwardRecordPOList = Lists.newArrayList();
            for (UserInfoVO vo : userInfoVOS) {
                VIPAwardRecordV2PO po = new VIPAwardRecordV2PO();
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
            List<VIPAwardRecordV2PO> awardRecordPOList = this.lambdaQuery().eq(VIPAwardRecordV2PO::getUserId, userId)
                    .eq(VIPAwardRecordV2PO::getReceiveStatus, ActivityReceiveStatusEnum.UN_RECEIVE.getCode())
                    .gt(VIPAwardRecordV2PO::getExpiredTime, System.currentTimeMillis())
                    .list();

            if (ObjectUtil.isEmpty(awardRecordPOList)) {
                log.info("该会员id:{} 没有需要一键领取的VIP福利", userId);
                return false;
            }

            List<SiteCurrencyInfoRespVO> currencyInfoRespVOS = siteCurrencyInfoService.getBySiteCode(
                    userInfoVO.getSiteCode());
            Map<String, BigDecimal> currencyMap = currencyInfoRespVOS.stream().collect(Collectors
                    .toMap(SiteCurrencyInfoRespVO::getCurrencyCode, SiteCurrencyInfoRespVO::getFinalRate));
            ResponseVO<List<SiteVIPRankVO>> vipRankResponse = vipRankApi.getVipRankListBySiteCode(CurrReqUtils.getSiteCode());
            if (null == vipRankResponse || !vipRankResponse.isOk()) {
                log.error("该会员id:{} 获取VIP段位信息配置异常", userId);
                return false;
            }
            for (VIPAwardRecordV2PO award : awardRecordPOList) {
                long receiveTime = System.currentTimeMillis();
                VIPAwardV2Enum vipAwardEnum = VIPAwardV2Enum.nameOfCode(award.getAwardType());

                if (CommonConstant.PLAT_CURRENCY_CODE.equals(award.getCurrency())) {
                    // 平台币账变 打码量
                    UserPlatformCoinAddVO userCoinAddVO = new UserPlatformCoinAddVO();
                    userCoinAddVO.setUserId(userId);
                    userCoinAddVO.setCoinType(PlatformWalletEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
                    userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
                    userCoinAddVO.setOrderNo(award.getOrderId());
                    userCoinAddVO.setRemark(VIPAwardV2Enum.nameOfCode(award.getAwardType()).getName());
                    userCoinAddVO.setBusinessCoinType(PlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
                    userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
                    userCoinAddVO.setCoinValue(award.getAwardAmount());
                    userCoinAddVO.setActivityFlag(vipAwardEnum.getAccountCoinType());
                    CoinRecordResultVO coinRecordResultVO = walletUserCommonPlatformCoinService.userCommonPlatformCoin(userCoinAddVO);

                    if (coinRecordResultVO.getResult()) {
                        // 增加打码量
                        addTypeAmount(userInfoVO, award);
                        LambdaUpdateWrapper<VIPAwardRecordV2PO> updateWrapper = new LambdaUpdateWrapper<>();
                        updateWrapper.eq(VIPAwardRecordV2PO::getOrderId, award.getOrderId());
                        updateWrapper.eq(VIPAwardRecordV2PO::getReceiveStatus, ActivityReceiveStatusEnum.UN_RECEIVE.getCode());
                        updateWrapper.set(VIPAwardRecordV2PO::getReceiveStatus, ActivityReceiveStatusEnum.RECEIVE.getCode());
                        updateWrapper.set(VIPAwardRecordV2PO::getReceiveTime, System.currentTimeMillis());
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
                    userCoinAddVO.setRemark(VIPAwardV2Enum.nameOfCode(award.getAwardType()).getName());
                    userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
                    userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
                    userCoinAddVO.setCoinValue(award.getAwardAmount());
                    userCoinAddVO.setActivityFlag(vipAwardEnum.getAccountCoinType());
                    CoinRecordResultVO recordResultVO = walletUserCommonCoinService.userCommonCoinAdd(userCoinAddVO);
//                    CoinRecordResultVO recordResultVO = userCoinService.addCoin(userCoinAddVO);
                    if (recordResultVO.getResult()) {
                        // 增加打码量
                        addTypeAmount(userInfoVO, award);
                        LambdaUpdateWrapper<VIPAwardRecordV2PO> updateWrapper = new LambdaUpdateWrapper<>();
                        updateWrapper.eq(VIPAwardRecordV2PO::getOrderId, award.getOrderId());
                        updateWrapper.eq(VIPAwardRecordV2PO::getReceiveStatus, ActivityReceiveStatusEnum.UN_RECEIVE.getCode());
                        updateWrapper.set(VIPAwardRecordV2PO::getReceiveStatus, ActivityReceiveStatusEnum.RECEIVE.getCode());
                        updateWrapper.set(VIPAwardRecordV2PO::getReceiveTime, receiveTime);
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
     * @param viPAwardRecordPO 包含订单记录信息的对象
     * @param userInfoVO       包含用户信息的对象
     */
    private void handleSendWinLossMessage(VIPAwardRecordV2PO viPAwardRecordPO, UserInfoVO userInfoVO, long receiveTime) {
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
        if (CommonConstant.PLAT_CURRENCY_CODE.equals(viPAwardRecordPO.getCurrency())) {
            userWinLoseMqVO.setPlatformFlag(true);
            userWinLoseMqVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        } else {
            userWinLoseMqVO.setPlatformFlag(false);
            userWinLoseMqVO.setCurrency(viPAwardRecordPO.getCurrency());
        }
        KafkaUtil.send(TopicsConstants.USER_WIN_LOSE_CHANNEL, userWinLoseMqVO);
    }


    private void addTypeAmount(UserInfoVO userInfoVO, VIPAwardRecordV2PO award) {
        // 添加打码量
        UserTypingAmountMqVO userTypingAmountMqVO = new UserTypingAmountMqVO();
        UserTypingAmountRequestVO userTypingAmount = new UserTypingAmountRequestVO();
        // 注单号
        userTypingAmount.setUserAccount(userInfoVO.getUserAccount());
        userTypingAmount.setUserId(userInfoVO.getUserId());
        userTypingAmount.setOrderNo(award.getOrderId());
        userTypingAmount.setAdjustType(TypingAmountAdjustTypeEnum.VIP_BENEFITS.getCode());
        userTypingAmount.setRemark(TypingAmountAdjustTypeEnum.VIP_BENEFITS.getName());
        userTypingAmount.setTypingAmount(award.getRequireTypingAmount());
        userTypingAmount.setType(TypingAmountEnum.ADD.getCode());
        // 发送打码量mq
        List<UserTypingAmountRequestVO> userTypingAmountRequestVOS = List.of(userTypingAmount);
        userTypingAmountMqVO.setUserTypingAmountRequestVOList(userTypingAmountRequestVOS);
        KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC, userTypingAmountMqVO);
    }

    public boolean receiveActiveAward(String id) {
        try {
            VIPAwardRecordV2PO award = this.getOne(new LambdaQueryWrapper<VIPAwardRecordV2PO>()
                    .eq(VIPAwardRecordV2PO::getId, id)
                    .eq(VIPAwardRecordV2PO::getReceiveStatus, CommonConstant.business_zero)
                    .gt(VIPAwardRecordV2PO::getExpiredTime, System.currentTimeMillis()));
            if (null == award) {
                log.info("该id:{} VIP福利 为空或已过期", id);
                return false;
            }
            UserInfoVO userInfoVO = userInfoApi.getUserInfoByUserId(award.getUserId());
            UserCoinAddVO userCoinAddVO = new UserCoinAddVO();
            userCoinAddVO.setUserId(award.getUserId());
            userCoinAddVO.setCoinType(WalletEnum.CoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
            userCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
            userCoinAddVO.setCoinValue(award.getAwardAmount());
            userCoinAddVO.setOrderNo(award.getOrderId());
            userCoinAddVO.setRemark(VIPAwardV2Enum.nameOfCode(award.getAwardType()).getName());
            userCoinAddVO.setBusinessCoinType(WalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode());
            userCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
            CoinRecordResultVO coinRecordResultVO = walletUserCommonCoinService.userCommonCoinAdd(userCoinAddVO);
            if (coinRecordResultVO.getResultStatus()!=null) {
                long receiveTime = System.currentTimeMillis();
                LambdaUpdateWrapper<VIPAwardRecordV2PO> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(VIPAwardRecordV2PO::getId, id);
                updateWrapper.eq(VIPAwardRecordV2PO::getReceiveStatus, ActivityReceiveStatusEnum.UN_RECEIVE.getCode());
                updateWrapper.set(VIPAwardRecordV2PO::getReceiveStatus, ActivityReceiveStatusEnum.RECEIVE.getCode());
                updateWrapper.set(VIPAwardRecordV2PO::getReceiveTime, receiveTime);
                //NOTE 如果重复提交了，直接改状态
                if (UpdateBalanceStatusEnums.SUCCESS==coinRecordResultVO.getResultStatus()){
                    // 增加打码量
                    addTypeAmount(userInfoVO, award);
                    // 发送会员盈亏消息(currency 是WTC走平台币，其他走主货币)
                    handleSendWinLossMessage(award, userInfoVO, receiveTime);
                    return this.update(null, updateWrapper);
                }else if (UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS==coinRecordResultVO.getResultStatus()){
                    return this.update(null, updateWrapper);
                }
            }
        } catch (Exception e) {
            log.error("领取某个活动id:{} 发生异常", id, e);
        }
        return false;
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
            userCoinAddVO.setRemark(VIPAwardV2Enum.nameOfCode(String.valueOf(obj.getFlag())).getName());
            userCoinAddVO.setUserInfoVO(obj.getUserInfoVO());
            log.info("站点:{}, 会员:{}红包触发账变金额:{}", obj.getSiteCode(), obj.getUserAccount(), obj.getRebateAmount());
            if (obj.getRebateAmount().compareTo(BigDecimal.ZERO) > 0) {
                CoinRecordResultVO coinRecordResultVO = walletUserCommonCoinService.userCommonCoinAdd(userCoinAddVO);
                // 有返水金额才会触发账变
                if (coinRecordResultVO.getResult()) {
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

    public boolean recordVIPAward(final List<UserAwardRecordV2VO> userAwardRecordVOList) {
        try {
            if (null != userAwardRecordVOList) {
                long currentTime = System.currentTimeMillis();
                List<VIPAwardRecordV2PO> vipAwardRecordPOList = Lists.newArrayList();
                for (UserAwardRecordV2VO vo : userAwardRecordVOList) {
                    VIPAwardRecordV2PO po = new VIPAwardRecordV2PO();
                    if (BigDecimal.ZERO.compareTo(vo.getAwardAmount()) >= 0) {
                        // VIP等级为0或者奖励为0都不发
                        continue;
                    }
                    BeanUtils.copyProperties(vo, po);
                    //NOTE 订单号已经特殊处理生成， 奖励类型 + userID + 年月日（20250101， 如果是周奖励， 就是奖励的周一）
                    po.setOrderId(vo.getOrderId());
                    po.setReceiveStatus(ActivityReceiveStatusEnum.UN_RECEIVE.getCode());
                    po.setAccountType(String.valueOf(vo.getAccountType()));
                    po.setExpiredTime(currentTime + vo.getExpiredTime());
                    po.setCreatedTime(currentTime);
                    po.setUpdatedTime(currentTime);
                    po.setRequireTypingAmount(vo.getRequireTypingAmount());
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
    private void handleSendWinLossMessage(List<VIPAwardRecordV2PO> vipAwardRecordPOList) {
        for (VIPAwardRecordV2PO vipAwardRecordPO : vipAwardRecordPOList) {

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
        List<VIPAwardRecordV2PO> awardRecordPOS = this.lambdaQuery()
                .eq(VIPAwardRecordV2PO::getReceiveStatus, ReceiveStatusEnum.NOT_RECEIVED.getCode())
                .le(VIPAwardRecordV2PO::getExpiredTime, System.currentTimeMillis()).list();
        log.info("vip统一过期活动待处理的订单号:{}", awardRecordPOS.stream().map(VIPAwardRecordV2PO::getOrderId).toList());
        awardRecordPOS.forEach(obj -> {
            LambdaUpdateWrapper<VIPAwardRecordV2PO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(VIPAwardRecordV2PO::getId, obj.getId());
            updateWrapper.set(VIPAwardRecordV2PO::getReceiveStatus, ReceiveStatusEnum.EXPIRED.getCode());
            updateWrapper.set(VIPAwardRecordV2PO::getUpdatedTime, System.currentTimeMillis());
            vipAwardRecordV2Repository.update(null, updateWrapper);
        });
        log.info("vip统一过期活动update状态完成");

        /* //NOTE 先不处理

        List<SiteRebateRewardRecordPO> siteRebateRewardRecordPOs =siteRebateRewardRecordService.list(Wrappers.<SiteRebateRewardRecordPO>lambdaQuery().eq(SiteRebateRewardRecordPO::getOpenStatus, ReceiveStatusEnum.NOT_RECEIVED.getCode())
                .le(SiteRebateRewardRecordPO::getInvalidTime, System.currentTimeMillis()));
        log.info("返水统一过期活动待处理的订单号:{}", siteRebateRewardRecordPOs.stream().map(SiteRebateRewardRecordPO::getOrderNo).toList());
        siteRebateRewardRecordPOs.forEach(obj->{
            obj.setOpenStatus( ReceiveStatusEnum.EXPIRED.getCode());
            obj.setUpdatedTime(System.currentTimeMillis());
        });
        siteRebateRewardRecordService.saveOrUpdateBatch(siteRebateRewardRecordPOs);*/
        log.info("返水统一过期update状态完成");

        log.info("活动相关的处理时效的任务开始");
        activityFinanceApi.bachInvalidData();
        log.info("活动相关的处理时效的任务结束修改完成");
    }

    public ResponseVO<Boolean> awardHasReceive(UserAwardRecordV2ReqVO vo) {

        LambdaQueryWrapper<VIPAwardRecordV2PO> wrapper = new LambdaQueryWrapper<VIPAwardRecordV2PO>()
                .eq(VIPAwardRecordV2PO::getSiteCode, vo.getSiteCode())
                .eq(VIPAwardRecordV2PO::getUserId, vo.getUserId())
                .eq(VIPAwardRecordV2PO::getOrderId, vo.getOrderId());
        return ResponseVO.success(this.baseMapper.selectCount(wrapper) > 0);
    }

    public ResponseVO<UserAwardRecordV2VO> awardRecordByUserId(UserAwardRecordV2ReqVO vo) {

        LambdaQueryWrapper<VIPAwardRecordV2PO> wrapper = new LambdaQueryWrapper<VIPAwardRecordV2PO>()
                .eq(StrUtil.isNotEmpty(vo.getSiteCode()), VIPAwardRecordV2PO::getSiteCode, vo.getSiteCode())
                .eq(StrUtil.isNotEmpty(vo.getUserId()), VIPAwardRecordV2PO::getUserId, vo.getUserId())
                .eq(StrUtil.isNotEmpty(vo.getVipGradeCode()), VIPAwardRecordV2PO::getVipGradeCode, vo.getVipGradeCode())
                .eq(vo.getReceiveStatus() != null, VIPAwardRecordV2PO::getReceiveStatus, vo.getReceiveStatus())
                .eq(vo.getAwardType() != null, VIPAwardRecordV2PO::getAwardType, vo.getAwardType())
                .orderByDesc(VIPAwardRecordV2PO::getRecordStartTime);

        VIPAwardRecordV2PO vipAwardRecordV2PO = this.baseMapper.selectOne(wrapper);
        return ResponseVO.success(BeanUtil.copyProperties(vipAwardRecordV2PO, UserAwardRecordV2VO.class));
    }

    /**
     * 查询vip等级奖励记录
     */
    // 查询vip等级奖励记录
    public List<UserAwardRecordV2VO> awardRecordByVipGrade(UserAwardRecordV2ReqVO vo) {

        LambdaQueryWrapper<VIPAwardRecordV2PO> wrapper = new LambdaQueryWrapper<VIPAwardRecordV2PO>()
                .eq(StrUtil.isNotEmpty(vo.getSiteCode()), VIPAwardRecordV2PO::getSiteCode, vo.getSiteCode())
                .eq(StrUtil.isNotEmpty(vo.getUserId()), VIPAwardRecordV2PO::getUserId, vo.getUserId());
        List<VIPAwardRecordV2PO> vipAwardRecordV2POS = this.baseMapper.selectList(wrapper);
        return vipAwardRecordV2POS.stream().map(item -> BeanUtil.copyProperties(item, UserAwardRecordV2VO.class)).toList();

    }


   /* @DistributedLock(name = RedisUserConstants.REWARD_VIP_REWARD_LOCK, unique = "#userId", waitTime = 60, leaseTime = 180)
    public ResponseVO<?> getUserVipRewards(UserVipRewardReqVO requestVO) {
        TaskReceiveAppResVO receiveAppResVO = new TaskReceiveAppResVO();
        // 1. 判断奖励是否存在 site_vip_award_record_v2
        LambdaQueryWrapper<VIPAwardRecordV2PO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(VIPAwardRecordV2PO::getSiteCode, requestVO.getSiteCode())
                .eq(VIPAwardRecordV2PO::getUserId, requestVO.getUserId())
                .eq(VIPAwardRecordV2PO::getOrderId, requestVO.getOrderId());
        VIPAwardRecordV2PO recordPO = this.baseMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNull(recordPO)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        // 3. 判断记录是否过期
        if (recordPO.getRecordEndTime() < System.currentTimeMillis()) {
            updateTaskOrderRecordExpired(recordPO);
            // ACTIVITY_NOT_YET_CLAIM_EXPIRED
            throw new BaowangDefaultException(ResultCode.ACTIVITY_NOT_YET_CLAIM_EXPIRED);
        }
        // 2. 获取用户最新登录信息 (IP 和设备号)
        UserInfoVO userInfoVO = userInfoApi.getByUserId(requestVO.getUserId());
        List<UserLoginInfoVO>  latestLoginInfoByUsers = userInfoApi.getLatestLoginInfoByUserIds(Collections.singletonList(requestVO.getUserId()));
        UserLoginInfoVO latestLoginInfoByUser = new UserLoginInfoVO();
        if (CollectionUtil.isEmpty(latestLoginInfoByUsers)) {
            latestLoginInfoByUser = latestLoginInfoByUsers.get(0);
        }


        for (SiteTaskOrderRecordPO recordPO : recordPOs) {
            // 如果已经过期了，则需要更改状态
            if (recordPO.getReceiveEndTime() < System.currentTimeMillis()) {
                updateTaskOrderRecordExpired(recordPO);
                // ACTIVITY_NOT_YET_CLAIM_EXPIRED
                throw new BaowangDefaultException(ResultCode.ACTIVITY_NOT_YET_CLAIM_EXPIRED);
            }
            // 4. 更新领取状态和时间
            updateTaskOrderRecord(recordPO, userInfoVO, latestLoginInfoByUser);
            // 5. 获取用户信息，并更新用户钱包
            boolean flag = updateWallet(recordPO, userInfoVO);
            if (flag) {
                // 6. 返回奖励金额
                //receiveAppResVO.setRewardAmount(recordPO.getTaskAmount());
                // 7. 发送mq消息
                handleSendWinLossMessage(recordPO, userInfoVO);
                sendTypingAmount(recordPO, userInfoVO, finalRate);

            } else {
                throw new BaowangDefaultException(ResultCode.RECEIVE_FAIL_DESCRIPTION);
            }
        }
        BigDecimal totalAmount = recordPOs.stream().map(
                        e -> e.getTaskAmount() == null ? BigDecimal.ZERO : e.getTaskAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        receiveAppResVO.setRewardAmount(totalAmount);
        return ResponseVO.success(ResultCode.RECEIVE_SUCCESS, receiveAppResVO);
    }*/

}
