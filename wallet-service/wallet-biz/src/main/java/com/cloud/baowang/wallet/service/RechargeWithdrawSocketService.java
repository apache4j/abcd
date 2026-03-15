package com.cloud.baowang.wallet.service;


import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.user.api.enums.UserSysMessageEnum;
import com.cloud.baowang.agent.api.vo.WithdrawApplySocketVO;
import com.cloud.baowang.common.core.vo.base.MessageNotifyVO;
import com.cloud.baowang.common.core.vo.base.MessageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.system.api.api.member.BusinessAdminApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.notice.UserNoticeApi;
import com.cloud.baowang.common.core.vo.SystemMessageEnum;
import com.cloud.baowang.user.api.vo.UserSystemMessageConfigVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeTargetAddVO;
import com.cloud.baowang.user.api.vo.UserLanguageVO;
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
public class RechargeWithdrawSocketService {


    private final UserInfoApi userInfoApi;

    private final UserNoticeApi userNoticeApi;

    private final BusinessAdminApi businessAdminApi;


    @Async
    public void sendDepositWithdrawSocket(SystemMessageEnum messageType, String siteCode,
                                          String userId, String userAccount, BigDecimal amount,
                                          String msgTopIc, String currencyCode) {
        UserLanguageVO languageVO = new UserLanguageVO();
        languageVO.setUserType(CommonConstant.business_zero_str);
        languageVO.setMessageType(messageType);
        languageVO.setUserId(userId);
        UserSystemMessageConfigVO messageConfigVO = userInfoApi.getUserLanguage(languageVO);
        if (null != messageConfigVO && StringUtils.hasText(messageConfigVO.getTitle())
                && StringUtils.hasText(messageConfigVO.getContent())) {
            String title = messageConfigVO.getTitle();
            String message = messageConfigVO.getContent();
            amount = amount.setScale(2, RoundingMode.DOWN);
            if (messageType.getCode().equals(SystemMessageEnum.MEMBER_DEPOSIT_SUCCESS.getCode()) || messageType.getCode().equals(SystemMessageEnum.MEMBER_WITHDRAWAL_SUCCESS.getCode())) {
                message = String.format(messageConfigVO.getContent(), userAccount, amount, currencyCode);
            } else if (messageType.getCode().equals(SystemMessageEnum.MEMBER_WITHDRAWAL_FAILED.getCode())) {
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

            vo.setPlatform(CommonConstant.business_one);
            vo.setDeleteState(CommonConstant.business_one);
            vo.setRevokeState(CommonConstant.business_one);
            vo.setMessageContentI18nCode(messageConfigVO.getContentI18nCode());
            vo.setNoticeTitleI18nCode(messageConfigVO.getTitleI18nCode());
            vo.setSystemMessageCode(messageType.getCode());
            Integer businessLine = null;
            if (messageType.getCode().equals(SystemMessageEnum.MEMBER_DEPOSIT_SUCCESS.getCode())) {
                businessLine = UserSysMessageEnum.DEPOSIT.getCode();
            } else if (messageType.getCode().equals(SystemMessageEnum.MEMBER_WITHDRAWAL_SUCCESS.getCode())
                    || messageType.getCode().equals(SystemMessageEnum.MEMBER_WITHDRAWAL_FAILED.getCode())) {
                businessLine = UserSysMessageEnum.WITHDRAWAL.getCode();
            }
            String contentConvertValue = "";
            if (messageType.getCode().equals(SystemMessageEnum.MEMBER_DEPOSIT_SUCCESS.getCode()) || messageType.getCode().equals(SystemMessageEnum.MEMBER_WITHDRAWAL_SUCCESS.getCode())) {
                contentConvertValue = userAccount + "," + amount + "," + currencyCode;
            } else if (messageType.getCode().equals(SystemMessageEnum.MEMBER_WITHDRAWAL_FAILED.getCode())) {
                contentConvertValue = userAccount;
            }
            vo.setContentConvertValue(contentConvertValue);
            userNoticeApi.add(vo);

        }
    }


    public void sendUserWithdrawApply(String siteCode, Long pendingCount, String route) {
       /* ResponseVO<SystemDictConfigRespVO> resp = dictConfigApi.getByCode(DictCodeConfigEnums.WITHDRAW_SOUND_SWITCH.getCode(), siteCode);
        if (resp.isOk()) {
            SystemDictConfigRespVO data = resp.getData();
            //当前站点设置了小铃铛打开,发送消息通知
            if (String.valueOf(EnableStatusEnum.ENABLE.getCode()).equals(data.getConfigParam())) {
                log.info("当前站点:{},推送开关为开启状态,发起提示音推送", siteCode);
                WsMessageMqVO messageMqVO = new WsMessageMqVO();
                messageMqVO.setSiteCode(siteCode);
                //筛选出当前站点满足权限的管理员账号,发送消息todo,与前端沟通后,不需要根据权限判断发送消息,改为前端处理逻辑
                *//*List<String> userIds = businessAdminApi.selectUserBySiteCodeAndApiUrl(siteCode,menuKey);
                if (CollectionUtil.isNotEmpty(userIds)) {
                    log.info("获取到当前站点:{},拥有权限的管理员列表:{},开始发送websocket消息",siteCode, JSON.toJSONString(userIds));
                    messageMqVO.setUidList(userIds);
                    messageMqVO.setClientTypeEnum(ClientTypeEnum.SITE);
                    messageMqVO.setMessage(new WSBaseResp<>(WSSubscribeEnum.USER_WITHDRAW_APPLY.getTopic(),
                            ResponseVO.success("")));
                    KafkaUtil.send(WsMessageConstant.WS_MESSAGE_BROADCAST_TOPIC, messageMqVO);
                }*//*

                messageMqVO.setUidList(null);
                messageMqVO.setClientTypeEnum(ClientTypeEnum.SITE);
                messageMqVO.setMessage(new WSBaseResp<>(WSSubscribeEnum.USER_WITHDRAW_APPLY.getTopic(),
                        ResponseVO.success(WithdrawApplySocketVO.builder().route(route).pendingCount(pendingCount))));
                KafkaUtil.send(WsMessageConstant.WS_MESSAGE_BROADCAST_TOPIC, messageMqVO);
            }

        }*/
        try {
            WsMessageMqVO messageMqVO = new WsMessageMqVO();
            messageMqVO.setSiteCode(siteCode);
            List<String> userIds = businessAdminApi.getUserIdsBySiteCode(siteCode);
            messageMqVO.setUidList(userIds);
            messageMqVO.setClientTypeEnum(ClientTypeEnum.SITE);
            WithdrawApplySocketVO withdrawApplySocketVO = new WithdrawApplySocketVO();
            withdrawApplySocketVO.setPendingCount(pendingCount);
            withdrawApplySocketVO.setRoute(route);
            messageMqVO.setMessage(new WSBaseResp<>(WSSubscribeEnum.USER_WITHDRAW_APPLY.getTopic(),
                    ResponseVO.success(withdrawApplySocketVO)));
            KafkaUtil.send(WsMessageConstant.WS_MESSAGE_BROADCAST_TOPIC, messageMqVO);
        } catch (Exception e) {
            log.error("发送消息推送失败:{}", e.getMessage());
        }
    }
}
