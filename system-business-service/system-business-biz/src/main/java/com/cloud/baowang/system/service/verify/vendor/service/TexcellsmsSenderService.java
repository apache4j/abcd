package com.cloud.baowang.system.service.verify.vendor.service;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.system.api.vo.verify.MailChannelConfigVO;
import com.cloud.baowang.system.api.vo.verify.SmsChannelConfigVO;
import com.cloud.baowang.system.service.verify.vendor.VerifyCodeSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: mufan
 * @createTime: 2025/7/7 0:23
 * @description: texCell短信
 */
@Slf4j
@Service("texCell")
public class TexcellsmsSenderService implements VerifyCodeSenderService {

    @Override
    public void sendSms(SmsChannelConfigVO configVO) {
        log.info("Texcellsms短信发送消息体：{} ", JSON.toJSONString(configVO));
        String appkey = configVO.getUserAccount();
        String appsecret = configVO.getPassword();
        String phone = configVO.getReceiver();
        String msg = String.format(configVO.getTemplate(), configVO.getVerifyCode());
        String host = configVO.getHost();
        String url = host + "?UserName="
                +appkey+"&PassWord=" + appsecret + "&Caller=0123456789" +
                "&Callee=" + phone + "&CharSet=0&DCS=8" +
                "&Text=" + msg;
        HttpResponse response = HttpRequest.get(url)
                .timeout(30000)
                .execute();
        log.info("Texcellsms短信发送URL：{} ,返回内容:{}.",url,response);
    }

    @Override
    public void sendMail(MailChannelConfigVO configVO) {
    }
}
