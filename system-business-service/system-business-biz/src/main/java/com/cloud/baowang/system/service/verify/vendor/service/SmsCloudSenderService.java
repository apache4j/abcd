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
 * @description: 短信云
 */
@Slf4j
@Service("SCL")
public class SmsCloudSenderService implements VerifyCodeSenderService {
    public static void main(String[] args) {
        /*appkey：3J2U9Z   菲律宾
        appsecret：WR2Vu4
        appcode：1000

            appkey：hDB3AP   越南
            appsecret：fYgb4x
            appcode：1000

        appkey：shNWCL  马来
        appsecret：G3E5n8
        appcode：1000
        */


        Long requestTime = System.currentTimeMillis();
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("appkey", "3J2U9Z");
        bodyJson.put("appcode", "1000");
        bodyJson.put("timestamp", requestTime);
        bodyJson.put("msg", "Your verification code is 123456");
        bodyJson.put("phone", "639564877315");
     //（appkey+appsecret+timestamp）
        String signStr = "3J2U9Z" + "WR2Vu4" + requestTime;
        String sign = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();
        bodyJson.put("sign", sign);

        String url = "http://101.44.162.101:9090/sms/batch/v1";

        //String result = HttpClient4Util.doPostJson(url, bodyJson.toJSONString());

        HttpResponse response = HttpRequest.post(url)
                .timeout(30000)
                .body(bodyJson.toJSONString())
                .header("Content-Type", "application/json;charset=UTF-8")
                .execute();
        log.info("短信发送结构：{}",response);
    }

    @Override
    public void sendSms(SmsChannelConfigVO configVO) {
        Long requestTime = System.currentTimeMillis();
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("appkey", configVO.getUserAccount());
        bodyJson.put("appcode", configVO.getUserId());
        bodyJson.put("timestamp", requestTime);
        bodyJson.put("msg", String.format(configVO.getTemplate(), configVO.getVerifyCode()));
        bodyJson.put("phone", configVO.getReceiver());
        //（appkey+appsecret+timestamp）
        String signStr = configVO.getUserAccount() + configVO.getPassword() + requestTime;
        String sign = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();
        bodyJson.put("sign", sign);

        String url = configVO.getHost();
        HttpResponse response = HttpRequest.post(url)
                .timeout(30000)
                .body(bodyJson.toJSONString())
                .header("Content-Type", "application/json;charset=UTF-8")
                .execute();
        log.info(configVO.getReceiver() +" 短信云SCL发送短信返回：{}", response);
        log.info(configVO.getReceiver() + "短信云SCL短信已发送成功 " + "您的验证码为: " + configVO.getVerifyCode());
    }

    @Override
    public void sendMail(MailChannelConfigVO configVO) {

    }
}
