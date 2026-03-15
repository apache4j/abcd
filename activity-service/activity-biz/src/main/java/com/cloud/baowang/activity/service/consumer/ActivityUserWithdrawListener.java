package com.cloud.baowang.activity.service.consumer;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.vo.UserBaseReqVO;
import com.cloud.baowang.activity.api.vo.v2.newHand.ConditionFirstWithdrawalVO;
import com.cloud.baowang.activity.param.CalculateParamV2;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.po.v2.SiteActivityNewHandPO;
import com.cloud.baowang.activity.service.base.activityV2.SiteActivityBaseV2Service;
import com.cloud.baowang.activity.service.v2.SiteActivityNewHandService;
import com.cloud.baowang.activity.utils.OrderNoUtils;
import com.cloud.baowang.activity.vo.mq.ActivitySendListMqVO;
import com.cloud.baowang.activity.vo.mq.ActivitySendMqVO;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserWithdrawRecordApi;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawalRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawTriggerVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 会员提现消息消费
 * 本类中仅针对参加首次提现
 * 的会员活动进行处理
 */
@Slf4j
@Component
public class ActivityUserWithdrawListener {

    @Autowired
    private SiteApi siteApi;
    @Autowired
    private SiteActivityBaseV2Service siteActivityBaseV2Service;
    @Autowired
    private SiteActivityNewHandService siteActivityNewHandService;

    @Autowired
    private UserWithdrawRecordApi userWithdrawRecordApi;

    @Autowired
    private SiteCurrencyInfoApi siteCurrencyInfoApi;

    @Autowired
    private SystemDictConfigApi systemDictConfigApi;
    /**
     * 1 day的毫秒数
     */
    private static final long _1_DAY_TIME = 24 * 3600 * 1000;

    /**
     * 活动开启后,活动开始时间前6天和后6天注册的会员仍然可以参加活动
     */
    private int validDays;

    /**
     * 处理逻辑：
     * 1.接收会员提现消息
     * 2.活动的开始时间+-6天判断,然后与会员的注册时间进行比较；
     * 在此区间内，则可以参加活动，否则不能参加
     * 3.计算彩金
     * 4.向福利中新消息队列中发送彩金消息
     */
    @KafkaListener(topics = TopicsConstants.MEMBER_WITHDRAW, groupId = GroupConstants.MEMBER_WITHDRAW_GROUP)
    public void memberWithdrawMessage(UserWithdrawTriggerVO triggerVO, Acknowledgment ack) {

        log.info("首提，接收到会员提现消息:{}", triggerVO);

        //是否可以参加活动
        boolean isJoin = false;
        String processCurrency;
        SiteActivityBaseV2PO siteActivityBasePO = null;
        //设置为自动派发
        triggerVO.setApplyFlag(false);
        //获取站点信息
        SiteVO siteInfo = siteApi.getSiteInfo(triggerVO.getSiteCode()).getData();
        if (siteInfo == null) {
            log.info("site info without configuration,no need dispatcher reward!");
            ack.acknowledge();
            return;
        }
        //获取数据字典中配置的新手活动有效天数
        String value = this.systemDictConfigApi.getByCode(DictCodeConfigEnums.ACTIVIY_NEW_HAND_FIRST_WITHDRAW_DAYS.getCode(), siteInfo.getSiteCode()).getData().getConfigParam();


        if (StrUtil.isEmptyIfStr(value)) {
            validDays = 6;
        } else {
            validDays = Integer.parseInt(value);
        }
        //设置站点时区
        triggerVO.setTimezone(siteInfo.getTimezone());
        //本次活动仅对大陆开放，需要判断活动盘口
        if (Objects.equals(SiteHandicapModeEnum.China.getCode(), siteInfo.getHandicapMode())) {
            //获取站点活动
            siteActivityBasePO = siteActivityBaseV2Service.getSiteActivityBasePO(triggerVO.getSiteCode(), ActivityTemplateV2Enum.NEW_HAND.getType());
            //判断站点活动是否存在或生效
            if (siteActivityBasePO == null) {
                log.info("站点:{} 不存在已生效的首次提现活动v2", triggerVO.getSiteCode());
                ack.acknowledge();
                return;
            }
            isJoin = this.validWithdraw(triggerVO, siteActivityBasePO);
            //不符合参加活动的要求
            if (!isJoin) {
                log.info("Member id={} no match activity condition!", triggerVO.getUserId());
                ack.acknowledge();
                return;
            }

            Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(triggerVO.getSiteCode());
            if (ObjectUtil.isEmpty(currencyRateMap) || CollectionUtil.isEmpty(currencyRateMap)) {
                log.info("首次提现活动货币转换异常.,siteCode:{},userId:{},currencyRateMap:{}", triggerVO.getSiteCode(), triggerVO.getUserId(), currencyRateMap);
                ack.acknowledge();
                return;
            }
            //获取新手活动
            SiteActivityNewHandPO newHandPO = siteActivityNewHandService.info(siteActivityBasePO.getId());
            List<ConditionFirstWithdrawalVO> firstWithdrawalVOList = JSON.parseArray(newHandPO.getConditionFirstWithdrawal(), ConditionFirstWithdrawalVO.class);
            //判断首次提现配置条件是否存在
            if (firstWithdrawalVOList == null || firstWithdrawalVOList.isEmpty()) {
                log.info("first withdraw configuration is null.");
                ack.acknowledge();
                return;
            }
            ConditionFirstWithdrawalVO withdrawalVO = firstWithdrawalVOList.get(0);
            processCurrency = (("1".equals(withdrawalVO.getCurrencyCode()) ? withdrawalVO.getCurrencyCode() : "WTC"));
            //计算奖励
            CalculateParamV2 calculateParam = new CalculateParamV2();
            calculateParam.setConditionFirstWithdrawalVO(withdrawalVO);
            calculateParam.setRate(currencyRateMap.get(processCurrency));
            calculateParam.setSourceCurrencyCode(processCurrency);
            calculateParam.setSiteCode(siteInfo.getSiteCode());
            calculateParam.setSourceAmount(triggerVO.getWithdrawAmount());
            calculateParam.setRewardCurrencyCode(processCurrency);
            calculateParam.setNewHandType(2);

            //计算奖金
            this.siteActivityNewHandService.calculateRewardAmount(calculateParam);

            if (calculateParam.getRewardAmount().compareTo(BigDecimal.ZERO) > 0) {
                //发送奖励消息
                pushRewardMessage(triggerVO, newHandPO, calculateParam);
                ack.acknowledge();
            }
        }

    }

    /**
     * 验证会员是否可以参加首次提现活动
     *
     * @param triggerVO
     * @param siteActivityBasePO
     * @return
     */
    private boolean validWithdraw(UserWithdrawTriggerVO triggerVO, SiteActivityBaseV2PO siteActivityBasePO) {

        //验证会员是否可以参加首次提现活动
        String userId = triggerVO.getUserId();
        Long registerTime = triggerVO.getRegisterTime();
        //活动开始时间,在活动开始时间基础上-6天
        long activityStartTime = Math.subtractExact(siteActivityBasePO.getActivityStartTime(), _1_DAY_TIME * validDays);
        //可参加活动的时间上限,在活动开始的时间基础上+6天
        long activityTopTime = Math.addExact(activityStartTime, _1_DAY_TIME * validDays);
        //会员注册时间早于活动开始时间,不能参加
        if (registerTime < activityStartTime) {
            log.info("Member id={} register date early activity start date, can not join!", userId);
            return false;
        }
        //会员注册时间晚于参加活动的时间上限,不能参加
        if (registerTime > activityTopTime) {
            log.info("Member id={} register date late activity date top,can not join!", userId);
            return false;
        }
        //获取会员提现记录条数
        UserWithdrawalRecordRequestVO withdrawalRecordRequestVO = new UserWithdrawalRecordRequestVO();
        withdrawalRecordRequestVO.setCreateStartTime(activityStartTime);
        withdrawalRecordRequestVO.setCreateEndTime(activityTopTime);
        withdrawalRecordRequestVO.setUserAccount(triggerVO.getUserAccount());
        //获取会员在规定时间内的提现次数
        Long count = userWithdrawRecordApi.withdrawalRecordPageCount(withdrawalRecordRequestVO);
        //非首次提现,不能参加活动
        if (count != 1) {
            log.info("no first withdraw or not exists withdraw record, can not join!");
            return false;
        }

        return true;
    }

    /**
     * 发送奖金消息到福利中心
     *
     * @param triggerVO
     * @param newHandPO
     * @param calculateParam
     */
    private void pushRewardMessage(UserWithdrawTriggerVO triggerVO, SiteActivityNewHandPO newHandPO, CalculateParamV2 calculateParam) {

        BigDecimal rewardAmount = calculateParam.getRewardAmount();
        String currencyCode = calculateParam.getRewardCurrencyCode();
        if (rewardAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("Memeber id={} without reward amount!", triggerVO.getUserId());
            return;
        }

        String activityId = newHandPO.getActivityId();
        String siteCode = newHandPO.getSiteCode();
        String userId = triggerVO.getUserId();
        UserBaseReqVO userBaseReqVO = new UserBaseReqVO();
        userBaseReqVO.setActivityId(activityId);
        userBaseReqVO.setUserId(userId);
        userBaseReqVO.setSiteCode(triggerVO.getSiteCode());
        userBaseReqVO.setApplyFlag(triggerVO.isApplyFlag());

        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(triggerVO, userInfoVO);
        userInfoVO.setUserId(triggerVO.getUserId());
        userInfoVO.setSiteCode(triggerVO.getSiteCode());

        List<ActivitySendMqVO> sendMqVOList = Lists.newArrayList();
        ActivitySendMqVO sendMqVO = new ActivitySendMqVO();
        //生成消息订单号,跟活动ID无关， 新手活动模板下， 只能领取一次
        String orderNo = OrderNoUtils.genOrderNo(userId, ActivityTemplateV2Enum.NEW_HAND.getSerialNo(), calculateParam.getNewHandType() + "");

        sendMqVO.setOrderNo(orderNo);

        sendMqVO.setActivityId(activityId);
        sendMqVO.setUserId(userId);
        sendMqVO.setSiteCode(siteCode);
        sendMqVO.setDistributionType(newHandPO.getDistributionType());
        sendMqVO.setActivityTemplate(ActivityTemplateV2Enum.NEW_HAND.getType());
        sendMqVO.setReceiveStartTime(System.currentTimeMillis());
        //72小时失效
        sendMqVO.setReceiveEndTime(System.currentTimeMillis() + _1_DAY_TIME * 3);
        sendMqVO.setActivityAmount(rewardAmount);
        sendMqVO.setCurrencyCode(currencyCode);
        sendMqVO.setRunningWater(calculateParam.getRequiredTurnover());
        sendMqVO.setRunningWaterMultiple(calculateParam.getWashRatio());
        sendMqVO.setParticipationMode(newHandPO.getParticipationMode());
        sendMqVO.setHandicapMode(calculateParam.getNewHandType());
        log.info("starting to send member first withdraw message:{}", sendMqVO);
        sendMqVOList.add(sendMqVO);
        ActivitySendListMqVO sendListMqVO = new ActivitySendListMqVO();
        sendListMqVO.setList(sendMqVOList);
        //发送奖金消息
        KafkaUtil.send(TopicsConstants.SEND_USER_ACTIVITY_ORDER_LIST, sendListMqVO);
        log.info("finished send member first withdraw message:{}", sendMqVO);


    }

}
