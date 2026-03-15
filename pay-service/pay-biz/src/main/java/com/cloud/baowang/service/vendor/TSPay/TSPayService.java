package com.cloud.baowang.service.vendor.TSPay;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.wallet.PayoutStatusEnum;
import com.cloud.baowang.common.core.utils.HttpClient4Util;
import com.cloud.baowang.pay.api.vo.*;
import com.cloud.baowang.service.vendor.BasePayService;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service(value = "TSPay")
public class TSPayService implements BasePayService {

    static final String STATUS = "status";
    static final String DATA = "data";
    static final String MESSAGE = "msg";
    static final String LINK = "link";
    final static String PLATORDERNO = "platOrderNo";
    static final String AMOUNT = "amount";
    static final String PLAT_ORDER_NO = "platOrderNo";
    static final String ORDER_STATUS = "orderStatus";

    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        paymentResponseVO.setCode(CommonConstant.business_negative1);
        String notifyUrl = channelRespVO.getCallbackUrl() + "/pay/callback/api/tsPayCallback" ;
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl() + "/pay/callback/api/tsPayAgentCallback";
        }

        Optional<TSCurrencyBankCodePayEnum> byCurrencyAndCode = TSCurrencyBankCodePayEnum.findByCurrencyAndCode(channelRespVO.getCurrencyCode(), channelRespVO.getChannelCode());
        if (byCurrencyAndCode.isEmpty()) {
            paymentResponseVO.setMessage("币种和支付匹配bankCode错误");
            log.error("TSPay代收请求失败, 币种：{}, bank code {}", paymentVO.getCurrency(), paymentVO.getBankCode());
            return paymentResponseVO;
        }
        TSCurrencyBankCodePayEnum tsCurrencyBankCodePayEnum = byCurrencyAndCode.get();

        Map<String, Object> paramJson = new HashMap<>();
        paramJson.put("mchId", channelRespVO.getMerNo());

        paramJson.put("txChannel",  TSChannelEnum.TX_INDIA_001.getChannel());
        paramJson.put("appId", channelRespVO.getSecretKey());
        paramJson.put("timestamp", System.currentTimeMillis() / 1000);
        paramJson.put("mchOrderNo", orderNo);
        paramJson.put("bankCode", channelRespVO.getChannelCode());
        paramJson.put("amount", paymentVO.getAmount());
        if (!StrUtil.isEmpty(paymentVO.getDepositName())){
            paramJson.put("name", paymentVO.getDepositName());
        }else if (!StrUtil.isEmpty(paymentVO.getFirstName())){
            paramJson.put("name", paymentVO.getFirstName());
        }
        if (!StrUtil.isEmpty(paymentVO.getPhoneNum())){
            paramJson.put("phone", paymentVO.getPhoneNum());
        }else {
            paramJson.put("phone", generateIndianPhoneNumber());
        }
        if (!StrUtil.isEmpty(paymentVO.getEmail())){
            paramJson.put("email", paymentVO.getEmail());
        }else {
            paramJson.put("email", generateEmail());
        }
        paramJson.put("productInfo", "oksport-Rechange");
        paramJson.put("notifyUrl", notifyUrl);
        String privateKey = channelRespVO.getPrivateKey();
        String signature = "signature";
        try {
            signature = TSSignUtils.createSign(paramJson, privateKey);
        } catch (Exception e) {
            log.error("TSPay签名发生错误:", e);
            paymentResponseVO.setMessage("签名发生错误");
            return paymentResponseVO;
        }
        paramJson.put("sign", signature);
        log.info("TSPay代收支付请求参数: {}", paramJson);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + tsCurrencyBankCodePayEnum.getUri(), JSON.toJSONString(paramJson));
        JSONObject response = JSON.parseObject(result);
        if (response.get(STATUS) != null && Objects.equals(TSPayResponseCodeEnum.SUCCESS.getCode().toString(), response.get(STATUS).toString())) {            JSONObject dataJson = response.getJSONObject(DATA);
            paymentResponseVO.setCode(CommonConstant.business_zero);
            paymentResponseVO.setOrderId(orderNo);
            //这里的请求金额, 是取成功返回
            paymentResponseVO.setAmount(dataJson.getString(AMOUNT));
            paymentResponseVO.setThirdOrderId(dataJson.getString(PLATORDERNO));
            paymentResponseVO.setPaymentUrl(dataJson.getString(LINK));
        } else {
            paymentResponseVO.setMessage(response.getString(MESSAGE));
            log.error("TSPay代收请求失败, 返回：{}", response);
        }
        return paymentResponseVO;
    }

    @Override
    public WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, WithdrawalVO withdrawalVO, String orderNo) {

        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        withdrawalResponseVO.setCode(CommonConstant.business_negative1);

        String notifyUrl = channelRespVO.getCallbackUrl() + "/pay/callback/api/tsPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl() + "/pay/callback/api/tsPayoutAgentCallback";
        }
        Optional<TSCurrencyBankCodePayoutEnum> byCurrencyAndCode = TSCurrencyBankCodePayoutEnum.findByCurrencyAndCode(channelRespVO.getCurrencyCode(), channelRespVO.getChannelCode());
        if (byCurrencyAndCode.isEmpty()) {
            withdrawalResponseVO.setMessage("TSPay代付币种和支付匹配bankCode错误");
            log.error("TSPay代付请求失败, 币种：{}, bank code {}", withdrawalVO.getCurrency(), withdrawalVO.getBankCode());
            return withdrawalResponseVO;
        }
        TSCurrencyBankCodePayoutEnum tsCurrencyBankCodePayoutEnum = byCurrencyAndCode.get();

        Map<String, Object> paramJson = new HashMap<>();
        paramJson.put("mchId", channelRespVO.getMerNo());
        paramJson.put("txChannel", TSChannelEnum.TX_INDIA_001.getChannel());
        paramJson.put("appId", channelRespVO.getSecretKey());
        paramJson.put("timestamp", System.currentTimeMillis() / 1000);
        paramJson.put("mchOrderNo", orderNo);

        if (!StrUtil.isEmpty(withdrawalVO.getBankUserName())){
            paramJson.put("name", withdrawalVO.getBankUserName());
        }else {
            withdrawalResponseVO.setMessage("银行名字错误");
            return withdrawalResponseVO;
        }
        if (!StrUtil.isEmpty(withdrawalVO.getTelephone())){
            paramJson.put("phone", withdrawalVO.getTelephone());
        }else {
            withdrawalResponseVO.setMessage("电话号码错误");
            return withdrawalResponseVO;
        }
        if (!StrUtil.isEmpty(withdrawalVO.getEmail())){
            paramJson.put("email", withdrawalVO.getEmail());
        }else {
            withdrawalResponseVO.setMessage("email错误");
            return withdrawalResponseVO;
        }
        paramJson.put("bankCode", channelRespVO.getChannelCode());
        paramJson.put("account", withdrawalVO.getBankNo());
        paramJson.put("amount", withdrawalVO.getAmount());
        paramJson.put("notifyUrl", notifyUrl);
        if(channelRespVO.getChannelCode().equals("BANK_IN")){
            if (!StrUtil.isEmpty(withdrawalVO.getIfscCode())){
                paramJson.put("ifsc", withdrawalVO.getIfscCode());
            }else {
                paramJson.put("ifsc", "SBIN0001706");
            }
        }
        String privateKey = channelRespVO.getPrivateKey();
        String signature = "signature";
        try {
            signature = TSSignUtils.createSign(paramJson, privateKey);
        } catch (Exception e) {
            log.error("TSPay提现签名发生错误:", e);
            withdrawalResponseVO.setMessage("签名发生错误");
            return withdrawalResponseVO;
        }
        paramJson.put("sign", signature);
        log.info("TSPay代付请求：{}", paramJson);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + tsCurrencyBankCodePayoutEnum.getUri(), JSONObject.toJSONString(paramJson));

        JSONObject response = JSON.parseObject(result);
        withdrawalResponseVO.setOrderNo(orderNo);

        if (response.getInteger(STATUS) != null && Objects.equals(TSPayResponseCodeEnum.SUCCESS.getCode().toString(), response.get(STATUS).toString())) {
            JSONObject dataJson = response.getJSONObject(DATA);
            String orderStatus = dataJson.getString(ORDER_STATUS);
            withdrawalResponseVO.setWithdrawOrderId(dataJson.getString(PLAT_ORDER_NO));
            switch (TSOrderStatusEnum.valueOf(orderStatus)){
                case PENDING:
                    withdrawalResponseVO.setCode(CommonConstant.business_zero);
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
                    break;
                case SUCCESS:
                    withdrawalResponseVO.setCode(CommonConstant.business_zero);
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
                default:
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
                    withdrawalResponseVO.setMessage(response.getString("msg"));
                    log.error("TSPay提现请求失败, 返回：{}", response);
            }
        } else {
            withdrawalResponseVO.setMessage(response.getString("msg"));
            withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            log.error("TSPay提现请求异常, 返回：{}", response);
        }
        return withdrawalResponseVO;
    }

    @Override
    public PayOrderResponseVO queryPayOrder(SystemRechargeChannelBaseVO channelRespVO, String orderNo) {

        PayOrderResponseVO payOrderResponseVO = new PayOrderResponseVO();
        payOrderResponseVO.setCode(CommonConstant.business_negative1);
        Map<String, Object> paramJson = new HashMap<>();
        paramJson.put("mchId", channelRespVO.getMerNo());
        paramJson.put("timestamp", System.currentTimeMillis()/1000);
        paramJson.put("mchOrderNo", orderNo);
        //paramJson.put("trxId", orderNo); //商户单号和UTR必须传一个

        Optional<TSCurrencyBankCodePayoutEnum> byCurrencyAndCode = TSCurrencyBankCodePayoutEnum.findByCurrencyAndCode(channelRespVO.getCurrencyCode(), channelRespVO.getChannelCode());
        if (byCurrencyAndCode.isEmpty()) {
            payOrderResponseVO.setMessage("币种和支付匹配bankCode错误");
            log.error("TSPay代收查询请求通道和币种匹配失败, 币种：{}, bank code {}", channelRespVO.getCurrencyCode(), channelRespVO.getChannelCode());
            return payOrderResponseVO;
        }
        TSCurrencyBankCodePayoutEnum tsCurrencyBankCodePayoutEnum = byCurrencyAndCode.get();
        String privateKey = channelRespVO.getPrivateKey();
        String signature = "signature";
        try {
            signature = TSSignUtils.createSign(paramJson, privateKey);
        } catch (Exception e) {
            log.error("TSPay支付订单查询请求签名发生错误:", e);
            payOrderResponseVO.setMessage("签名发生错误");
            return payOrderResponseVO;
        }
        paramJson.put("signature", signature);
        log.info("TSPay支付订单查询请求参数: {}", paramJson);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + tsCurrencyBankCodePayoutEnum.getUri(), JSONObject.toJSONString(paramJson));

        JSONObject response = JSON.parseObject(result);
        payOrderResponseVO.setOrderNo(orderNo);
        if (response.get(STATUS) != null && Objects.equals(TSPayResponseCodeEnum.SUCCESS.getCode(), Integer.getInteger(response.get(STATUS).toString()))) {
            JSONObject dataJson = response.getJSONObject(DATA);
            payOrderResponseVO.setCode(CommonConstant.business_zero);
            payOrderResponseVO.setMessage(response.getString(MESSAGE));
            payOrderResponseVO.setThirdOrderNo(dataJson.getString(PLAT_ORDER_NO));
            switch (TSOrderStatusEnum.valueOf(dataJson.getString(ORDER_STATUS))){
                case PENDING:payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());break;
                case SUCCESS:payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());break;
                case FAILED,REFUND:payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());break;
                case PART_SUC:payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.ErrorAmount.getCode());break;
                //case ABNORMAL:payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Abnormal.getCode());break;
                default:payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Abnormal.getCode());break;
            }
        } else {
            log.error("TSPay支付订单查询请求错误, 返回：{}", response);
        }
        return payOrderResponseVO;
    }

    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        withdrawalResponseVO.setCode(CommonConstant.business_negative1);

        Map<String, Object> paramJson = new HashMap<>();
        paramJson.put("mchId", channelRespVO.getMerNo());
        paramJson.put("timestamp", System.currentTimeMillis()/1000);
        paramJson.put("mchOrderNo", orderNo);
        // paramJson.put("trxId", orderNo);

        Optional<TSCurrencyBankCodePayoutEnum> byCurrencyAndCode = TSCurrencyBankCodePayoutEnum.findByCurrencyAndCode(channelRespVO.getCurrencyCode(), channelRespVO.getChannelCode());
        if (byCurrencyAndCode.isEmpty()) {
            withdrawalResponseVO.setMessage("币种和支付匹配bankCode错误");
            log.error("TSPay代付订单查询请求通道和币种匹配失败, 币种：{}, bank code {}", channelRespVO.getCurrencyCode(), channelRespVO.getChannelCode());
            return withdrawalResponseVO;
        }
        TSCurrencyBankCodePayoutEnum tsCurrencyBankCodePayoutEnum = byCurrencyAndCode.get();
        String privateKey = channelRespVO.getPrivateKey();
        String signature = "signature";
        try {
            signature = TSSignUtils.createSign(paramJson, privateKey);
        } catch (Exception e) {
            log.error("TSPay代付订单查询请求签名发生错误:", e);
            withdrawalResponseVO.setMessage("签名发生错误");
            return withdrawalResponseVO;
        }
        paramJson.put("signature", signature);
        log.info("TSPay代付订单查询请求参数: {}", paramJson);

        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + tsCurrencyBankCodePayoutEnum.getUri(), JSONObject.toJSONString(paramJson));
        JSONObject response = JSON.parseObject(result);
        withdrawalResponseVO.setOrderNo(orderNo);
        if (response.get(STATUS) != null && Objects.equals(TSPayResponseCodeEnum.SUCCESS.getCode(), Integer.getInteger(response.get(STATUS).toString()))) {
            JSONObject dataJson = response.getJSONObject(DATA);
            withdrawalResponseVO.setCode(CommonConstant.business_zero);
            withdrawalResponseVO.setMessage(response.getString(MESSAGE));
            withdrawalResponseVO.setWithdrawOrderId(dataJson.getString(PLAT_ORDER_NO));
            switch (TSOrderStatusEnum.valueOf(dataJson.getString(ORDER_STATUS))){
                case PENDING:withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());break;
                case SUCCESS:withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());break;
                case FAILED, REFUND:withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());break;
                case PART_SUC:withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.ErrorAmount.getCode());break;
                default:withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Abnormal.getCode());break;
            }
        } else {
            log.error("TSPay代付订单查询请求错误, 返回：{}", response);
        }
        return withdrawalResponseVO;
    }

    public static String generateIndianPhoneNumber() {
        Random rand = new Random();
        // 印度手机号一般是10位数，开头是 6, 7, 8, 或 9
        StringBuilder phoneNumber = new StringBuilder();
        phoneNumber.append((6 + rand.nextInt(4)));
        for (int i = 0; i < 9; i++) {
            phoneNumber.append(rand.nextInt(10)); // 添加剩下的9个数字
        }
        return phoneNumber.toString();
    }

    public static String generateEmail() {
        Random rand = new Random();
        StringBuilder emailNumber = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            emailNumber.append(rand.nextInt(10)); // 添加剩下的9个数字
        }
        return emailNumber.append("@gmail.com").toString();
    }

    public static void main(String[] args) {

        System.out.println(generateIndianPhoneNumber());
        System.out.println(generateIndianPhoneNumber());
        System.out.println(generateIndianPhoneNumber());
        System.out.println(generateIndianPhoneNumber());
        System.out.println(generateIndianPhoneNumber());
        System.out.println(generateIndianPhoneNumber());
        System.out.println(generateIndianPhoneNumber());
        System.out.println(generateIndianPhoneNumber());
        System.out.println(generateIndianPhoneNumber());
        System.out.println(generateIndianPhoneNumber());

        String mchId = "7163660293";
        /*String mchId = "7163660293";
        String appId = "R22e9c0f1x3hycRKd9";
        String key = """
                MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7cj9naniQJyhMgcDnXli7AwQ0PpwL+qXhGrpPalYY5Ff4PxqDpkOIsD9z77Xkjv/EhWWGP/kZ9vhaM0IQkzwQMtVK7UlpvfVie2fwadKCHiaYWqOXikWW+Q1lzITdQXtlMYsXCcCubayRyeInClFGTv5BUFQHNcHph/5GwomCIR8UaO+6Y+aglpr3YXSOWxt9Cuk4/6eqj3PqqJFlZFOez3acEVw6wPt7b30z5GvAIqNZ63X4TQEIrf+VFVCbzhUUSFckQAPQ1sf5TAbkwYUUph1w+xYi7q61i1HU3466jVvmfmiBvImhMlcjr0EDw+RdmWYpWNCZ/2l+OVCADmMFQIDAQAB""";

        String privateKey = """
                MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDtyP2dqeJAnKEyBwOdeWLsDBDQ+nAv6peEauk9qVhjkV/g/GoOmQ4iwP3PvteSO/8SFZYY/+Rn2+FozQhCTPBAy1UrtSWm99WJ7Z/Bp0oIeJphao5eKRZb5DWXMhN1Be2UxixcJwK5trJHJ4icKUUZO/kFQVAc1wemH/kbCiYIhHxRo77pj5qCWmvdhdI5bG30K6Tj/p6qPc+qokWVkU57PdpwRXDrA+3tvfTPka8Aio1nrdfhNAQit/5UVUJvOFRRIVyRAA9DWx/lMBuTBhRSmHXD7FiLurrWLUdTfjrqNW+Z+aIG8iaEyVyOvQQPD5F2ZZilY0Jn/aX45UIAOYwVAgMBAAECggEAOsiA1hy8159BBFaSkLgE+fjDoX4ABQB4I56weXNna/nw23RbHa+9vF68gEKcFUUqugRHHngINZq4f0mMnKNbk5EQxaTbIYtMKXRqlZuvJCf4dlO6zVFX+zCQBQg2oivsf2Z8ae2k8VdWfFusHHrhX6shVSi/ztljxTBLS2F3ZIwuDwOofTMhpUampXkAkL/T2hLHb8+8nR2KCpLv/smNJxQKNJRVlPd2yjfwFPNcjhQD1P8V2vR49J1GCxfbRM5RyjGDW5rDHmeJYFGDPkgdtbYrhVSxtH3tNSVj0N+jdkvWcs7Huw6+9vgkSfbwQQeQG26JSCedo+wjkfKwaNgKEwKBgQD87U8ebeRSm/WuBWkeIM1gN54ZDxpyaCXj1+SsEZlaLigLWaJz0n97lVIZrrlcZxpH7irHL7InOeKpGkenrPg/13tyzOaRZHgv4d/wH+9raiTOqFxX0+4AIXXL90BHHAEmtVvOCe1gj3G1Ab5zODmQnTg2k9jRg/LsLK+cZjWPkwKBgQDwrJXMTzYGJ0iX42JAQ081psTnplrGw0Ol4tuk6ZF8WIGbWJz3RrgCWlZoxTEyALqHlfyX1LLnG5EV/D/EWjUKnXrg2DBMAgiKHVLIqDKqzo5KInDBeLRIMeQIjc97c0PED0B9rYxAMQlzhDCMfpMNYvOurTrOoTD66Yo/an6utwKBgQD7EB0/WVoRXlqrGRfVHj5/SfYXbdSUyCkEV+PXociVqcd6LMmDKun8pKZQdA3vWTDPQe0Yt5GiOmVx8nI6UQpDZQRXWmls3UwoS5Bc7XZdK4nJ0XmIfQh8/LnPc2lMrNG/uLqedWA6P2lhgY+pE6Cgmk+YbX4Fy3KxWVIgb5yvHQKBgC82K8yAXWCnpB3nhQaGLaugo0+t2nDTLRoFfoDDML7rvcDziRcY0E/l2L2EsTnV2sFuMra2CsI7LiLZ8MhbybvjWI9y2UaPv40YY3zpQccv7cmtJ+FMBFGFN+5VozXcTpbPRx9gQ/rzo579d2iYC9C8cID7imWuSSVvWqdGBMPRAoGBANh441MXSWg0VaG/HXYf+gjmE4JxxrZGDsl6CYMVhX4V3mYfEFSRIajxXh80ZRtZYFh3mt7DUo3qugRKawy5kMYZlCZ6s2/dTh1teVyjFFA12KxpZAulWjg0p0mVQZGbv4nonREHFIjwCxrT6y8B0+TU3YT0+W0j5vBly//7U+O1""";

        String jsonString = """
                {
                  "bankCode" : "UPI",
                  "amount" : "600",
                  "mchId" : "2061545184",
                  "mchOrderNo" : "CKINR20250422133953AP",
                  "sign" : "nEBmlD+64kwTkQ6c9zvMkR92II3zsHTe1fluPEVufaXemE3WB/M8oz4tNAwP+zS5/FWayK62k5kMXHbT7tkoA9bJL3nz8b2Tu+qXegXEGy3GCyl5o14o6lIIyxP0/81MyNXHCq3trhMdIQ1awwZXKGqg2qCEKOfcdZU4FixLpHtGnGKTmWNKPr++f8PAX9U3jwIV0yFpNOsdkakyw1Ufv2/ujuTD4j9FQZlLm5q3gq6Bf+ACNP67baw8nq/0e2wRIy0beGdb6GrsCoiRjWuE7cVM12SIND6xkTuYaGN7AckcLvrgWwv26P/jL+ARraDEYydJBnhwOuo3+ikbdbS7IQ==",
                  "txChannel" : "TX_INDIA_001",
                  "productInfo" : "xyc-Rechange",
                  "phone" : "0123456789",
                  "appId" : "R22e9c0f1x3hycRKd9",
                  "name" : "mf600",
                  "notifyUrl" : "https://9adc-103-20-81-119.ngrok-free.app/pay/callback/api/tsPayCallback",
                  "returnUrl" : "www.google.com",
                  "email" : "123456789@gmail.com",
                  "timestamp" : 1745304003
                }""";


        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        Map<String, Object> mapObject = jsonObject.toJavaObject(Map.class);

        String signatureNew = "";


        try {
            signatureNew = TSSignUtils.createSign(mapObject, privateKey);
            System.out.println(signatureNew);
        } catch (Exception e) {
            log.error("签名发生错误");
        }

        mapObject.put("sign" ,signatureNew);

        try {
            boolean b = TSSignUtils.verifySign(mapObject, key);
            System.out.println(b);
        } catch (Exception e) {
            log.error("签名发生错误2");
        }*/

    }

}
