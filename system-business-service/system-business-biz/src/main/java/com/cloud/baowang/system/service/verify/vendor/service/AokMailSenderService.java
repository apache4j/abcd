package com.cloud.baowang.system.service.verify.vendor.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.system.api.vo.verify.MailChannelConfigVO;
import com.cloud.baowang.system.api.vo.verify.SmsChannelConfigVO;
import com.cloud.baowang.system.service.verify.vendor.VerifyCodeSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: fangfei
 * @createTime: 2024/05/09 0:23
 * @description: AOK 邮箱发送
 */
@Slf4j
@Service("AOK")
public class AokMailSenderService implements VerifyCodeSenderService {

    public static void main(String[] args) {
        JSONObject bodyJson = new JSONObject();
      /*  bodyJson.put("app_key", "7d260e2827187824c80a66aa35e72680");
        bodyJson.put("template_id", "E_107323075726");*/
       // bodyJson.put("to", "wfei31162@gmail.com");
        bodyJson.put("app_key", "cfd6c593525e5fb9c28f8ae03f14a765");
        bodyJson.put("template_id", "E_100585265258");
        bodyJson.put("to", "ford20250501@gmail.com");
        bodyJson.put("reply_to", "info@bwintl.site");
        bodyJson.put("action", "send");
        JSONObject dataJson = new JSONObject();
        dataJson.put("text", "AOK2861");
        bodyJson.put("data", dataJson.toJSONString());

        String url = "https://www.aoksend.com/index/api/send_email";
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
    public void sendMail(MailChannelConfigVO configVO){
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("app_key", configVO.getApiKey());
        bodyJson.put("template_id", configVO.getTemplate());
        bodyJson.put("to", configVO.getReceiver());
        bodyJson.put("reply_to", configVO.getSender());
        bodyJson.put("action", "send");
        JSONObject dataJson = new JSONObject();
        dataJson.put("text", configVO.getVerifyCode());
        bodyJson.put("data", dataJson.toJSONString());

        String url = configVO.getHost();
        HttpResponse response = HttpRequest.post(url)
                .timeout(30000)
                .body(bodyJson.toJSONString())
                .header("Content-Type", "application/json;charset=UTF-8")
                .execute();
        log.info("AOK邮件发送发送返回：{}",response);
        log.info(configVO.getReceiver() + "文本邮件已发送成功 " + "您的验证码是: " + configVO.getVerifyCode());
    }
}
