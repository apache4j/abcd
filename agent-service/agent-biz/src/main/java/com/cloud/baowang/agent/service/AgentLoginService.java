package com.cloud.baowang.agent.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloud.baowang.agent.api.vo.AgentUserLanguageVO;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginParamVO;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginRecordInsertVO;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginUpdateVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.security.AgentResetPasswordVO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.util.AgentServerUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.agent.api.enums.AgentTypeEnum;
import com.cloud.baowang.common.core.enums.LoginTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.common.core.vo.base.MessageNotifyVO;
import com.cloud.baowang.common.core.vo.base.MessageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.core.vo.IPRespVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.utils.IpAPICoUtils;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.notice.UserNoticeApi;
import com.cloud.baowang.common.core.vo.SystemMessageEnum;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeTargetAddVO;
import com.cloud.baowang.agent.api.vo.AgentSystemMessageConfigVO;
import com.cloud.baowang.websocket.api.constants.WsMessageConstant;
import com.cloud.baowang.websocket.api.enums.WSSubscribeEnum;
import com.cloud.baowang.websocket.api.vo.WSBaseResp;
import com.cloud.baowang.websocket.api.vo.WsMessageMqVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.cloud.baowang.common.core.constants.CommonConstant.X_CUSTOM;

/**
 * @author: fangfei
 * @createTime: 2024/06/17 22:40
 * @description:
 */
@AllArgsConstructor
@Slf4j
@Service
public class AgentLoginService {

    private final AgentInfoService agentInfoService;
    private final AgentLoginRecordService agentLoginRecordService;
    private final SiteApi siteApi;
    private final UserNoticeApi userNoticeApi;
    private final AgentCommonService agentCommonService;

    public AgentInfoVO agentLogin(AgentLoginParamVO agentLoginParamVO) {
        AgentInfoPO agentInfoPO = agentInfoService.getAgentInfoPO(agentLoginParamVO.getSiteCode(),agentLoginParamVO.getAgentAccount());
        if (agentInfoPO == null) {
            agentInfoPO = new AgentInfoPO();
            agentInfoPO.setAgentAccount(agentLoginParamVO.getAgentAccount());
            agentInfoPO.setAgentType(Integer.parseInt(AgentTypeEnum.FORMAL.getCode()));
            agentLoginLog(agentInfoPO, agentLoginParamVO, LoginTypeEnum.FAIL.getCode().toString(), ResultCode.AGENT_ACCOUNT_NOT_EXIST.getDesc());
            throw new BaowangDefaultException(ResultCode.AGENT_ACCOUNT_NOT_EXIST);
        }

        String salt = agentInfoPO.getSalt();
        // 密码加密
        String encryptPassword = AgentServerUtil.getEncryptPassword(agentLoginParamVO.getPassword(), salt);

        if (!encryptPassword.equals(agentInfoPO.getAgentPassword())) {
            agentLoginLog(agentInfoPO, agentLoginParamVO, LoginTypeEnum.FAIL.getCode().toString(), ResultCode.USER_LOGIN_ERROR.getDesc());
            throw new BaowangDefaultException(ResultCode.USER_LOGIN_ERROR);
        }

        //是否是首次登录，在更新前判断
        boolean isFirstLogin = agentInfoPO.getLastLoginTime() == null;

        Long loginTime = System.currentTimeMillis();
        AgentLoginUpdateVO agentLoginUpdateVO = new AgentLoginUpdateVO();
        agentLoginUpdateVO.setAgentId(agentInfoPO.getAgentId());
        agentLoginUpdateVO.setAgentAccount(agentLoginParamVO.getAgentAccount());
        agentLoginUpdateVO.setLastLoginTime(loginTime);
        agentLoginUpdateVO.setUpdatedTime(loginTime);
        agentLoginUpdateVO.setOfflineDays(0);

        agentInfoService.updateAgentLoginInfo(agentLoginUpdateVO);

        AgentInfoVO infoVO = new AgentInfoVO();
        BeanUtils.copyProperties(agentInfoPO, infoVO);

        infoVO.setIsPayPassword(ObjectUtil.isNotEmpty(agentInfoPO.getPayPassword())
                ? BigDecimal.ZERO.intValue() : BigDecimal.ONE.intValue());

        agentLoginLog(agentInfoPO, agentLoginParamVO, LoginTypeEnum.SUCCESS.getCode().toString(), ResultCode.LOGIN_SUCCESS.getDesc());

        //首次登录发送消息通知
        if (isFirstLogin) {
            sendWsMessage(infoVO);
        }

        return infoVO;
    }

    public void saveLoginLog(AgentLoginParamVO agentLoginParamVO, String loginStatus, String remark) {
        AgentInfoPO agentInfoPO = agentInfoService.getAgentInfoPO(agentLoginParamVO.getSiteCode(),agentLoginParamVO.getAgentAccount());
        if (agentInfoPO != null) {
            agentLoginLog(agentInfoPO, agentLoginParamVO, loginStatus, remark);
        }
    }

    public String agentLoginLog(AgentInfoPO agentInfoPO,  AgentLoginParamVO agentLoginParamVO, String loginStatus, String remark) {
        HttpServletRequest request = ServletUtil.getRequest();
        String ip = CurrReqUtils.getReqIp();
       // IPInfo ipInfo = IPInfoUtils.getIpInfo(ip);
        IPRespVO ipApiVO= IpAPICoUtils.getIp(ip);
        Integer deviceType = CurrReqUtils.getReqDeviceType();
        if (deviceType == null) deviceType = 1;
        AgentLoginRecordInsertVO vo = new AgentLoginRecordInsertVO();
        vo.setLoginIp(ip);
        vo.setAgentAccount(agentInfoPO.getAgentAccount());
        vo.setAgentId(agentInfoPO.getAgentId());
        vo.setLoginStatus(loginStatus);
        vo.setAgentType(String.valueOf(agentInfoPO.getAgentType()));
        vo.setLoginIp(ip);
        vo.setLoginTime(System.currentTimeMillis());
        vo.setDeviceNumber(agentLoginParamVO.getDeviceNo());
        vo.setDeviceVersion("1.0");
        vo.setIpAttribution(ipApiVO.getAddress());
        vo.setLoginDevice(String.valueOf(deviceType));
        vo.setLoginTime(System.currentTimeMillis());
        String url = request.getHeader(X_CUSTOM);
        if (!url.contains("http")){
            url = "https://" + url;
        }
        vo.setLoginAddress(url);
        vo.setSiteCode(agentInfoPO.getSiteCode());
        vo.setSiteCode(agentInfoPO.getSiteCode());
        String deviceVersion = StringUtil.getVersion(request.getHeader("userAgent"));
        vo.setDeviceVersion(deviceType == 3 || deviceType == 5 ? null : deviceVersion);
        vo.setRemark(remark);
        vo.setAgentLabelId(agentInfoPO.getAgentLabelId());

        agentLoginRecordService.insertAgentLoginRecord(vo);

        return ip;
    }


    public boolean checkPassword(AgentLoginParamVO agentLoginParamVO) {
        AgentInfoPO agentInfoPO = agentInfoService.getAgentInfoPO(agentLoginParamVO.getSiteCode(),agentLoginParamVO.getAgentAccount());
        if (agentInfoPO == null) {
            return false;
        }

        String salt = agentInfoPO.getSalt();
        // 密码加密
        String encryptPassword = AgentServerUtil.getEncryptPassword(agentLoginParamVO.getPassword(), salt);

        return encryptPassword.equals(agentInfoPO.getAgentPassword());
    }

    public ResponseVO resetPassword(AgentResetPasswordVO agentResetPasswordVO) {

        AgentInfoPO agentInfoPO = agentInfoService.getAgentInfoPO(agentResetPasswordVO.getSiteCode(),agentResetPasswordVO.getAgentAccount());
        if (agentInfoPO == null) {
            throw new BaowangDefaultException(ResultCode.USER_NOT_EXIST);
        }

        if (!agentResetPasswordVO.getNewPassword().equals(agentResetPasswordVO.getConfirmPassword())) {
            throw new BaowangDefaultException(ResultCode.PASSWORD_CONFIRM_ERROR);
        }

        String salt = agentInfoPO.getSalt();
        // 密码加密
        String encryptPassword = AgentServerUtil.getEncryptPassword(agentResetPasswordVO.getNewPassword(), salt);

        agentInfoService.updatePasswordByAgentAccount(agentResetPasswordVO.getSiteCode(),agentInfoPO.getAgentAccount(), encryptPassword);
        RedisUtil.deleteKey(String.format(RedisConstants.AGENT_FORGET_PASSWORD_VERIFY,agentResetPasswordVO.getAgentAccount()));

        return ResponseVO.success();
    }

    private void sendWsMessage(AgentInfoVO agentInfoVO) {
        log.info("代理注册登录消息发送");
        try {
            //会员加入通知
            sendMessage(agentInfoVO, SystemMessageEnum.AGENT_WELCOME);
            //资金安全通知
            sendMessage(agentInfoVO, SystemMessageEnum.AGENT_FUNDS_SECURITY);

        } catch (Exception e) {
            e.printStackTrace();
            log.info("{}注册消息发送失败", agentInfoVO.getAgentAccount());
        }
    }
    public void sendMessage(AgentInfoVO agentInfoVO, SystemMessageEnum messageType) {
        AgentUserLanguageVO languageVO = new AgentUserLanguageVO();
        languageVO.setUserType("1");
        languageVO.setMessageType(messageType);
        languageVO.setUserId(agentInfoVO.getAgentId());
        AgentSystemMessageConfigVO messageConfigVO = agentCommonService.getAgentLanguage(languageVO);

        ResponseVO<SiteVO> responseVO = siteApi.getSiteInfo(agentInfoVO.getSiteCode());
        SiteVO siteVO = responseVO.getData();
        MessageNotifyVO notifyVO = new MessageNotifyVO();

        String titleString = I18nMessageUtil.getI18NMessage(messageConfigVO.getTitleI18nCode());
        String messageString = I18nMessageUtil.getI18NMessage(messageConfigVO.getContentI18nCode());
        String title = null, message = null;
        String titleConvertValue = null;
        String contentConvertValue = null;
        if (messageType.getCode().equals(SystemMessageEnum.AGENT_WELCOME.getCode())) {
            title = String.format(titleString, siteVO.getSiteName());
            message =  String.format(messageString, agentInfoVO.getAgentAccount(), siteVO.getSiteName());
            titleConvertValue = siteVO.getSiteName();
            contentConvertValue = agentInfoVO.getAgentAccount() + "," + siteVO.getSiteName();
        } else {
            title = titleString;
            message = String.format(messageConfigVO.getContent(), siteVO.getSiteName(), siteVO.getSiteName());
            contentConvertValue = siteVO.getSiteName() + "," + siteVO.getSiteName();
        }

        notifyVO.setUserIds(List.of(agentInfoVO.getAgentId()));
        notifyVO.setSiteCode(agentInfoVO.getSiteCode());
        notifyVO.setMessageI18nCode(messageConfigVO.getContentI18nCode());
        notifyVO.setTitleI18nCode(messageConfigVO.getTitleI18nCode());
        notifyVO.setTitleConvertValue(titleConvertValue);
        notifyVO.setContentConvertValue(contentConvertValue);
        MessageVO messageVO = MessageVO.builder()
                .title(title)
                .message(message)
                .build();
        notifyVO.setMessageVO(messageVO);
        notifyVO.setMsgTopic(WSSubscribeEnum.AGENT_REGISTER_SUCCESS.getTopic());
        notifyVO.setSystemMessageCode(messageType.getCode());

        log.info("注册登录消息发送：{}", JSONObject.toJSONString(notifyVO));
        sendSocketMessage(notifyVO);
    }

    public void sendSocketMessage(MessageNotifyVO notifyVO) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // ws信息推送
        List<String> userIds = notifyVO.getUserIds();
        WsMessageMqVO messageMqVO = new WsMessageMqVO();
        messageMqVO.setSiteCode(notifyVO.getSiteCode());
        messageMqVO.setUidList(userIds);
        messageMqVO.setMessage(new WSBaseResp<>(notifyVO.getMsgTopic(), ResponseVO.success(notifyVO.getMessageVO())));
        log.info("代理注册登录消息开始发送：{}", JSONObject.toJSONString(messageMqVO));
        KafkaUtil.send(WsMessageConstant.WS_MESSAGE_BROADCAST_TOPIC, messageMqVO);
        log.info("代理注册登录消息结束发送：{}", JSONObject.toJSONString(messageMqVO));


        UserNoticeTargetAddVO result = new UserNoticeTargetAddVO();
        result.setUserId(userIds.get(0));
        result.setNoticeType(CommonConstant.business_four);
        result.setReadState(CommonConstant.business_zero);
        result.setPlatform(CommonConstant.business_two);
        result.setDeleteState(CommonConstant.business_one);
        result.setRevokeState(CommonConstant.business_one);
        result.setMessageContentI18nCode(notifyVO.getMessageI18nCode());
        result.setNoticeTitleI18nCode(notifyVO.getTitleI18nCode());
        result.setContentConvertValue(notifyVO.getContentConvertValue());
        result.setTitleConvertValue(notifyVO.getTitleConvertValue());
        result.setSystemMessageCode(notifyVO.getSystemMessageCode());
        userNoticeApi.add(result);
        log.info("注册登录消息保存记录完成");
    }
}
