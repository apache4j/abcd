package com.cloud.baowang.service.vendor.FIXPay;

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
 * @createTime: 2025/07/01
 * @description:
 * 印度一类通道规则：
 * 模式：唤醒
 * 1. 本通道为印度一类资金唤醒通道，禁止运行其他任何类型资金，如违规将冻结你方全部余额。
 *
 * 2. 当出现投诉订单后，将在你方余额中1:1扣留投诉金额，直到你方撤诉处理，处理完成后将退还扣留金额。撤诉完成需向我方提供撤诉凭证，撤诉过程中产生的费用需你方自行承担。
 *
 * 3. 工作时间为每天的印度时间10:00~22:30。
 * 单笔代收限额100RS～50000RS
 * 单笔代付限额100RS～50000RS
 *
 * 4. 实时D0结算，24小时下发回U不限金额
 */
@Slf4j
@Service(value = "FIXPay")
public class FIXPayService implements BasePayService {

    @Autowired
    private DomainService domainService;

    public static void main(String[] args) {
        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("YD");
        //vnd币种
       // channelRespVO.setMerNo("wbdk_ovnd_zcsh");
        //channelRespVO.setSecretKey("a75907ccc74358b5c8b2324fcb4654ff");
        //MYR币种
         channelRespVO.setMerNo("Oksport");
         channelRespVO.setSecretKey("3765408E40A6DC5D4D8C30C055055394");

        channelRespVO.setApiUrl("https://pay.fixyd.com");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setUserId("4547");
        paymentVO.setAmount("10000");
        paymentVO.setEmail("test123@gmail.com");
        paymentVO.setPhoneNum("6524757");
        paymentVO.setFirstName("HFTest");
        paymentVO.setDepositName("HFTest");
        paymentVO.setApplyIp("127.0.0.1");
        paymentVO.setCountryCode("BR");

        String orderNO = OrderUtil.getOrderNoNum("P", 10);

        new FIXPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
         new FIXPayService().queryPayOrder(channelRespVO, orderNO);

        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = new SystemWithdrawChannelResponseVO();
        withdrawChannelResponseVO.setChannelCode("YD");
        withdrawChannelResponseVO.setMerNo("Oksport");
        withdrawChannelResponseVO.setSecretKey("3765408E40A6DC5D4D8C30C055055394");
        withdrawChannelResponseVO.setApiUrl("https://pay.fixyd.com");
        withdrawChannelResponseVO.setCallbackUrl("https://gw.playesoversea.store");

        orderNO = OrderUtil.getOrderNoNum("W", 10);


        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("10");
        withdrawalVO.setBankUserName("Betty");
        withdrawalVO.setBankNo("54545454514");
        withdrawalVO.setBankCode("TCB");
        withdrawalVO.setBankName("Kỹ Thương (TCB)");
        withdrawalVO.setTelephone("8534225");
        withdrawalVO.setEmail("test@gmail.com");
        withdrawalVO.setApplyIp("127.0.0.1");

        // new FIXPayService().creatPayoutOrder(withdrawChannelResponseVO, withdrawalVO, orderNO);
        // new FIXPayService().queryPayoutOrder(withdrawChannelResponseVO, orderNO);
    }


    /**

     * @param channelRespVO
     * @param paymentVO
     * @param orderNo
     * @return
     */
    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
       // String siteCode= CurrReqUtils.getSiteCode();
        String returnUrl= "";
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/fixPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/fixPayAgentCallback";
           // returnUrl=domainService.getReturnUrl(siteCode,1);
        }else {
          //  returnUrl= domainService.getReturnUrl(siteCode,2);
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantNo", channelRespVO.getMerNo());
        paramMap.put("method", channelRespVO.getChannelCode());
        paramMap.put("merchantOrderNo", orderNo);
        paramMap.put("payAmount", paymentVO.getAmount());

        paramMap.put("name", paymentVO.getUserId());
        paramMap.put("mobile", "586428755");
        if(StringUtils.hasText(paymentVO.getPhoneNum())){
            paramMap.put("mobile", paymentVO.getPhoneNum());
        }
        paramMap.put("email", "okluck8888@gmail.com");
        if(StringUtils.hasText(paymentVO.getEmail())){
            paramMap.put("email", paymentVO.getEmail());
        }
        paramMap.put("description", paymentVO.getUserId());

        paramMap.put("notifyUrl", notifyUrl);
        if(StringUtils.hasText(returnUrl)){
            paramMap.put("pageUrl", returnUrl);
        }

        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.genFixSignByMapObj(paramMap, key);
        paramMap.put("sign", sign);
        String reqUrl=channelRespVO.getApiUrl() + "/api/payin/order";
        log.info("FIXPay代收支付,请求地址:{},请求参数: {}",reqUrl, paramMap);
        String result = HttpClient4Util.doPostJson(reqUrl, JSONObject.toJSONString(paramMap));
        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("FIXPay代收支付返回：{}", jsonObject);
        } catch (Exception e) {
           log.error("FIXPay代收支付返回异常:{0}",e);
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage("三方发生异常");
           return paymentResponseVO;
        }

        if ("200".equals(jsonObject.getString("status"))) {
            paymentResponseVO.setCode(CommonConstant.business_zero);
            paymentResponseVO.setOrderId(orderNo);
            paymentResponseVO.setAmount(paymentVO.getAmount());
            JSONObject dataJson=jsonObject.getJSONObject("data");
            paymentResponseVO.setPaymentUrl(dataJson.getString("paymentInfo"));
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
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/fixPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/fixPayoutAgentCallback";
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantNo", channelRespVO.getMerNo());
        paramMap.put("merchantOrderNo", orderNo);
        paramMap.put("payAmount", withdrawalVO.getAmount());

        paramMap.put("mobile", "586428755");
        if(StringUtils.hasText(withdrawalVO.getTelephone())){
            paramMap.put("mobile", withdrawalVO.getTelephone());
        }
        paramMap.put("email", "okluck8888@gmail.com");
        if(StringUtils.hasText(withdrawalVO.getEmail())){
            paramMap.put("email", withdrawalVO.getEmail());
        }

        paramMap.put("bankNumber", withdrawalVO.getBankNo());
        paramMap.put("bankCode", withdrawalVO.getBankCode());
        if(StringUtils.hasText(withdrawalVO.getIfscCode())){
            paramMap.put("bankCode", withdrawalVO.getIfscCode());
        }

        paramMap.put("accountHoldName", withdrawalVO.getBankUserName());
        paramMap.put("notifyUrl", notifyUrl);
        paramMap.put("description", withdrawalVO.getBankUserName());


        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.genFixSignByMapObj(paramMap, key);
        paramMap.put("sign", sign);

        String reqUrl=channelRespVO.getApiUrl() + "/api/payout/order";
        log.info("FIXPay代付请求地址:{}请求参数：{}",reqUrl, paramMap);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPostJson(reqUrl, JSONObject.toJSONString(paramMap));
            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("FIXPay代付返回：{}", resultObj.toString());
            String status = resultObj.getString("status");
            withdrawalResponseVO.setMessage(resultObj.getString("message"));
            withdrawalResponseVO.setOrderNo(orderNo);
            //code 此字段标识是否成功 200成功 其他失败
            if ("200".equals(status)) {
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                JSONObject dataJson=resultObj.getJSONObject("data");
                withdrawalResponseVO.setWithdrawOrderId(dataJson.getString("platOrderNo"));
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
            }  else  {
                withdrawalResponseVO.setCode(CommonConstant.business_negative1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("FIXPay代付返回渠道层：{}", withdrawalResponseVO);
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
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantNo", channelRespVO.getMerNo());
        paramMap.put("merchantOrderNo", orderNo);

        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.genFixSignByMapObj(paramMap, key);
        paramMap.put("sign", sign);

        String reqUrl=channelRespVO.getApiUrl() + "/api/payin/query";
        log.info("FIXPay支付订单查询,请求地址:{},请求参数: {}", reqUrl,paramMap);
        String result = HttpClient4Util.doPostJson(reqUrl, JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("FIXPay支付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("status");
            if ("200".equals(status)) {
                payOrderResponseVO.setCode(0);
                payOrderResponseVO.setOrderNo(orderNo);
                //订单状态
                JSONObject dataJson=jsonObject.getJSONObject("data");
                String orderStatus = dataJson.getString("orderStatus");
                BigDecimal payAmt = dataJson.getBigDecimal("factAmount");
                payOrderResponseVO.setMessage(dataJson.getString("orderMessage"));
                payOrderResponseVO.setThirdOrderNo(dataJson.getString("platOrderNo"));
                if ("SUCCESS".equals(orderStatus)) {
                    payOrderResponseVO.setAmount(payAmt.toString());
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                } else {
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                }
            }
            log.info("FIXPay支付订单查询返回渠道层：{}", payOrderResponseVO);
        } catch (Exception e) {
            log.info("FIXPay支付订单查询返回异常：{0}", e);
        }
        return payOrderResponseVO;
    }

    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantNo", channelRespVO.getMerNo());
        paramMap.put("merchantOrderNo", orderNo);

        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.genFixSignByMapObj(paramMap, key);
        paramMap.put("sign", sign);

        String reqUrl=channelRespVO.getApiUrl() + "/api/payout/query";
        log.info("FIXPay代付付订单查询,请求地址:{},请求参数: {}", reqUrl,paramMap);
        String result = HttpClient4Util.doPostJson(reqUrl, JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("FIXPay代付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("status");
            withdrawalResponseVO.setOrderNo(orderNo);
            if ("200".equals(status)) {
                //订单状态
                JSONObject dataJson=jsonObject.getJSONObject("data");
                String orderStatus = dataJson.getString("orderStatus");
                String amount = dataJson.getString("amount");
                withdrawalResponseVO.setCode(0);
                withdrawalResponseVO.setOrderNo(orderNo);
                //订单状态:PENDING	订单处理中
                //SUCCESS	成功
                //FAILED	失败
                withdrawalResponseVO.setWithdrawOrderId(jsonObject.getString("platOrderNo"));
                if ("SUCCESS" .equals( orderStatus)) {
                    withdrawalResponseVO.setAmount(amount);
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
                } else if ("FAILED".equals(orderStatus)) {
                    withdrawalResponseVO.setAmount(amount);
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
                } else {
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
                }
            } else {
                withdrawalResponseVO.setCode(-1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("FIXPay代付订单查询返回渠道层：{}", withdrawalResponseVO);
        } catch (Exception e) {
            log.info("FIXPay代付订单查询返回异常：{0}", e);
        }
        return withdrawalResponseVO;
    }
}
