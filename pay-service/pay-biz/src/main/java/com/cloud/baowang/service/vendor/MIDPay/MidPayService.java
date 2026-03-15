package com.cloud.baowang.service.vendor.MIDPay;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.wallet.PayoutStatusEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.HttpClient4Util;
import com.cloud.baowang.pay.api.vo.PayOrderResponseVO;
import com.cloud.baowang.pay.api.vo.PaymentResponseVO;
import com.cloud.baowang.pay.api.vo.PaymentVO;
import com.cloud.baowang.pay.api.vo.WithdrawalResponseVO;
import com.cloud.baowang.pay.api.vo.WithdrawalVO;
import com.cloud.baowang.service.DomainService;
import com.cloud.baowang.service.vendor.BasePayService;
import com.cloud.baowang.service.vendor.MIDPay.vo.CountryCodeEnum;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: fangfei
 * @createTime: 2024/09/30 14:55
 * @description: 88mids支付
 */
@Slf4j
@Service(value = "MidPay")
public class MidPayService implements BasePayService {

    @Autowired
    private DomainService domainService;

   /* public static void main(String[] args) {
        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("BANK_TRANSFER");
        channelRespVO.setMerNo("dgpay_687");
        channelRespVO.setPubKey("rTAUc365pYehdbZcbeK6tHgVc81aTQ");
        channelRespVO.setSecretKey("TCDJ2meTXqFk88rYrtu9ry7P8QAnMx99");
        channelRespVO.setApiUrl("https://prod10.88mids.com");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setAmount("200");
        paymentVO.setApplyIp("127.0.0.1");
        paymentVO.setCurrency("VND");  //MYR
        paymentVO.setCountryCode("VN"); //MY

        String orderNO = OrderUtil.getOrderNo("B", 10);

        new MidPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
        //new MidPayService().queryPayOrder(channelRespVO, "Bv1755HZk1u");

        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("10000");
        withdrawalVO.setCurrency("VND");
        withdrawalVO.setCountryCode("VN");
        withdrawalVO.setEmail("email@test.com");
        withdrawalVO.setBankCode("VCBB"); //VIETCOMBANK
        withdrawalVO.setBankNo("0551000294373");
        withdrawalVO.setBankUserName("duong thuy nlga");
        withdrawalVO.setBankName("Vietcombank");
        withdrawalVO.setApplyIp("8.210.214.184");

       // new MidPayService().creatPayoutOrder(channelRespVO, withdrawalVO, orderNO);
    }*/

    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        Integer deviceType = CurrReqUtils.getReqDeviceType();
        String siteCode = CurrReqUtils.getSiteCode();

        String returnUrl = "";
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/midPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/midPayAgentCallback";
            returnUrl=domainService.getReturnUrl(siteCode,1);
        } else {
            returnUrl= domainService.getReturnUrl(siteCode,2);
        }

        String countryCode = CountryCodeEnum.getCountryCode(paymentVO.getCurrency());
        JSONObject paramObj = new JSONObject();
        paramObj.put("amount", paymentVO.getAmount());
        paramObj.put("callbackUrl", notifyUrl);
        paramObj.put("country", countryCode);
        paramObj.put("currency", paymentVO.getCurrency());
        paramObj.put("email", paymentVO.getEmail() == null ? "email@test.com": paymentVO.getEmail());
        paramObj.put("ip", paymentVO.getApplyIp());
        paramObj.put("orderId", orderNo);
        paramObj.put("method", channelRespVO.getChannelCode());
        paramObj.put("returnUrl", returnUrl);
        paramObj.put("traffic", "TRUSTED");

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("apikey", channelRespVO.getPubKey());

        log.info("88mids支付请求参数: {}", paramObj);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl()
                + "/api/v1/external/payment/hosted",
                JSONObject.toJSONString(paramObj), headerMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("88mids支付返回：{}", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        if ("PENDING".equals(jsonObject.getString("status"))) {
            paymentResponseVO.setCode(CommonConstant.business_zero);
            paymentResponseVO.setOrderId(orderNo);
            paymentResponseVO.setAmount(paymentVO.getAmount());
            paymentResponseVO.setThirdOrderId(jsonObject.getString("id"));
            paymentResponseVO.setPaymentUrl(jsonObject.getString("paymentUrl"));
        } else {
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage(jsonObject.getString("message"));
        }

        return paymentResponseVO;
    }

    @Override
    public WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, WithdrawalVO withdrawalVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/midPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/midPayoutAgentCallback";
        }

        String countryCode = CountryCodeEnum.getCountryCode(withdrawalVO.getCurrency());
        JSONObject paramObj = new JSONObject();
        paramObj.put("account", withdrawalVO.getBankNo());
        paramObj.put("amount", withdrawalVO.getAmount());
        paramObj.put("country", countryCode);
        paramObj.put("currency", withdrawalVO.getCurrency());
        paramObj.put("email", withdrawalVO.getEmail() == null ? "email@test.com": withdrawalVO.getEmail());
        paramObj.put("ip", withdrawalVO.getApplyIp());
        paramObj.put("callbackUrl", notifyUrl);
        paramObj.put("method", "BANK_TRANSFER");
        paramObj.put("orderId", orderNo);
        JSONObject dataJson = new JSONObject();
        dataJson.put("bankCode", withdrawalVO.getBankCode());
        dataJson.put("bankBranch", withdrawalVO.getBankName());
        dataJson.put("accountName", withdrawalVO.getBankUserName());
        paramObj.put("data", dataJson);

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("apikey", channelRespVO.getPubKey());

        log.info("MidPay代付请求：{}", paramObj);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/api/v5/external/payout/s2s", paramObj.toJSONString(), headerMap);

            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("MidPay代付返回：{}", resultObj.toString());


            if ("PENDING".equals(resultObj.getString("status"))) {
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                withdrawalResponseVO.setWithdrawOrderId(resultObj.getString("id"));
                withdrawalResponseVO.setOrderNo(orderNo);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
            }  else  {
                withdrawalResponseVO.setCode(-1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }

        return withdrawalResponseVO;
    }

    @Override
    public PayOrderResponseVO queryPayOrder(SystemRechargeChannelBaseVO channelRespVO, String orderNo) {
        PayOrderResponseVO payOrderResponseVO = new PayOrderResponseVO();
        JSONObject paramObj = new JSONObject();
        paramObj.put("orderId", orderNo);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("apikey", channelRespVO.getPubKey());


        log.info("88mids订单查询请求参数: {}", paramObj);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/api/v2/external/payment/status", paramObj.toJSONString(), headerMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("88mids订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("status");
            payOrderResponseVO.setOrderNo(orderNo);
            if ("SUCCESS".equals(status)) {
                String amount = jsonObject.get("amount").toString();
                payOrderResponseVO.setMessage(jsonObject.getString("description"));
                payOrderResponseVO.setOrderNo(orderNo);
                payOrderResponseVO.setCode(0);
                payOrderResponseVO.setAmount(String.valueOf(amount));
                payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
            } else if ("PENDING".equals(status)){
                String amount = jsonObject.get("amount").toString();
                payOrderResponseVO.setAmount(amount);
                payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
            }  else if ("REJECTED".equals(status) || "REVERSED".equals(status) || "FAIL".equals(status)){
                String amount = jsonObject.get("amount").toString();
                payOrderResponseVO.setAmount(amount);
                payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
            } else {
                payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Abnormal.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payOrderResponseVO;
    }

    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        JSONObject paramObj = new JSONObject();
        paramObj.put("orderId", orderNo);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("apikey", channelRespVO.getPubKey());


        log.info("88mids代付订单查询请求参数: {}", paramObj);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/api/v2/external/payout/status", paramObj.toJSONString(), headerMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("88mids代付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("status");
            withdrawalResponseVO.setOrderNo(orderNo);
            if ("SUCCESS".equals(status)) {
                String amount = jsonObject.get("amount").toString();
                withdrawalResponseVO.setMessage(jsonObject.getString("description"));
                withdrawalResponseVO.setOrderNo(orderNo);
                withdrawalResponseVO.setCode(0);
                withdrawalResponseVO.setAmount(String.valueOf(amount));
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
            } else if ("PENDING".equals(status)){
                String amount = jsonObject.get("amount").toString();
                withdrawalResponseVO.setAmount(amount);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
            }  else if ("REJECTED".equals(status) || "REVERSED".equals(status) || "FAIL".equals(status)){
                String amount = jsonObject.get("amount").toString();
                withdrawalResponseVO.setAmount(amount);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
            } else {
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return withdrawalResponseVO;
    }
}
