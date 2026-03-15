package com.cloud.baowang.system.service.verify.vendor.service;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.system.api.vo.verify.MailChannelConfigVO;
import com.cloud.baowang.system.api.vo.verify.SmsChannelConfigVO;
import com.cloud.baowang.system.service.verify.vendor.VerifyCodeSenderService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("ZNY")
public class ZNYSenderService implements VerifyCodeSenderService {

    private static final String GATEWAY = "http://119.23.241.6:8001/sms";

    private static final String API = "/api";

    @Override
    public void sendSms(SmsChannelConfigVO configVO) {
        Gson gson = new GsonBuilder().create();
        log.info("ZNY sms短信发送消息体：{} ", gson.toJson(configVO));
        String host = configVO.getHost();
        String userName = configVO.getUserAccount();
        String content = String.format(configVO.getTemplate(), configVO.getVerifyCode());
        List<String> phoneList = new ArrayList(){{
            add(configVO.getReceiver());
        }};
        long timestamp = System.currentTimeMillis();
        String sign;
        String passwordMd5 = SecureUtil.md5(configVO.getPassword());
        sign = SecureUtil.md5(String.format("%s%s%s", userName, timestamp, passwordMd5));

        Map<String, Object> map = new LinkedHashMap(){{
            put("userName", userName);
            put("content", content);
            put("phoneList", phoneList);
            put("timestamp", timestamp);
            put("sign", sign);
        }};

        try {
            HttpResponse response = HttpRequest
                    .post(host)
                    .contentType("application/json")
                    .body(JSONUtil.toJsonStr(map))
                    .timeout(6000)
                    .execute();
            //NOTE 处理返回值
            if (response.isOk()) {
                JSONObject jsonBody = JSON.parseObject(response.body());
                if (jsonBody == null || jsonBody.getInteger("result") == null || jsonBody.getInteger("result") != 0) {
                    //log.error("MXT发送信息失败,请求参数:{} 返回:{}",paramMap, response);
                }
            } else {
                throw new Exception("ZNY短信发送失败");
            }
        } catch (Exception e) {
            log.error("ZNY短信发送失败:", e);
        }
        log.info("ZNY短信发送结束");
    }

    @Override
    public void sendMail(MailChannelConfigVO configVO) {

    }

    public static void main(String[] args){
        String userName = "331660";
        String password = "eEjVr5qO9kTd";
        String receiver = "13938384439";
        String content = "【顺莱信息】尊敬的用户，您的验证码为7751，如非本人操作，请忽略此短信。";
        List<String> phoneList = new ArrayList(){{
            add(receiver);
        }};
        long timestamp = System.currentTimeMillis();
        String sign;
        String passwordMd5_ = SecureUtil.md5(password);
        sign = SecureUtil.md5(String.format("%s%s%s", userName, timestamp, passwordMd5_));
        Map<String, Object> map = new LinkedHashMap(){{
            put("userName", userName);
            put("content", content);
            put("phoneList", phoneList);
            put("timestamp", timestamp);
            put("sign", sign);
        }};
        // 短信批量发送接口
        JSONObject jsonBody1 = sendMessageMass(map);
        System.out.println(jsonBody1);
        // 模拟耗时操作
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 回执状态获取接口
        JSONObject jsonBody2 = getReport(map);
        System.out.println(jsonBody2);

    }

    /**
     * 短信批量发送接口
     */
    private static JSONObject sendMessageMass(Map paramMap) {
        String host = GATEWAY + API + "/sendMessageMass";
        JSONObject jsonBody = null;
        try {
            HttpResponse response = HttpRequest
                    .post(host)
                    .contentType("application/json")
                    .body(JSONUtil.toJsonStr(paramMap))
                    .timeout(6000)
                    .execute();
            //NOTE 处理返回值
            if (response.isOk()) {
                jsonBody = JSON.parseObject(response.body());
                if (jsonBody == null || jsonBody.getInteger("result") == null || jsonBody.getInteger("result") != 0) {
                    //log.error("MXT发送信息失败,请求参数:{} 返回:{}",paramMap, response);
                }
                return jsonBody;
            } else {
                throw new Exception("ZNY短信发送失败");
            }
        } catch (Exception e) {
            log.error("ZNY短信发送失败:", e);
        }
        log.info("ZNY短信发送结束");
        return jsonBody;
    }

    /**
     * 回执状态获取接口
     */
    private static JSONObject getReport(Map paramMap) {
        String host = GATEWAY + API + "/getReport";
        JSONObject jsonBody = null;
        try {
            HttpResponse response = HttpRequest
                    .post(host)
                    .contentType("application/json")
                    .body(JSONUtil.toJsonStr(paramMap))
                    .timeout(6000)
                    .execute();
            //NOTE 处理返回值
            if (response.isOk()) {
                jsonBody = JSON.parseObject(response.body());
                if (jsonBody == null || jsonBody.getInteger("result") == null || jsonBody.getInteger("result") != 0) {
                    //log.error("MXT发送信息失败,请求参数:{} 返回:{}",paramMap, response);
                }
                return jsonBody;
            } else {
                throw new Exception("回执状态获取失败");
            }
        } catch (Exception e) {
            log.error("回执状态获取失败:", e);
        }
        return jsonBody;
    }
}
