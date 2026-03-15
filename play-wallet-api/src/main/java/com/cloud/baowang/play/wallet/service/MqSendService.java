package com.cloud.baowang.play.wallet.service;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.user.api.enums.UserTypeEnum;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountMqVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.play.api.enums.order.OrderStatusEnum;
import com.cloud.baowang.play.api.vo.order.GeOrderListVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.play.wallet.vo.mq.MqRequest;
import com.cloud.baowang.wallet.api.api.UserTypingAmountApi;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountEnum;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.GetUserTypingAmountVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingAmountVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: fangfei
 * @createTime: 2024/05/31 15:31
 * @description:
 */
@Slf4j
@Service
@AllArgsConstructor
public class MqSendService {

    private final UserTypingAmountApi userTypingAmountApi;
    private final OrderRecordApi orderRecordApi;

    /**
     * 统一处理入口
     * @param requestList
     */
    public void mqHandler(List<MqRequest> requestList) {
        List<MqRequest>  settleList = new ArrayList<>();
        List<MqRequest>  reSettleList = new ArrayList<>();
        List<MqRequest>  cancelSettleList = new ArrayList<>();
        for (MqRequest request : requestList) {
            if (ClassifyEnum.SETTLED.getCode().equals(request.getOrderClassify())) {
                settleList.add(request);
            } else if (ClassifyEnum.RESETTLED.getCode().equals(request.getOrderClassify())){
                reSettleList.add(request);
            } else if (ClassifyEnum.CANCEL.getCode().equals(request.getOrderClassify())){
                cancelSettleList.add(request);
            }
        }

        sendSettleMq(settleList);
        sendReSettleMq(reSettleList);
        sendCancelMq(cancelSettleList);
    }

    /**
     * 结算mq
     * @param requestList
     */
    public void sendSettleMq(List<MqRequest> requestList) {
        if (ObjectUtil.isEmpty(requestList)) return;
        //打码量处理
        List<UserTypingAmountRequestVO> userTypingAmountRequestVOS = new ArrayList<>();
        for (MqRequest request : requestList) {
            if (!request.getAccountType().equals(UserTypeEnum.FORMAL.getCode())) {
                continue;
            }

            UserTypingAmountRequestVO requestVO = new UserTypingAmountRequestVO();
            requestVO.setUserAccount(request.getUserAccount());
            requestVO.setType(TypingAmountEnum.SUBTRACT.getCode());
            requestVO.setAdjustType(TypingAmountAdjustTypeEnum.BET.getCode());
            requestVO.setOrderNo(request.getOrderId());
            requestVO.setTypingAmount(request.getValidAmount());
            userTypingAmountRequestVOS.add(requestVO);
        }

        if (ObjectUtil.isNotEmpty(userTypingAmountRequestVOS)) {
            UserTypingAmountMqVO userTypingAmountMqVO = UserTypingAmountMqVO.builder().userTypingAmountRequestVOList(userTypingAmountRequestVOS).build();
            KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC,userTypingAmountMqVO);
        }

    }

    /**
     * 重结算mq
     * @param requestList
     */
    public void sendReSettleMq(List<MqRequest> requestList) {
        if (ObjectUtil.isEmpty(requestList)) return;
        //打码量处理
        List<UserTypingAmountRequestVO> userTypingAmountRequestVOS = new ArrayList<>();
        List<String> userAccountList  = requestList.stream().map(MqRequest::getUserAccount).toList();
        List<String> orderIdList = requestList.stream().map(MqRequest::getOrderId).toList();
        GetUserTypingAmountVO vo = GetUserTypingAmountVO.builder().userAccountList(userAccountList).build();
        List<UserTypingAmountVO> userTypingAmountList = userTypingAmountApi.getUserTypingAmountListByAccounts(vo);
        Map<String, UserTypingAmountVO> userTypingMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(userTypingAmountList)) {
            userTypingMap = userTypingAmountList.stream()
                    .collect(Collectors.toMap(UserTypingAmountVO::getUserAccount, p -> p, (k1, k2) -> k2));
        }

        GeOrderListVO orderListVO = GeOrderListVO.builder().orderIdList(orderIdList).build();
        List<OrderRecordVO> orderRecordList = orderRecordApi.getOrderListByOrderIds(orderListVO);
        Map<String, OrderRecordVO> orderMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(orderRecordList)) {
            orderMap = orderRecordList.stream()
                    .collect(Collectors.toMap(OrderRecordVO::getOrderId, p -> p, (k1, k2) -> k2));
        }

        for (MqRequest request : requestList) {
            if (!request.getAccountType().equals(UserTypeEnum.FORMAL.getCode())) {
                return;
            }

            OrderRecordVO orderRecordVO = orderMap.get(request.getOrderId());
            if (orderRecordVO == null) {
                //说明首次拉过来就是重结算，按首次结算处理
                UserTypingAmountRequestVO requestVO = new UserTypingAmountRequestVO();
                requestVO.setUserAccount(request.getUserAccount());
                requestVO.setType(TypingAmountEnum.SUBTRACT.getCode());
                requestVO.setAdjustType(TypingAmountAdjustTypeEnum.BET.getCode());
                requestVO.setOrderNo(request.getOrderId());
                requestVO.setTypingAmount(request.getValidAmount());
                userTypingAmountRequestVOS.add(requestVO);
            } else {
                //重结算需要重算打码量
                UserTypingAmountVO userTypingAmountVO = userTypingMap.get(request.getUserAccount());
                if (userTypingAmountVO != null && userTypingAmountVO.getTypingAmount().compareTo(BigDecimal.ZERO) > 0) {
                    UserTypingAmountRequestVO requestVO = new UserTypingAmountRequestVO();
                    requestVO.setUserAccount(request.getUserAccount());
                    requestVO.setOrderNo(request.getOrderId());
                    requestVO.setAdjustType(TypingAmountAdjustTypeEnum.BET.getCode());
                    if (request.getValidAmount().compareTo(orderRecordVO.getValidAmount()) > 0) {
                        requestVO.setType(TypingAmountEnum.SUBTRACT.getCode());
                        requestVO.setTypingAmount(request.getValidAmount().subtract(orderRecordVO.getValidAmount()));
                    } else {
                        requestVO.setType(TypingAmountEnum.ADD.getCode());
                        requestVO.setTypingAmount(orderRecordVO.getValidAmount().subtract(request.getValidAmount()));
                    }

                    userTypingAmountRequestVOS.add(requestVO);
                }
            }

            if (ObjectUtil.isNotEmpty(userTypingAmountRequestVOS)) {
                UserTypingAmountMqVO userTypingAmountMqVO = UserTypingAmountMqVO.builder().userTypingAmountRequestVOList(userTypingAmountRequestVOS).build();
                KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC,userTypingAmountMqVO);
            }

        }
    }

    /**
     * 撤销mq
     * @param requestList
     */
    public void sendCancelMq(List<MqRequest> requestList) {
        if (ObjectUtil.isEmpty(requestList)) return;
        List<UserTypingAmountRequestVO> userTypingAmountRequestVOS = new ArrayList<>();
        List<String> orderIdList = requestList.stream().map(MqRequest::getOrderId).toList();
        List<String> userAccountList  = requestList.stream().map(MqRequest::getUserAccount).toList();

        GetUserTypingAmountVO vo = GetUserTypingAmountVO.builder().userAccountList(userAccountList).build();
        List<UserTypingAmountVO> userTypingAmountList = userTypingAmountApi.getUserTypingAmountListByAccounts(vo);
        Map<String, UserTypingAmountVO> userTypingMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(userTypingAmountList)) {
            userTypingMap = userTypingAmountList.stream()
                    .collect(Collectors.toMap(UserTypingAmountVO::getUserAccount, p -> p, (k1, k2) -> k2));
        }
        GeOrderListVO orderListVO = GeOrderListVO.builder().orderIdList(orderIdList).build();
        List<OrderRecordVO> orderRecordList = orderRecordApi.getOrderListByOrderIds(orderListVO);
        Map<String, OrderRecordVO> orderMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(orderRecordList)) {
            orderMap = orderRecordList.stream()
                    .collect(Collectors.toMap(OrderRecordVO::getOrderId, p -> p, (k1, k2) -> k2));
        }
        for (MqRequest request : requestList) {
            if (!request.getAccountType().equals(UserTypeEnum.FORMAL.getCode())) {
                log.info("{}会员不是正式或商务会员：{}", request.getUserAccount(), request.getOrderId());
                return;
            }
            OrderRecordVO recordVO = orderMap.get(request.getOrderId());
            if (recordVO.getOrderStatus().equals(OrderStatusEnum.CANCEL.getCode())) {
                return;
            }

            if (ClassifyEnum.SETTLED.getCode().equals(recordVO.getOrderClassify()) ||
                    ClassifyEnum.RESETTLED.getCode().equals(recordVO.getOrderClassify())) {
                //结算后撤销
                //结算后撤销需要加回打码量
                UserTypingAmountRequestVO requestVO = new UserTypingAmountRequestVO();
                requestVO.setUserAccount(request.getUserAccount());
                requestVO.setAdjustType(TypingAmountAdjustTypeEnum.BET.getCode());
                requestVO.setOrderNo(request.getOrderId());

                UserTypingAmountVO userTypingAmountVO = userTypingMap.get(request.getUserAccount());
                if (userTypingAmountVO != null && userTypingAmountVO.getTypingAmount().compareTo(BigDecimal.ZERO) > 0) {
                    if (request.getValidAmount().compareTo(recordVO.getValidAmount()) > 0) {
                        requestVO.setType(TypingAmountEnum.SUBTRACT.getCode());
                        requestVO.setTypingAmount(request.getValidAmount().subtract(recordVO.getValidAmount()));
                    } else {
                        requestVO.setType(TypingAmountEnum.ADD.getCode());
                        requestVO.setTypingAmount(recordVO.getValidAmount().subtract(request.getValidAmount()));
                    }

                    log.info("玩家注单撤消操作打码量, 帐号: {}, 订单号: {}, 金额: {}", request.getUserAccount(),
                            request.getOrderId(), request.getValidAmount().subtract(recordVO.getValidAmount()));
                    userTypingAmountRequestVOS.add(requestVO);
                }
            } else {
                //todo
            }


        }
        if (ObjectUtil.isNotEmpty(userTypingAmountRequestVOS)) {
            UserTypingAmountMqVO userTypingAmountMqVO = UserTypingAmountMqVO.builder().userTypingAmountRequestVOList(userTypingAmountRequestVOS).build();
            KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC,userTypingAmountMqVO);
        }
    }

}
