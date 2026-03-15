package com.cloud.baowang.wallet.service;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.WalletConstants;
import com.cloud.baowang.common.core.utils.ECDSAUtil;
import com.cloud.baowang.common.core.utils.HttpClientUtil;
import com.cloud.baowang.wallet.api.vo.recharge.RegisterVirtualWalletInfosVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class VirtualWalletInfoRegisterService {

    @Value("${common.config.jvPayDomain}")
    private String jvPayDomainUrl;


    @Value("${common.config.jvPayPrivateKey}")
    private String jvPayPrivateKey;

    public void registerVirtualWalletInfos(List<RegisterVirtualWalletInfosVO> siteVirtualWalletList) {
        final String apiUrl = jvPayDomainUrl + "api/address/submitCommonAddress";
        String paramJson = JSON.toJSONString(siteVirtualWalletList);
        Map<String,String> headerMap=buildHeadMap(paramJson);
        try {
            String result = HttpClientUtil.doPostJson(apiUrl, paramJson,headerMap);
            log.info("提交钱包地址结果:{}" ,result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            Object success = jsonObject.get("success");
            if (success != null && success.equals("true")) {
                log.error("http request success:{1}", jsonObject.get("code"));
            }
        } catch (Exception e) {
            log.error("http request error:{0}", e.getMessage());
        }

    }

    /**
     * 报文加密
     * @param paramJson 请求参数
     * @return
     */
    private Map<String, String> buildHeadMap(String paramJson) {
        String  timestamp=System.currentTimeMillis()+"";
        String   random= RandomStringUtils.randomAlphabetic(6);
        String signVal="";
        if(JSON.isValidArray(paramJson)){
            JSONArray bodyJsonArray=JSONArray.parseArray(paramJson);
            signVal=ECDSAUtil.signParam(timestamp,random, bodyJsonArray,jvPayPrivateKey);
        }else {
            signVal=ECDSAUtil.signParam(timestamp,random, JSON.parseObject(paramJson),jvPayPrivateKey);
        }
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(WalletConstants.HEAD_TIMESTAMP,timestamp);
        headerMap.put(WalletConstants.HEAD_RANDOM,random);
        headerMap.put(WalletConstants.HEAD_SIGN_NAME,signVal);
        return headerMap;
    }

    /**
     * 保存商户号
     */
    public boolean saveOrUpdateSiteMerchantInfo(RegisterVirtualWalletInfosVO siteMerchantInfo) {
        final String apiUrl = jvPayDomainUrl + "/api/address/submitPlatPub";
        String paramJson = JSON.toJSONString(siteMerchantInfo);
        Map<String,String> headerMap=buildHeadMap(paramJson);
        try {
            String result = HttpClientUtil.doPostJson(apiUrl, paramJson,headerMap);
            log.info("提交商户号结果：" + result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            Object success = jsonObject.get("code");
            if (success != null && success.equals("200")) {
                log.error("http request success:{1}", jsonObject.get("code"));
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("http request error:{0}", e.getMessage());
        }
        return false;
    }


}
