package com.cloud.baowang.service.vendor.XPay;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.wallet.PayoutStatusEnum;
import com.cloud.baowang.common.core.utils.HttpClient4Util;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.pay.api.vo.*;
import com.cloud.baowang.service.vendor.BasePayService;
import com.cloud.baowang.util.SignUtil;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: fangfei
 * @createTime: 2024/09/30 14:55
 * @description: XPay
 */
@Slf4j
@Service(value = "XPay")
public class XPayService implements BasePayService {

    public static void main(String[] args) {
        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("221");
        channelRespVO.setMerNo("10793");
        channelRespVO.setSecretKey("kaewl2cPjRA3NHGEAw59PQRUwZKRBcPe");
        channelRespVO.setApiUrl("https://api.blizzardpay.pw");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setAmount("100");
        paymentVO.setUserId("8721389");
        paymentVO.setApplyIp("127.0.0.1");
        paymentVO.setCurrency("MYR");
        paymentVO.setBankCode("");

        String orderNO = OrderUtil.getOrderNo("B", 10);

        //new XPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
        //new XPayService().queryPayOrder(channelRespVO, "BZvUnfMqTd5");

        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = new SystemWithdrawChannelResponseVO();
        withdrawChannelResponseVO.setChannelCode("BANK");
        withdrawChannelResponseVO.setMerNo("10793");
        withdrawChannelResponseVO.setSecretKey("kaewl2cPjRA3NHGEAw59PQRUwZKRBcPe");
        withdrawChannelResponseVO.setApiUrl("https://api.blizzardpay.pw");
        withdrawChannelResponseVO.setCallbackUrl("https://gw.playesoversea.store");

        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("100.00");
        withdrawalVO.setCurrency("MYR");
        withdrawalVO.setBankCode("PBBB");
        withdrawalVO.setBankName("Public Bank Berhad");
        withdrawalVO.setBankNo("504333311001");
        withdrawalVO.setBankUserName("Wang Bao Lu");

        new XPayService().creatPayoutOrder(withdrawChannelResponseVO, withdrawalVO, orderNO);
        //  new XPayService().queryPayoutOrder(withdrawChannelResponseVO, "BvaAk5geWJb");
    }

    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String notifyUrl = "";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/xPayAgentCallback";
        } else {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/xPayCallback";
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appId", channelRespVO.getMerNo());
        paramMap.put("clientUserId", paymentVO.getUserId());
        paramMap.put("outTradeNo", orderNo);
        paramMap.put("channelId", channelRespVO.getChannelCode());
        paramMap.put("amount", paymentVO.getAmount());
        paramMap.put("callbackUrl", notifyUrl);
        paramMap.put("successUrl", notifyUrl);
        paramMap.put("clientUserIp", paymentVO.getApplyIp());

        String sign = SignUtil.paramSignsPay(paramMap, channelRespVO.getSecretKey()).toLowerCase();
        paramMap.put("sign", sign);

        log.info("XPay支付请求参数: {}", paramMap);
        String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/order/v2/create", paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("XPay支付返回：{}", jsonObject);
        } catch (Exception e) {
            log.error("XPay支付返回数据解析异常:{0}",e);
        }

        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        Integer code = jsonObject.getIntValue("code");
        if (200 == code) {
            JSONObject data = jsonObject.getJSONObject("data");

            paymentResponseVO.setCode(CommonConstant.business_zero);
            paymentResponseVO.setOrderId(orderNo);
            paymentResponseVO.setAmount(paymentVO.getAmount());
            paymentResponseVO.setThirdOrderId(data.getString("orderNo"));
            paymentResponseVO.setPaymentUrl(data.getString("payUrl"));
        } else {
            paymentResponseVO.setCode(code);
            paymentResponseVO.setMessage(jsonObject.getString("message"));
        }

        return paymentResponseVO;
    }

    @Override
    public WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, WithdrawalVO withdrawalVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/xPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/xPayoutAgentCallback";
        }

        String bankCode = XpayBankEnum.getBankCodeBySource(withdrawalVO.getBankCode());
        if (bankCode == null) {
            log.info("{} 不支持此银行出款：{}", orderNo, withdrawalVO.getBankCode());
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appId", channelRespVO.getMerNo());
        paramMap.put("outOrderNo", orderNo);
        paramMap.put("amount", withdrawalVO.getAmount());
        paramMap.put("bankName", bankCode);
        paramMap.put("bankBranch", "");
        paramMap.put("bankUserName", withdrawalVO.getBankUserName());
        paramMap.put("bankCard", withdrawalVO.getBankNo());
        paramMap.put("currency", withdrawalVO.getCurrency());
        paramMap.put("callbackUrl", notifyUrl);

        String sign = SignUtil.paramSignsPay(paramMap, channelRespVO.getSecretKey()).toLowerCase();
        paramMap.put("sign", sign);

        log.info("XPay代付请求：{}", paramMap);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/withdraw/apply", paramMap);

            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("XPay代付返回：{}", resultObj.toString());
            Integer code = resultObj.getIntValue("code");
            withdrawalResponseVO.setCode(code);

            if (code == 200) {
                JSONObject dataObject = resultObj.getJSONObject("data");
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                withdrawalResponseVO.setAmount(withdrawalVO.getAmount());
                withdrawalResponseVO.setOrderNo(dataObject.getString("outOrderNo"));
                withdrawalResponseVO.setWithdrawOrderId(dataObject.getString("orderNo"));
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
        paramMap.put("appId", channelRespVO.getMerNo());
        paramMap.put("outTradeNo", orderNo);

        String sign = SignUtil.paramSignsPay(paramMap, channelRespVO.getSecretKey()).toLowerCase();
        paramMap.put("sign", sign);

        log.info("XPay订单查询请求参数: {}", paramMap);
        String url = channelRespVO.getApiUrl() + "/order/query";

        JSONObject jsonObject = new JSONObject();
        try {
            String result = HttpUtil.get(url, paramMap);
            jsonObject = JSONObject.parseObject(result);
            log.info("XPay订单查询返回：{}", jsonObject);
            Integer code = jsonObject.getIntValue("code");
            payOrderResponseVO.setOrderNo(orderNo);
            if (200 == code) {
                String paySuccess = jsonObject.getJSONObject("data").get("paySuccess").toString();
                if ("true".equals(paySuccess)) {
                    String amount = jsonObject.getJSONObject("data").getString("amountTrue");
                    payOrderResponseVO.setCode(0);
                    payOrderResponseVO.setAmount(String.valueOf(amount));
                    payOrderResponseVO.setOrderNo(orderNo);
                    payOrderResponseVO.setThirdOrderNo(jsonObject.getJSONObject("data").getString("orderNo"));
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                } else {
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
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
        paramMap.put("appId", channelRespVO.getMerNo());
        paramMap.put("outOrderNo", orderNo);

        String sign = SignUtil.paramSignsPay(paramMap, channelRespVO.getSecretKey()).toLowerCase();
        paramMap.put("sign", sign);

        log.info("XPay代付订单查询请求参数: {}", paramMap);
        String url = channelRespVO.getApiUrl() + "/withdraw/query";

        JSONObject jsonObject = new JSONObject();
        try {
            String result = HttpUtil.get(url, paramMap);
            jsonObject = JSONObject.parseObject(result);
            log.info("XPay代付订单查询返回：{}", jsonObject);
            Integer code = jsonObject.getIntValue("code");
            withdrawalResponseVO.setOrderNo(orderNo);
            if (200 == code) {
                Integer status = jsonObject.getJSONObject("data").getIntValue("orderStatus");
                String pxId =  jsonObject.getJSONObject("data").getString("orderNo");
                if (status == 0 || status == 3) {
                    String amount = jsonObject.getJSONObject("data").getString("arrive");
                    withdrawalResponseVO.setOrderNo(orderNo);
                    withdrawalResponseVO.setWithdrawOrderId(pxId);
                    withdrawalResponseVO.setAmount(String.valueOf(amount));
                    withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                } else if (1 == status) {
                    String amount = jsonObject.getJSONObject("data").getString("arrive");
                    withdrawalResponseVO.setCode(0);
                    withdrawalResponseVO.setOrderNo(orderNo);
                    withdrawalResponseVO.setAmount(String.valueOf(amount));
                    withdrawalResponseVO.setWithdrawOrderId(pxId);
                    withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                } else if (2 == status) {
                    String amount = jsonObject.getJSONObject("data").getString("arrive");
                    withdrawalResponseVO.setOrderNo(orderNo);
                    withdrawalResponseVO.setWithdrawOrderId(pxId);
                    withdrawalResponseVO.setAmount(String.valueOf(amount));
                    withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                } else {
                    withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Abnormal.getCode());
                }
            } else {
                withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Abnormal.getCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return withdrawalResponseVO;
    }


}
