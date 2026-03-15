/*
package com.cloud.baowang.agent.service.ws;


import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.user.api.enums.UserSysMessageEnum;
import com.cloud.baowang.common.core.vo.base.MessageNotifyVO;
import com.cloud.baowang.common.core.vo.base.MessageVO;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.notice.UserNoticeApi;
import com.cloud.baowang.common.core.vo.SystemMessageEnum;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeTargetAddVO;
import com.cloud.baowang.agent.api.vo.SystemMessageConfigVO;
import com.cloud.baowang.user.api.vo.UserLanguageVO;
import com.cloud.baowang.websocket.api.constants.WsMessageConstant;
import com.cloud.baowang.websocket.api.vo.WSBaseResp;
import com.cloud.baowang.websocket.api.vo.WsMessageMqVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendMsgSocketService {

    private final AgentInfoRepository agentInfoRepository;

    private final UserNoticeApi userNoticeApi;


    */
/**
     * 转代-溢出审核通过,通知代理
     * @param messageType 消息类型
     * @param siteCode
     * @param agentId
     * @param agentAccount
     * @param msgTopIc
     *//*

    @Async
    public void sendDepositWithdrawSocket(SystemMessageEnum messageType,String siteCode, String agentId,String agentAccount, String msgTopIc){
        UserLanguageVO languageVO = new UserLanguageVO();
        AgentLang
        languageVO.setUserType(CommonConstant.business_one_str);
        languageVO.setMessageType(messageType);
        languageVO.setUserId(userId);
        SystemMessageConfigVO messageConfigVO = agentInfoRepository.getLanguage(languageVO);
        if(null != messageConfigVO && StringUtils.hasText(messageConfigVO.getTitle())
            && StringUtils.hasText(messageConfigVO.getContent())){
            String title = messageConfigVO.getTitle();
            String message = messageConfigVO.getContent();
            amount = amount.setScale(2);
            if(messageType.getCode().equals(SystemMessageEnum.MEMBER_DEPOSIT_SUCCESS.getCode()) || messageType.getCode().equals(SystemMessageEnum.MEMBER_WITHDRAWAL_SUCCESS.getCode())){
                message = String.format(messageConfigVO.getContent(), userAccount, amount,currencyCode);
            }else if(messageType.getCode().equals(SystemMessageEnum.MEMBER_WITHDRAWAL_FAILED.getCode())){
                message = String.format(messageConfigVO.getContent(), userAccount);
            }
            MessageNotifyVO notifyVO = new MessageNotifyVO();
            notifyVO.setUserIds(List.of(userId));
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
            vo.setUserId(userIds.get(0));
//            vo.setNoticeId(messageConfigVO.getId());
            vo.setNoticeType(CommonConstant.business_four);
            vo.setReadState(CommonConstant.business_zero);
            vo.setMessageType(UserSysMessageEnum.REGISTRATION.getCode());
            vo.setPlatform(CommonConstant.business_one);
            vo.setDeleteState(CommonConstant.business_one);
            vo.setRevokeState(CommonConstant.business_one);
            vo.setMessageContentI18nCode(messageConfigVO.getContentI18nCode());
            vo.setNoticeTitleI18nCode(messageConfigVO.getTitleI18nCode());
            vo.setSystemMessageCode(messageType.getCode());
            Integer businessLine = null;
            if(messageType.getCode().equals(SystemMessageEnum.MEMBER_DEPOSIT_SUCCESS.getCode())){
                businessLine = UserSysMessageEnum.DEPOSIT.getCode();
            } else if(messageType.getCode().equals(SystemMessageEnum.MEMBER_WITHDRAWAL_SUCCESS.getCode())
                || messageType.getCode().equals(SystemMessageEnum.MEMBER_WITHDRAWAL_FAILED.getCode())){
                businessLine = UserSysMessageEnum.WITHDRAWAL.getCode();
            }
            String contentConvertValue = "";
            if(messageType.getCode().equals(SystemMessageEnum.MEMBER_DEPOSIT_SUCCESS.getCode()) || messageType.getCode().equals(SystemMessageEnum.MEMBER_WITHDRAWAL_SUCCESS.getCode())){
                contentConvertValue = userAccount+"," +amount+","+currencyCode;
            }else if(messageType.getCode().equals(SystemMessageEnum.MEMBER_WITHDRAWAL_FAILED.getCode())){
                contentConvertValue = userAccount;
            }
            vo.setContentConvertValue(contentConvertValue);
            vo.setBusinessLine(businessLine);
            userNoticeApi.add(vo);

        }
    }


}
*/
