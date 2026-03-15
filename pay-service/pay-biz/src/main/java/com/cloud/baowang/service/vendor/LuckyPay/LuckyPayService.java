package com.cloud.baowang.service.vendor.LuckyPay;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.wallet.PayoutStatusEnum;
import com.cloud.baowang.common.core.utils.HttpClient4Util;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.pay.api.vo.PayOrderResponseVO;
import com.cloud.baowang.pay.api.vo.PaymentResponseVO;
import com.cloud.baowang.pay.api.vo.PaymentVO;
import com.cloud.baowang.pay.api.vo.WithdrawalResponseVO;
import com.cloud.baowang.pay.api.vo.WithdrawalVO;
import com.cloud.baowang.service.vendor.BasePayService;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: fangfei
 * @createTime: 2024/09/30 14:55
 * @description: LuckyPay
 *
 * URL: https://www.showdoc.com.cn/2372139446426692
 * Password:LP51
 * It includes explanations and examples on using the interface.
 */
@Slf4j
@Service(value = "LuckyPay")
public class LuckyPayService implements BasePayService {

    public static void main(String[] args) {
        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("DUITNOW");
        channelRespVO.setMerNo("code");
        channelRespVO.setSecretKey("priKey");
        channelRespVO.setApiUrl("https://api1.luckymy.club");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setAmount("100");
        paymentVO.setUserId("8721389");
        paymentVO.setApplyIp("127.0.0.1");
        paymentVO.setCurrency("MYR");
        paymentVO.setBankCode("");

        String orderNO = OrderUtil.getOrderNo("B", 10);

        new LuckyPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
        //new LuckyPayService().queryPayOrder(channelRespVO, "Brqrb4YmdbX");

        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = new SystemWithdrawChannelResponseVO();
        withdrawChannelResponseVO.setChannelCode("BANK");
        withdrawChannelResponseVO.setMerNo("code");
        withdrawChannelResponseVO.setSecretKey("priKey");
        withdrawChannelResponseVO.setApiUrl("https://api1.luckymy.club");
        withdrawChannelResponseVO.setCallbackUrl("https://gw.playesoversea.store");

        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("100");
        withdrawalVO.setCurrency("MYR");
        withdrawalVO.setBankCode("PBBB");
        withdrawalVO.setBankNo("5045451001");
        withdrawalVO.setBankUserName("Wang Bao Lu");

       // new LuckyPayService().creatPayoutOrder(withdrawChannelResponseVO, withdrawalVO, orderNO);
       // new LuckyPayService().queryPayoutOrder(channelRespVO, "BY5jVrhmWtc");
    }

    /**
     * 充值请求
     * @param channelRespVO
     * @param paymentVO
     * @param orderNo
     * @return
     */
    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String notifyUrl = "";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/luckyPayAgentCallback";
        } else {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/luckyPayCallback";
        }

        String requestTime = String.valueOf(System.currentTimeMillis());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("clientCode", channelRespVO.getMerNo());
        paramMap.put("chainName", "BANK");
        paramMap.put("coinUnit", paymentVO.getCurrency());
        paramMap.put("clientNo", orderNo);
        paramMap.put("memberFlag", paymentVO.getUserId());
        paramMap.put("requestAmount", paymentVO.getAmount());
        paramMap.put("requestTimestamp", requestTime);
        paramMap.put("callbackurl", notifyUrl);
        paramMap.put("hrefbackurl", notifyUrl);
        paramMap.put("toPayQr", "0");
        paramMap.put("channel", channelRespVO.getChannelCode());

        //md5(clientCode&chainName&coinUnit&clientNo&requestTimestamp+privateKey)
        String key = channelRespVO.getSecretKey();
        String signStr = channelRespVO.getMerNo() + "&" +
                "BANK"  + "&" +
                paymentVO.getCurrency() + "&" +
                orderNo + "&" +
                requestTime  + key ;

        String sign = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();
        paramMap.put("sign", sign);

        log.info("LuckyPay支付请求参数: {}", paramMap);
        String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/api/coin/pay/request", paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("LuckyPay支付返回：{}", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * 代付请求
     * @param channelRespVO
     * @param withdrawalVO
     * @param orderNo
     * @return
     */
    @Override
    public WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, WithdrawalVO withdrawalVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/luckyPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/luckyPayoutAgentCallback";
        }

        String requestTime = String.valueOf(System.currentTimeMillis());

        String bankCode = BankEnum.getBankCodeBySource(withdrawalVO.getBankCode());
        if (bankCode == null) {
            log.info("{} 不支持此银行出款：{}", orderNo, withdrawalVO.getBankCode());
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("clientCode", channelRespVO.getMerNo());
        paramMap.put("chainName", channelRespVO.getChannelCode());
        paramMap.put("coinUnit", withdrawalVO.getCurrency());
        paramMap.put("clientNo", orderNo);
        paramMap.put("bankCardNum", withdrawalVO.getBankNo());
        paramMap.put("bankUserName", withdrawalVO.getBankUserName());
        paramMap.put("ifsc", "ifsc");
        paramMap.put("bankName", bankCode);
        paramMap.put("amount", withdrawalVO.getAmount());
        paramMap.put("requestTimestamp", requestTime);
        paramMap.put("callbackurl", notifyUrl);

        String key = channelRespVO.getSecretKey();
        String signStr = channelRespVO.getMerNo() + "&" +
                channelRespVO.getChannelCode()  + "&" +
                withdrawalVO.getCurrency() + "&" +
                orderNo + "&" +
                requestTime  + key ;

        String sign = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();
        paramMap.put("sign", sign);

        log.info("LuckyPay代付请求：{}", paramMap);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/api/bank/agentPay/request", paramMap);

            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("LuckyPay代付返回：{}", resultObj.toString());
            Integer code = resultObj.getIntValue("code");
            withdrawalResponseVO.setCode(code);

            if (code == 200) {
                JSONObject dataObject = resultObj.getJSONObject("data");
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
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

    /**
     * 充值订单查询
     * @param channelRespVO
     * @param orderNo
     * @return
     */
    @Override
    public PayOrderResponseVO queryPayOrder(SystemRechargeChannelBaseVO channelRespVO, String orderNo) {
        PayOrderResponseVO payOrderResponseVO = new PayOrderResponseVO();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("clientCode", channelRespVO.getMerNo());
        paramMap.put("clientNo", orderNo);

        String key = channelRespVO.getSecretKey();
        String signStr = channelRespVO.getMerNo() + "&" + orderNo + key ;

        String sign = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();
        paramMap.put("sign", sign);

        log.info("LuckyPay订单查询请求参数: {}", paramMap);
        String url = channelRespVO.getApiUrl() + "/api/coin/pay/checkOrder?" +
                "clientCode=" + channelRespVO.getMerNo() +
                "&clientNo=" + orderNo +
                "&sign=" + sign;

        JSONObject jsonObject = new JSONObject();
        try {
            String result = HttpClient4Util.get(url);
            jsonObject = JSONObject.parseObject(result);
            log.info("LuckyPay订单查询返回：{}", jsonObject);
            Integer code = jsonObject.getIntValue("code");
            payOrderResponseVO.setOrderNo(orderNo);
            if (200 == code) {
                String status = jsonObject.getJSONObject("data").getString("status");
                if ("CREATE".equals(status)) {
                    String amount = jsonObject.getJSONObject("data").getString("payAmount");
                    payOrderResponseVO.setAmount(String.valueOf(amount));
                    payOrderResponseVO.setOrderNo(orderNo);
                    payOrderResponseVO.setThirdOrderNo(jsonObject.getJSONObject("data").getString("orderNo"));
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                } else if ("PAID".equals(status) || "FINISH".equals(status)) {
                    String amount = jsonObject.getJSONObject("data").getString("payAmount");
                    payOrderResponseVO.setCode(0);
                    payOrderResponseVO.setOrderNo(orderNo);
                    payOrderResponseVO.setThirdOrderNo(jsonObject.getJSONObject("data").getString("orderNo"));
                    payOrderResponseVO.setAmount(String.valueOf(amount));
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                } else if ("CANCEL".equals(status) || "FAILED".equals(status)) {
                    String amount = jsonObject.getJSONObject("data").getString("requestAmount");
                    payOrderResponseVO.setAmount(String.valueOf(amount));
                    payOrderResponseVO.setOrderNo(orderNo);
                    payOrderResponseVO.setThirdOrderNo(jsonObject.getJSONObject("data").getString("orderNo"));
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
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

    /**
     * 提现订单查询
     * @param channelRespVO
     * @param orderNo
     * @return
     */
    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("clientCode", channelRespVO.getMerNo());
        paramMap.put("clientNo", orderNo);

        String key = channelRespVO.getSecretKey();
        String signStr = channelRespVO.getMerNo() + "&" + orderNo + key ;

        String sign = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();
        paramMap.put("sign", sign);

        log.info("LuckyPay代付订单查询请求参数: {}", paramMap);
        String url = channelRespVO.getApiUrl() + "/api/coin/agentPay/checkOrder?" +
                "clientCode=" + channelRespVO.getMerNo() +
                "&clientNo=" + orderNo +
                "&sign=" + sign;

        JSONObject jsonObject = new JSONObject();
        try {
            String result = HttpClient4Util.get(url);
            jsonObject = JSONObject.parseObject(result);
            log.info("LuckyPay代付订单查询返回：{}", jsonObject);
            Integer code = jsonObject.getIntValue("code");
            withdrawalResponseVO.setOrderNo(orderNo);
            if (200 == code) {
                String status = jsonObject.getJSONObject("data").getString("status");
                if ("PAYING".equals(status) || "CREATE".equals(status)) {
                    String amount = jsonObject.getJSONObject("data").getString("payAmount");
                    withdrawalResponseVO.setOrderNo(orderNo);
                    withdrawalResponseVO.setAmount(String.valueOf(amount));
                    withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                } else if ("PAID".equals(status) || "FINISH".equals(status)) {
                    String amount = jsonObject.getJSONObject("data").getString("payAmount");
                    withdrawalResponseVO.setCode(0);
                    withdrawalResponseVO.setAmount(String.valueOf(amount));
                    withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                } else if ("CANCEL".equals(status) || "FAILED".equals(status)) {
                    String amount = jsonObject.getJSONObject("data").getString("requestAmount");
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
