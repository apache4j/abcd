package com.cloud.baowang.system.service.verify.vendor.service;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.system.api.vo.verify.MailChannelConfigVO;
import com.cloud.baowang.system.api.vo.verify.SmsChannelConfigVO;
import com.cloud.baowang.system.service.verify.vendor.VerifyCodeSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * @author: fangfei
 * @createTime: 2024/11/09 0:23
 * @description: 明姨短信
 */
@Slf4j
@Service("MYS")
public class MingYiSenderService implements VerifyCodeSenderService {

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
        String appkey = "yxnmzG";
        String appsecret = "DeAgCq";
        String appcode = "1000";
        String phone = "84367778384";
        String msg = "Your verification code is 666661";

        Long requestTime = System.currentTimeMillis();
        String signStr = appkey + appsecret + requestTime;
        String sign = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();

        String balanceUrl = "http://47.242.85.7:9090/sms/balance/v1?appkey="
                +appkey+ "&appcode=1000&sign=" +sign + "&timestamp=" + requestTime;


        String url = "http://47.242.85.7:9090/sms/batch/v2?appkey="
                +appkey+"&appsecret=" + appsecret + "&appcode=1000" +
                "&phone=" + phone +
                "&msg=" + msg;
        HttpResponse response = HttpRequest.get(balanceUrl)
                .timeout(30000)
                .execute();
        log.info("短信发送结构：{}",response);


    }


    @Override
    public void sendSms(SmsChannelConfigVO configVO) {
        Long requestTime = System.currentTimeMillis();
        String appkey = configVO.getUserAccount();
        String appsecret = configVO.getPassword();
        String appcode = configVO.getUserId();
        String phone = configVO.getReceiver();
        String msg = String.format(configVO.getTemplate(), configVO.getVerifyCode());
        String host = configVO.getHost();
        String url = host + "?appkey="
                +appkey+"&appsecret=" + appsecret + "&appcode=1000" +
                "&phone=" + phone +
                "&msg=" + msg;
        HttpResponse response = HttpRequest.get(url)
                .timeout(30000)
                .execute();
        log.info("明姨短信发送结构：{}",response);
        log.info(configVO.getReceiver() +"明姨发送短信返回：{}", response);
        log.info(configVO.getReceiver() + "明姨短信已发送成功 " + "您的验证码为: " + configVO.getVerifyCode());
    }

    @Override
    public void sendMail(MailChannelConfigVO configVO) {

    }
}
