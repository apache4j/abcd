package com.cloud.baowang.service.vendor.HYPay;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.wallet.PayoutStatusEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.HttpClient4Util;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.pay.api.vo.PayOrderResponseVO;
import com.cloud.baowang.pay.api.vo.PaymentResponseVO;
import com.cloud.baowang.pay.api.vo.PaymentVO;
import com.cloud.baowang.pay.api.vo.WithdrawalResponseVO;
import com.cloud.baowang.pay.api.vo.WithdrawalVO;
import com.cloud.baowang.service.DomainService;
import com.cloud.baowang.service.vendor.BasePayService;
import com.cloud.baowang.util.SignUtil;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: ford
 * @createTime: 2025/03/03
 * @description:  HyPay支付 废弃
 */
@Slf4j
@Service(value = "HyPay")
public class HyPayService implements BasePayService {

    @Autowired
    private DomainService domainService;

    public static void main(String[] args) {
        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("100");
        //vnd币种
       // channelRespVO.setMerNo("wbdk_ovnd_zcsh");
        //channelRespVO.setSecretKey("a75907ccc74358b5c8b2324fcb4654ff");
        //MYR币种
         channelRespVO.setMerNo("wbdk_omyr_zcsh");
         channelRespVO.setSecretKey("dbf077551c67a83c75f56977fa5fdb20");

        channelRespVO.setApiUrl("https://gyapi.gwt74o19ip.com");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setUserId("4547");
        paymentVO.setAmount("10000");
        paymentVO.setFirstName("HYTest");
        paymentVO.setApplyIp("127.0.0.1");

        String orderNO = OrderUtil.getOrderNoNum("P", 10);

        new HyPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
        new HyPayService().queryPayOrder(channelRespVO, orderNO);
        //  new HyPayService().queryPayOrder(channelRespVO, "P6808973558");

        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = new SystemWithdrawChannelResponseVO();
        withdrawChannelResponseVO.setChannelCode("100");
        withdrawChannelResponseVO.setMerNo("wbdk_ovnd_zcsh");
        withdrawChannelResponseVO.setSecretKey("a75907ccc74358b5c8b2324fcb4654ff");
        withdrawChannelResponseVO.setApiUrl("https://gyapi.gwt74o19ip.com");
        withdrawChannelResponseVO.setCallbackUrl("https://gw.playesoversea.store");

        orderNO = OrderUtil.getOrderNoNum("W", 10);


        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("100000");
        withdrawalVO.setBankUserName("Betty");
        withdrawalVO.setBankNo("54545454514");
        withdrawalVO.setBankCode("TCB");
        withdrawalVO.setBankName("Kỹ Thương (TCB)");
        withdrawalVO.setApplyIp("127.0.0.1");

     //  new HyPayService().creatPayoutOrder(withdrawChannelResponseVO, withdrawalVO, orderNO);
     //  new HyPayService().queryPayoutOrder(withdrawChannelResponseVO, orderNO);
        // new HyPayService().queryPayoutOrder(withdrawChannelResponseVO, "W400586780");
    }


    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String siteCode = CurrReqUtils.getSiteCode();
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/hyPayCallback";
        String returnUrl = "";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/hyPayAgentCallback";
            returnUrl=domainService.getReturnUrl(siteCode,1);
        }else {
            returnUrl= domainService.getReturnUrl(siteCode,2);
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantNo", channelRespVO.getMerNo());
        paramMap.put("merOrderNo", orderNo);
        paramMap.put("orderAmt", paymentVO.getAmount());
        paramMap.put("source", 30);
        paramMap.put("payType", channelRespVO.getChannelCode());
        paramMap.put("userName", paymentVO.getUserId());
        paramMap.put("bankAcctName", paymentVO.getUserId());
        paramMap.put("randomStr", OrderUtil.getOrderNo("HY",16));
        paramMap.put("deviceType", "PC");
        paramMap.put("userIp", paymentVO.getApplyIp());
        paramMap.put("notifyUrl", notifyUrl);
        if(StringUtils.hasText(returnUrl)){
            paramMap.put("pageUrl", returnUrl);
        }

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.getSignByMapObj(paramMap, key);
        paramMap.put("sign", sign.toLowerCase());

        log.info("HYPay代收支付请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/api/pay/public/api/pay/create", JSONObject.toJSONString(paramMap));
        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("HYPay代收支付返回：{}", jsonObject);
        } catch (Exception e) {
           log.error("HYPay代收支付返回异常:{0}",e);
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage("三方发生异常");
           return paymentResponseVO;
        }

        if ("1".equals(jsonObject.getString("code"))) {
            paymentResponseVO.setCode(CommonConstant.business_zero);
            paymentResponseVO.setOrderId(orderNo);
            paymentResponseVO.setAmount(paymentVO.getAmount());
            //paymentResponseVO.setThirdOrderId(dataJson.getString("ordernum"));
            paymentResponseVO.setPaymentUrl(jsonObject.getString("payUrl"));
        } else {
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage(jsonObject.getString("msg"));
        }

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
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/hyPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/hyPayoutAgentCallback";
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantNo", channelRespVO.getMerNo());
        paramMap.put("merOrderNo", orderNo);
        paramMap.put("orderAmt", withdrawalVO.getAmount());
        paramMap.put("userName", withdrawalVO.getUserId());
        paramMap.put("notifyUrl", notifyUrl);
        paramMap.put("userIp", withdrawalVO.getApplyIp());
        paramMap.put("payType", channelRespVO.getChannelCode());

        paramMap.put("recBankCode", withdrawalVO.getBankCode());
        paramMap.put("recBankName", withdrawalVO.getBankName());
        paramMap.put("recAcctName", withdrawalVO.getBankUserName());
        paramMap.put("recAcctNo", withdrawalVO.getBankNo());
        paramMap.put("randomStr", OrderUtil.getOrderNo("HY",16));


        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.getSignByMapObj(paramMap, key).toLowerCase();
        paramMap.put("sign", sign.toLowerCase());

        log.info("HYPay代付请求：{}", paramMap);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/api/pay/public/api/withdraw/create", JSONObject.toJSONString(paramMap));
            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("HYPay代付返回：{}", resultObj.toString());
            String status = resultObj.getString("code");
            withdrawalResponseVO.setMessage(resultObj.getString("msg"));
            withdrawalResponseVO.setOrderNo(orderNo);
            //订单状态:未支付=0;支付中=10;支付成功=20;支付失败=30。
            if ("1".equals(status)) {
                //JSONObject dataJson=resultObj.getJSONObject("data");
             //   int orderStatus=dataJson.getIntValue("status");
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                withdrawalResponseVO.setWithdrawOrderId(resultObj.getString("orderNo"));
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
            }  else  {
                withdrawalResponseVO.setCode(CommonConstant.business_negative1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("HYPay代付返回渠道层：{}", withdrawalResponseVO);
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
        PayOrderResponseVO payOrderResponseVO = new PayOrderResponseVO();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("merchantNo", channelRespVO.getMerNo());
        paramMap.put("randomStr", OrderUtil.getOrderNo("HY",16));
        paramMap.put("merOrderNo", orderNo);

        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.getSignByMap(paramMap, key);
        paramMap.put("sign", sign.toLowerCase());

        log.info("HYPay支付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/api/pay/public/api/pay/query", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("HYPay支付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("code");
            if ("1".equals(status)) {
                payOrderResponseVO.setCode(0);
                payOrderResponseVO.setOrderNo(orderNo);
                //订单状态 100成功 -100失败 别的都算未完结
                Integer orderStatus = jsonObject.getIntValue("status");
                BigDecimal orderAmt = jsonObject.getBigDecimal("orderAmt");
                BigDecimal payAmt = jsonObject.getBigDecimal("payAmt");
                payOrderResponseVO.setMessage(jsonObject.getString("msg"));
                payOrderResponseVO.setThirdOrderNo(jsonObject.getString("orderNo"));
                if (100 == orderStatus) {
                    payOrderResponseVO.setAmount(payAmt.toString());
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                }else if (-100 == orderStatus) {
                    payOrderResponseVO.setAmount(orderAmt.toString());
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                } else {
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                }
            }
            log.info("HYPay支付订单查询返回渠道层：{}", payOrderResponseVO);
        } catch (Exception e) {
            log.info("HYPay支付订单查询返回异常：{0}", e);
        }
        return payOrderResponseVO;
    }

    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("merchantNo", channelRespVO.getMerNo());
        paramMap.put("randomStr", OrderUtil.getOrderNo("HY",16));
        paramMap.put("merOrderNo", orderNo);

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.getSignByMap(paramMap, key);
        paramMap.put("sign", sign.toLowerCase());

        log.info("HYPay代付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/api/pay/public/api/withdraw/query", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("HYPay代付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("code");
            withdrawalResponseVO.setOrderNo(orderNo);
            if ("1".equals(status)) {
                withdrawalResponseVO.setCode(0);
                withdrawalResponseVO.setOrderNo(orderNo);
                withdrawalResponseVO.setMessage(jsonObject.getString("message"));
                //订单状态:未支付=0;支付中=10;支付成功=20;支付失败=30
                Integer orderStatus = jsonObject.getIntValue("status");
                withdrawalResponseVO.setWithdrawOrderId(jsonObject.getString("orderNo"));
                if (100 == orderStatus) {
                    withdrawalResponseVO.setAmount(jsonObject.getString("payAmt"));
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
                } else if (-100 == orderStatus) {
                    withdrawalResponseVO.setAmount(jsonObject.getString("orderAmt"));
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
                } else {
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
                }
            } else {
                withdrawalResponseVO.setCode(-1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("HYPay代付订单查询返回渠道层：{}", withdrawalResponseVO);
        } catch (Exception e) {
            log.info("HYPay代付订单查询返回异常：{0}", e);
        }
        return withdrawalResponseVO;
    }
}
