package com.cloud.baowang.agent.service;


import com.alibaba.fastjson.JSONObject;
import com.cloud.baowang.agent.api.vo.AgentUserLanguageVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.agent.api.vo.WithdrawApplySocketVO;
import com.cloud.baowang.common.core.vo.base.MessageNotifyVO;
import com.cloud.baowang.common.core.vo.base.MessageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.system.api.api.member.BusinessAdminApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.notice.UserNoticeApi;
import com.cloud.baowang.common.core.vo.SystemMessageEnum;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeTargetAddVO;
import com.cloud.baowang.agent.api.vo.AgentSystemMessageConfigVO;
import com.cloud.baowang.websocket.api.constants.WsMessageConstant;
import com.cloud.baowang.websocket.api.enums.ClientTypeEnum;
import com.cloud.baowang.websocket.api.enums.WSSubscribeEnum;
import com.cloud.baowang.websocket.api.vo.WSBaseResp;
import com.cloud.baowang.websocket.api.vo.WsMessageMqVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgentRechargeWithdrawSocketService {


    private final UserInfoApi userInfoApi;

    private final UserNoticeApi userNoticeApi;

    private final AgentCommonService agentCommonService;
    private final BusinessAdminApi businessAdminApi;


    @Async
    public void sendAgentDepositWithdrawSocket(SystemMessageEnum messageType, String siteCode,
                                               String agentId, BigDecimal amount,
                                               String msgTopIc, String currencyCode) {
        log.info("发送代理充提消息开始,类型:{}", messageType.getName());
        AgentUserLanguageVO languageVO = new AgentUserLanguageVO();
        languageVO.setUserType(CommonConstant.business_one_str);
        languageVO.setMessageType(messageType);
        languageVO.setUserId(agentId);
        AgentSystemMessageConfigVO messageConfigVO = agentCommonService.getAgentLanguage(languageVO);
        if (null != messageConfigVO && StringUtils.hasText(messageConfigVO.getTitle())
                && StringUtils.hasText(messageConfigVO.getContent())) {
            String title = messageConfigVO.getTitle();
            String message = messageConfigVO.getContent();
            amount = amount.setScale(2, RoundingMode.DOWN);
            if (messageType.getCode().equals(SystemMessageEnum.AGENT_DEPOSIT_SUCCESS.getCode()) || messageType.getCode().equals(SystemMessageEnum.AGENT_WITHDRAWAL_SUCCESS.getCode())) {
                message = String.format(messageConfigVO.getContent(), amount, currencyCode);
            }
            MessageNotifyVO notifyVO = new MessageNotifyVO();
            notifyVO.setUserIds(List.of(agentId));
            notifyVO.setSiteCode(siteCode);
            MessageVO messageVO = MessageVO.builder().title(title).message(message).build();
            notifyVO.setMessageVO(messageVO);
            notifyVO.setMsgTopic(msgTopIc);


            // ws信息推送
            List<String> userIds = notifyVO.getUserIds();
            WsMessageMqVO messageMqVO = new WsMessageMqVO();
            messageMqVO.setSiteCode(notifyVO.getSiteCode());
            messageMqVO.setUidList(userIds);
            messageMqVO.setClientTypeEnum(ClientTypeEnum.AGENT);
            messageMqVO.setMessage(new WSBaseResp<>(notifyVO.getMsgTopic(), notifyVO.getMessageVO()));
            KafkaUtil.send(WsMessageConstant.WS_MESSAGE_BROADCAST_TOPIC, messageMqVO);
            log.info("发送代理充提消息开始,发送完成：{}", JSONObject.toJSONString(messageMqVO));
            //保存记录
            UserNoticeTargetAddVO vo = new UserNoticeTargetAddVO();
            vo.setUserId(userIds.get(0));
//            vo.setNoticeId(messageConfigVO.getId());
            vo.setNoticeType(CommonConstant.business_four);
            vo.setReadState(CommonConstant.business_zero);
            vo.setPlatform(CommonConstant.business_two);
            vo.setDeleteState(CommonConstant.business_one);
            vo.setRevokeState(CommonConstant.business_one);
            vo.setMessageContentI18nCode(messageConfigVO.getContentI18nCode());
            vo.setNoticeTitleI18nCode(messageConfigVO.getTitleI18nCode());
            vo.setSystemMessageCode(messageType.getCode());
            String contentConvertValue = "";
            if (messageType.getCode().equals(SystemMessageEnum.AGENT_DEPOSIT_SUCCESS.getCode()) || messageType.getCode().equals(SystemMessageEnum.AGENT_WITHDRAWAL_SUCCESS.getCode())) {
                contentConvertValue = amount + "," + currencyCode;
            }
            vo.setContentConvertValue(contentConvertValue);
            userNoticeApi.add(vo);
            log.info("发送代理充提消息开始,保存完成：{}", JSONObject.toJSONString(vo));
        }
    }


    public void sendUserWithdrawApply(String siteCode, Long pendingCount, String route) {
        WsMessageMqVO messageMqVO = new WsMessageMqVO();
        messageMqVO.setSiteCode(siteCode);
        List<String> userIds = businessAdminApi.getUserIdsBySiteCode(siteCode);
        messageMqVO.setUidList(userIds);
        messageMqVO.setClientTypeEnum(ClientTypeEnum.SITE);
        WithdrawApplySocketVO withdrawApplySocketVO = new WithdrawApplySocketVO();
        withdrawApplySocketVO.setRoute(route);
        withdrawApplySocketVO.setPendingCount(pendingCount);
        messageMqVO.setMessage(new WSBaseResp<>(WSSubscribeEnum.AGENT_WITHDRAW_APPLY.getTopic(),
                ResponseVO.success(withdrawApplySocketVO)));
        KafkaUtil.send(WsMessageConstant.WS_MESSAGE_BROADCAST_TOPIC, messageMqVO);
    }
}
