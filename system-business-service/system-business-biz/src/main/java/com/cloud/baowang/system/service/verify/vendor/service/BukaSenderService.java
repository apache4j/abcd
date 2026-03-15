package com.cloud.baowang.system.service.verify.vendor.service;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.cloud.baowang.system.api.vo.verify.MailChannelConfigVO;
import com.cloud.baowang.system.api.vo.verify.SmsChannelConfigVO;
import com.cloud.baowang.system.service.verify.vendor.VerifyCodeSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author: fangfei
 * @createTime: 2024/11/09 0:23
 * @description: 明姨短信
 */
@Slf4j
@Service("Buka")
public class BukaSenderService implements VerifyCodeSenderService {

    public static void main(String[] args) {
        /*
                菲律宾
            appkey：yxnmzG
            appsecret：DeAgCq
            appcode：1000
            越南
            appkey：vAe3zh
            appsecret：EB2fOw
            appcode：1000
            马来
            appkey：NL2QqW
            appsecret：uM6Jl1
            appcode：1000
        */
        final String baseUrl = "https://api.onbuka.com/v3";
        final String apiKey = "dBfsTLc6";
        final String apiPwd = "ipxI9FZ1";
        final String appId = "oKVNcU6J";

        final String numbers = "081695389";
        final String content = "hello world";
//        final String senderId = "";
//        final String orderId = "";

        final String url = baseUrl.concat("/sendSms");

        HttpRequest request = HttpRequest.post(url);

        //generate md5 key
        final String datetime = String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
        final String sign = SecureUtil.md5(apiKey.concat(apiPwd).concat(datetime));

        System.out.println("BukaSenderService.main - " + apiKey.concat(apiPwd).concat(datetime));

        request.header(Header.CONNECTION, "Keep-Alive")
                .header(Header.CONTENT_TYPE, "application/json;charset=UTF-8")
                .header("Sign", sign)
                .header("Timestamp", datetime)
                .header("Api-Key", apiKey);


        final String params = JSONUtil.createObj()
                .set("appId", appId)
                .set("numbers", numbers)
                .set("content", content)
//                .set("senderId", senderId)
                .toString();

        HttpResponse response = request.body(params).execute();
        if (response.isOk()) {
            String result = response.body();
            System.out.println(result);
        }


    }


    @Override
    public void sendSms(SmsChannelConfigVO configVO) {
        final String baseUrl = "https://api.onbuka.com/v3";
        final String apiKey = configVO.getUserAccount();
        final String apiPwd = configVO.getPassword();
        final String appId = configVO.getPlatformCode();

        final String numbers = configVO.getReceiver();
        final String content = String.format(configVO.getTemplate(), configVO.getVerifyCode());;

        final String url = baseUrl.concat("/sendSms");

        HttpRequest request = HttpRequest.post(url);

        //generate md5 key
        final String datetime = String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
        final String sign = SecureUtil.md5(apiKey.concat(apiPwd).concat(datetime));

        request.header(Header.CONNECTION, "Keep-Alive")
                .header(Header.CONTENT_TYPE, "application/json;charset=UTF-8")
                .header("Sign", sign)
                .header("Timestamp", datetime)
                .header("Api-Key", apiKey);


        final String params = JSONUtil.createObj()
                .set("appId", appId)
                .set("numbers", numbers)
                .set("content", content)
                .toString();

        HttpResponse response = request.body(params).execute();
        if (response.isOk()) {
            String result = response.body();
            System.out.println(result);
        }
        log.info("buka短信发送结构：{}", response);
        log.info(configVO.getReceiver() + "buka发送短信返回：{}", response);
        log.info(configVO.getReceiver() + "buka短信已发送成功 " + "您的验证码为: " + configVO.getVerifyCode());
    }

    @Override
    public void sendMail(MailChannelConfigVO configVO) {

    }
}
