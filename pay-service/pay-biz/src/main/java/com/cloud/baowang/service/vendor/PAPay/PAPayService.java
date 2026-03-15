package com.cloud.baowang.service.vendor.PAPay;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.wallet.PayoutStatusEnum;
import com.cloud.baowang.common.core.utils.HttpClient4Util;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.pay.api.vo.*;
import com.cloud.baowang.service.vendor.BasePayService;
import com.cloud.baowang.service.vendor.PAPay.vo.BankEnum;
import com.cloud.baowang.util.SignUtil;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author: fangfei
 * @createTime: 2024/09/30 14:55
 * @description: pinganpay
 */
@Slf4j
@Service(value = "PAPay")
public class PAPayService implements BasePayService {

    public static void main(String[] args) {
//        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
//        channelRespVO.setChannelCode("VNCard");
//        channelRespVO.setMerNo("90000052");
//        channelRespVO.setPrivateKey("MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIvvSV0PGbiQBUxh8DGYPoyjDpZoMgNgMQHdY52ujdgf4jGo6pHRj/hp0tOsWvnI3Cnr51XJbaE8DAr5eRyTWsKSPrDbOBHfI4HOKUiYCDHHuCj5YzC8ZnK6blTEYYcizjZRL/evY7/dMdQltnEzRz01iMyxKnH/PGef2FJEyK2TAgMBAAECgYBmz+WQCOM19iLeburCFYCHZdn26hEs58S6AQYKuVAgJbTUFa5aeqUTt/SdbvncohYYeumZ3we8OdkDSy4Pr7gzMdRwyp+silZbgeGeTBV/E3hHXjjZNE+KpESW/0VM9VYpnAxAUShwZz7UJssw/nnireP6OSy1M3CdhLGQrHhaiQJBAPmjpzJA4CU/80rrc9da8j+cIBeZtCY5c+0xBBaF/G6D4IDVT2ECXABvBizOvvJdjC3htluT0eEPJkyOfKmaWrUCQQCPgA16HPki1tsr6787i28GbiPS6lSVZ/0YmehlBLp1wB6gWelDJy5N9KPuOOBw0krUdfzd8ZylNX8DJpaxzuwnAkBC09SGV2epkR0ICU/Rbr862AifSL45HVgzPtPhU6znyNXopzHAvmjylMYKaK4lI3XLMokmqsOcg/m0tNAadv+hAkBFeneL/0k08BhQDKWbe+g2kc8wiJqJeD7X0XTurDpnVqy9cKr7A5Zs0h0o80rf9UGb2sCi6R+x8dPcP6vIzWoTAkBhRaVSPImRVB7moiZGvK/oodzAfY3bWOWOWfwuSFA+IInki59501vBSO/4P6RycNhjxWVwcohzhNrnv9PDi+i8");
//        channelRespVO.setApiUrl("http://api.nsafepay.com"); //http://api.pingan.mom
//        channelRespVO.setCallbackUrl("https://gw.playesoversea.pro");
//
//        PaymentVO paymentVO = new PaymentVO();
//        paymentVO.setAmount("50000");
//        paymentVO.setApplyIp("127.0.0.1");
//        paymentVO.setCurrency("CNY");
//
        String orderNO = OrderUtil.getOrderNo("B", 10);

//        new PAPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
//        new PAPayService().queryPayOrder(channelRespVO, "BTfb8BZWYWp");


        SystemWithdrawChannelResponseVO withdrawchannelRespVO = new SystemWithdrawChannelResponseVO();
        withdrawchannelRespVO.setChannelCode("VNDBank");
        withdrawchannelRespVO.setMerNo("90000052");
        withdrawchannelRespVO.setPrivateKey("MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIvvSV0PGbiQBUxh8DGYPoyjDpZoMgNgMQHdY52ujdgf4jGo6pHRj/hp0tOsWvnI3Cnr51XJbaE8DAr5eRyTWsKSPrDbOBHfI4HOKUiYCDHHuCj5YzC8ZnK6blTEYYcizjZRL/evY7/dMdQltnEzRz01iMyxKnH/PGef2FJEyK2TAgMBAAECgYBmz+WQCOM19iLeburCFYCHZdn26hEs58S6AQYKuVAgJbTUFa5aeqUTt/SdbvncohYYeumZ3we8OdkDSy4Pr7gzMdRwyp+silZbgeGeTBV/E3hHXjjZNE+KpESW/0VM9VYpnAxAUShwZz7UJssw/nnireP6OSy1M3CdhLGQrHhaiQJBAPmjpzJA4CU/80rrc9da8j+cIBeZtCY5c+0xBBaF/G6D4IDVT2ECXABvBizOvvJdjC3htluT0eEPJkyOfKmaWrUCQQCPgA16HPki1tsr6787i28GbiPS6lSVZ/0YmehlBLp1wB6gWelDJy5N9KPuOOBw0krUdfzd8ZylNX8DJpaxzuwnAkBC09SGV2epkR0ICU/Rbr862AifSL45HVgzPtPhU6znyNXopzHAvmjylMYKaK4lI3XLMokmqsOcg/m0tNAadv+hAkBFeneL/0k08BhQDKWbe+g2kc8wiJqJeD7X0XTurDpnVqy9cKr7A5Zs0h0o80rf9UGb2sCi6R+x8dPcP6vIzWoTAkBhRaVSPImRVB7moiZGvK/oodzAfY3bWOWOWfwuSFA+IInki59501vBSO/4P6RycNhjxWVwcohzhNrnv9PDi+i8");
        withdrawchannelRespVO.setApiUrl("http://api.nsafepay.com"); //http://api.pingan.mom
        withdrawchannelRespVO.setCallbackUrl("https://gw.playesoversea.pro");

        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("100000");
        withdrawalVO.setCurrency("VND");
        withdrawalVO.setBankCode("ACBB");
        withdrawalVO.setBankNo("106882681911");
        withdrawalVO.setBankUserName("Nguyễn văn khỏe");

        new PAPayService().creatPayoutOrder(withdrawchannelRespVO, withdrawalVO, orderNO);
        new PAPayService().queryPayoutOrder(withdrawchannelRespVO, orderNO);
    }

    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/paPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/paPayAgentCallback";
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mchId", channelRespVO.getMerNo());
        paramMap.put("outTradeNo", orderNo);
        paramMap.put("payAmount", paymentVO.getAmount());
        paramMap.put("tradeType", channelRespVO.getChannelCode());
        paramMap.put("nonceStr", OrderUtil.createCharacter(10));
        paramMap.put("notifyUrl", notifyUrl);
        paramMap.put("attach", channelRespVO.getMerNo());

        String key = channelRespVO.getPrivateKey();

        String sign = SignUtil.getPaSignStr(paramMap, key);
        paramMap.put("sign", sign);

        log.info("PA支付请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/api/unifiedorder", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("PA支付返回：{}", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        if ("200".equals(jsonObject.getString("code"))) {
            JSONObject data = jsonObject.getJSONObject("data");
            paymentResponseVO.setCode(CommonConstant.business_zero);
            paymentResponseVO.setOrderId(orderNo);
            paymentResponseVO.setAmount(paymentVO.getAmount());
            paymentResponseVO.setThirdOrderId(data.getString("payOrderid"));
            paymentResponseVO.setPaymentUrl(data.getString("redirect"));

        } else {
            paymentResponseVO.setCode(jsonObject.getIntValue("code"));
            paymentResponseVO.setMessage(jsonObject.getString("msg"));
        }

        return paymentResponseVO;
    }

    @Override
    public WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, WithdrawalVO withdrawalVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/paPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/paPayoutAgentCallback";
        }

        String bankName = BankEnum.getVnBankCode(withdrawalVO.getBankCode());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mchId", channelRespVO.getMerNo());
        paramMap.put("payMoney", withdrawalVO.getAmount());
        paramMap.put("outTradeNo", orderNo);
        paramMap.put("notifyurl", notifyUrl);
        paramMap.put("type", channelRespVO.getChannelCode());
        JSONObject dataJson = new JSONObject();
        dataJson.put("banknumber", withdrawalVO.getBankNo());
        dataJson.put("bankname", bankName);
        dataJson.put("name", withdrawalVO.getBankUserName());
        paramMap.put("data", dataJson);

        String key = channelRespVO.getPrivateKey();

        String sign = SignUtil.getPaSignStr(paramMap, key);
        paramMap.put("sign", sign);

        log.info("PAPay代付请求：{}", paramMap);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/api/agentPay/issued", JSONObject.toJSONString(paramMap));

            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("PAPay代付返回：{}", resultObj.toString());
            Integer code = resultObj.getIntValue("code");
            withdrawalResponseVO.setCode(code);
            withdrawalResponseVO.setMessage(resultObj.getString("msg"));

            if (code == 200) {
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                withdrawalResponseVO.setWithdrawOrderId(resultObj.getString("payOrderid"));
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
            }  else  {
                withdrawalResponseVO.setCode(CommonConstant.business_negative1);
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
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mchId", channelRespVO.getMerNo());
        paramMap.put("outTradeNo", orderNo);

        String key = channelRespVO.getPrivateKey();

        String sign = SignUtil.getPaSignStr(paramMap, key);
        paramMap.put("sign", sign);

        log.info("PA订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/api/payQuery", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("PA订单查询返回：{}", jsonObject);
            payOrderResponseVO.setOrderNo(orderNo);
            Integer code = jsonObject.getIntValue("code");
            if (200 == code) {
                payOrderResponseVO.setMessage(jsonObject.getString("msg"));
                payOrderResponseVO.setOrderNo(orderNo);
                JSONObject data = jsonObject.getJSONObject("data");
                String amount = data.getString("payAmount");
                Integer status = data.getIntValue("status");
                if (0 == status) {
                    payOrderResponseVO.setAmount(String.valueOf(amount));
                    payOrderResponseVO.setThirdOrderNo(data.getString("payOrderid"));
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                } else if (1 == status || 2 == status) {
                    payOrderResponseVO.setCode(0);
                    payOrderResponseVO.setAmount(String.valueOf(amount));
                    payOrderResponseVO.setThirdOrderNo(data.getString("payOrderid"));
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                } else {
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Abnormal.getCode());
                }
            } else {
                payOrderResponseVO.setCode(-1);
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
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mchId", channelRespVO.getMerNo());
        paramMap.put("outTradeNo", orderNo);

        String key = channelRespVO.getPrivateKey();

        String sign = SignUtil.getPaSignStr(paramMap, key);
        paramMap.put("sign", sign);

        log.info("PA代付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/api/agentPay/query", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("PA代付订单查询返回：{}", jsonObject);
            Integer code = jsonObject.getIntValue("code");
            withdrawalResponseVO.setOrderNo(orderNo);
            if (200 == code) {
                withdrawalResponseVO.setMessage(jsonObject.getString("msg"));
                withdrawalResponseVO.setOrderNo(orderNo);
                JSONObject data = jsonObject.getJSONObject("data");
                String amount = data.getString("payAmount");
                Integer status = data.getIntValue("status");
                if (3 == status) {
                    withdrawalResponseVO.setAmount(String.valueOf(amount));
                    withdrawalResponseVO.setWithdrawOrderId(data.getString("payOrderid"));
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
                } else if (2 == status) {
                    withdrawalResponseVO.setCode(0);
                    withdrawalResponseVO.setAmount(String.valueOf(amount));
                    withdrawalResponseVO.setWithdrawOrderId(data.getString("payOrderid"));
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
                } else {
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
                }
            } else {
                withdrawalResponseVO.setCode(-1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return withdrawalResponseVO;
    }

}
