package com.cloud.baowang.system.service.verify.vendor.service;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.system.api.vo.verify.MailChannelConfigVO;
import com.cloud.baowang.system.api.vo.verify.SmsChannelConfigVO;
import com.cloud.baowang.system.api.vo.verify.VerifyCodeSendVO;
import com.cloud.baowang.system.service.verify.vendor.VerifyCodeSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * @author: fangfei
 * @createTime: 2024/05/09 0:23
 * @description: FRS 邮箱发送
 * http://showwebsite.ssycloud.com/
 * FRS-616L/FRS123456
 */
@Slf4j
@Service("FRS")
public class FrsMailSenderService implements VerifyCodeSenderService {
    public static void main(String[] args) {
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("from", "info");
        //bodyJson.put("emails", "wfei31162@gmail.com");
        bodyJson.put("emails", "ford20250501@gmail.com");
        bodyJson.put("theme", "邮箱验证码");
        bodyJson.put("content", "您的验证码是: {{code}}");
        JSONObject body = new JSONObject();
        body.put("code", 5324);
        bodyJson.put("placeholders", body.toJSONString());

        String userAccount="FRS-616L";
        String apiKey="n06Rc8k7";

        Long requestTime = System.currentTimeMillis()/1000;
        String signStr = userAccount + apiKey + requestTime;
        String sign = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();
        String url = "http://websiteapi.ssycloud.com/api/v1/email/sendEmail";
        url = url + "?account= " + userAccount + "&sign=" +sign + "&datetime=" +requestTime;
        HttpResponse response = HttpRequest.post(url)
                .timeout(30000)
                .body(bodyJson.toJSONString())
                .header("Content-Type", "application/json;charset=UTF-8")
                .execute();
        log.info("AOK邮件发送发送返回：{}",response);
    }

    @Override
    public void sendSms(SmsChannelConfigVO configVO) {

    }

    @Override
    public void sendMail(MailChannelConfigVO configVO) {
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("from", "info");
        bodyJson.put("emails", configVO.getReceiver());
        bodyJson.put("theme", "verification code");
        bodyJson.put("content", "your verification code is: {{code}}");
        JSONObject body = new JSONObject();
        body.put("code", configVO.getVerifyCode());
        bodyJson.put("placeholders", body.toJSONString());

        Long requestTime = System.currentTimeMillis()/1000;
        String signStr = configVO.getUserAccount() + configVO.getApiKey() + requestTime;
        String sign = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();
        String url = configVO.getHost() + "/api/v1/email/sendEmail";
        url = url + "?account= " + configVO.getUserAccount() + "&sign=" +sign + "&datetime=" +requestTime;
        HttpResponse response = HttpRequest.post(url)
                .timeout(30000)
                .body(bodyJson.toJSONString())
                .header("Content-Type", "application/json;charset=UTF-8")
                .execute();
        log.info("FRS邮件发送发送返回：{}",response);
        log.info(configVO.getReceiver() + "文本邮件已发送成功 " + "您的验证码是: " + configVO.getVerifyCode());
    }
}
