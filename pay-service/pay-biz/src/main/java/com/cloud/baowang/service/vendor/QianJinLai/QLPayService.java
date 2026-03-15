package com.cloud.baowang.service.vendor.QianJinLai;

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
import com.cloud.baowang.util.SignUtil;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: ford
 * @createTime: 2025/03/02
 * @description:  钱进来支付
 */
@Slf4j
@Service(value = "QLPay")
public class QLPayService implements BasePayService {


    public static void main(String[] args) {
        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("28");
        channelRespVO.setMerNo("20eb8cabda60b8aec3861232");
        channelRespVO.setSecretKey("7EC198349bea0Da313435BF8Efe3bc5f05dCE292");
        channelRespVO.setApiUrl("http://quechaomon-api.meisuobudamiya.com");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setAmount("15000");
        paymentVO.setApplyIp("127.0.0.1");

        String orderNO = OrderUtil.getOrderNoNum("P", 10);

        new QLPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
        new QLPayService().queryPayOrder(channelRespVO, orderNO);
        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = new SystemWithdrawChannelResponseVO();
        withdrawChannelResponseVO.setChannelCode("14");
        withdrawChannelResponseVO.setMerNo("20eb8cabda60b8aec3861232");
        withdrawChannelResponseVO.setSecretKey("7EC198349bea0Da313435BF8Efe3bc5f05dCE292");
        withdrawChannelResponseVO.setApiUrl("http://quechaomon-api.meisuobudamiya.com");
        withdrawChannelResponseVO.setCallbackUrl("https://gw.playesoversea.store");

        orderNO = OrderUtil.getOrderNoNum("W", 10);


        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("13000");
        withdrawalVO.setBankNo("9272100947");
        withdrawalVO.setBankUserName("Betty");
        withdrawalVO.setBankName("VietinBank");

        // new QLPayService().creatPayoutOrder(withdrawChannelResponseVO, withdrawalVO, orderNO);
        // new QLPayService().queryPayoutOrder(withdrawChannelResponseVO, orderNO);
    }


    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/qjlPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/qjlPayAgentCallback";
        }
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("app_id", channelRespVO.getMerNo());
        paramMap.put("product_id", channelRespVO.getChannelCode());
        paramMap.put("out_trade_no", orderNo);
        paramMap.put("notify_url", notifyUrl);
        paramMap.put("amount", paymentVO.getAmount());
        paramMap.put("time", timestamp);

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.getSignByMap(paramMap, key);
        paramMap.put("sign", sign.toLowerCase());

        log.info("QLPay代收支付请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/api/order", JSONObject.toJSONString(paramMap));
        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("QLPay代收支付返回：{}", jsonObject);
        } catch (Exception e) {
           log.error("QLPay代收支付返回异常:{0}",e);
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage("三方发生异常");
           return paymentResponseVO;
        }

        if ("200".equals(jsonObject.getString("code"))) {
            JSONObject dataJson=jsonObject.getJSONObject("data");
            paymentResponseVO.setCode(CommonConstant.business_zero);
            paymentResponseVO.setOrderId(orderNo);
            paymentResponseVO.setAmount(paymentVO.getAmount());
            //paymentResponseVO.setThirdOrderId("");
            paymentResponseVO.setPaymentUrl(dataJson.getString("url"));
        } else {
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage(jsonObject.getString("message"));
        }
        log.info("QLPay代收返回渠道层：{}", paymentResponseVO);
        return paymentResponseVO;
    }


    /**
     * 发起提现请求
     * @param channelRespVO
     * @param withdrawalVO
     * @param orderNo
     * @return
     */
    @Override
    public WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, WithdrawalVO withdrawalVO, String orderNo) {
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/qjlPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/qjlPayoutAgentCallback";
        }


        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("app_id", channelRespVO.getMerNo());
        paramMap.put("product_id", channelRespVO.getChannelCode());
        paramMap.put("out_trade_no", orderNo);
        paramMap.put("notify_url", notifyUrl);
        paramMap.put("amount", withdrawalVO.getAmount());
        paramMap.put("time", timestamp);



        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.getSignByMapObj(paramMap, key).toUpperCase();
        paramMap.put("sign", sign.toLowerCase());

        JSONObject accountJson=new JSONObject();
        accountJson.put("bankName",withdrawalVO.getBankName());
        accountJson.put("accountName",withdrawalVO.getBankUserName());
        accountJson.put("accountNumber",withdrawalVO.getBankNo());
        paramMap.put("ext", accountJson);

        log.info("QLPay代付请求：{}", paramMap);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/api/payment", JSONObject.toJSONString(paramMap));

            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("QLPay代付返回：{}", resultObj.toString());
            String status = resultObj.getString("code");
            withdrawalResponseVO.setMessage(resultObj.getString("msg"));
            if ("200".equals(status)) {
                JSONObject dataJson =resultObj.getJSONObject("data");
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                withdrawalResponseVO.setOrderNo(orderNo);
                withdrawalResponseVO.setWithdrawOrderId(dataJson.getString("trade_no"));
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
            }  else  {
                withdrawalResponseVO.setCode(CommonConstant.business_negative1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("QLPay代付返回渠道层：{}", withdrawalResponseVO);
        } catch (Exception e) {
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
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        PayOrderResponseVO payOrderResponseVO = new PayOrderResponseVO();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("app_id", channelRespVO.getMerNo());
        paramMap.put("out_trade_no", orderNo);
        paramMap.put("time", timestamp);

        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.getSignByMap(paramMap, key);
        paramMap.put("sign", sign.toLowerCase());

        log.info("QLPay支付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/api/order/status", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("QLPay支付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("code");
            payOrderResponseVO.setMessage(jsonObject.getString("message"));
            if ("200".equals(status)) {
                payOrderResponseVO.setOrderNo(orderNo);
                JSONObject data = jsonObject.getJSONObject("data");
                payOrderResponseVO.setThirdOrderNo(data.getString("trade_no"));
                //交易状态:1成功,-1处理中,2失败
                Integer orderStatus = data.getIntValue("trade_status");
                String amount = data.getString("real_amount");
                payOrderResponseVO.setAmount(String.valueOf(amount));
                if (2 == orderStatus) {
                    payOrderResponseVO.setPayOrderStatus(PayoutStatusEnum.Fail.getCode());
                } else if (-1== orderStatus) {
                    payOrderResponseVO.setPayOrderStatus(PayoutStatusEnum.Pending.getCode());
                } else if (1 == orderStatus) {
                    payOrderResponseVO.setPayOrderStatus(PayoutStatusEnum.Success.getCode());
                }
            }else {
                payOrderResponseVO.setPayOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("QLPay支付订单查询返回渠道层：{}", payOrderResponseVO);
        } catch (Exception e) {
            log.info("QLPay支付订单查询返回异常：{0}", e);
        }
        return payOrderResponseVO;
    }

    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("app_id", channelRespVO.getMerNo());
        paramMap.put("out_trade_no", orderNo);
        paramMap.put("time", timestamp);

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.getSignByMap(paramMap, key);
        paramMap.put("sign", sign.toLowerCase());

        log.info("QLPay代付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/api/payment/status", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("QLPay代付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("code");
            withdrawalResponseVO.setOrderNo(orderNo);
            withdrawalResponseVO.setMessage(jsonObject.getString("message"));
            if ("200".equals(status)) {
                withdrawalResponseVO.setOrderNo(orderNo);
                JSONObject data = jsonObject.getJSONObject("data");
                String amount = data.getString("real_amount");
                withdrawalResponseVO.setAmount(String.valueOf(amount));
                withdrawalResponseVO.setWithdrawOrderId(data.getString("trade_no"));
                withdrawalResponseVO.setCode(0);
                //交易状态:1成功,-1处理中,2失败
                Integer orderStatus = data.getIntValue("trade_status");
                if (2 == orderStatus) {
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
                } else if (-1== orderStatus) {
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
                } else if (1 == orderStatus) {
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
                } else {
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
                }
            } else {
                withdrawalResponseVO.setCode(-1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("QLPay代付订单查询返回渠道层：{}", withdrawalResponseVO);
        } catch (Exception e) {
            log.info("QLPay代付订单查询返回异常：{0}", e);
        }
        return withdrawalResponseVO;
    }
}
