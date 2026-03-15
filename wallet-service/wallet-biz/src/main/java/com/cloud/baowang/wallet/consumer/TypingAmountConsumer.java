package com.cloud.baowang.wallet.consumer;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountMqVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountEnum;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.service.SyncCommonService;
import com.cloud.baowang.wallet.service.UserTypingAmountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Component
@AllArgsConstructor
public class TypingAmountConsumer {

    private final UserTypingAmountService userTypingAmountService;

    private final SyncCommonService syncCommonService;

    private final UserInfoApi userInfoApi;


    @KafkaListener(topics = TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC, groupId = GroupConstants.PUSH_TYPING_AMOUNT_GROUP)
    public void betOrderMessage(UserTypingAmountMqVO userTypingAmountMqVO, Acknowledgment ackItem) {
        if (null == userTypingAmountMqVO) {
            log.error("会员打码量批量-MQ队列-参数不能为空");
            return;
        }
        log.info("打码量消息,the msg: {} by kafka,MQ消息id:{}", JSONObject.toJSONString(userTypingAmountMqVO), userTypingAmountMqVO.getMsgId());
        long start = System.currentTimeMillis();
        try {
            List<UserTypingAmountRequestVO> voList = userTypingAmountMqVO.getUserTypingAmountRequestVOList();
            syncCommonService.saveUserTypingAmountMqMessage(JSON.toJSONString(voList));
            Map<String, List<UserTypingAmountRequestVO>> groupMap = voList.stream()
                    .collect(Collectors.groupingBy(UserTypingAmountRequestVO::getUserId));

            List<String> userIds = new ArrayList<>(groupMap.keySet());
            List<UserInfoVO> userInfoVOS = userInfoApi.getUserInfoByUserIds(userIds);
            Map<String, UserInfoVO> userInfoMap = userInfoVOS.stream()
                    .collect(Collectors.toMap(UserInfoVO::getUserId, userInfoVO -> userInfoVO));

            for (Map.Entry<String, List<UserTypingAmountRequestVO>> entry : groupMap.entrySet()) {
                String userId = entry.getKey();

                UserTypingAmountRequestVO vo = new UserTypingAmountRequestVO();
                vo.setUserId(userId);
                vo.setMsgId(userTypingAmountMqVO.getMsgId());
                List<UserTypingAmountRequestVO> list = entry.getValue();
                UserTypingAmountRequestVO userTypingAmountRequestVO = list.get(0);
                vo.setOrderNo(userTypingAmountRequestVO.getOrderNo());
                vo.setUserAccount(userTypingAmountRequestVO.getUserAccount());
                vo.setSiteCode(userTypingAmountRequestVO.getSiteCode());
                if (StringUtils.isNotBlank(userTypingAmountRequestVO.getType())) {
                    vo.setType(userTypingAmountRequestVO.getType());
                } else {
                    vo.setType(TypingAmountEnum.SUBTRACT.getCode());
                }
                vo.setIsClear(userTypingAmountRequestVO.getIsClear());
                vo.setOnlyActivity(userTypingAmountRequestVO.getOnlyActivity());
                vo.setTypingList(list);
                UserInfoVO userInfoVO = userInfoMap.get(userId);
                boolean res = userTypingAmountService.addUserTypingAmount(vo,userInfoVO);
                if (res) {
                    log.info("打码量消息,会员:{},打码量批量-MQ队列-------------------------------执行success,耗时{}毫秒", userId, System.currentTimeMillis() - start);
                } else {
                    log.error("打码量消息,会员:{},打码量批量-MQ队列-------------------------------抢锁fail,耗时{}毫秒", userId, System.currentTimeMillis() - start);
                }
            }

        } catch (Exception e) {
            log.info("打码量消息,会员打码量批量-MQ队列执行报错，报错信息{}", e.getMessage());
           // throw new BaowangDefaultException("会员打码量批量-MQ队列执行报错e");
        } finally {
            log.info("打码量消息,MQ队列-消息id:{},整体耗时:{}毫秒", userTypingAmountMqVO.getMsgId(), System.currentTimeMillis() - start);
            ackItem.acknowledge();
        }
    }

}
