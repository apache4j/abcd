package com.cloud.baowang.service.vendor.EbPay;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.wallet.PayoutStatusEnum;
import com.cloud.baowang.common.core.utils.BigDecimalUtils;
import com.cloud.baowang.common.core.utils.HttpClient4Util;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.pay.api.vo.BankInfoVO;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @Desciption: EbPay支付 CNY支付通道
 * @Author: Ford
 * @Date: 2025/6/16 09:44
 * @Version: V1.0
 **/
@Slf4j
@Service(value = "EbPay")
public class EbPayService implements BasePayService {
    public static void main(String[] args) {

        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        //channelRespVO.setChannelCode("3");
        // channelRespVO.setMerNo("OkSportG");
        // channelRespVO.setPubKey("knvVEIuvZYdSzBCjSMQG");

        channelRespVO.setMerNo("60009");
        channelRespVO.setSecretKey("a1mrcs071nfs206u");
        channelRespVO.setChannelCode("2182");

        channelRespVO.setApiUrl("http://test-wallet-api-gateway.fbnma.com");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.pro");

        PaymentVO paymentVO = new PaymentVO();
        // paymentVO.setEmail("test01@gmail.com");
        //paymentVO.setPhoneNum("601012456789");
        paymentVO.setUserId("2536423365");
        paymentVO.setAmount("100");
        paymentVO.setApplyIp("103.20.81.119");

        String orderNO = OrderUtil.getOrderNo("LilO0", 16);

         new EbPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
        orderNO="LilO0QjHtUvaTdUduAn99";
        //  new EbPayService().queryPayOrder(channelRespVO, orderNO);


        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = new SystemWithdrawChannelResponseVO();
        withdrawChannelResponseVO.setMerNo("60009");
        withdrawChannelResponseVO.setSecretKey("a1mrcs071nfs206u");
        withdrawChannelResponseVO.setApiUrl("http://test-wallet-api-gateway.fbnma.com");
        withdrawChannelResponseVO.setCallbackUrl("https://gw.playesoversea.pro");

        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("10");
        withdrawalVO.setUserId("855325");
        withdrawalVO.setBankCode("ABC");
         withdrawalVO.setBankBranch("Kentucky");
        withdrawalVO.setBankUserName("Betty");
        withdrawalVO.setBankNo("12332343432");
        withdrawalVO.setApplyIp("103.20.81.119");

        // new EbPayService().creatPayoutOrder(withdrawChannelResponseVO, withdrawalVO, orderNO);
        // new EbPayService().queryPayoutOrder(withdrawChannelResponseVO, "LilO0QjHtUvaTdUduAn99");
    }

    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/ebPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/ebPayAgentCallback";
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantNo", channelRespVO.getMerNo());
        paramMap.put("userName", "OK".concat(channelRespVO.getMerNo()).concat(paymentVO.getUserId()));
        paramMap.put("deviceType", 9);
        paramMap.put("merchantOrderId", orderNo);


        String key = channelRespVO.getSecretKey();
        String queryBuffer = "merchantNo" + "=" + paramMap.get("merchantNo") + "&" +
                "merchantOrderId" + "=" + paramMap.get("merchantOrderId") + "&" +
                "userName" + "=" + paramMap.get("userName") + "&" +
                "deviceType" + "=" + paramMap.get("deviceType") + "&" +
                "key" + "=" + key;

        String sign = SignUtil.signOriginStr(queryBuffer);
        paramMap.put("sign", sign);

        paramMap.put("loginIp", paymentVO.getApplyIp());
        paramMap.put("depositNotifyUrl", notifyUrl);
        paramMap.put("payAmount",  BigDecimalUtils.formatFourKeep2Dec(new BigDecimal(paymentVO.getAmount())));
        if(StringUtils.hasText(paymentVO.getFirstName())){
            paramMap.put("depositName", paymentVO.getFirstName());
        }
        String channelCode= channelRespVO.getChannelCode();
        if(!StringUtils.hasText(channelCode)){
            channelCode="2182";
        }
        paramMap.put("payTypeId", channelCode);
        //金额类型: 0人民币 1虚拟币, 默认人民币
       // paramMap.put("amountType", "0");
        paramMap.put("virtualProtocol", "0");
     //   String deviceNum= MD5Util.md5(SnowFlakeUtils.getSnowId()).toLowerCase();
        //paramMap.put("merchantDeviceId", deviceNum);

        String url=channelRespVO.getApiUrl() + "/api/merchant/deposit/v2";
        log.info("EbPay支付,请求地址:{},请求参数: {}",url, paramMap);
        String result = HttpClient4Util.doPost(url, paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("EbPay 支付返回：{}", jsonObject);
        } catch (Exception e) {
            log.error("EbPay 支付返回异常:{0}",e);
        }

        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        if (jsonObject.getString("code").equals("200")) {
            JSONObject data = jsonObject.getJSONObject("data");
            paymentResponseVO.setCode(CommonConstant.business_zero);
            paymentResponseVO.setOrderId(orderNo);
            paymentResponseVO.setAmount(paymentVO.getAmount());
            paymentResponseVO.setPaymentUrl(data.getString("url"));
            int returnType=data.getIntValue("returnType");
            if(returnType==1){
                JSONObject cardInfoJson=data.getJSONObject("cardInfo");
                BankInfoVO bankInfo=new BankInfoVO();
                bankInfo.setBankName(cardInfoJson.getString("bankCodeName"));
                bankInfo.setBankRealName(cardInfoJson.getString("bankOwner"));
                bankInfo.setBankCardNumber(cardInfoJson.getString("bankNum"));
                paymentResponseVO.setBankInfo(bankInfo);
            }
        } else {
            paymentResponseVO.setCode(jsonObject.getIntValue("code"));
            paymentResponseVO.setMessage(jsonObject.getString("msg"));
        }

        return paymentResponseVO;
    }

    @Override
    public WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, WithdrawalVO withdrawalVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/ebPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/ebPayoutAgentCallback";
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantNo", channelRespVO.getMerNo());
        paramMap.put("userName", channelRespVO.getMerNo().concat(withdrawalVO.getUserId()));
        paramMap.put("deviceType", 9);
        paramMap.put("merchantOrderId", orderNo);
        paramMap.put("bankCode", withdrawalVO.getBankCode());
        paramMap.put("bankNum", withdrawalVO.getBankNo());
        paramMap.put("bankOwner", withdrawalVO.getBankUserName());
        paramMap.put("bankAddress", withdrawalVO.getBankBranch());
        paramMap.put("userIp", withdrawalVO.getApplyIp());
        paramMap.put("notifyUrl", notifyUrl);
        paramMap.put("payAmount",  BigDecimalUtils.formatFourKeep2Dec(new BigDecimal(withdrawalVO.getAmount())));

        String channelCode= channelRespVO.getChannelCode();
        if(!StringUtils.hasText(channelCode)){
            channelCode="2182";
        }
        paramMap.put("orderType", channelCode);
        //金额类型: 0人民币 1虚拟币, 默认人民币
       // paramMap.put("amountType", "0");
        paramMap.put("virtualProtocol", "0");


        String key = channelRespVO.getSecretKey();
        String queryBuffer = "merchantNo" + "=" + paramMap.get("merchantNo") + "&" +
                "merchantOrderId" + "=" + paramMap.get("merchantOrderId") + "&" +
                "userName" + "=" + paramMap.get("userName") + "&" +
                "orderType" + "=" + paramMap.get("orderType") + "&" +
                "payAmount" + "=" + paramMap.get("payAmount") + "&" +
                "bankNum" + "=" + paramMap.get("bankNum") + "&" +
                "key" + "=" + key;

        String sign = SignUtil.signOriginStr(queryBuffer);
        paramMap.put("sign", sign);

        String url=channelRespVO.getApiUrl() + "/api/merchant/withdraw/create/v2";
        log.info("EbPay代付订单,请求地址:{},请求参数: {}",url, paramMap);
        String result = HttpClient4Util.doPost(url, paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("EbPay代付订单返回：{}", jsonObject);
        } catch (Exception e) {
            log.error("EbPay代付订单返回异常:{0}",e);
        }

        //统一返回字段
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        withdrawalResponseVO.setOrderNo(orderNo);
        withdrawalResponseVO.setCode(0);
        withdrawalResponseVO.setMessage(jsonObject.getString("msg"));
        withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("EbPay 代付订单返回：{}", jsonObject);
            Integer code = jsonObject.getIntValue("code");
            if (200 == code) {
                JSONObject dataJson = jsonObject.getJSONObject("data");
                String amount = dataJson.getString("payAmount");
                withdrawalResponseVO.setWithdrawOrderId(dataJson.getString("orderId"));
                withdrawalResponseVO.setAmount(String.valueOf(amount));
            }
        } catch (Exception e) {
            log.error("EbPay 代付订单异常:{0}",e);
        }
        return withdrawalResponseVO;
    }

    @Override
    public PayOrderResponseVO queryPayOrder(SystemRechargeChannelBaseVO channelRespVO, String orderNo) {
        PayOrderResponseVO payOrderResponseVO = new PayOrderResponseVO();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantNo", channelRespVO.getMerNo());
        paramMap.put("merchantOrderId", orderNo);

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.paramSignsPay(paramMap, key);
        paramMap.put("sign", sign);

        log.info("EbPay 支付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/api/merchant/getDepositOrder/v2", paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("EbPay 支付订单查询返回：{}", jsonObject);
            Integer code = jsonObject.getIntValue("code");
            payOrderResponseVO.setOrderNo(orderNo);
            payOrderResponseVO.setMessage(jsonObject.getString("msg"));
            payOrderResponseVO.setCode(0);
            payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
            if (200 == code) {
                payOrderResponseVO.setOrderNo(orderNo);
                JSONObject data = jsonObject.getJSONObject("data");
                String amount = data.getString("orderAmount");
                payOrderResponseVO.setAmount(String.valueOf(amount));
                int status = data.getIntValue("orderStatus");
                if(status==1){
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                }
                if(status==2){
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                }
            }
        } catch (Exception e) {
            log.error("EbPay 支付订单查询异常:{0}",e);
        }
        return payOrderResponseVO;
    }

    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantNo", channelRespVO.getMerNo());
        paramMap.put("merchantOrderId", orderNo);

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.paramSignsPay(paramMap, key);
        paramMap.put("sign", sign);

        log.info("EbPay 代付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/api/merchant/getWithdrawOrder/v2", paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("EbPay 代付订单查询返回：{}", jsonObject);
            Integer code = jsonObject.getIntValue("code");
            withdrawalResponseVO.setOrderNo(orderNo);
            withdrawalResponseVO.setCode(0);
            withdrawalResponseVO.setMessage(jsonObject.getString("msg"));
            withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
            if (200 == code) {
                withdrawalResponseVO.setOrderNo(orderNo);
                JSONObject dataJson = jsonObject.getJSONObject("data");
                withdrawalResponseVO.setAmount(dataJson.getString("paidAmount"));
                withdrawalResponseVO.setWithdrawOrderId(dataJson.getString("orderNo"));
                //订单状态(Status):
               // 订单状态 0=待支付1=成功 2=失败
                int status = dataJson.getIntValue("orderStatus");
                if(status==1){
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
                }
                if(status==2){
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
                }
            }
        } catch (Exception e) {
            log.error("EbPay 代付订单查询异常:{0}",e);
        }
        return withdrawalResponseVO;
    }
}
