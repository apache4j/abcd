package com.cloud.baowang.service.vendor.JZPay;

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
 * @description: JZPay
 * https://apifox.com/apidoc/shared-1aa56c24-930b-4aca-90d8-4cb45d86773f
 * 访问密码: Nf9KN7FJ
 */
@Slf4j
@Service(value = "JZPay")
public class JZPayService implements BasePayService {

    public static void main(String[] args) {
        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("3");
        channelRespVO.setMerNo("MN000000000619");
        channelRespVO.setSecretKey("trSsmCrzpfo57XFF");
        channelRespVO.setApiUrl("https://api.jzpay168.info");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setAmount("100");
        paymentVO.setApplyIp("127.0.0.1");

        String orderNO = OrderUtil.getOrderNo("B", 10);

        //new JZPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
        //new JZPayService().queryPayOrder(channelRespVO, "BwDDWhXvjRe");
        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = new SystemWithdrawChannelResponseVO();
        withdrawChannelResponseVO.setChannelCode("Maya");
        withdrawChannelResponseVO.setMerNo("MN000000000619");
        withdrawChannelResponseVO.setSecretKey("trSsmCrzpfo57XFF");
        withdrawChannelResponseVO.setApiUrl("https://api.jzpay168.info");
        withdrawChannelResponseVO.setCallbackUrl("https://gw.playesoversea.store");


        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("10");
        withdrawalVO.setBankNo("9272100947");
        withdrawalVO.setBankUserName("Betty");
        withdrawalVO.setBankName("Maya");

        new JZPayService().creatPayoutOrder(withdrawChannelResponseVO, withdrawalVO, orderNO);
        //new JZPayService().queryPayoutOrder(channelRespVO, "BnCVNrrskKw");
    }


    /**
     * 付款请求
     * @param channelRespVO
     * @param paymentVO
     * @param orderNo
     * @return
     */
    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/jzPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/jzPayAgentCallback";
        }
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("mer_id", channelRespVO.getMerNo());
        paramMap.put("mer_no", orderNo);
        paramMap.put("amount", paymentVO.getAmount());
        paramMap.put("paytype", channelRespVO.getChannelCode());  //1Gcash 3Maya
        paramMap.put("notifyurl", notifyUrl);
        paramMap.put("timestamp", timestamp);


        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.getSignByMap(paramMap, key);
        paramMap.put("sign", sign);

        log.info("JZPay代收支付请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/ds_api", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("JZPay代收支付返回：{}", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        if ("success".equals(jsonObject.getString("status"))) {
            paymentResponseVO.setCode(CommonConstant.business_zero);
            paymentResponseVO.setOrderId(orderNo);
            paymentResponseVO.setAmount(paymentVO.getAmount());
            paymentResponseVO.setThirdOrderId(jsonObject.getString("transaction_id"));
            paymentResponseVO.setPaymentUrl(jsonObject.getString("payurl"));
        } else {
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage(jsonObject.getString("msg"));
        }

        return paymentResponseVO;
    }

    /**
     * 提款请求
     * @param channelRespVO
     * @param withdrawalVO
     * @param orderNo
     * @return
     */
    @Override
    public WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, WithdrawalVO withdrawalVO, String orderNo) {
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/jzPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/jzPayoutAgentCallback";
        }


        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("mer_id", channelRespVO.getMerNo());
        paramMap.put("mer_no", orderNo);
        paramMap.put("amount", withdrawalVO.getAmount());
        paramMap.put("bankname", channelRespVO.getChannelCode());  //fixme
        paramMap.put("accountname", withdrawalVO.getBankUserName());
        paramMap.put("cardnumber", withdrawalVO.getBankNo());
        paramMap.put("notifyurl", notifyUrl);
        paramMap.put("timestamp", timestamp);

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.getSignByMap(paramMap, key).toUpperCase();
        paramMap.put("sign", sign);

        log.info("JZPay代付请求：{}", paramMap);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/df_api", JSONObject.toJSONString(paramMap));

            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("JZPay代付返回：{}", resultObj.toString());
            String status = resultObj.getString("status");
            withdrawalResponseVO.setMessage(resultObj.getString("msg"));

            if ("success".equals(status)) {
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                withdrawalResponseVO.setOrderNo(orderNo);
                withdrawalResponseVO.setWithdrawOrderId(resultObj.getString("transaction_id"));
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
     * 查询订单
     * @param channelRespVO
     * @param orderNo
     * @return
     */
    @Override
    public PayOrderResponseVO queryPayOrder(SystemRechargeChannelBaseVO channelRespVO, String orderNo) {
        PayOrderResponseVO payOrderResponseVO = new PayOrderResponseVO();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("custId", channelRespVO.getMerNo());
        paramMap.put("merchantOrderId", orderNo);

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.getSignByMap(paramMap, key);
        paramMap.put("sign", sign);

        log.info("JZPay支付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/mer-api/order-in/query", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("JZPay支付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("status");
            if ("success".equals(status)) {
                payOrderResponseVO.setMessage(jsonObject.getString("msg"));
                payOrderResponseVO.setOrderNo(orderNo);
                JSONObject data = jsonObject.getJSONObject("data");

                //未支付、处理中状态=（1或者2） 成功状态=（4或者5或者6或者7）
                Integer orderStatus = data.getIntValue("orderStatus");
                 if (1 == orderStatus || 2 == orderStatus) {
                     payOrderResponseVO.setThirdOrderNo(data.getString("order"));
                     payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                } else if (4 == orderStatus || 5 == orderStatus || 6 == orderStatus || 7 == orderStatus) {
                     String amount = data.getString("orderAmount");
                     payOrderResponseVO.setThirdOrderNo(data.getString("order"));
                     payOrderResponseVO.setAmount(String.valueOf(amount));
                     payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                } else {
                     payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Abnormal.getCode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payOrderResponseVO;
    }

    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("custId", channelRespVO.getMerNo());
        paramMap.put("merchantOrderId", orderNo);

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.getSignByMap(paramMap, key);
        paramMap.put("sign", sign);

        log.info("JZPay代付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/mer-api/order-out/query", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("JZPay代付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("status");
            withdrawalResponseVO.setOrderNo(orderNo);
            if ("success".equals(status)) {
                withdrawalResponseVO.setMessage(jsonObject.getString("msg"));
                withdrawalResponseVO.setOrderNo(orderNo);
                JSONObject data = jsonObject.getJSONObject("data");

                //处理中状态：（1或者3或者21） ；成功状态=（8或者9或者10或者11）； 失败状态=（12或者13或者19或者20）；
                Integer orderStatus = data.getIntValue("orderStatus");
                if (12 == orderStatus || 13 == orderStatus || 19 == orderStatus || 20 == orderStatus) {
                    String amount = data.getString("orderAmount");
                    withdrawalResponseVO.setAmount(String.valueOf(amount));
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
                } else if (1 == orderStatus || 3 == orderStatus || 21 == orderStatus) {
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
                } else if (8 == orderStatus || 9 == orderStatus || 10 == orderStatus || 11 == orderStatus) {
                    String amount = data.getString("orderAmount");
                    withdrawalResponseVO.setCode(0);
                    withdrawalResponseVO.setWithdrawOrderId(data.getString("order"));
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
