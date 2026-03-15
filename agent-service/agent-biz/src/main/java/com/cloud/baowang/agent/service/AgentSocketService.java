package com.cloud.baowang.agent.service;


import com.cloud.baowang.agent.api.vo.AgentUserLanguageVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.vo.base.MessageNotifyVO;
import com.cloud.baowang.common.core.vo.base.MessageVO;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.user.api.api.notice.UserNoticeApi;
import com.cloud.baowang.common.core.vo.SystemMessageEnum;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeTargetAddVO;
import com.cloud.baowang.agent.api.vo.AgentSystemMessageConfigVO;
import com.cloud.baowang.websocket.api.constants.WsMessageConstant;
import com.cloud.baowang.websocket.api.vo.WSBaseResp;
import com.cloud.baowang.websocket.api.vo.WsMessageMqVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgentSocketService {


    private final AgentInfoService agentInfoService;

    private final UserNoticeApi userNoticeApi;

    private final AgentCommonService agentCommonService;


    /**
     * 代理转账通知
     * @param messageType
     * @param siteCode
     * @param agentId
     * @param msgTopIc
     */
    @Async
    public void sendAgentTransferSocket(SystemMessageEnum messageType,String siteCode,
                                   String agentId, String msgTopIc,
                                   Object... params){
        AgentUserLanguageVO languageVO = new AgentUserLanguageVO();
        languageVO.setUserType(CommonConstant.business_one_str);
        languageVO.setMessageType(messageType);
        languageVO.setUserId(agentId);
        AgentSystemMessageConfigVO messageConfigVO = agentCommonService.getAgentLanguage(languageVO);
        if(null != messageConfigVO && StringUtils.hasText(messageConfigVO.getTitle())
            && StringUtils.hasText(messageConfigVO.getContent())){
            String title = messageConfigVO.getTitle();
            String message =  String.format(messageConfigVO.getContent(), params);
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
            if (params.length > 0 ){
                contentConvertValue = Arrays.stream(params).map(String::valueOf).collect(Collectors.joining(CommonConstant.COMMA));
            }
            vo.setContentConvertValue(contentConvertValue);
            userNoticeApi.add(vo);

        }
    }


}
