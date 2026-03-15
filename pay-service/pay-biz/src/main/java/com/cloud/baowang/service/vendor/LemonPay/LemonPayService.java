package com.cloud.baowang.service.vendor.LemonPay;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.BigDecimalConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.wallet.PayoutStatusEnum;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.HttpClient4Util;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.pay.api.vo.PayOrderResponseVO;
import com.cloud.baowang.pay.api.vo.PaymentResponseVO;
import com.cloud.baowang.pay.api.vo.PaymentVO;
import com.cloud.baowang.pay.api.vo.WithdrawalResponseVO;
import com.cloud.baowang.pay.api.vo.WithdrawalVO;
import com.cloud.baowang.service.vendor.BasePayService;
import com.cloud.baowang.util.SignUtil;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author : 小智
 * @Date : 2025/3/17 07:40
 * @Version : 1.0
 */
@Slf4j
@Service(value = "LemonPay")
public class LemonPayService implements BasePayService {


    public static void main(String[] args) {
        //INR通道
        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("8045");
        channelRespVO.setMerNo("1071");
        channelRespVO.setSecretKey("CC9h52felywm1ZyfW2E2lcuk");
        channelRespVO.setCurrencyCode("INR");
        channelRespVO.setApiUrl("https://init.lemonpay.cc");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setAmount("30100");
        paymentVO.setApplyIp("127.0.0.1");

        String orderNO = OrderUtil.getOrderNoNum("P", 10);

//         new LemonPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
          new LemonPayService().queryPayOrder(channelRespVO, "X0126031811245993173");

//        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = new SystemWithdrawChannelResponseVO();
//        withdrawChannelResponseVO.setChannelCode("bank_dpay");
//        withdrawChannelResponseVO.setMerNo("sportVN");
//        withdrawChannelResponseVO.setSecretKey("CC9h52felywm1ZyfW2E2lcuk");
//        withdrawChannelResponseVO.setApiUrl("https://dopay.grabs.pro");
//        withdrawChannelResponseVO.setCallbackUrl("https://gw.playesoversea.store");
//
//        orderNO = OrderUtil.getOrderNoNum("W", 10);
//
//
//        WithdrawalVO withdrawalVO = new WithdrawalVO();
//        withdrawalVO.setAmount("101");
//        withdrawalVO.setBankUserName("Betty");
//        withdrawalVO.setBankNo("MSB");
//        withdrawalVO.setBankName("Maritime Bank");
//
//        new DoPayService().creatPayoutOrder(withdrawChannelResponseVO, withdrawalVO, orderNO);
//        new DoPayService().queryPayoutOrder(withdrawChannelResponseVO, orderNO);





    }

    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+ "/pay/callback/api/lemonPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/lemonPayAgentCallback";
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mchId", Long.parseLong(channelRespVO.getMerNo()));
        paramMap.put("productId", Integer.parseInt(channelRespVO.getChannelCode()));
        paramMap.put("mchOrderNo", orderNo);
        paramMap.put("currency", channelRespVO.getCurrencyCode());
        paramMap.put("amount", new BigDecimal(paymentVO.getAmount()).multiply(BigDecimalConstants.HUNDRED).intValue());
        paramMap.put("subject", "payment");
        paramMap.put("body", "payment");
        paramMap.put("notifyUrl", notifyUrl);


        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.getSignByMapObj(paramMap, key);
        paramMap.put("sign", sign.toUpperCase());

        log.info("LemonPay代收支付请求参数: {}", paramMap);
        String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/api/pay/neworder", paramMap);
        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("LemonPay代收支付返回：{}", jsonObject);
        } catch (Exception e) {
            log.error("LemonPay代收支付返回异常:{0}",e);
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage("三方发生异常");
            return paymentResponseVO;
        }

        if ("SUCCESS".equals(jsonObject.getString("retCode"))) {
            JSONObject dataJson=jsonObject.getJSONObject("payParams");
            paymentResponseVO.setCode(CommonConstant.business_zero);
            paymentResponseVO.setOrderId(orderNo);
            paymentResponseVO.setAmount(paymentVO.getAmount());
            paymentResponseVO.setThirdOrderId(jsonObject.getString("payOrderId"));
            paymentResponseVO.setPaymentUrl(dataJson.getString("payUrl"));
        } else {
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage(jsonObject.getString("retMsg"));
        }

        return paymentResponseVO;
    }

    @Override
    public WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, WithdrawalVO withdrawalVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/lemonPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/lemonPayoutAgentCallback";
        }


        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mchId", Long.parseLong(channelRespVO.getMerNo()));
        paramMap.put("productId", Integer.parseInt(channelRespVO.getChannelCode()));
        paramMap.put("mchOrderNo", orderNo);
        paramMap.put("currency", channelRespVO.getCurrencyCode());
        paramMap.put("amount", new BigDecimal(withdrawalVO.getAmount()).multiply(BigDecimalConstants.HUNDRED).intValue());
        paramMap.put("collectionType", "bank");
        paramMap.put("bankName", withdrawalVO.getBankCode());
        paramMap.put("accountName", withdrawalVO.getBankUserName());
        paramMap.put("accountNo",  withdrawalVO.getBankNo());
        paramMap.put("bankNumber",  "IFSC");
        paramMap.put("userName",  withdrawalVO.getBankUserName());
//        paramMap.put("userMobile",  withdrawalVO.get);
        if(ObjectUtil.isNotEmpty(withdrawalVO.getEmail())){
            paramMap.put("userEmail",  withdrawalVO.getEmail());
        }
        paramMap.put("remark", "withdraw"+ withdrawalVO.getAmount());
        paramMap.put("reqTime", DateUtils.dateToyyyyMMddHHmmss(new Date()));
        paramMap.put("notifyUrl", notifyUrl);


        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.getSignByMapObj(paramMap, key);
        paramMap.put("sign", sign.toUpperCase());

        log.info("LemonPay代付请求：{}", paramMap);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/api/agentpay/apply", paramMap);
            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("LemonPay代付返回：{}", resultObj.toString());
            String code = resultObj.getString("retCode");
            withdrawalResponseVO.setMessage(resultObj.getString("retMsg"));
            withdrawalResponseVO.setOrderNo(orderNo);
            if ("SUCCESS".equals(code)) {
                int status = resultObj.getIntValue("status");
                // 状态:0-待处理,1-处理中,2-成功,3-失败,6-冲正(成功后退款变成失败了)
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                withdrawalResponseVO.setWithdrawOrderId(resultObj.getString("agentpayOrderId"));
                withdrawalResponseVO.setCommissionAmount(resultObj.getString("fee"));
                if(0 == status || 1 == status){
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
                }
                else if(2 == status || 6 == status){
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
                }
                else if(3 == status){
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
                }
                // }
            }  else  {
                withdrawalResponseVO.setCode(CommonConstant.business_negative1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return withdrawalResponseVO;
    }

    @Override
    public PayOrderResponseVO queryPayOrder(SystemRechargeChannelBaseVO channelRespVO, String orderNo) {
        PayOrderResponseVO payOrderResponseVO = new PayOrderResponseVO();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mchId", channelRespVO.getMerNo());
        paramMap.put("mchOrderNo", orderNo);

        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.getSignByMapObj(paramMap, key);
        paramMap.put("sign", sign.toLowerCase());

        log.info("LemonPay支付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/api/pay/orderquery", paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("LemonPay支付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("retCode");
            if ("SUCCESS".equals(status)) {
                payOrderResponseVO.setOrderNo(orderNo);
                //订单状态:未支付=0;支付中=10;支付成功=20;支付失败=30
                Integer orderStatus = jsonObject.getIntValue("status");
                payOrderResponseVO.setMessage(jsonObject.getString("retMsg"));
                String amount = String.valueOf(new BigDecimal(jsonObject.getString("amount")).divide(BigDecimalConstants.HUNDRED,
                        2, RoundingMode.FLOOR));
                if (2 == orderStatus || 3 == orderStatus) {
                    payOrderResponseVO.setThirdOrderNo(jsonObject.getString("payOrderId"));
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                    payOrderResponseVO.setAmount(amount);
                }else if (-1 == orderStatus) {
                    payOrderResponseVO.setThirdOrderNo(jsonObject.getString("payOrderId"));
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                    payOrderResponseVO.setAmount(amount);
                } else {
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                }
            }
        } catch (Exception e) {
            log.info("LemonPay支付订单查询返回异常：{0}", e);
        }
        return payOrderResponseVO;
    }

    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mchId", channelRespVO.getMerNo());
        paramMap.put("mchOrderNo", orderNo);
        paramMap.put("reqTime", DateUtils.dateToyyyyMMddHHmmss(new Date()));

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.getSignByMapObj(paramMap, key);
        paramMap.put("sign", sign.toLowerCase());

        log.info("LemonPay代付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/api/agentpay/query_order", paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("LemonPay代付订单查询返回：{}", jsonObject);
            String code = jsonObject.getString("retCode");
            withdrawalResponseVO.setOrderNo(orderNo);
            if ("SUCCESS".equals(code)) {
                int orderStatus = jsonObject.getIntValue("status");
                // 状态:0-待处理,1-处理中,2-成功,3-失败,6-冲正(成功后退款变成失败了)
                BigDecimal amount = new BigDecimal(jsonObject.getString("amount"))
                        .divide(BigDecimalConstants.HUNDRED, 2, RoundingMode.FLOOR);
                if (3 == orderStatus) {
                    withdrawalResponseVO.setWithdrawOrderId(jsonObject.getString("agentpayOrderId"));
                    withdrawalResponseVO.setAmount(amount.toString());
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
                } else if (2 == orderStatus || 6 == orderStatus) {
                    withdrawalResponseVO.setCode(0);
                    withdrawalResponseVO.setWithdrawOrderId(jsonObject.getString("agentpayOrderId"));
                    withdrawalResponseVO.setAmount(amount.toString());
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
                } else {
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
                }
            } else {
                withdrawalResponseVO.setCode(-1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
        } catch (Exception e) {
            log.info("LemonPay代付订单查询返回异常：{0}", e);
        }
        return withdrawalResponseVO;
    }
}
