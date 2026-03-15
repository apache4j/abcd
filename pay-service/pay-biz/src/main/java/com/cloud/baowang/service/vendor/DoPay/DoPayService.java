package com.cloud.baowang.service.vendor.DoPay;

import com.alibaba.fastjson2.JSONObject;
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
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: ford
 * @createTime: 2025/03/03
 * @description:  DoPay支付
 */
@Slf4j
@Service(value = "DoPay")
public class DoPayService implements BasePayService {


    public static void main(String[] args) {
        //VND通道
        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("vnbank_qr");
        channelRespVO.setMerNo("sportVN");
        channelRespVO.setSecretKey("946D510CF0B101EC2B95B02D6A103A18");
        channelRespVO.setApiUrl("https://dopay.grabs.pro");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setAmount("55000");
        paymentVO.setApplyIp("127.0.0.1");

        String orderNO = OrderUtil.getOrderNoNum("P", 10);

       // new DoPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
      //  new DoPayService().queryPayOrder(channelRespVO, orderNO);

        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = new SystemWithdrawChannelResponseVO();
        withdrawChannelResponseVO.setChannelCode("bank_dpay");
        withdrawChannelResponseVO.setMerNo("sportVN");
        withdrawChannelResponseVO.setSecretKey("946D510CF0B101EC2B95B02D6A103A18");
        withdrawChannelResponseVO.setApiUrl("https://dopay.grabs.pro");
        withdrawChannelResponseVO.setCallbackUrl("https://gw.playesoversea.store");

        orderNO = OrderUtil.getOrderNoNum("W", 10);


        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("100");
        withdrawalVO.setBankUserName("Betty");
        withdrawalVO.setBankNo("MSB");
        withdrawalVO.setBankName("Maritime Bank");

       new DoPayService().creatPayoutOrder(withdrawChannelResponseVO, withdrawalVO, orderNO);
       new DoPayService().queryPayoutOrder(withdrawChannelResponseVO, orderNO);





        //=============菲律宾通道 PHP

        SystemRechargeChannelBaseVO channelPHPRespVO = new SystemRechargeChannelBaseVO();
        channelPHPRespVO.setChannelCode("gcash_qr");
        channelPHPRespVO.setMerNo("sport");
        channelPHPRespVO.setSecretKey("595987743DAFF1FF2BF924C097D650A1");
        channelPHPRespVO.setApiUrl("https://dopay.grabs.pro");
        channelPHPRespVO.setCallbackUrl("https://gw.playesoversea.store");

        PaymentVO paymentVOPHP = new PaymentVO();
        paymentVOPHP.setAmount("5000");
        paymentVOPHP.setApplyIp("127.0.0.1");

        String orderNOPHP = OrderUtil.getOrderNoNum("P", 10);

      //   new DoPayService().creatPayOrder(channelPHPRespVO, paymentVOPHP, orderNOPHP);
       //  new DoPayService().queryPayOrder(channelPHPRespVO, orderNOPHP);

        //代付通道的类型 [gcash代付填写gcash_dpay, paymaya代付填写paymaya_dpay,bank代付 填写bank_dpay
        SystemWithdrawChannelResponseVO withdrawChannelPHPResponseVO = new SystemWithdrawChannelResponseVO();
        withdrawChannelPHPResponseVO.setChannelCode("gcash_dpay");
        withdrawChannelPHPResponseVO.setMerNo("sport");
        withdrawChannelPHPResponseVO.setSecretKey("595987743DAFF1FF2BF924C097D650A1");
        withdrawChannelPHPResponseVO.setApiUrl("https://dopay.grabs.pro");
        withdrawChannelPHPResponseVO.setCallbackUrl("https://gw.playesoversea.store");

        orderNOPHP = OrderUtil.getOrderNoNum("W", 10);

      //gcash_dpay 和paymaya_dpay
        WithdrawalVO withdrawalPHPVO = new WithdrawalVO();
        withdrawalPHPVO.setAmount("100");
        withdrawalPHPVO.setBankUserName("Betty");
       // withdrawalPHPVO.setBankCode("gcash_dpay");
        //withdrawalPHPVO.setBankName("gcash");
        withdrawalPHPVO.setBankNo("456454654654");

      //  new DoPayService().creatPayoutOrder(withdrawChannelPHPResponseVO, withdrawalPHPVO, orderNOPHP);
       // new DoPayService().queryPayoutOrder(withdrawChannelPHPResponseVO, orderNOPHP);
    }


    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/doPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/doPayAgentCallback";
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("mchNo", channelRespVO.getMerNo());
        paramMap.put("fee", paymentVO.getAmount());
        paramMap.put("time", DateUtils.formatDateByZoneId(System.currentTimeMillis(),DateUtils.FULL_FORMAT_1,"UTC+8"));
        paramMap.put("orderNo", orderNo);
        paramMap.put("type", channelRespVO.getChannelCode());
        paramMap.put("notifyUrl", notifyUrl);
        paramMap.put("notifytype", "json");


        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.getSignByMap(paramMap, key);
        paramMap.put("sign", sign.toLowerCase());

        log.info("DoPay代收支付请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/payapi", JSONObject.toJSONString(paramMap));
        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("DoPay代收支付返回：{}", jsonObject);
        } catch (Exception e) {
           log.error("DoPay代收支付返回异常:{0}",e);
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage("三方发生异常");
           return paymentResponseVO;
        }

        if ("1".equals(jsonObject.getString("code"))) {
            JSONObject dataJson=jsonObject.getJSONObject("data");
            paymentResponseVO.setCode(CommonConstant.business_zero);
            paymentResponseVO.setOrderId(orderNo);
            paymentResponseVO.setAmount(paymentVO.getAmount());
            paymentResponseVO.setThirdOrderId(dataJson.getString("ordernum"));
            paymentResponseVO.setPaymentUrl(dataJson.getString("pay_url"));
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
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/doPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/doPayoutAgentCallback";
        }


        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("mchNo", channelRespVO.getMerNo());
        paramMap.put("fee", withdrawalVO.getAmount());
        paramMap.put("time", DateUtils.formatDateByZoneId(System.currentTimeMillis(),DateUtils.FULL_FORMAT_1,"UTC+8"));
        paramMap.put("orderNo", orderNo);
        paramMap.put("type", channelRespVO.getChannelCode());
        paramMap.put("notifyUrl", notifyUrl);
        paramMap.put("notifytype", "json");

        if(StringUtils.hasText(withdrawalVO.getBankCode())){
            paramMap.put("bankCode", withdrawalVO.getBankCode());//银行编码
        }
       // paramMap.put("bankName", withdrawalVO.getBankUserName());//收款人姓名
        paramMap.put("customerName", withdrawalVO.getBankUserName());//收款人姓名
        paramMap.put("bankAccount", withdrawalVO.getBankNo());//收款人账号


        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.getSignByMap(paramMap, key).toLowerCase();
        paramMap.put("sign", sign.toLowerCase());

        log.info("DoPay代付请求：{}", paramMap);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/dpayapi", JSONObject.toJSONString(paramMap));
            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("DoPay代付返回：{}", resultObj.toString());
            String status = resultObj.getString("code");
            withdrawalResponseVO.setMessage(resultObj.getString("msg"));
            withdrawalResponseVO.setOrderNo(orderNo);
            if ("1".equals(status)) {
                JSONObject dataJson=resultObj.getJSONObject("data");
               // 订单状态:未支付=0;支付中=10;支付成功=20;支付失败=30。
                // int orderStatus=dataJson.getIntValue("status");
                 //if(10==orderStatus){
                     withdrawalResponseVO.setCode(CommonConstant.business_zero);
                     withdrawalResponseVO.setWithdrawOrderId(dataJson.getString("ordernum"));
                     withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
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
        paramMap.put("mchNo", channelRespVO.getMerNo());
        paramMap.put("time", DateUtils.formatDateByZoneId(System.currentTimeMillis(),DateUtils.FULL_FORMAT_1,"UTC+8"));
        paramMap.put("mchOrdernum", orderNo);

        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.getSignByMap(paramMap, key);
        paramMap.put("sign", sign.toLowerCase());

        log.info("DoPay支付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/payapi/query", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("DoPay支付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("code");
            if ("1".equals(status)) {
                payOrderResponseVO.setOrderNo(orderNo);
                JSONObject data = jsonObject.getJSONObject("data");
                //订单状态:未支付=0;支付中=10;支付成功=20;支付失败=30
                Integer orderStatus = data.getIntValue("status");
                payOrderResponseVO.setMessage(data.getString("msg"));
                if (20 == orderStatus) {
                    payOrderResponseVO.setThirdOrderNo(data.getString("ordernum"));
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                    String amount = data.getString("fee");
                    payOrderResponseVO.setAmount(String.valueOf(amount));
                }else if (30 == orderStatus) {
                    payOrderResponseVO.setThirdOrderNo(data.getString("ordernum"));
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                    String amount = data.getString("fee");
                    payOrderResponseVO.setAmount(String.valueOf(amount));
                } else {
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                }
            }
        } catch (Exception e) {
            log.info("DoPay支付订单查询返回异常：{0}", e);
        }
        return payOrderResponseVO;
    }

    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("mchNo", channelRespVO.getMerNo());
        paramMap.put("time", DateUtils.formatDateByZoneId(System.currentTimeMillis(),DateUtils.FULL_FORMAT_1,"UTC+8"));
        paramMap.put("mchOrdernum", orderNo);

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.getSignByMap(paramMap, key);
        paramMap.put("sign", sign.toLowerCase());

        log.info("DoPay代付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/dpayapi/query", JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("DoPay代付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("code");
            withdrawalResponseVO.setOrderNo(orderNo);
            if ("1".equals(status)) {
                withdrawalResponseVO.setOrderNo(orderNo);
                withdrawalResponseVO.setMessage(jsonObject.getString("msg"));
                JSONObject data = jsonObject.getJSONObject("data");
                //订单状态:未支付=0;支付中=10;支付成功=20;支付失败=30
                Integer orderStatus = data.getIntValue("status");
                if (30 == orderStatus) {
                    withdrawalResponseVO.setWithdrawOrderId(data.getString("ordernum"));
                    String amount = data.getString("fee");
                    if(data.containsKey("realfee")){
                        amount = data.getString("realfee");
                    }
                    withdrawalResponseVO.setAmount(amount);
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
                } else if (20 == orderStatus) {
                    String amount = data.getString("fee");
                    if(data.containsKey("realfee")){
                        amount = data.getString("realfee");
                    }
                    withdrawalResponseVO.setCode(0);
                    withdrawalResponseVO.setWithdrawOrderId(data.getString("ordernum"));
                    withdrawalResponseVO.setAmount(amount);
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
                } else {
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
                }
            } else {
                withdrawalResponseVO.setCode(-1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
        } catch (Exception e) {
            log.info("DoPay代付订单查询返回异常：{0}", e);
        }
        return withdrawalResponseVO;
    }
}
