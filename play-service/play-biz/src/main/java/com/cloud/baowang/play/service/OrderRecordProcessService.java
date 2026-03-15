package com.cloud.baowang.play.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.OrderProcessMqVO;
import com.cloud.baowang.common.kafka.vo.UserLatestBetMqVO;
import com.cloud.baowang.common.kafka.vo.UserLatestBetVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountMqVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.common.kafka.vo.UserVIPFlowMqVO;
import com.cloud.baowang.common.kafka.vo.UserVIPFlowRequestVO;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossMqVO;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossSendVO;
import com.cloud.baowang.common.kafka.vo.UserWinLoseMqVO;
import com.cloud.baowang.play.api.enums.AbnormalTypeEnum;
import com.cloud.baowang.play.api.enums.ChangeStatusEnum;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.play.api.enums.FreeGameChangeTypeEnum;
import com.cloud.baowang.play.api.enums.ResettleStatusEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.enums.order.ProcessStatusEnum;
import com.cloud.baowang.play.api.vo.AbnormalOrder.OrderAbnormalDetailVO;
import com.cloud.baowang.play.api.vo.AbnormalOrder.OrderAbnormalRecordVO;
import com.cloud.baowang.play.api.vo.AbnormalOrder.OrderHistoryVO;
import com.cloud.baowang.play.api.vo.mq.FreeGameRecordVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.game.sh.enums.SHPlayTypeEnum;
import com.cloud.baowang.play.po.OrderAbnormalRecordPO;
import com.cloud.baowang.play.po.OrderRecordPO;
import com.cloud.baowang.wallet.api.api.UserTypingAmountApi;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingAmountVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class OrderRecordProcessService {
    private final OrderRecordService orderRecordService;
    private final UserTypingAmountApi userTypingAmountApi;
    private final OrderAbnormalRecordService orderAbnormalRecordService;


    public int orderProcess(List<OrderRecordVO> orderRecordList) {
        List<OrderRecordPO> insertList = new ArrayList<>();
        //报表mqList
        OrderProcessMqVO orderProcessMqVO = OrderProcessMqVO.init();

        List<String> thirdOrderIds = Optional.of(orderRecordList).orElse(Lists.newArrayList()).stream()
                .map(OrderRecordVO::getThirdOrderId).collect(Collectors.toList());
        OrderRecordVO orderRecordVO = orderRecordList.get(0);
        List<OrderRecordPO> orderRecordPOList = orderRecordService.findByThirdOrderIds(thirdOrderIds, orderRecordVO.getVenueCode());
        Map<String, OrderRecordPO> orderRecordPOMap = orderRecordPOList.stream().collect(Collectors.toMap(OrderRecordPO::getThirdOrderId, e -> e));

        for (OrderRecordVO order : orderRecordList) {
            order.setUpdatedTime(System.currentTimeMillis());
            OrderRecordPO recordPO = new OrderRecordPO();
            BeanUtils.copyProperties(order, recordPO);
            OrderRecordPO dbRecordPO = orderRecordPOMap.get(order.getThirdOrderId());

            // 未落盘注单数据
            if (dbRecordPO == null) {
                // 基础数据必填校验
                validBaseData(recordPO);
                // 未结算注单
                if (ClassifyEnum.NOT_SETTLE.getCode().equals(order.getOrderClassify())) {
                    log.info("{} 未落盘 未结算注单 注单id: {}, 详情: {}", order.getVenueCode(), order.getOrderId(), JSON.toJSONString(order));
                    populateBetMqVO(recordPO, true, orderProcessMqVO, true);
                    //未结算的订单， 有效金额和输赢统一设置为0
                    recordPO.setValidAmount(BigDecimal.ZERO);
                    recordPO.setWinLossAmount(BigDecimal.ZERO);
                }
                // 重结算
                else if (ClassifyEnum.RESETTLED.getCode().equals(order.getOrderClassify())) {
                    log.info("{} 未落盘 重结算注单 注单id: {}, 详情: {}", order.getVenueCode(), order.getOrderId(), JSON.toJSONString(order));
                    recordPO.setFirstSettleTime(order.getSettleTime());
                    populateSettleMqVO(recordPO, false, orderProcessMqVO, true);
                }
                // 撤消结算
                else if (ClassifyEnum.CANCEL.getCode().equals(order.getOrderClassify())) {
                    log.info("{} 未落盘 撤消结算注单 注单id: {}, 详情: {}", order.getVenueCode(), order.getOrderId(), JSON.toJSONString(order));
                    populateCancelBetMqVO(recordPO, null, true, orderProcessMqVO, true);
                    //撤销的订单， 有效金额和输赢统一设置为0
                    recordPO.setValidAmount(BigDecimal.ZERO);
                    recordPO.setWinLossAmount(BigDecimal.ZERO);
                    if (recordPO.getBetTime() == null) {
                        recordPO.setBetTime(System.currentTimeMillis());
                    }
                }
                // 第一次结算
                else if (ClassifyEnum.SETTLED.getCode().equals(order.getOrderClassify())) {
                    log.info("{} 未落盘 已结算注单 注单id: {}, 详情: {}", order.getVenueCode(), order.getOrderId(), JSON.toJSONString(order));
                    recordPO.setFirstSettleTime(order.getSettleTime());
                    populateSettleMqVO(recordPO, false, orderProcessMqVO, true);
                }

                // 初始化创建时间
                recordPO.setCreatedTime(System.currentTimeMillis());
                recordPO.setLatestTime(System.currentTimeMillis());
                insertList.add(recordPO);
            }
            // 已落盘注单数据
            else {
                // 根据id更新
                recordPO.setId(dbRecordPO.getId());
                recordPO.setOrderId(dbRecordPO.getOrderId());
                recordPO.setBetTime(dbRecordPO.getBetTime());
                order.setOrderId(dbRecordPO.getOrderId());
                // 如果上次落盘状态是结算||重结算 本次是结算 则本次更改为重结算
                if ((ClassifyEnum.SETTLED.getCode().equals(dbRecordPO.getOrderClassify()) || ClassifyEnum.RESETTLED.getCode().equals(dbRecordPO.getOrderClassify()))
                        && ClassifyEnum.SETTLED.getCode().equals(recordPO.getOrderClassify())) {
                    recordPO.setOrderClassify(ClassifyEnum.RESETTLED.getCode());
                    order.setOrderStatus(ClassifyEnum.RESETTLED.getCode());
                }
                // 基础数据必填校验
                validBaseData(recordPO);

                // 落盘的注单数据为[未结算], 但拉取过来的最新数据为[重结算], 在这里修改为[结算]状态
                if (ClassifyEnum.NOT_SETTLE.getCode().equals(dbRecordPO.getOrderClassify()) && ClassifyEnum.RESETTLED.getCode().equals(order.getOrderClassify())) {
                    order.setOrderStatus(OrderStatusEnum.SETTLED.getCode());
                    order.setOrderClassify(ClassifyEnum.SETTLED.getCode());
                    log.info("{} 已落盘(状态异常的注单) 注单id: {}, 落盘的注单数据为[未结算], 但拉取过来的最新数据为[重结算], 在这里修改为[结算]状态, 详情: {}",
                            order.getVenueCode(), order.getOrderId(), JSON.toJSONString(order));
                }

                // 第一次结算
                if (ClassifyEnum.SETTLED.getCode().equals(order.getOrderClassify())) {
                    log.info("{} 112-已落盘 结算注单 注单id: {}, 详情: {},ab:{},cc:{}", order.getVenueCode(), order.getOrderId(), JSON.toJSONString(order),
                            JSON.toJSONString(dbRecordPO),JSON.toJSONString(recordPO));
                    if (ObjectUtils.isEmpty(dbRecordPO.getSettleTime()) || recordPO.getSettleTime() > dbRecordPO.getSettleTime()) {
                        log.info("{} 已落盘 结算注单 注单id: {}, 详情: {}", order.getVenueCode(), order.getOrderId(), JSON.toJSONString(recordPO));
                        recordPO.setFirstSettleTime(order.getSettleTime());
                        if (updateOrder(recordPO)) {
                            populateSettleMqVO(recordPO, false, orderProcessMqVO, false);
                        }
                    }
                    // 撤消结算
                } else if (ClassifyEnum.CANCEL.getCode().equals(order.getOrderClassify())) {
                    if (ClassifyEnum.CANCEL.getCode().equals(dbRecordPO.getOrderClassify())) {
                        continue;
                    }

                    // 已结算后, 撤消结算
                    if (ClassifyEnum.SETTLED.getCode().equals(dbRecordPO.getOrderClassify()) ||
                            ClassifyEnum.RESETTLED.getCode().equals(dbRecordPO.getOrderClassify())) {
                        order.setChangeStatus(ChangeStatusEnum.CHANGED.getCode());
                        log.info("{} 已落盘 已结算注单、撤消结算 注单id: {}, 详情: {}", order.getVenueCode(), order.getOrderId(), JSON.toJSONString(order));
                        if (processAbnormalCancelOrder(order, dbRecordPO)) {
                            populateCancelSettleMqVO(recordPO, dbRecordPO, orderProcessMqVO, false);
                        }

                        // 未结算, 撤消结算
                    } else {
                        log.info("{} 已落盘 未结算注单、撤消结算 注单id: {}, 详情: {}", order.getVenueCode(), order.getOrderId(), JSON.toJSONString(order));
                        recordPO.setValidAmount(BigDecimal.ZERO);
                        recordPO.setWinLossAmount(BigDecimal.ZERO);
                        updateOrder(recordPO);
                        populateCancelBetMqVO(recordPO, dbRecordPO, false, orderProcessMqVO, false);
                    }
                }
                // 重结算
                else if (ClassifyEnum.RESETTLED.getCode().equals(order.getOrderClassify())) {
                    if (ObjectUtils.isEmpty(dbRecordPO.getSettleTime()) || recordPO.getSettleTime() > dbRecordPO.getSettleTime()) {
                        //如果是异常订单，保存在异常订单表里面，同时更新主订单
                        order.setChangeStatus(ChangeStatusEnum.CHANGED.getCode());

                        log.info("{} 已落盘 重结算 注单id: {}, 结算时间: {}, 详情: {}", dbRecordPO.getVenueCode(), dbRecordPO.getOrderId(), recordPO.getSettleTime(),
                                JSON.toJSONString(order));
                        if (processAbnormalOrder(order, dbRecordPO)) {
                            populateReSettleMq(recordPO, dbRecordPO, orderProcessMqVO, false);
                        }


                    }
                } //--重结算
            }
        }

        Boolean addSuccess = false;
        if (CollectionUtil.isNotEmpty(insertList)) {
            addSuccess = orderRecordService.saveBatch(insertList);
            log.info("新增订单数：{}", insertList.size());

        }
        sendMessageBatch(orderProcessMqVO, addSuccess);

        return 0;
    }

    private void validBaseData(OrderRecordPO order) {
        // userId
        if (StringUtils.isEmpty(order.getUserId())) {
            log.error("注单用户id为空,{}", order);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        // userAccount
        if (StringUtils.isEmpty(order.getUserAccount())) {
            log.error("注单用户account为空,{}", order);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        // betTime
        if (order.getBetTime() == null) {
            log.error("注单用户投注时间为空,{}", order);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

    }

    private void dealFreeGame(OrderRecordVO order, List<FreeGameRecordVO> freeGameVOS) {
        FreeGameRecordVO freeGameVO = new FreeGameRecordVO();
        freeGameVO.setType(FreeGameChangeTypeEnum.USED.getCode());
        freeGameVO.setUserId(order.getUserId());
        freeGameVO.setAcquireNum(1);
        freeGameVO.setVenueCode(order.getVenueCode());
        freeGameVO.setSiteCode(order.getSiteCode());
        freeGameVO.setOrderNo(order.getThirdOrderId());
        freeGameVOS.add(freeGameVO);
    }

    /**
     * 批量发送所有mq消息
     */
    private void sendMessageBatch(OrderProcessMqVO orderProcessMqVO, Boolean addSuccess) {

        List<UserWinLoseMqVO> winLoseMqVOS = orderProcessMqVO.getUserWinLoseUpdateMqList();
        List<UserTypingAmountRequestVO> typingAmountList = orderProcessMqVO.getTypingAmountUpdateList();
        List<UserVenueWinLossMqVO> userVenueReportList = orderProcessMqVO.getUserVenueWinLossMqList();
        List<UserVIPFlowRequestVO> userVIPFlowList = orderProcessMqVO.getUserVIPFlowUpdateList();
        List<UserLatestBetVO> userLatestBetVOList = orderProcessMqVO.getUserLatestBetVOList();

        if (addSuccess) {
            typingAmountList.addAll(orderProcessMqVO.getTypingAmountList());
            winLoseMqVOS.addAll(orderProcessMqVO.getUserWinLoseMqList());
            userVIPFlowList.addAll(orderProcessMqVO.getUserVIPFlowList());
        }
        //发送会员盈亏消息
        if (ObjectUtil.isNotEmpty(winLoseMqVOS)) {
            UserWinLoseMqVO userWinLoseMqVO = new UserWinLoseMqVO();
            userWinLoseMqVO.setOrderList(winLoseMqVOS);
            log.info("投注发送会员盈亏消息. send user_win_lose_channel: {}", JSON.toJSONString(userWinLoseMqVO));
            KafkaUtil.send(TopicsConstants.USER_WIN_LOSE_CHANNEL, userWinLoseMqVO);

        }
        // 打码量消息
        if (ObjectUtil.isNotEmpty(typingAmountList)) {
            UserTypingAmountMqVO userTypingAmountMqVO = new UserTypingAmountMqVO();
            userTypingAmountMqVO.setUserTypingAmountRequestVOList(typingAmountList);
            KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC, userTypingAmountMqVO);
        }

        // VIP晋级消息
        if (ObjectUtil.isNotEmpty(userVIPFlowList)) {
            UserVIPFlowMqVO userVIPFlowMqVO = new UserVIPFlowMqVO();
            userVIPFlowMqVO.setVipFlowRequestList(userVIPFlowList);
            log.info("5555555.... send VIP_FLOW_LIST_TOPIC: {}", JSON.toJSONString(userVIPFlowList));
            KafkaUtil.send(TopicsConstants.VIP_FLOW_LIST_TOPIC, userVIPFlowMqVO);
        }
        // 平台盈利消息
        if (ObjectUtil.isNotEmpty(userVenueReportList)) {
            UserVenueWinLossSendVO sendVO = new UserVenueWinLossSendVO();
            sendVO.setVoList(userVenueReportList);
            KafkaUtil.send(TopicsConstants.USER_VENUE_WIN_LOSE_BATCH_QUEUE, sendVO);

            KafkaUtil.send(TopicsConstants.VENUE_SITE_DAY_TOTAL_BET_AMOUNT_TOPIC, sendVO);
        }
        // 会员最新投注消息
        if (CollectionUtil.isNotEmpty(userLatestBetVOList)) {
            UserLatestBetMqVO userLatestBetMqVO = new UserLatestBetMqVO();
            userLatestBetMqVO.setUserLatestBetVOS(userLatestBetVOList);
            KafkaUtil.send(TopicsConstants.USER_LATEST_BET_QUEUE, userLatestBetMqVO);
        }

    }


    public void populateBetMqVO(OrderRecordPO orderRecordPO, boolean isFirst, OrderProcessMqVO orderProcessMqVO, boolean isAdd) {
        boolean isTips = SHPlayTypeEnum.TIPS.getCode().equals(orderRecordPO.getPlayType());
        //会员盈亏投注mq
        UserWinLoseMqVO betVO = new UserWinLoseMqVO();
        betVO.setUserId(orderRecordPO.getUserId());
        betVO.setAgentId(orderRecordPO.getAgentId());
        betVO.setDayHourMillis(DateUtil.beginOfDay(DateUtil.date(orderRecordPO.getBetTime())).getTime());
        betVO.setBizCode(CommonConstant.business_one);
        //betVO.setBetAmount(orderRecordPO.getBetAmount());
        betVO.setOrderId(orderRecordPO.getOrderId());
        betVO.setDeviceType(orderRecordPO.getDeviceType() == null ? 1 : orderRecordPO.getDeviceType());
        betVO.setOrderStatus(OrderStatusEnum.NOT_SETTLE.getCode());
        betVO.setSiteCode(orderRecordPO.getSiteCode());
        betVO.setCurrency(orderRecordPO.getCurrency());
        betVO.setBetType(orderRecordPO.getExId1());
        betVO.setAccountType(orderRecordPO.getAccountType());
        if (isTips) {
            betVO.setBetAmount(BigDecimal.ZERO);
            betVO.setTipsAmount(orderRecordPO.getBetAmount());
        }else{
            betVO.setBetAmount(orderRecordPO.getBetAmount());
            betVO.setTipsAmount(BigDecimal.ZERO);
        }
        log.info("发送投注会员盈亏报表mq,订单号: {}, {}", orderRecordPO.getOrderId(), JSON.toJSONString(betVO));

        if (isAdd) {
            orderProcessMqVO.getUserWinLoseMqList().add(betVO);
        } else {
            orderProcessMqVO.getUserWinLoseUpdateMqList().add(betVO);
        }


        // 最新投注信息
        UserLatestBetVO userLatestBetVO = new UserLatestBetVO();
        userLatestBetVO.setUserId(orderRecordPO.getUserId());
        userLatestBetVO.setBetTime(orderRecordPO.getBetTime());
        userLatestBetVO.setAccountType(orderRecordPO.getAccountType());
        orderProcessMqVO.getUserLatestBetVOList().add(userLatestBetVO);

    }

    public void populateSettleMqVO(OrderRecordPO po, boolean isFirst, OrderProcessMqVO orderProcessMqVO, boolean isAdd) {
        //BigDecimal tipsAmount = ShOrderStatusEnum.ALREADY_SETTLED.getCode().equals(po.getPlayType()) ? po.getBetAmount() : BigDecimal.ZERO;
        boolean isTips = SHPlayTypeEnum.TIPS.getCode().equals(po.getPlayType());

        //会员盈亏结算mq
        UserWinLoseMqVO mqVO = new UserWinLoseMqVO();

        mqVO.setUserId(po.getUserId());
        mqVO.setAgentId(po.getAgentId());
        //mqVO.setBetAmount(po.getBetAmount());
        mqVO.setDayHourMillis(TimeZoneUtils.convertToUtcStartOfHour(po.getSettleTime()));
        mqVO.setBizCode(CommonConstant.business_two);
        mqVO.setValidBetAmount(po.getValidAmount());
        mqVO.setBetType(po.getExId1());
        mqVO.setBetWinLose(po.getWinLossAmount());
        mqVO.setOrderId(po.getOrderId());
        mqVO.setLastBetAmount(BigDecimal.ZERO);
        mqVO.setLastValidBetAmount(BigDecimal.ZERO);
        mqVO.setLastBetWinLose(BigDecimal.ZERO);
        mqVO.setOrderStatus(OrderStatusEnum.SETTLED.getCode());
        mqVO.setSiteCode(po.getSiteCode());
        mqVO.setCurrency(po.getCurrency());
        mqVO.setAccountType(po.getAccountType());
        mqVO.setPlayType(po.getPlayType());
        mqVO.setLastTipsAmount(BigDecimal.ZERO);
        mqVO.setBetAmount(po.getBetAmount());
        // 产品说的， 如果是打赏金额，投注金额也是打赏金额，打赏金额就是投注金额
        if (isTips) {
            mqVO.setTipsAmount(po.getBetAmount());
        } else {
            mqVO.setTipsAmount(BigDecimal.ZERO);
        }

        log.info("发送结算会员盈亏报表mq,订单号: {}", po.getOrderId());
        //扣除打码量 结算才扣除打码量
        UserTypingAmountRequestVO requestVO = new UserTypingAmountRequestVO();
        requestVO.setOrderNo(po.getOrderId());
        requestVO.setUserId(po.getUserId());
        requestVO.setUserAccount(po.getUserAccount());
        requestVO.setType(TypingAmountEnum.SUBTRACT.getCode());
        requestVO.setTypingAmount(po.getValidAmount());
        requestVO.setAdjustType(TypingAmountAdjustTypeEnum.BET.getCode());
        requestVO.setAccountType(po.getAccountType());
        log.info("玩家扣除打码量, 帐号: {}, 订单号: {}, 金额: {}", po.getUserAccount(), po.getOrderId(), po.getValidAmount());

        //结算后再插入VIP晋级
        UserVIPFlowRequestVO userVIPFlowRequestVO = new UserVIPFlowRequestVO();
        userVIPFlowRequestVO.setUserId(po.getUserId());
        userVIPFlowRequestVO.setUserAccount(po.getUserAccount());
        userVIPFlowRequestVO.setVenueType(po.getVenueType());
        userVIPFlowRequestVO.setValidAmount(po.getValidAmount());
        userVIPFlowRequestVO.setAccountType(po.getAccountType());
        userVIPFlowRequestVO.setSiteCode(po.getSiteCode());
        if (isAdd) {
            orderProcessMqVO.getTypingAmountList().add(requestVO);
            orderProcessMqVO.getUserWinLoseMqList().add(mqVO);
            orderProcessMqVO.getUserVIPFlowList().add(userVIPFlowRequestVO);
        } else {
            orderProcessMqVO.getTypingAmountUpdateList().add(requestVO);
            orderProcessMqVO.getUserWinLoseUpdateMqList().add(mqVO);
            orderProcessMqVO.getUserVIPFlowUpdateList().add(userVIPFlowRequestVO);
        }

        //会员每日场馆盈亏
        UserVenueWinLossMqVO userVenueWinLossMqVO = new UserVenueWinLossMqVO();
        userVenueWinLossMqVO.setVenueType(po.getVenueType());
        userVenueWinLossMqVO.setVenueGameType(po.getThirdGameCode());
        userVenueWinLossMqVO.setBetAmount(po.getBetAmount());
        userVenueWinLossMqVO.setUserAccount(po.getUserAccount());
        userVenueWinLossMqVO.setUserId(po.getUserId());
        userVenueWinLossMqVO.setAgentId(po.getAgentId());
        userVenueWinLossMqVO.setAgentAccount(po.getAgentAcct());
        userVenueWinLossMqVO.setVenueCode(po.getVenueCode());
        userVenueWinLossMqVO.setDayHour(DateUtil.beginOfHour(DateUtil.date(po.getSettleTime())).getTime());
        userVenueWinLossMqVO.setValidAmount(po.getValidAmount());
        userVenueWinLossMqVO.setWinLossAmount(po.getWinLossAmount());
        userVenueWinLossMqVO.setBetCount(CommonConstant.business_one);
        userVenueWinLossMqVO.setOrderId(po.getOrderId());
        userVenueWinLossMqVO.setSiteCode(po.getSiteCode());
        userVenueWinLossMqVO.setCurrency(po.getCurrency());
        userVenueWinLossMqVO.setAccountType(po.getAccountType());
        userVenueWinLossMqVO.setRoomType(po.getRoomType());
        userVenueWinLossMqVO.setBetAmount(po.getBetAmount());
        // 产品说的， 如果是打赏金额，投注金额也是打赏金额，打赏金额就是投注金额
        if (isTips) {
            userVenueWinLossMqVO.setTipsAmount(po.getBetAmount());
        }else{

            userVenueWinLossMqVO.setTipsAmount(BigDecimal.ZERO);
        }
        userVenueWinLossMqVO.setLastTipsAmount(BigDecimal.ZERO);
        if (po.getFirstSettleTime() != null) {
            userVenueWinLossMqVO.setFirstSettleDayHour(DateUtil.beginOfHour(DateUtil.date(po.getSettleTime())).getTime());
        }
        userVenueWinLossMqVO.setBetType(po.getExId1());
        orderProcessMqVO.getUserVenueWinLossMqList().add(userVenueWinLossMqVO);

    }

    public Boolean updateOrder(OrderRecordPO po) {
        po.setUpdatedTime(System.currentTimeMillis());

        LambdaUpdateWrapper<OrderRecordPO> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(OrderRecordPO::getId, po.getId());
        updateWrapper.set(OrderRecordPO::getOrderInfo, po.getOrderInfo());
        updateWrapper.set(OrderRecordPO::getResultList, po.getResultList());
        updateWrapper.set(OrderRecordPO::getFirstSettleTime, po.getSettleTime());
        updateWrapper.set(OrderRecordPO::getSettleTime, po.getSettleTime());
        updateWrapper.set(OrderRecordPO::getResultTime, po.getResultTime());
        updateWrapper.set(OrderRecordPO::getOrderInfo, po.getOrderInfo());
        updateWrapper.set(OrderRecordPO::getResultList, po.getResultList());
        if (po.getBetAmount() != null) {
            updateWrapper.set(OrderRecordPO::getBetAmount, po.getBetAmount());
        }
        if (po.getValidAmount() != null) {
            updateWrapper.set(OrderRecordPO::getValidAmount, po.getValidAmount());
        }
        if (po.getOdds() != null) {
            updateWrapper.set(OrderRecordPO::getOdds, po.getOdds());
        }
        if (po.getAgentId() != null) {
            updateWrapper.set(OrderRecordPO::getAgentId, po.getAgentId());
        }
        if (po.getAgentAcct() != null) {
            updateWrapper.set(OrderRecordPO::getAgentAcct, po.getAgentAcct());
        }
        if (po.getPlayInfo() != null) {
            updateWrapper.set(OrderRecordPO::getPlayInfo, po.getPlayInfo());
        }
        updateWrapper.set(OrderRecordPO::getPayoutAmount, po.getPayoutAmount());
        updateWrapper.set(OrderRecordPO::getWinLossAmount, po.getWinLossAmount());
        updateWrapper.set(OrderRecordPO::getOrderStatus, po.getOrderStatus());
        updateWrapper.set(OrderRecordPO::getOrderClassify, po.getOrderClassify());
        updateWrapper.set(OrderRecordPO::getResultList, po.getResultList());
        updateWrapper.set(OrderRecordPO::getUpdatedTime, po.getUpdatedTime());
        updateWrapper.set(OrderRecordPO::getChangeStatus, po.getChangeStatus());
        updateWrapper.set(OrderRecordPO::getGameNo, po.getGameNo());
        updateWrapper.set(OrderRecordPO::getPlayType, po.getPlayType());
        updateWrapper.set(OrderRecordPO::getEventInfo, po.getEventInfo());
        if (ChangeStatusEnum.CHANGED.getCode().equals(po.getChangeStatus())) {
            updateWrapper.set(OrderRecordPO::getChangeTime, po.getSettleTime());
        }
        updateWrapper.set(OrderRecordPO::getParlayInfo, po.getParlayInfo());
        updateWrapper.set(OrderRecordPO::getLatestTime, System.currentTimeMillis());

        return orderRecordService.update(null, updateWrapper);

    }

    public void populateReSettleMq(OrderRecordPO po, OrderRecordPO dbRecordPO, OrderProcessMqVO orderProcessMqVO, boolean isAdd) {
        boolean isTips = SHPlayTypeEnum.TIPS.getCode().equals(po.getPlayType());

        //重结算需要重算打码量
        UserTypingAmountVO userTypingAmountVO = userTypingAmountApi.getUserTypingAmountByAccount(po.getSiteCode(), po.getUserAccount());
        if (userTypingAmountVO != null && userTypingAmountVO.getTypingAmount().compareTo(BigDecimal.ZERO) > 0) {
            UserTypingAmountRequestVO requestVO = new UserTypingAmountRequestVO();
            requestVO.setUserAccount(po.getUserAccount());
            requestVO.setUserId(po.getUserId());
            requestVO.setOrderNo(po.getOrderId());
            requestVO.setAdjustType(TypingAmountAdjustTypeEnum.BET.getCode());
            requestVO.setAccountType(po.getAccountType());
            if (po.getValidAmount().compareTo(dbRecordPO.getValidAmount()) > 0) {
                requestVO.setType(TypingAmountEnum.SUBTRACT.getCode());
                requestVO.setTypingAmount(po.getValidAmount().subtract(dbRecordPO.getValidAmount()));
            } else {
                requestVO.setType(TypingAmountEnum.ADD.getCode());
                requestVO.setTypingAmount(dbRecordPO.getValidAmount().subtract(po.getValidAmount()));
            }

            //walletServiceFeignResource.addUserTypingAmount(requestVO);
            log.info("玩家注单重结算操作打码量, 帐号: {}, 订单号: {}, 金额: {}", po.getUserAccount(), po.getOrderId(), po.getValidAmount().subtract(dbRecordPO.getValidAmount()));
            if (isAdd) {
                orderProcessMqVO.getTypingAmountList().add(requestVO);
            } else {
                orderProcessMqVO.getTypingAmountUpdateList().add(requestVO);
            }
        }

        //会员每日场馆盈亏
        UserVenueWinLossMqVO userVenueWinLossMqVO = new UserVenueWinLossMqVO();
        userVenueWinLossMqVO.setVenueType(po.getVenueType());
        userVenueWinLossMqVO.setVenueGameType(po.getThirdGameCode());
        userVenueWinLossMqVO.setBetAmount(po.getBetAmount());
        userVenueWinLossMqVO.setUserAccount(po.getUserAccount());
        userVenueWinLossMqVO.setUserId(po.getUserId());
        userVenueWinLossMqVO.setOrderId(po.getOrderId());
        userVenueWinLossMqVO.setAgentId(po.getAgentId());
        userVenueWinLossMqVO.setAgentAccount(po.getAgentAcct());
        userVenueWinLossMqVO.setVenueCode(po.getVenueCode());
        userVenueWinLossMqVO.setDayHour(DateUtil.beginOfHour(DateUtil.date(po.getSettleTime())).getTime());
        userVenueWinLossMqVO.setValidAmount(po.getValidAmount());
        userVenueWinLossMqVO.setWinLossAmount(po.getWinLossAmount());
        userVenueWinLossMqVO.setLastDayHour(DateUtil.beginOfHour(DateUtil.date(dbRecordPO.getSettleTime())).getTime());
        userVenueWinLossMqVO.setLastBetAmount(dbRecordPO.getBetAmount());
        userVenueWinLossMqVO.setLastValidBetAmount(dbRecordPO.getValidAmount());
        userVenueWinLossMqVO.setLastBetWinLose(dbRecordPO.getWinLossAmount());
        userVenueWinLossMqVO.setLastAgentId(dbRecordPO.getAgentId());
        userVenueWinLossMqVO.setLastAgentAccount(dbRecordPO.getAgentAcct());
        userVenueWinLossMqVO.setBetCount(CommonConstant.business_one);
        userVenueWinLossMqVO.setSiteCode(po.getSiteCode());
        userVenueWinLossMqVO.setCurrency(po.getCurrency());
        userVenueWinLossMqVO.setAccountType(po.getAccountType());
        userVenueWinLossMqVO.setBetAmount(po.getBetAmount());
        userVenueWinLossMqVO.setLastBetAmount(dbRecordPO.getBetAmount());
        if (isTips) {
            // 这次
            userVenueWinLossMqVO.setTipsAmount(po.getBetAmount());
            // 上一次
            userVenueWinLossMqVO.setLastTipsAmount(dbRecordPO.getBetAmount());
        } else {
            // 这次
            userVenueWinLossMqVO.setTipsAmount(BigDecimal.ZERO);
            // 上一次
            userVenueWinLossMqVO.setLastTipsAmount(BigDecimal.ZERO);
        }
        userVenueWinLossMqVO.setBetType(po.getExId1());
        orderProcessMqVO.getUserVenueWinLossMqList().add(userVenueWinLossMqVO);

        //会员每日场馆盈亏
        UserWinLoseMqVO userWinLoseMqVO = new UserWinLoseMqVO();
        //userWinLoseMqVO.setBetAmount(po.getBetAmount());
        userWinLoseMqVO.setUserId(po.getUserId());
        userWinLoseMqVO.setAgentId(po.getAgentId());
        userWinLoseMqVO.setDayHourMillis(TimeZoneUtils.convertToUtcStartOfHour(po.getSettleTime()));
        userWinLoseMqVO.setValidBetAmount(po.getValidAmount());
        userWinLoseMqVO.setBetWinLose(po.getWinLossAmount());
        userWinLoseMqVO.setOrderId(po.getOrderId());
        userWinLoseMqVO.setLastDayHour(TimeZoneUtils.convertToUtcStartOfHour(dbRecordPO.getSettleTime()));
        userWinLoseMqVO.setBetType(po.getExId1());
        //userWinLoseMqVO.setLastBetAmount(dbRecordPO.getBetAmount());
        userWinLoseMqVO.setLastValidBetAmount(dbRecordPO.getValidAmount());
        userWinLoseMqVO.setLastBetWinLose(dbRecordPO.getWinLossAmount());
        userWinLoseMqVO.setLastAgentId(dbRecordPO.getAgentId());
        userWinLoseMqVO.setLastAgentAccount(dbRecordPO.getAgentAcct());
        userWinLoseMqVO.setSiteCode(po.getSiteCode());
        userWinLoseMqVO.setCurrency(po.getCurrency());
        userWinLoseMqVO.setBizCode(CommonConstant.business_two);
        userWinLoseMqVO.setOrderStatus(OrderStatusEnum.RESETTLED.getCode());
        userWinLoseMqVO.setAccountType(po.getAccountType());
        userWinLoseMqVO.setBetAmount(po.getBetAmount());
        userWinLoseMqVO.setLastBetAmount(dbRecordPO.getBetAmount());
        if (isTips) {
            // 这次
            userWinLoseMqVO.setTipsAmount(po.getBetAmount());
            // 上一次
            userWinLoseMqVO.setLastTipsAmount(dbRecordPO.getBetAmount());
        } else {
            // 这次
            userWinLoseMqVO.setTipsAmount(BigDecimal.ZERO);
            // 上一次
            userWinLoseMqVO.setLastTipsAmount(BigDecimal.ZERO);
        }
        orderProcessMqVO.getUserWinLoseUpdateMqList().add(userWinLoseMqVO);

    }

    public Boolean processAbnormalOrder(OrderRecordVO order, OrderRecordPO recordPO) {
        log.info("{} 异常注单, 注单号: {}, 状态: {}, 详情：{}", order.getVenueCode(), order.getThirdOrderId(),
                order.getOrderStatus(), JSON.toJSONString(order));
        OrderAbnormalRecordVO historyVO = orderAbnormalRecordService.findByOrderId(order.getOrderId());
        Integer changeCount = 1;
        OrderAbnormalRecordVO orderAbnormalRecordVO = new OrderAbnormalRecordVO();
        if (historyVO == null) {
            BeanUtils.copyProperties(order, orderAbnormalRecordVO);
        } else {
            changeCount = historyVO.getChangeCount() + 1;
            BeanUtils.copyProperties(historyVO, orderAbnormalRecordVO);
        }
        orderAbnormalRecordVO.setOrderId(recordPO.getOrderId());
        orderAbnormalRecordVO.setSiteCode(order.getSiteCode());
        orderAbnormalRecordVO.setOrderStatus(order.getOrderStatus());
        orderAbnormalRecordVO.setWinLossAmount(order.getWinLossAmount());
        orderAbnormalRecordVO.setValidAmount(order.getValidAmount());
        orderAbnormalRecordVO.setPayoutAmount(order.getPayoutAmount());
        orderAbnormalRecordVO.setSettleTime(order.getSettleTime());
        orderAbnormalRecordVO.setFirstSettleTime(recordPO.getFirstSettleTime());
        orderAbnormalRecordVO.setChangeStatus(order.getChangeStatus());
        orderAbnormalRecordVO.setChangeCount(changeCount);
        orderAbnormalRecordVO.setChangeTime(ObjectUtil.isEmpty(order.getSettleTime()) ? order.getUpdatedTime() : order.getSettleTime());
        orderAbnormalRecordVO.setReSettleStatus(ResettleStatusEnum.RESETTLE.getCode());
        orderAbnormalRecordVO.setReSettleResult("重结算成功");
        orderAbnormalRecordVO.setAbnormalType(AbnormalTypeEnum.RESETTLE.getCode());
        orderAbnormalRecordVO.setOrderClassify(order.getOrderClassify());
        orderAbnormalRecordVO.setProcessStatus(ProcessStatusEnum.PROCESSED.getCode());
        orderAbnormalRecordVO.setUpdatedTime(order.getUpdatedTime());
        orderAbnormalRecordVO.setResultList(order.getResultList());

        String historyStr = orderAbnormalRecordVO.getHistory();
        if (historyVO != null) {
            List<OrderHistoryVO> hisList = com.alibaba.fastjson2.JSON.parseArray(historyStr, OrderHistoryVO.class);

            //第n次修改
            OrderHistoryVO orderHistoryVO = new OrderHistoryVO();
            OrderAbnormalDetailVO detailVO = new OrderAbnormalDetailVO();
            BeanUtils.copyProperties(orderAbnormalRecordVO, detailVO);
            detailVO.setSequenceNo(hisList.size());
            detailVO.setAbnormalType(AbnormalTypeEnum.RESETTLE.getCode());
            detailVO.setOrderClassify(order.getOrderClassify());
            detailVO.setProcessStatus(ProcessStatusEnum.PROCESSED.getCode());
            detailVO.setParlayInfo(order.getParlayInfo());
            detailVO.setResultList(order.getResultList());

            orderHistoryVO.setOrderAbnormalDetailVO(detailVO);
            hisList.add(orderHistoryVO);
            orderAbnormalRecordVO.setHistory(com.alibaba.fastjson2.JSON.toJSONString(hisList));
        } else {
            //原始注单信息
            OrderHistoryVO orderHistoryVO = new OrderHistoryVO();
            OrderAbnormalDetailVO orderAbnormalDetailVO = new OrderAbnormalDetailVO();
            BeanUtils.copyProperties(recordPO, orderAbnormalDetailVO);
            orderAbnormalDetailVO.setParlayInfo(recordPO.getParlayInfo());
            orderAbnormalDetailVO.setSequenceNo(0);
            orderAbnormalDetailVO.setOrderClassify(recordPO.getOrderClassify());
            orderAbnormalDetailVO.setProcessStatus(ProcessStatusEnum.PROCESSED.getCode());

            orderHistoryVO.setOrderAbnormalDetailVO(orderAbnormalDetailVO);

            //第一次修改
            OrderHistoryVO orderHistoryVO1 = new OrderHistoryVO();
            OrderAbnormalDetailVO detailVO = new OrderAbnormalDetailVO();
            BeanUtils.copyProperties(orderAbnormalRecordVO, detailVO);
            detailVO.setSequenceNo(1);
            detailVO.setAbnormalType(AbnormalTypeEnum.RESETTLE.getCode());
            detailVO.setOrderClassify(order.getOrderClassify());
            detailVO.setProcessStatus(ProcessStatusEnum.PROCESSED.getCode());

            orderHistoryVO1.setOrderAbnormalDetailVO(detailVO);

            List<OrderHistoryVO> hisList = new ArrayList<>();
            hisList.add(orderHistoryVO);
            hisList.add(orderHistoryVO1);
            orderAbnormalRecordVO.setHistory(com.alibaba.fastjson2.JSON.toJSONString(hisList));
        }

        //更新原始注单信息
        LambdaUpdateWrapper<OrderRecordPO> updateWrapper = Wrappers.<OrderRecordPO>lambdaUpdate();
        updateWrapper.eq(OrderRecordPO::getId, recordPO.getId());
        updateWrapper.set(OrderRecordPO::getResultTime, order.getResultTime());
        updateWrapper.set(OrderRecordPO::getSettleTime, order.getSettleTime());
        updateWrapper.set(OrderRecordPO::getBetAmount, order.getBetAmount());
        updateWrapper.set(OrderRecordPO::getValidAmount, order.getValidAmount());
        updateWrapper.set(OrderRecordPO::getPayoutAmount, order.getPayoutAmount());
        updateWrapper.set(OrderRecordPO::getWinLossAmount, order.getWinLossAmount());
        updateWrapper.set(OrderRecordPO::getOrderStatus, order.getOrderStatus());
        updateWrapper.set(OrderRecordPO::getOrderClassify, order.getOrderClassify());
        updateWrapper.set(OrderRecordPO::getResultList, order.getResultList());
        updateWrapper.set(OrderRecordPO::getUpdatedTime, System.currentTimeMillis());
        updateWrapper.set(OrderRecordPO::getChangeStatus, order.getChangeStatus());
        updateWrapper.set(OrderRecordPO::getChangeTime, order.getSettleTime());
        updateWrapper.set(OrderRecordPO::getChangeCount, orderAbnormalRecordVO.getChangeCount());
        updateWrapper.set(OrderRecordPO::getOrderInfo, order.getOrderInfo());
        updateWrapper.set(OrderRecordPO::getResultList, order.getResultList());
        //结算时间不是当天就不更新返水
        Long settleTime = recordPO.getFirstSettleTime();
        if (settleTime == null) {  //SG没有结算时间
            settleTime = order.getBetTime();
        }

        updateWrapper.set(OrderRecordPO::getParlayInfo, order.getParlayInfo());
        updateWrapper.set(OrderRecordPO::getLatestTime, System.currentTimeMillis());
        updateWrapper.set(OrderRecordPO::getSettleTime, order.getSettleTime());

        boolean bool = orderRecordService.update(null, updateWrapper);
        log.info("订单号：{}， 成功更新", order.getOrderId());
        if (historyVO != null) {
            // 有历史记录 更新历史记录
            OrderAbnormalRecordPO orderAbnormalRecordPO = new OrderAbnormalRecordPO();
            BeanUtils.copyProperties(orderAbnormalRecordVO, orderAbnormalRecordPO);
            orderAbnormalRecordService.updateById(orderAbnormalRecordPO);
        } else {
            orderAbnormalRecordService.saveAbnormalOrder(orderAbnormalRecordVO);
        }

        return bool;
    }

    public Boolean processAbnormalCancelOrder(OrderRecordVO order, OrderRecordPO recordPO) {
        //只能撤销一次
        if (OrderStatusEnum.CANCEL.getCode().equals(recordPO.getOrderStatus())) {
            return false;
        }

        if (ChangeStatusEnum.CHANGED.getCode().equals(order.getChangeStatus())) {
            OrderAbnormalRecordVO historyVO = orderAbnormalRecordService.findByOrderId(order.getOrderId());
            Integer changeCount = 1;
            OrderAbnormalRecordVO orderAbnormalRecordVO = new OrderAbnormalRecordVO();
            if (historyVO == null) {
                BeanUtils.copyProperties(order, orderAbnormalRecordVO);
            } else {
                changeCount = historyVO.getChangeCount() + 1;
                BeanUtils.copyProperties(historyVO, orderAbnormalRecordVO);
            }

            orderAbnormalRecordVO.setSiteCode(order.getSiteCode());
            orderAbnormalRecordVO.setOrderStatus(order.getOrderStatus());
            orderAbnormalRecordVO.setWinLossAmount(order.getWinLossAmount());
            orderAbnormalRecordVO.setValidAmount(order.getValidAmount());
            orderAbnormalRecordVO.setPayoutAmount(order.getPayoutAmount());
            orderAbnormalRecordVO.setSettleTime(order.getSettleTime());
            orderAbnormalRecordVO.setChangeStatus(order.getChangeStatus());
            orderAbnormalRecordVO.setChangeCount(changeCount);
            orderAbnormalRecordVO.setChangeTime(ObjectUtil.isEmpty(order.getSettleTime()) ? order.getUpdatedTime() : order.getSettleTime());
            orderAbnormalRecordVO.setReSettleStatus(ResettleStatusEnum.RESETTLE.getCode());
            orderAbnormalRecordVO.setReSettleResult("注单撤销");
            orderAbnormalRecordVO.setAbnormalType(AbnormalTypeEnum.RESETTLE.getCode());
            orderAbnormalRecordVO.setOrderClassify(order.getOrderClassify());
            orderAbnormalRecordVO.setProcessStatus(ProcessStatusEnum.PROCESSED.getCode());
            orderAbnormalRecordVO.setUpdatedTime(order.getUpdatedTime());

            String historyStr = orderAbnormalRecordVO.getHistory();
            if (historyVO != null) {
                List<OrderHistoryVO> hisList = com.alibaba.fastjson2.JSON.parseArray(historyStr, OrderHistoryVO.class);

                //第n次修改
                OrderHistoryVO orderHistoryVO = new OrderHistoryVO();
                OrderAbnormalDetailVO detailVO = new OrderAbnormalDetailVO();
                BeanUtils.copyProperties(orderAbnormalRecordVO, detailVO);
                detailVO.setSequenceNo(hisList.size());
                detailVO.setAbnormalType(AbnormalTypeEnum.RESETTLE.getCode());
                detailVO.setOrderClassify(order.getOrderClassify());
                detailVO.setProcessStatus(ProcessStatusEnum.PROCESSED.getCode());
                detailVO.setParlayInfo(order.getParlayInfo());

                orderHistoryVO.setOrderAbnormalDetailVO(detailVO);
                hisList.add(orderHistoryVO);
                orderAbnormalRecordVO.setHistory(com.alibaba.fastjson2.JSON.toJSONString(hisList));
            } else {
                //原始注单信息
                OrderHistoryVO orderHistoryVO = new OrderHistoryVO();
                OrderAbnormalDetailVO orderAbnormalDetailVO = new OrderAbnormalDetailVO();
                BeanUtils.copyProperties(recordPO, orderAbnormalDetailVO);
                orderAbnormalDetailVO.setSequenceNo(0);
                orderAbnormalDetailVO.setOrderClassify(recordPO.getOrderClassify());
                orderAbnormalDetailVO.setProcessStatus(ProcessStatusEnum.PROCESSED.getCode());

                orderHistoryVO.setOrderAbnormalDetailVO(orderAbnormalDetailVO);

                //第一次修改
                OrderHistoryVO orderHistoryVO1 = new OrderHistoryVO();
                OrderAbnormalDetailVO detailVO = new OrderAbnormalDetailVO();
                BeanUtils.copyProperties(orderAbnormalRecordVO, detailVO);
                detailVO.setSequenceNo(1);
                detailVO.setAbnormalType(AbnormalTypeEnum.RESETTLE.getCode());
                detailVO.setOrderClassify(order.getOrderClassify());
                detailVO.setProcessStatus(ProcessStatusEnum.PROCESSED.getCode());

                orderHistoryVO1.setOrderAbnormalDetailVO(detailVO);

                List<OrderHistoryVO> hisList = new ArrayList<>();
                hisList.add(orderHistoryVO);
                hisList.add(orderHistoryVO1);
                orderAbnormalRecordVO.setHistory(com.alibaba.fastjson2.JSON.toJSONString(hisList));
            }

            //更新原始注单信息
            LambdaUpdateWrapper<OrderRecordPO> updateWrapper = Wrappers.<OrderRecordPO>lambdaUpdate();
            updateWrapper.eq(OrderRecordPO::getId, recordPO.getId());

            updateWrapper.set(OrderRecordPO::getSettleTime, order.getSettleTime());
            updateWrapper.set(OrderRecordPO::getBetAmount, order.getBetAmount());
            updateWrapper.set(OrderRecordPO::getValidAmount, BigDecimal.ZERO);
            updateWrapper.set(OrderRecordPO::getPayoutAmount, order.getPayoutAmount());
            updateWrapper.set(OrderRecordPO::getWinLossAmount, BigDecimal.ZERO);
            updateWrapper.set(OrderRecordPO::getOrderStatus, order.getOrderStatus());
            updateWrapper.set(OrderRecordPO::getOrderClassify, order.getOrderClassify());
            updateWrapper.set(OrderRecordPO::getResultList, order.getResultList());
            updateWrapper.set(OrderRecordPO::getUpdatedTime, System.currentTimeMillis());
            updateWrapper.set(OrderRecordPO::getChangeStatus, order.getChangeStatus());
            updateWrapper.set(OrderRecordPO::getChangeTime, order.getSettleTime());
            updateWrapper.set(OrderRecordPO::getChangeCount, orderAbnormalRecordVO.getChangeCount());
            updateWrapper.set(OrderRecordPO::getOrderInfo, order.getOrderInfo());
            updateWrapper.set(OrderRecordPO::getResultList, order.getResultList());
            //结算时间不是当天就不更新返水
            Long settleTime = recordPO.getFirstSettleTime();
            if (settleTime == null) {  //SG没有结算时间
                settleTime = order.getBetTime();
            }

            updateWrapper.set(OrderRecordPO::getParlayInfo, order.getParlayInfo());
            updateWrapper.set(OrderRecordPO::getLatestTime, System.currentTimeMillis());
            boolean bool = orderRecordService.update(null, updateWrapper);
            if (historyVO != null) {
                // 有历史记录 更新历史记录
                OrderAbnormalRecordPO orderAbnormalRecordPO = new OrderAbnormalRecordPO();
                BeanUtils.copyProperties(orderAbnormalRecordVO, orderAbnormalRecordPO);
                orderAbnormalRecordService.updateById(orderAbnormalRecordPO);
            } else {
                orderAbnormalRecordService.saveAbnormalOrder(orderAbnormalRecordVO);
            }

            return bool;
        }

        return false;
    }

    public void populateCancelBetMqVO(OrderRecordPO po, OrderRecordPO dbRecordPO, boolean isFirst, OrderProcessMqVO orderProcessMqVO, boolean isAdd) {
        if (dbRecordPO != null && dbRecordPO.getOrderStatus().equals(OrderStatusEnum.CANCEL.getCode())) {
            return;
        }
        if (po.getSettleTime() == null) {
            // 无结算时间不发mq
            return;
        }
        boolean isTips = SHPlayTypeEnum.TIPS.getCode().equals(po.getPlayType());
        //会员每日场馆盈亏
        UserVenueWinLossMqVO userVenueWinLossMqVO = new UserVenueWinLossMqVO();
        userVenueWinLossMqVO.setVenueType(po.getVenueType());
        userVenueWinLossMqVO.setCurrency(po.getCurrency());
        userVenueWinLossMqVO.setVenueGameType(po.getThirdGameCode());
        userVenueWinLossMqVO.setBetAmount(po.getBetAmount());
        // 产品说的， 如果是打赏金额，投注金额也是打赏金额，打赏金额就是投注金额
        if (isTips) {
            userVenueWinLossMqVO.setTipsAmount(po.getBetAmount());
        } else {
            userVenueWinLossMqVO.setTipsAmount(BigDecimal.ZERO);
        }
        userVenueWinLossMqVO.setUserAccount(po.getUserAccount());
        userVenueWinLossMqVO.setUserId(po.getUserId());
        userVenueWinLossMqVO.setOrderId(po.getOrderId());
        userVenueWinLossMqVO.setAgentId(po.getAgentId());
        userVenueWinLossMqVO.setAgentAccount(po.getAgentAcct());
        userVenueWinLossMqVO.setVenueCode(po.getVenueCode());
        userVenueWinLossMqVO.setDayHour(DateUtil.beginOfHour(DateUtil.date(po.getSettleTime())).getTime());
        userVenueWinLossMqVO.setValidAmount(BigDecimal.ZERO);
        userVenueWinLossMqVO.setWinLossAmount(BigDecimal.ZERO);
        userVenueWinLossMqVO.setSiteCode(po.getSiteCode());
        userVenueWinLossMqVO.setAccountType(po.getAccountType());
        userVenueWinLossMqVO.setBetType(po.getExId1());
        if (isTips) {
            // 这次
            userVenueWinLossMqVO.setTipsAmount(po.getBetAmount());
            // 上一次
            userVenueWinLossMqVO.setLastTipsAmount(dbRecordPO.getBetAmount());
        } else {
            // 这次
            userVenueWinLossMqVO.setTipsAmount(BigDecimal.ZERO);
            // 上一次
            userVenueWinLossMqVO.setLastTipsAmount(BigDecimal.ZERO);
        }

        if (dbRecordPO == null || dbRecordPO.getSettleTime() == null) {
            userVenueWinLossMqVO.setLastValidBetAmount(BigDecimal.ZERO);
            userVenueWinLossMqVO.setLastBetWinLose(BigDecimal.ZERO);
            userVenueWinLossMqVO.setLastBetAmount(BigDecimal.ZERO);
            userVenueWinLossMqVO.setLastTipsAmount(BigDecimal.ZERO);
        } else {
            userVenueWinLossMqVO.setLastValidBetAmount(dbRecordPO.getValidAmount());
            userVenueWinLossMqVO.setLastBetWinLose(dbRecordPO.getWinLossAmount());
            userVenueWinLossMqVO.setLastBetAmount(dbRecordPO.getBetAmount());
            userVenueWinLossMqVO.setLastDayHour(DateUtil.beginOfHour(DateUtil.date(dbRecordPO.getSettleTime())).getTime());
            userVenueWinLossMqVO.setLastAgentId(dbRecordPO.getAgentId());
            userVenueWinLossMqVO.setLastAgentAccount(dbRecordPO.getAgentAcct());

        }
        userVenueWinLossMqVO.setBetCount(CommonConstant.business_one);
        orderProcessMqVO.getUserVenueWinLossMqList().add(userVenueWinLossMqVO);

        //会员每日盈亏
        UserWinLoseMqVO userWinLoseMqVO = new UserWinLoseMqVO();
        // 产品说的， 如果是打赏金额，投注金额也是打赏金额，打赏金额就是投注金额
        userWinLoseMqVO.setBetAmount(po.getBetAmount());
        if (isTips) {
            userWinLoseMqVO.setTipsAmount(po.getBetAmount());
        } else {
            userWinLoseMqVO.setTipsAmount(BigDecimal.ZERO);
        }
        //userWinLoseMqVO.setBetAmount(po.getBetAmount());
        userWinLoseMqVO.setUserId(po.getUserId());
        userWinLoseMqVO.setAgentId(po.getAgentId());
        userWinLoseMqVO.setDayHourMillis(TimeZoneUtils.convertToUtcStartOfHour(po.getSettleTime()));
        userWinLoseMqVO.setValidBetAmount(po.getValidAmount());
        userWinLoseMqVO.setBetWinLose(po.getWinLossAmount());
        userWinLoseMqVO.setOrderId(po.getOrderId());
        userWinLoseMqVO.setBizCode(CommonConstant.business_two);
        userWinLoseMqVO.setAccountType(po.getAccountType());
        if (isTips) {
            // 这次
            userWinLoseMqVO.setTipsAmount(po.getBetAmount());
            // 上一次
            userWinLoseMqVO.setLastTipsAmount(dbRecordPO.getBetAmount());
        } else {
            // 这次
            userWinLoseMqVO.setTipsAmount(BigDecimal.ZERO);
            // 上一次
            userWinLoseMqVO.setLastTipsAmount(BigDecimal.ZERO);
        }

        if (dbRecordPO == null || dbRecordPO.getSettleTime() == null) {
            userWinLoseMqVO.setLastValidBetAmount(BigDecimal.ZERO);
            userWinLoseMqVO.setLastBetWinLose(BigDecimal.ZERO);
            userWinLoseMqVO.setLastBetAmount(BigDecimal.ZERO);
            userWinLoseMqVO.setLastTipsAmount(BigDecimal.ZERO);
        } else {
            userWinLoseMqVO.setLastValidBetAmount(dbRecordPO.getValidAmount());
            userWinLoseMqVO.setLastBetWinLose(dbRecordPO.getWinLossAmount());
            userWinLoseMqVO.setLastBetAmount(dbRecordPO.getBetAmount());
            userWinLoseMqVO.setLastDayHour(TimeZoneUtils.convertToUtcStartOfHour(dbRecordPO.getSettleTime()));
            userWinLoseMqVO.setLastAgentId(dbRecordPO.getAgentId());
            userWinLoseMqVO.setLastAgentAccount(dbRecordPO.getAgentAcct());
            userWinLoseMqVO.setLastBetAmount(dbRecordPO.getBetAmount());

        }
        userWinLoseMqVO.setSiteCode(po.getSiteCode());
        userWinLoseMqVO.setCurrency(po.getCurrency());
        userWinLoseMqVO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        userWinLoseMqVO.setBetType(po.getExId1());
        orderProcessMqVO.getUserWinLoseUpdateMqList().add(userWinLoseMqVO);
    }

    public void populateCancelSettleMqVO(OrderRecordPO po, OrderRecordPO dbRecordPO, OrderProcessMqVO orderProcessMqVO, boolean isAdd) {
        if (dbRecordPO != null && dbRecordPO.getOrderStatus().equals(OrderStatusEnum.CANCEL.getCode())) {
            return;
        }

        //结算后撤销需要加回打码量
        UserTypingAmountRequestVO requestVO = new UserTypingAmountRequestVO();
        requestVO.setUserAccount(po.getUserAccount());
        requestVO.setUserId(po.getUserId());
        requestVO.setAdjustType(TypingAmountAdjustTypeEnum.BET.getCode());
        requestVO.setOrderNo(po.getOrderId());
        requestVO.setAccountType(po.getAccountType());
        UserTypingAmountVO userTypingAmountVO = userTypingAmountApi.getUserTypingAmountByAccount(po.getSiteCode(), po.getUserAccount());
        if (userTypingAmountVO != null && userTypingAmountVO.getTypingAmount().compareTo(BigDecimal.ZERO) > 0) {
            if (po.getValidAmount().compareTo(dbRecordPO.getValidAmount()) > 0) {
                requestVO.setType(TypingAmountEnum.SUBTRACT.getCode());
                requestVO.setTypingAmount(po.getValidAmount().subtract(dbRecordPO.getValidAmount()));
            } else {
                requestVO.setType(TypingAmountEnum.ADD.getCode());
                requestVO.setTypingAmount(dbRecordPO.getValidAmount().subtract(po.getValidAmount()));
            }
            log.info("玩家注单撤消操作打码量, 帐号: {}, 订单号: {}, 金额: {}", po.getUserAccount(), po.getOrderId(), po.getValidAmount().subtract(dbRecordPO.getValidAmount()));

            if (isAdd) {
                orderProcessMqVO.getTypingAmountList().add(requestVO);
            } else {
                orderProcessMqVO.getTypingAmountUpdateList().add(requestVO);
            }
        }
        boolean isTips = SHPlayTypeEnum.TIPS.getCode().equals(po.getPlayType());
        //会员每日场馆盈亏
        UserVenueWinLossMqVO userVenueWinLossMqVO = new UserVenueWinLossMqVO();
        userVenueWinLossMqVO.setVenueType(po.getVenueType());
        userVenueWinLossMqVO.setCurrency(po.getCurrency());
        userVenueWinLossMqVO.setVenueGameType(po.getThirdGameCode());
        userVenueWinLossMqVO.setBetAmount(po.getBetAmount());
        userVenueWinLossMqVO.setUserAccount(po.getUserAccount());
        userVenueWinLossMqVO.setUserId(po.getUserId());
        userVenueWinLossMqVO.setOrderId(po.getOrderId());
        userVenueWinLossMqVO.setAgentId(po.getAgentId());
        userVenueWinLossMqVO.setAgentAccount(po.getAgentAcct());
        userVenueWinLossMqVO.setVenueCode(po.getVenueCode());
        userVenueWinLossMqVO.setDayHour(DateUtil.beginOfHour(DateUtil.date(po.getSettleTime())).getTime());
        userVenueWinLossMqVO.setValidAmount(BigDecimal.ZERO);
        userVenueWinLossMqVO.setWinLossAmount(BigDecimal.ZERO);
        if (isTips) {
            // 这次
            userVenueWinLossMqVO.setTipsAmount(po.getBetAmount());
            // 上一次
            userVenueWinLossMqVO.setLastTipsAmount(dbRecordPO.getBetAmount());
        } else {
            // 这次
            userVenueWinLossMqVO.setTipsAmount(BigDecimal.ZERO);
            // 上一次
            userVenueWinLossMqVO.setLastTipsAmount(BigDecimal.ZERO);
        }

        userVenueWinLossMqVO.setSiteCode(po.getSiteCode());
        userVenueWinLossMqVO.setAccountType(po.getAccountType());
        if (dbRecordPO == null) {
            userVenueWinLossMqVO.setLastValidBetAmount(BigDecimal.ZERO);
            userVenueWinLossMqVO.setLastBetWinLose(BigDecimal.ZERO);
            userVenueWinLossMqVO.setLastBetAmount(BigDecimal.ZERO);
            userVenueWinLossMqVO.setLastTipsAmount(BigDecimal.ZERO);
        } else {
            userVenueWinLossMqVO.setLastValidBetAmount(dbRecordPO.getValidAmount());
            userVenueWinLossMqVO.setLastBetWinLose(dbRecordPO.getWinLossAmount());
            userVenueWinLossMqVO.setLastBetAmount(dbRecordPO.getBetAmount());
            userVenueWinLossMqVO.setLastDayHour(DateUtil.beginOfHour(DateUtil.date(dbRecordPO.getSettleTime())).getTime());
            userVenueWinLossMqVO.setLastAgentId(dbRecordPO.getAgentId());
            userVenueWinLossMqVO.setLastAgentAccount(dbRecordPO.getAgentAcct());
            userVenueWinLossMqVO.setLastBetAmount(dbRecordPO.getBetAmount());
        }
        userVenueWinLossMqVO.setBetCount(CommonConstant.business_one);
        userVenueWinLossMqVO.setBetType(po.getExId1());
        orderProcessMqVO.getUserVenueWinLossMqList().add(userVenueWinLossMqVO);

        //会员盈亏结算撤销mq
        UserWinLoseMqVO mqVO = new UserWinLoseMqVO();
        mqVO.setUserId(po.getUserId());
        mqVO.setAgentId(po.getAgentId());
        mqVO.setBetAmount(po.getBetAmount());
        mqVO.setDayHourMillis(TimeZoneUtils.convertToUtcStartOfHour(po.getSettleTime()));
        mqVO.setBizCode(CommonConstant.business_two);
        mqVO.setValidBetAmount(po.getValidAmount());
        mqVO.setBetWinLose(po.getWinLossAmount());
        mqVO.setOrderId(po.getOrderId());
        mqVO.setBetType(po.getExId1());
        mqVO.setAccountType(po.getAccountType());
        if (isTips) {
            // 这次
            mqVO.setTipsAmount(po.getBetAmount());
            // 上一次
            mqVO.setLastTipsAmount(dbRecordPO.getBetAmount());
        } else {
            // 这次
            mqVO.setTipsAmount(BigDecimal.ZERO);
            // 上一次
            mqVO.setLastTipsAmount(BigDecimal.ZERO);
        }

        if (dbRecordPO == null) {
            mqVO.setLastValidBetAmount(BigDecimal.ZERO);
            mqVO.setLastBetWinLose(BigDecimal.ZERO);
            mqVO.setLastBetAmount(BigDecimal.ZERO);
            mqVO.setLastTipsAmount(BigDecimal.ZERO);
        } else {
            mqVO.setLastValidBetAmount(dbRecordPO.getValidAmount());
            mqVO.setLastBetWinLose(dbRecordPO.getWinLossAmount());
            mqVO.setLastBetAmount(dbRecordPO.getBetAmount());
            mqVO.setLastDayHour(DateUtil.beginOfHour(DateUtil.date(dbRecordPO.getSettleTime())).getTime());
            mqVO.setLastAgentId(dbRecordPO.getAgentId());
            mqVO.setLastAgentAccount(dbRecordPO.getAgentAcct());

        }
        mqVO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        mqVO.setSiteCode(po.getSiteCode());
        mqVO.setCurrency(po.getCurrency());
        orderProcessMqVO.getUserWinLoseUpdateMqList().add(mqVO);
    }

}
