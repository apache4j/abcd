package com.cloud.baowang.system.service.verify.vendor.service;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.MD5Util;
import com.cloud.baowang.system.api.vo.verify.MailChannelConfigVO;
import com.cloud.baowang.system.api.vo.verify.SmsChannelConfigVO;
import com.cloud.baowang.system.service.verify.vendor.VerifyCodeSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service("MXT")
public class MXTSenderService implements VerifyCodeSenderService {

    @Override
    public void sendSms(SmsChannelConfigVO configVO) {
        log.info("MXT sms短信发送消息体：{} ", JSON.toJSONString(configVO));
        String msg = String.format(configVO.getTemplate(), configVO.getVerifyCode());
        String host = configVO.getHost();
        boolean needstatus = true;//是否需要状态报告，需要true，不需要false
        String product = "";//产品ID
        String respType = "json";

        try {
            String dateFormat = DateUtils.dateToyyyyMMddHHmmss(new Date());

            String mded5 = MD5Util.md5(configVO.getUserAccount() + configVO.getPassword() + dateFormat);
            String mded52 = SecureUtil.md5(configVO.getUserAccount() + configVO.getPassword() + dateFormat);

            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("account", configVO.getUserAccount());
            //paramMap.put("ts", dateFormat);
            paramMap.put("pswd", configVO.getPassword());
            paramMap.put("mobile", configVO.getReceiver());
            paramMap.put("msg", msg);
            paramMap.put("needstatus", needstatus);
            paramMap.put("product", product);
            paramMap.put("resptype", respType);

            HttpResponse response = HttpRequest.get(host)
                    .form(paramMap)
                    .timeout(6000)
                    .execute();
            //NOTE 处理返回值
            if (response.isOk()) {
                JSONObject jsonBody = JSON.parseObject(response.body());
                if (jsonBody == null || jsonBody.getInteger("result") == null || jsonBody.getInteger("result") != 0) {
                    log.error("MXT发送信息状态异常,请求参数:{} 返回:{}", configVO, response);
                }
            } else {
                log.error("MXT发送信息请求异常,请求参数:{} 返回:{}", configVO, response);
            }
        } catch (Exception e) {
            log.error("MXT发送信息请求失败, 参数:{}", configVO, e);
        }
    }

    @Override
    public void sendMail(MailChannelConfigVO configVO) {
    }


    /*
        ★ 測試範例: https://weiwebs.cn/msg/HttpBatchSendSM?account=MXT802240_turtle&pswd=1qaz@WSX.*
        &mobile=13900000000&msg=【成都贤酷吉步】您的验证码为：965714，如非本人操作，请忽略。
        &needstatus=true&product=
     */
    public static void main(String[] args) {
        String url = "http://www.weiwebs.cn/msg/HttpBatchSendSM";//应用地址
        String account = "MXT802240_turtle";//账号
        String pswd = "1qaz@WSX.*";//密码
        String mobiles = "13838384438";//手机号码，多个号码使用","分割
        String content = "测试";//短信内容
        boolean needstatus = true;//是否需要状态报告，需要true，不需要false
        String product = "";//产品ID
        String respType = "json";
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("account", account);
            paramMap.put("pswd", pswd);
            paramMap.put("mobile", mobiles);
            paramMap.put("msg", content);
            paramMap.put("needstatus", needstatus);
            paramMap.put("product", product);
            paramMap.put("resptype", respType);

            HttpResponse response = HttpRequest.get(url)
                    .form(paramMap)
                    .timeout(6000)
                    .execute();
            //NOTE 处理返回值
            if (response.isOk()) {
                JSONObject jsonBody = JSON.parseObject(response.body());
                if (jsonBody == null || jsonBody.getInteger("result") == null || jsonBody.getInteger("result") != 0) {
                    //log.error("MXT发送信息失败,请求参数:{} 返回:{}",paramMap, response);
                }
            } else {
                throw new Exception("MXT短信发送失败");
            }
        } catch (Exception e) {
            log.error("MXT短信发送失败:", e);
        }
    }
}
