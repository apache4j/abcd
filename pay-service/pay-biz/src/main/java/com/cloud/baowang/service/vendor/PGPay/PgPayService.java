package com.cloud.baowang.service.vendor.PGPay;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.wallet.PayoutStatusEnum;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.HttpClient4Util;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.pay.api.vo.*;
import com.cloud.baowang.service.vendor.BasePayService;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: fangfei
 * @createTime: 2024/09/30 14:55
 * @description: 国际龙支付
 */
@Slf4j
@Service(value = "PGPay")
public class PgPayService implements BasePayService {

    public static void main(String[] args) {
        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("10");
        channelRespVO.setMerNo("dgpay_687");
        channelRespVO.setSecretKey("a759f2e7-dc55-4371-8a93-69fadc3d456f");
        channelRespVO.setApiUrl("https://dgpayapi.pwpgbo.com");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");
        channelRespVO.setPubKey("5");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setAmount("1000");
        paymentVO.setApplyIp("127.0.0.1");
        paymentVO.setCurrency("PHP");
        paymentVO.setBankCode("");

        String orderNO = OrderUtil.getOrderNo("B", 10);

        new PgPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
        //new PgPayService().queryPayOrder(channelRespVO, "B8UJTFsSHj6");

        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("1000");
        withdrawalVO.setCurrency("MYR");
        withdrawalVO.setBankCode("PBBB");
        withdrawalVO.setBankNo("5045451001");
        withdrawalVO.setBankUserName("Wang Bao Lu");

        //new PgPayService().creatPayoutOrder(channelRespVO, withdrawalVO, orderNO);
    }

    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/pgPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/pgPayCallback";
        }
        String requestTime = DateUtils.convertDateToString(new Date(), DateUtils.FULL_FORMAT_1);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("providerId", channelRespVO.getPubKey());
        paramMap.put("providerType", channelRespVO.getChannelCode());
        paramMap.put("currency", paymentVO.getCurrency());
        JSONObject dataJson = new JSONObject();
        dataJson.put("fromBankCode", paymentVO.getBankCode());
        dataJson.put("redirectUrl", "https://h5.playesoversea.store");
        paramMap.put("data", dataJson);
        paramMap.put("orderId", orderNo);
        paramMap.put("callbackUrl", notifyUrl);
        paramMap.put("amount", paymentVO.getAmount());
        paramMap.put("opCode", channelRespVO.getMerNo());
        paramMap.put("reqDateTime", requestTime);

        String key = channelRespVO.getSecretKey();
        String signStr = orderNo +
                channelRespVO.getPubKey() + channelRespVO.getChannelCode() +
                paymentVO.getCurrency() +
                paymentVO.getAmount() +
                requestTime +channelRespVO.getMerNo() + key ;

        String sign = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();
        paramMap.put("securityToken", sign);

        log.info("PG支付请求参数: {}", paramMap);
        String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/ajax/api/deposit", paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("PG支付返回：{}", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        if ("0".equals(jsonObject.getString("code"))) {
            paymentResponseVO.setCode(CommonConstant.business_zero);
            paymentResponseVO.setOrderId(orderNo);
            paymentResponseVO.setAmount(paymentVO.getAmount());
            paymentResponseVO.setPaymentUrl(jsonObject.getString("paymentUrl"));
        } else {
            paymentResponseVO.setCode(jsonObject.getIntValue("code"));
            paymentResponseVO.setMessage(jsonObject.getString("description"));
        }

        return paymentResponseVO;
    }

    @Override
    public WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, WithdrawalVO withdrawalVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/pgPayCallback";
        String requestTime = DateUtils.convertDateToString(new Date(), DateUtils.FULL_FORMAT_1);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("orderId", orderNo);
        paramMap.put("providerId", channelRespVO.getPubKey());
        paramMap.put("providerType", channelRespVO.getChannelCode());
        paramMap.put("currency", withdrawalVO.getCurrency());
        paramMap.put("amount", withdrawalVO.getAmount());
        paramMap.put("callbackUrl", notifyUrl);
        JSONObject dataJson = new JSONObject();
        dataJson.put("toBankCode", withdrawalVO.getBankCode());
        dataJson.put("toAccNo", withdrawalVO.getBankNo());
        dataJson.put("toAccName", withdrawalVO.getBankUserName());
        paramMap.put("data", dataJson);
        paramMap.put("opCode", channelRespVO.getMerNo());
        paramMap.put("reqDateTime", requestTime);

        String key = channelRespVO.getSecretKey();
        String signStr = orderNo +
                channelRespVO.getPubKey() + channelRespVO.getChannelCode() +
                withdrawalVO.getCurrency() +
                withdrawalVO.getAmount() +
                requestTime +channelRespVO.getMerNo() + key ;

        String sign = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();
        paramMap.put("securityToken", sign);

        log.info("PgPay代付请求：{}", paramMap);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/ajax/api/withdraw", paramMap);

            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("PgPay代付返回：{}", resultObj.toString());
            Integer code = resultObj.getIntValue("code");
            withdrawalResponseVO.setCode(code);
            withdrawalResponseVO.setMessage(resultObj.getString("msg"));

            if (code == 1) {
                JSONObject dataObject = resultObj.getJSONObject("data");

                withdrawalResponseVO.setWithdrawOrderId(dataObject.getString("id"));
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
        String requestTime = DateUtils.convertDateToString(new Date(), DateUtils.FULL_FORMAT_1);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("opCode", channelRespVO.getMerNo());
        paramMap.put("reqDateTime", requestTime);
        paramMap.put("orderId", orderNo);

        String key = channelRespVO.getSecretKey();
        String signStr = orderNo + requestTime +channelRespVO.getMerNo() + key ;

        String sign = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();
        paramMap.put("securityToken", sign);

        log.info("PG订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/ajax/api/queryDepositTrans", paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("PG订单查询返回：{}", jsonObject);
            String code = jsonObject.getString("code");
            if ("0".equals(code)) {
                payOrderResponseVO.setMessage(jsonObject.getString("description"));
                payOrderResponseVO.setOrderNo(orderNo);
                String amount = jsonObject.getString("amountToDeposit");
                // Expired = -20 Failed = -10 Pending = 0 Process = 10 Success = 20
                Integer status = jsonObject.getIntValue("status");
                if (0 == status) {
                    payOrderResponseVO.setAmount(String.valueOf(amount));
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                } else if (20 == status) {
                    payOrderResponseVO.setCode(0);
                    payOrderResponseVO.setAmount(String.valueOf(amount));
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                } else if (-10 == status) {
                    payOrderResponseVO.setAmount(String.valueOf(amount));
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

    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        return null;
    }


}
