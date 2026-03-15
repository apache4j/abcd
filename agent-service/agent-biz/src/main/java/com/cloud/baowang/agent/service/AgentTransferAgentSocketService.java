package com.cloud.baowang.agent.service;


import com.alibaba.fastjson.JSONObject;
import com.cloud.baowang.agent.api.vo.AgentUserLanguageVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.agent.api.vo.AgentSystemMessageConfigVO;
import com.cloud.baowang.common.core.vo.SystemMessageEnum;
import com.cloud.baowang.common.core.vo.base.MessageNotifyVO;
import com.cloud.baowang.common.core.vo.base.MessageVO;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.user.api.api.notice.UserNoticeApi;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeTargetAddVO;
import com.cloud.baowang.websocket.api.constants.WsMessageConstant;
import com.cloud.baowang.websocket.api.enums.ClientTypeEnum;
import com.cloud.baowang.websocket.api.vo.WSBaseResp;
import com.cloud.baowang.websocket.api.vo.WsMessageMqVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgentTransferAgentSocketService {


    private final UserNoticeApi userNoticeApi;

    private final AgentCommonService agentCommonService;


    @Async
    public void sendAgentDepositWithdrawSocket(SystemMessageEnum messageType, String siteCode,
                                               String agentId, String userAccount,
                                               String msgTopIc) {
        log.info("转代,溢出消息发送,类型:{}", messageType.getName());
        AgentUserLanguageVO languageVO = new AgentUserLanguageVO();
        languageVO.setUserType(CommonConstant.business_one_str);
        languageVO.setMessageType(messageType);
        languageVO.setUserId(agentId);
        AgentSystemMessageConfigVO messageConfigVO = agentCommonService.getAgentLanguage(languageVO);
        if (null != messageConfigVO && StringUtils.hasText(messageConfigVO.getTitle())
                && StringUtils.hasText(messageConfigVO.getContent())) {
            String title = messageConfigVO.getTitle();
            String message = messageConfigVO.getContent();

            if (messageType.getCode().equals(SystemMessageEnum.AGENT_MEMBER_OVERFLOW_SUCCESS.getCode()) || messageType.getCode().equals(SystemMessageEnum.AGENT_MEMBER_TRANSFER_SUCCESS.getCode())) {
                message = String.format(messageConfigVO.getContent(), userAccount);
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
            //保存记录
            UserNoticeTargetAddVO vo = new UserNoticeTargetAddVO();
            vo.setUserId(agentId);
            vo.setNoticeType(CommonConstant.business_four);
            vo.setReadState(CommonConstant.business_zero);
            vo.setPlatform(CommonConstant.business_two);
            vo.setDeleteState(CommonConstant.business_one);
            vo.setRevokeState(CommonConstant.business_one);
            vo.setMessageContentI18nCode(messageConfigVO.getContentI18nCode());
            vo.setNoticeTitleI18nCode(messageConfigVO.getTitleI18nCode());
            vo.setSystemMessageCode(messageType.getCode());
            String contentConvertValue = "";
            if (messageType.getCode().equals(SystemMessageEnum.AGENT_MEMBER_OVERFLOW_SUCCESS.getCode()) || messageType.getCode().equals(SystemMessageEnum.AGENT_MEMBER_TRANSFER_SUCCESS.getCode())) {
                contentConvertValue = userAccount;
            }
            vo.setContentConvertValue(contentConvertValue);
            userNoticeApi.add(vo);
            log.info("发送转代,溢出通知完成,参数：{}", JSONObject.toJSONString(vo));
        }
    }


}
