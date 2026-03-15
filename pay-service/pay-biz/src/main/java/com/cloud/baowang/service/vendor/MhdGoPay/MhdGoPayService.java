package com.cloud.baowang.service.vendor.MhdGoPay;

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
import org.springframework.util.DigestUtils;

import java.util.*;

/**
 * @author: fangfei
 * @createTime: 2024/09/30 14:55
 * @description: MhdPay
 */
@Slf4j
@Service(value = "MhdGoPay")
public class MhdGoPayService implements BasePayService {

    public static void main1(String[] args) {
        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("Gotyme");
        channelRespVO.setMerNo("1id0o271000d4re2flta38a4004z8z35");
        channelRespVO.setSecretKey("1id0o271000d4re2flta4kz500megb42");
        channelRespVO.setApiUrl("https://tiancheng.mhd16.com"); //http://api.pingan.mom
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setAmount("1000");
        paymentVO.setApplyIp("127.0.0.1");
        paymentVO.setCurrency("CNY");

        String orderNO = OrderUtil.getOrderNo("B", 10);

        //new MhdGoPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
        new MhdGoPayService().queryPayOrder(channelRespVO, "BDYTuu9uq5V");

        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("100");
        withdrawalVO.setBankNo("106882681911");
        withdrawalVO.setBankUserName("Betty");

        //new MhdGoPayService().creatPayoutOrder(channelRespVO, withdrawalVO, orderNO);
        //new MhdGoPayService().queryPayoutOrder(channelRespVO, "BKS4FkweAJB");
    }

    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/mhdGoPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/mhdGoPayAgentCallback";
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("parter", channelRespVO.getMerNo());
        paramMap.put("value", paymentVO.getAmount());
        paramMap.put("type", channelRespVO.getChannelCode());
        paramMap.put("orderid", orderNo);
        paramMap.put("notifyurl", notifyUrl);
        paramMap.put("callbackurl", "https://h5.playesoversea.store");

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.paramSignsPay(paramMap, key);
        paramMap.put("ip", paymentVO.getApplyIp());
        paramMap.put("remark", channelRespVO.getMerNo());
        paramMap.put("sign", sign);

        log.info("MhdGo支付请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/payment/create", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("MhdGo支付返回：{}", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        if ("200".equals(jsonObject.getString("code"))) {
            JSONObject data = jsonObject.getJSONObject("param");
            paymentResponseVO.setCode(CommonConstant.business_zero);
            paymentResponseVO.setOrderId(orderNo);
            paymentResponseVO.setAmount(paymentVO.getAmount());
            paymentResponseVO.setPaymentUrl(data.getString("payment_url"));

        } else {
            paymentResponseVO.setCode(jsonObject.getIntValue("code"));
            paymentResponseVO.setMessage(jsonObject.getString("info"));
        }

        return paymentResponseVO;
    }

    @Override
    public WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, WithdrawalVO withdrawalVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/mhdGoPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/mhdGoPayoutAgentCallback";
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("parter", channelRespVO.getMerNo());
        paramMap.put("money", withdrawalVO.getAmount());
        paramMap.put("order_no", orderNo);
        paramMap.put("notify_url", notifyUrl);
        paramMap.put("type", channelRespVO.getChannelCode());
        paramMap.put("name", withdrawalVO.getBankUserName());
        paramMap.put("account_number", withdrawalVO.getBankNo());

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.paramSignsPay(paramMap, key);
        paramMap.put("sign", sign);

        log.info("MhdPay代付请求：{}", paramMap);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/pay/transfer", JSONObject.toJSONString(paramMap));

            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("MhdPay代付返回：{}", resultObj.toString());
            Integer code = resultObj.getIntValue("code");
            withdrawalResponseVO.setCode(code);
            withdrawalResponseVO.setMessage(resultObj.getString("msg"));

            if (code == 200) {
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                withdrawalResponseVO.setWithdrawOrderId(resultObj.getString("payOrderid"));
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
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("parter", channelRespVO.getMerNo());
        paramMap.put("orderid", orderNo);

        String key = channelRespVO.getPrivateKey();

        String sign = SignUtil.paramSignsPay(paramMap, key);
        paramMap.put("sign", sign);

        log.info("MhdGo支付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/pay/query", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("MhdGo支付订单查询返回：{}", jsonObject);
            Integer code = jsonObject.getIntValue("code");
            payOrderResponseVO.setOrderNo(orderNo);
            if (200 == code) {
                payOrderResponseVO.setMessage(jsonObject.getString("msg"));
                payOrderResponseVO.setOrderNo(orderNo);
                JSONObject data = jsonObject.getJSONObject("param");
                String amount = data.getString("price");
                payOrderResponseVO.setAmount(String.valueOf(amount));
                payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
            } else if (300 == code) {
                payOrderResponseVO.setCode(-1);
                payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
            } else {
                payOrderResponseVO.setCode(-1);
                payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
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
        paramMap.put("parter", channelRespVO.getMerNo());
        paramMap.put("order_no", orderNo);

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.paramSignsPay(paramMap, key);
        paramMap.put("sign", sign);

        log.info("MhdGoPay代付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/pay/transfer/query", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("MhdGoPay代付订单查询返回：{}", jsonObject);
            Integer code = jsonObject.getIntValue("code");
            withdrawalResponseVO.setOrderNo(orderNo);
            if (200 == code) {
                withdrawalResponseVO.setMessage(jsonObject.getString("msg"));
                withdrawalResponseVO.setOrderNo(orderNo);
                JSONObject data = jsonObject.getJSONObject("param");

                Integer status = data.getIntValue("status");
                if (4 == status) {
                    String amount = data.getString("price");
                    withdrawalResponseVO.setAmount(String.valueOf(amount));
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
                } else if (1 == status || 2 == status) {
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
                } else if (3 == status) {
                    String amount = data.getString("price");
                    withdrawalResponseVO.setCode(0);
                    withdrawalResponseVO.setAmount(String.valueOf(amount));
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

    public static void main(String[] args) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("parter", "1id0o271000d4re2flta38a4004z8z35");
        paramMap.put("value", "1000.00");
        paramMap.put("opstate", "1");
        paramMap.put("orderid", "B4RU5XGcsVa");
        paramMap.put("remark", "1id0o271000d4re2flta38a4004z8z35");

        String key = "1id0o271000d4re2flta4kz500megb42";
        String str = "opstate=1&orderid=B4RU5XGcsVa&ovalue=1000.00&parter=1id0o271000d4re2flta38a4004z8z35&remark=1id0o271000d4re2flta38a4004z8z35&key=" + key;
        String sign = DigestUtils.md5DigestAsHex(str.getBytes());
        System.out.println(sign);
    }

}
