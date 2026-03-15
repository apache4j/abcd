package com.cloud.baowang.user.service;



import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.common.core.enums.DeviceType;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.HttpClient4Util;
import com.cloud.baowang.user.api.vo.ads.CustomData;
import com.cloud.baowang.user.api.vo.ads.EventData;
import com.cloud.baowang.user.api.vo.ads.EventVO;
import com.cloud.baowang.user.api.vo.ads.UserData;
import com.cloud.baowang.user.api.vo.ads.UserRechargeEventVO;
import com.cloud.baowang.user.api.vo.user.UserRegisterVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 广告埋点
 */
@Service
@Slf4j
@AllArgsConstructor
public class AdsService {


    private final AgentInfoApi agentInfoApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;


    @Async
    public void onFbAdRegisterEventArrive(UserRegisterVO userInfoVO, AgentInfoVO agentInfoVO,Integer deviceType){
        String fbPixId = agentInfoVO.getFbPixId();
        String fbToken = agentInfoVO.getFbToken();
        long eventTime = System.currentTimeMillis() / 1000;
        EventData eventData = new EventData();
        eventData.setEvent_name("CompleteRegistration");
        eventData.setEvent_time(eventTime);
        eventData.setEvent_id(userInfoVO.getEventId());
        UserData userData = new UserData();
        String ip = userInfoVO.getLoginAddress();
        userData.setClient_ip_address(ip);
        userData.setClient_user_agent(DeviceType.nameByCode(deviceType));
        String externalId = userInfoVO.getUserAccount()+"_" + ip;
        userData.setExternal_id(List.of(externalId));
        EventVO eventVO = new EventVO();
        eventData.setUser_data(userData);
        eventVO.setData(List.of(eventData));
        doFbAdEvent(eventVO,fbPixId,fbToken);
        log.info("onFbAdRegisterEventArrive : "+"siteCode : "+agentInfoVO.getSiteCode() +" userAccount : "+ userInfoVO.getUserAccount() +"eventName : "+ "CompleteRegistration" +" fbPixId : "+fbPixId );

    }

    public void onRechargeAdsEventArrive(UserRechargeEventVO userInfoVO){
        fbRechargeEvent(userInfoVO);
        googleRechargeEvent(userInfoVO);
    }

    private void googleRechargeEvent(UserRechargeEventVO userInfoVO) {
        AgentInfoVO agentInfoVO = agentInfoApi.getByAgentId(userInfoVO.getSuperAgentId());
        String googlePixId = agentInfoVO.getGooglePixId();
        String googleToken = agentInfoVO.getGoogleToken();

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("client_id",userInfoVO.getEventId());
        List<Map<String, Object>> events = new ArrayList<>();
        Map<String, Object> event = new HashMap<>();
        event.put("name", "purchase");
        Map<String, Object> params = new HashMap<>();
        params.put("currency", "USD");
        BigDecimal value = buildReqAmount(userInfoVO.getAmount(), userInfoVO.getMainCurrency(), userInfoVO.getSiteCode());
        params.put("value", value);
        params.put("transaction_id", userInfoVO.getOrderNo());
        event.put("params", params);
        events.add(event);
        jsonMap.put("events", events);

        doGoogleAdEvent(jsonMap,googlePixId,googleToken);
        log.info("googleRechargeEvent : "+"siteCode : "+userInfoVO.getSiteCode() +" userAccount : "+ userInfoVO.getUserAccount() +"eventName : "+ "Purchase" +" googlePixId : "+googlePixId + " amount : "+userInfoVO.getAmount() +" currencyCode : "+userInfoVO.getMainCurrency());

    }


    public void fbRechargeEvent(UserRechargeEventVO userInfoVO) {
        AgentInfoVO agentInfoVO = agentInfoApi.getByAgentId(userInfoVO.getSuperAgentId());
        String fbPixId = agentInfoVO.getFbPixId();
        String fbToken = agentInfoVO.getFbToken();
        long eventTime = System.currentTimeMillis() / 1000;
        EventData eventData = new EventData();
        eventData.setEvent_name("Purchase");
        eventData.setEvent_time(eventTime);
        eventData.setEvent_id(userInfoVO.getEventId());
        UserData userData = new UserData();
        if (StringUtils.isNotBlank(userInfoVO.getEmail())) {
            userData.setEm(List.of(sha256(userInfoVO.getEmail())));
        }
        if (StringUtils.isNotBlank(userInfoVO.getPhone())) {
            userData.setPh(List.of(sha256(userInfoVO.getPhone())));
        }
        String ip = userInfoVO.getReqIp();
        userData.setClient_ip_address(ip);
        userData.setClient_user_agent(DeviceType.nameByCode(userInfoVO.getDeviceType()));
        String externalId = userInfoVO.getUserAccount()+"_" + ip;
        userData.setExternal_id(List.of(externalId));
        CustomData customData = new CustomData();
        customData.setCurrency("USD");
        customData.setValue(buildReqAmount(userInfoVO.getAmount(),userInfoVO.getMainCurrency(),userInfoVO.getSiteCode()));
        customData.setContentIds(List.of(userInfoVO.getOrderNo()));

        EventVO eventVO = new EventVO();
        eventData.setUser_data(userData);
        eventData.setCustom_data(customData);
        eventVO.setData(List.of(eventData));
        log.info("fbRechargeEvent : "+"siteCode : "+userInfoVO.getSiteCode() +" userAccount : "+ userInfoVO.getUserAccount() +"eventName : "+ "Purchase" +" eventId : "+fbPixId + " amount : "+userInfoVO.getAmount() +" currencyCode : "+userInfoVO.getMainCurrency());

        doFbAdEvent(eventVO,fbPixId,fbToken);
    }

    public  String sha256(String input) {
        try {
            // 获取 SHA-256 摘要器
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // 计算哈希值
            byte[] hashBytes = digest.digest(input.getBytes
                    (StandardCharsets.UTF_8));
            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);  // & 0xff 确保是正数
                if (hex.length() == 1) hexString.append('0'); // 补零
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ignored) {
           return "";
        }
    }


    public BigDecimal buildReqAmount(BigDecimal amount,String currencyCode,String siteCode) {
        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        BigDecimal rate = currencyRateMap.get(currencyCode);
        BigDecimal wtc = AmountUtils.divide(amount, rate);
        BigDecimal usdRate = currencyRateMap.get("USD");
        return AmountUtils.multiply(wtc, usdRate,2);
    }

    public void doFbAdEvent(EventVO eventVO, String fbPixId,String fbToken){
        String fbUrl = "https://graph.facebook.com/v22.0/";
        String reqUrl = fbUrl + fbPixId + "/events?access_token=" + fbToken;
        HttpClient4Util.doPostJson(reqUrl,JSONObject.toJSONString(eventVO));
    }
    public void doGoogleAdEvent(Object eventVO, String googlePixId,String googleToken){
        String googleUrl = "https://www.google-analytics.com/mp/collect?measurement_id=";
        String reqUrl = googleUrl + googlePixId + "&api_secret=" + googleToken;
        HttpClient4Util.doPostJson(reqUrl,JSONObject.toJSONString(eventVO));
    }

    @Async
    public void onGoogleAdRegisterEventArrive(UserRegisterVO userInfoVO, AgentInfoVO agentInfoVO) {
        String googlePixId = agentInfoVO.getGooglePixId();
        String googleToken = agentInfoVO.getGoogleToken();
        Map<String, Object> jsonMap = new HashMap<>();

        jsonMap.put("client_id", userInfoVO.getEventId());
        List<Map<String, Object>> events = new ArrayList<>();

        Map<String, Object> event = new HashMap<>();
        event.put("name", "sign_up");
        Map<String, String> params = new HashMap<>();
        params.put("method", "email");
        event.put("params", params);
        events.add(event);
        jsonMap.put("events", events);

        doGoogleAdEvent(jsonMap,googlePixId,googleToken);

        log.info("onGoogleAdRegisterEventArrive : "+"siteCode : "+agentInfoVO.getSiteCode() +" userAccount : "+ userInfoVO.getUserAccount() +"eventName : "+ "sign_up" +" fbPixId : "+googlePixId );


    }

    public static void main(String[] args) {
        Map<String, Object> jsonMap = new HashMap<>();

        jsonMap.put("client_id","4d65a4d6as4dasdasdas");

        List<Map<String, Object>> events = new ArrayList<>();

        // 创建单个事件对象
        Map<String, Object> event = new HashMap<>();
        event.put("name", "purchase");

        // 创建 params 对象
        Map<String, Object> params = new HashMap<>();
        params.put("currency", "USD");
        BigDecimal value = new BigDecimal("66.66");
        params.put("value", value);
        params.put("transaction_id", "dsadasdasdasdqq");

        // 添加 params 到事件
        event.put("params", params);

        // 添加事件到事件列表
        events.add(event);

        // 添加事件列表到主 Map
        jsonMap.put("events", events);

        // 打印结果（可选：使用 Gson 或 Jackson 转换为 JSON 字符串）
        System.out.println(JSONObject.toJSONString(jsonMap));




    }
}
