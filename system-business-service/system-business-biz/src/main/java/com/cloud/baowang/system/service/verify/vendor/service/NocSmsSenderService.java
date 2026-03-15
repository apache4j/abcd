package com.cloud.baowang.system.service.verify.vendor.service;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.cloud.baowang.system.api.vo.verify.MailChannelConfigVO;
import com.cloud.baowang.system.api.vo.verify.SmsChannelConfigVO;
import com.cloud.baowang.system.service.verify.vendor.VerifyCodeSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: fangfei
 * @createTime: 2024/05/08 23:49
 * @description:
 */
@Slf4j
@Service("NOC")
public class NocSmsSenderService implements VerifyCodeSenderService {
    public static void main(String[] args) {
        SmsChannelConfigVO configVO = new SmsChannelConfigVO();
        configVO.setHost("http://61.244.118.74:8138/14.dox");
        configVO.setUserId("SKoksportOTP");
        configVO.setPassword("68715zib");
        configVO.setReceiver("8613603019253");
        configVO.setVerifyCode("333444");

        String url = configVO.getHost()  +
                "?UserName=" + configVO.getUserId() +
                "&PassWord=" + configVO.getPassword() +
                "&Caller=8624782398744" +
                "&CallerAddrTon=1" +
                "&CallerAddrNpi=1" +
                "&CalleeAddrTon=1" +
                "&CalleeAddrNpi=1" +
                "&CalleeTon=1" +
                "&CalleeNpi=1" +
                "&Callee=" + configVO.getReceiver() +
                "&Text=您的验证码为: " + configVO.getVerifyCode() + "【TXLC】"+
                "&DCS=8";
        log.info(configVO.getReceiver() +"发送短信URL：{}", url);
        HttpResponse response = HttpRequest.get(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .timeout(30000)
                .execute();
        log.info(configVO.getReceiver() +"发送短信返回：{}", response.body());
        log.info(configVO.getReceiver() + "短信已发送成功 " + "您的验证码为: " + configVO.getVerifyCode());
    }

    @Override
    public void sendSms(SmsChannelConfigVO configVO) {
        String url = configVO.getHost()  +
                "?UserName=" + configVO.getUserId() +
                "&PassWord=" + configVO.getPassword() +
                "&Caller=8624782398744" +
                "&CallerAddrTon=1" +
                "&CallerAddrNpi=1" +
                "&CalleeAddrTon=1" +
                "&CalleeAddrNpi=1" +
                "&CalleeTon=1" +
                "&CalleeNpi=1" +
                "&Callee=" + configVO.getReceiver() +
                "&Text=您的验证码为: " + configVO.getVerifyCode() + "【TXLC】"+
                "&DCS=8";
        log.info(configVO.getReceiver() +"发送短信URL：{}", url);
        HttpResponse response = HttpRequest.get(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .timeout(30000)
                .execute();
        log.info(configVO.getReceiver() +"NOC发送短信返回：{}", response.body());
        log.info(configVO.getReceiver() + "NOC短信已发送成功 " + "您的验证码为: " + configVO.getVerifyCode());
    }

    @Override
    public void sendMail(MailChannelConfigVO configVO) {

    }
}
