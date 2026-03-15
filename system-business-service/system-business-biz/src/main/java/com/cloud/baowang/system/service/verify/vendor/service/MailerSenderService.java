package com.cloud.baowang.system.service.verify.vendor.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.system.api.vo.verify.MailChannelConfigVO;
import com.cloud.baowang.system.api.vo.verify.SmsChannelConfigVO;
import com.cloud.baowang.system.service.verify.vendor.VerifyCodeSenderService;
import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.Recipient;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service("MAILER")
public class MailerSenderService implements VerifyCodeSenderService {

    public static void main(String[] args) {
        Email email = new Email();

        email.setFrom("oksport", "MS_apjFyP@test-y7zpl98x0ro45vx6.mlsender.net");

        Recipient recipient = new Recipient("name", "skyboyone002@gmail.com");

        email.AddRecipient(recipient);
        email.setSubject("Your verification code");

        email.setTemplateId("vywj2lpke6pg7oqz");

        email.addPersonalization(recipient, "code", "333333");
        email.addPersonalization(recipient, "account_name", "skyboy");

        MailerSend ms = new MailerSend();

        ms.setToken("mlsn.e79e65532c26cc5ea1ed2e3238d73c46afe5eb4428c792a5b3a314227b8c8e46");

        try {
            MailerSendResponse response = ms.emails().send(email);
            System.out.println(response.messageId);
        } catch (MailerSendException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendSms(SmsChannelConfigVO configVO) {

    }

    @Override
    public void sendMail(MailChannelConfigVO configVO) {
        Email email = new Email();

        email.setFrom("oksport", "MS_apjFyP@test-y7zpl98x0ro45vx6.mlsender.net");

        Recipient recipient = new Recipient("name", configVO.getReceiver());

        email.AddRecipient(recipient);
        email.setSubject(String.format(configVO.getTemplate(), configVO.getVerifyCode()));

        email.setTemplateId(configVO.getTemplate());

        email.addPersonalization(recipient, "code", configVO.getVerifyCode());
        email.addPersonalization(recipient, "account_name", configVO.getUserAccount());

        MailerSend ms = new MailerSend();

        ms.setToken(configVO.getApiKey());

        try {
            MailerSendResponse response = ms.emails().send(email);
            System.out.println(response.messageId);
        } catch (MailerSendException e) {
            e.printStackTrace();
        }
    }
}
