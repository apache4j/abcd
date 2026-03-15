package com.cloud.baowang.system.service.verify.vendor;

import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.SpringUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.verify.MailChannelConfigVO;
import com.cloud.baowang.system.api.vo.verify.SmsChannelConfigVO;
import com.cloud.baowang.system.api.vo.verify.VerifyCodeSendVO;
import com.cloud.baowang.system.po.verify.ChannelSendingStatisticPO;
import com.cloud.baowang.system.service.verify.ChannelSendingStatisticService;
import com.cloud.baowang.system.service.verify.MailChannelConfigService;
import com.cloud.baowang.system.service.verify.SmsChannelConfigService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: fangfei
 * @createTime: 2024/05/09 12:45
 * @description: 短信邮箱验证码发送服务
 */
@Slf4j
@Service
@AllArgsConstructor
public class SenderService {

    public MailChannelConfigService mailChannelConfigService;
    public SmsChannelConfigService smsChannelConfigService;

    private ChannelSendingStatisticService statisticService;

    public ResponseVO sendMail(VerifyCodeSendVO verifyCodeSendVO) {
        MailChannelConfigVO mailChannelConfigVO = mailChannelConfigService.querySiteChannel(verifyCodeSendVO.getSiteCode());
        if (mailChannelConfigVO == null) {
            log.info("发送验证码失败，平台未开启");
            throw new BaowangDefaultException(ResultCode.MAIL_CHANNEL_CLOSE);
        }
        mailChannelConfigVO.setReceiver(verifyCodeSendVO.getAccount());
        mailChannelConfigVO.setVerifyCode(verifyCodeSendVO.getVerifyCode());
        VerifyCodeSenderService senderService = SpringUtils.getBean(mailChannelConfigVO.getChannelCode());
        senderService.sendMail(mailChannelConfigVO);
        buildEmailEntity(mailChannelConfigVO);
        return ResponseVO.success();
    }

    public ResponseVO sendSms(VerifyCodeSendVO verifyCodeSendVO) {
        if (verifyCodeSendVO.getAreaCode().startsWith("+")) {
            verifyCodeSendVO.setAreaCode(verifyCodeSendVO.getAreaCode().substring(1));
        }
        SmsChannelConfigVO smsChannelConfigVO = smsChannelConfigService.querySiteChannel(verifyCodeSendVO);
        if (smsChannelConfigVO == null) {
            log.info("发送验证码失败，平台未开启");
            throw new BaowangDefaultException(ResultCode.SMS_CHANNEL_CLOSE);
        }
        
        String phone = verifyCodeSendVO.getAccount();
        if (verifyCodeSendVO.getAccount().startsWith("0")) {
            phone = verifyCodeSendVO.getAccount().substring(1);
        }
        smsChannelConfigVO.setReceiver(verifyCodeSendVO.getAreaCode() + phone);
        smsChannelConfigVO.setVerifyCode(verifyCodeSendVO.getVerifyCode());
        smsChannelConfigVO.setAreaCode(verifyCodeSendVO.getAreaCode());
        VerifyCodeSenderService senderService = SpringUtils.getBean(smsChannelConfigVO.getPlatformCode());
        log.info("短信发送消息体：{}", JSON.toJSONString(smsChannelConfigVO));
        senderService.sendSms(smsChannelConfigVO);
        buildSmsEntity(smsChannelConfigVO);
        return ResponseVO.success();
    }

    public void buildSmsEntity(SmsChannelConfigVO vo) {
        ChannelSendingStatisticPO.ChannelSendingStatisticPOBuilder builder = ChannelSendingStatisticPO.builder();
        builder.siteCode(CurrReqUtils.getSiteCode()).channelName(vo.getChannelName())
                .channelCode(vo.getChannelCode()).channelId(vo.getChannelId())
                .address(vo.getAddress()).addressCode(vo.getAddressCode()).receiver(vo.getReceiver()).channelType(CommonConstant.business_one_str);
        statisticService.addSendingInfo(builder.build());
    }

    public void buildEmailEntity(MailChannelConfigVO vo) {
        ChannelSendingStatisticPO.ChannelSendingStatisticPOBuilder builder = ChannelSendingStatisticPO.builder();
        builder.siteCode(CurrReqUtils.getSiteCode()).channelName(vo.getChannelName())
                .channelCode(vo.getChannelCode()).channelId(vo.getChannelId())
                .host(vo.getHost()).addressCode("").receiver(vo.getReceiver()).channelType(CommonConstant.business_two_str);
        statisticService.addSendingInfo(builder.build());
    }
}
