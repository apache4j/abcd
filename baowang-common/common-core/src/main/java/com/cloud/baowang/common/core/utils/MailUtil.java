package com.cloud.baowang.common.core.utils;

import com.cloud.baowang.common.core.utils.tool.vo.MailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Slf4j
public class MailUtil {
    public static void sendMail(MailVO mailVO) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setUsername(mailVO.getUsername());
        mailSender.setPassword(mailVO.getPassword());
        mailSender.setHost(mailVO.getHost());
        mailSender.setPort(mailVO.getPort());

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(mailVO.getFrom());
        simpleMailMessage.setTo(mailVO.getReceiver());
        simpleMailMessage.setSubject(mailVO.getSubject());
        simpleMailMessage.setText(mailVO.getMessageText());
        mailSender.send(simpleMailMessage);
        log.info(mailVO.getReceiver() + "文本邮件已发送成功 " + mailVO.getMessageText());
    }
}
