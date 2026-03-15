package com.cloud.baowang.service;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.HttpClientUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.pay.api.vo.TradeNotifyVo;
import com.cloud.baowang.common.core.constants.WalletConstants;
import com.cloud.baowang.pay.api.vo.HotWalletAddressRequestVO;
import com.cloud.baowang.pay.api.vo.HotWalletAddressResponseVO;
import com.cloud.baowang.pay.api.vo.OrderDateTimeQueryVO;
import com.cloud.baowang.pay.api.vo.OrderNoVO;
import com.cloud.baowang.pay.api.vo.VirtualCurrencyPayRequestVO;
import com.cloud.baowang.common.core.utils.ECDSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class VirtualCurrencyPayService {

    @Value("${common.config.jvPayDomain}")
    private String jvPayDomainUrl;

    @Value("${common.config.jvPayPrivateKey}")
    private String jvPayPrivateKey;

/*    private final SystemRechargeChannelApi systemRechargeChannelApi;

    public VirtualCurrencyPayService(final SystemRechargeChannelApi systemRechargeChannelApi) {
        this.systemRechargeChannelApi = systemRechargeChannelApi;
    }*/

    /**
     * 创建钱包
     * @param hotWalletAddressRequestVO 创建钱包参数
     */
    public ResponseVO<HotWalletAddressResponseVO> createHotWalletAddress(HotWalletAddressRequestVO hotWalletAddressRequestVO){
        final String apiUrl = jvPayDomainUrl+"api/address/genAddress";
        String paramJson = JSON.toJSONString(hotWalletAddressRequestVO);
        Map<String,String> headerMap=buildHeadMap(paramJson);
        try{
            String result = HttpClientUtil.doPostJson(apiUrl,paramJson , headerMap);
            log.info("创建钱包地址结果:{}",result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String retCode=jsonObject.getString("code");
            String retMsg=jsonObject.getString("msg");
            if("200".equals(retCode)){
                String data = jsonObject.get("data").toString();
                return   ResponseVO.success(JSONObject.parseObject(data,HotWalletAddressResponseVO.class));
            }
        }catch (Exception e){
            log.error("创建钱包地址 http request error:{0}",e);
        }
        return ResponseVO.fail(ResultCode.HOT_WALLET_ADDRESS_FAIL);
    }

    /**
     * 报文加密
     * @param paramJson 请求参数
     * @return
     */
    private Map<String, String> buildHeadMap(String paramJson) {
        String  timestamp=System.currentTimeMillis()+"";
        String   random= RandomStringUtils.randomAlphabetic(6);
        String signVal= ECDSAUtil.signParam(timestamp,random, JSONObject.parse(paramJson),jvPayPrivateKey);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(WalletConstants.HEAD_TIMESTAMP,timestamp);
        headerMap.put(WalletConstants.HEAD_RANDOM,random);
        headerMap.put(WalletConstants.HEAD_SIGN_NAME,signVal);
        return headerMap;
    }

    /**
     * 提现支付
     * @param virtualCurrencyPayRequestVO 提现参数
     */
    public  Boolean withdrawPay(VirtualCurrencyPayRequestVO virtualCurrencyPayRequestVO){
        final String apiUrl = jvPayDomainUrl+"api/trade/withdraw";
        String paramJson = JSON.toJSONString(virtualCurrencyPayRequestVO);
        Map<String,String> headerMap=buildHeadMap(paramJson);
        try{
            String result = HttpClientUtil.doPostJson(apiUrl,paramJson , headerMap);
            log.info("提现申请结果:{}",result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if((boolean) jsonObject.get("success")){
                return true;
            }
        }catch (Exception e){
            log.error("提现申请 http request error:{0}",e);
        }
        return false;
    }

    public TradeNotifyVo queryByOrderNo(OrderNoVO orderNoVO){
        final String apiUrl = jvPayDomainUrl+"api/trade/queryByOrderNo";
        String paramJson = JSON.toJSONString(orderNoVO);
        Map<String,String> headerMap=buildHeadMap(paramJson);
        try{
            String result = HttpClientUtil.doPostJson(apiUrl,paramJson , headerMap);
            log.info("查询订单结果：{}",result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if((boolean) jsonObject.get("success")){
                TradeNotifyVo tradeNotifyVo = JSONObject.parseObject(jsonObject.get("data").toString(), TradeNotifyVo.class);
                return tradeNotifyVo;
            }
        }catch (Exception e){
            log.error("查询订单 http request error:{0}",e);
        }
        return null;
    }

    public List<TradeNotifyVo> queryByTime(OrderDateTimeQueryVO vo){
       /* ChannelQueryReqVO channelQueryReqVO = new ChannelQueryReqVO();
        channelQueryReqVO.setChannelName(PayChannelNameEnum.JVPay.getName());
        SystemRechargeChannelBaseVO channelBaseVO = systemRechargeChannelApi.getChannelByCode(channelQueryReqVO);*/
        final String apiUrl = jvPayDomainUrl+"/api/notifyOrder/queryRechargeUnNotifyByTime";
        List<TradeNotifyVo> resultList = new ArrayList<>();
        Integer pageNum = 1,pageSize = 100;
        vo.setPageSize(pageSize);
        for (;;) {
            String paramJson = JSON.toJSONString(vo);
            Map<String,String> headerMap=buildHeadMap(paramJson);
            try{
                String result = HttpClientUtil.doPostJson(apiUrl,paramJson , headerMap);
                log.info("按照时间区间查询订单结果:{}",result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                if((boolean) jsonObject.get("success")){
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (data.getIntValue("total") == 0) {
                        break;
                    }
                    List<TradeNotifyVo> tradeNotifyVo = JSONArray.parseArray(data.get("records").toString(), TradeNotifyVo.class);
                    if(CollectionUtil.isEmpty(tradeNotifyVo)){
                        break;
                    }
                    resultList.addAll(tradeNotifyVo);
                    pageNum++;
                    vo.setPageNumber(pageNum);
                } else {
                    break;
                }
            }catch (Exception e){
                log.error("按照时间区间查询订单 http request error:{0}",e);
                break;
            }
        }
        return resultList;
    }

    /*public static void main(String[] args) {
        HotWalletAddressRequestVO vo = new HotWalletAddressRequestVO();
        vo.setChainType("ETH");
        vo.setOwnerUserId("sV26649560");
//        createHotWalletAddress(vo);
    }*/

}
