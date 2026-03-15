package com.cloud.baowang.service.vendor.MhdGcPay;

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
 * @description: MhdPay
 */
@Slf4j
@Service(value = "MhdGcPay")
public class MhdGcPayService implements BasePayService {

    public static void main(String[] args) {
        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("GCash");
        channelRespVO.setMerNo("1id0o271000d4rxjsnszhzi600ycgikj");
        channelRespVO.setSecretKey("1id0o271000d4rxjsnszlee700zlv0tp");
        channelRespVO.setApiUrl("https://tiancheng.mhd16.com"); //http://api.pingan.mom
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setAmount("1000");
        paymentVO.setApplyIp("127.0.0.1");
        paymentVO.setCurrency("CNY");

        String orderNO = OrderUtil.getOrderNo("B", 10);

        //new MhdGcPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
        //new MhdGcPayService().queryPayOrder(channelRespVO, "BDYTuu9uq5V");

        SystemWithdrawChannelResponseVO withResponseVO = new SystemWithdrawChannelResponseVO();
        withResponseVO.setChannelCode("GCash");
        withResponseVO.setMerNo("1id0o271000d4rxjsnszhzi600ycgikj");
        withResponseVO.setSecretKey("1id0o271000d4rxjsnszlee700zlv0tp");
        withResponseVO.setApiUrl("https://tiancheng.mhd16.com"); //http://api.pingan.mom
        withResponseVO.setCallbackUrl("https://gw.playesoversea.store");

        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("100");
        withdrawalVO.setBankNo("106882681911");
        withdrawalVO.setBankUserName("Betty");

        new MhdGcPayService().creatPayoutOrder(withResponseVO, withdrawalVO, orderNO);
        //new MhdGcPayService().queryPayoutOrder(channelRespVO, "BKS4FkweAJB");
    }

    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/mhdGcPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/mhdGcPayAgentCallback";
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("parter", channelRespVO.getMerNo());
        paramMap.put("value", paymentVO.getAmount());
        paramMap.put("type", channelRespVO.getChannelCode());
        paramMap.put("orderid", orderNo);
        paramMap.put("notifyurl", notifyUrl);
        paramMap.put("ip", paymentVO.getApplyIp());
        paramMap.put("remark", channelRespVO.getMerNo());
        paramMap.put("callbackurl", "https://h5.playesoversea.store");

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.paramSignsPay(paramMap, key);
        paramMap.put("sign", sign);

        log.info("MhdGc支付请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/payment/create", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("MhdGc支付返回：{}", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        if (jsonObject.get("code").toString().equals("200")) {
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
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/mhdGcPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/mhdGcPayoutAgentCallback";
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

        log.info("MhdGcPay代付请求：{}", paramMap);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/pay/transfer", JSONObject.toJSONString(paramMap));

            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("MhdGcPay代付返回：{}", resultObj.toString());
            Integer code = resultObj.getIntValue("code");
            withdrawalResponseVO.setCode(code);
            withdrawalResponseVO.setMessage(resultObj.getString("msg"));

            if (code == 200) {
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                withdrawalResponseVO.setOrderNo(orderNo);
                withdrawalResponseVO.setWithdrawOrderId(resultObj.getString("payOrderid"));
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
            }  else  {
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

        log.info("MhdGc支付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/pay/query", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("MhdGc支付订单查询返回：{}", jsonObject);
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

        log.info("MhdGcPay代付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/pay/transfer/query", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("MhdGcPay代付订单查询返回：{}", jsonObject);
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

}
