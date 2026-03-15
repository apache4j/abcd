package com.cloud.baowang.service.vendor.HFPay;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.service.vendor.SQPay.SQPayService;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: ford
 * @createTime: 2025/07/01
 * @description:  汇沣支付
 */
@Slf4j
@Service(value = "HFPay")
public class HFPayService implements BasePayService {

   /* @Autowired
    private DomainService domainService;

    public static void main(String[] args) {
        //泰国收  709014 付198
        //巴西收  193    付 194
        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("193");
        //vnd币种
       // channelRespVO.setMerNo("wbdk_ovnd_zcsh");
        //channelRespVO.setSecretKey("a75907ccc74358b5c8b2324fcb4654ff");
        //MYR币种
         channelRespVO.setMerNo("17294666185248");
         channelRespVO.setSecretKey("ccd14a1099e44a79a21a3fbeadae4ab6");

        channelRespVO.setApiUrl("https://hf-uat-api-merchant.imbillls.com");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setUserId("4547");
        paymentVO.setAmount("10000");
        paymentVO.setFirstName("HFTest");
        paymentVO.setDepositName("HFTest");
        paymentVO.setApplyIp("127.0.0.1");
        paymentVO.setCountryCode("BR");

        String orderNO = OrderUtil.getOrderNoNum("P", 10);

        // new HFPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
        // new HFPayService().queryPayOrder(channelRespVO, orderNO);

        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = new SystemWithdrawChannelResponseVO();
        withdrawChannelResponseVO.setChannelCode("194");
        withdrawChannelResponseVO.setMerNo("17294666185248");
        withdrawChannelResponseVO.setSecretKey("ccd14a1099e44a79a21a3fbeadae4ab6");
        withdrawChannelResponseVO.setApiUrl("https://hf-uat-api-merchant.imbillls.com");
        withdrawChannelResponseVO.setCallbackUrl("https://gw.playesoversea.store");

        orderNO = OrderUtil.getOrderNoNum("W", 10);


        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("15");
        withdrawalVO.setBankUserName("Betty");
        withdrawalVO.setBankNo("54545454514");
        withdrawalVO.setBankCode("TCB");
        withdrawalVO.setBankBranch("TCB");
        withdrawalVO.setCountryCode("BR");

         new HFPayService().creatPayoutOrder(withdrawChannelResponseVO, withdrawalVO, orderNO);
        new HFPayService().queryPayoutOrder(withdrawChannelResponseVO, orderNO);
    }


    *//**
     * 国家编码	国家
     * BR	巴西 (Brazil)
     * ID	印尼 (Indonesia)
     * IN	印度 (India)
     * JP	日本 (Japan)
     * KR	韩国 (Korea)
     * MY	马来西亚 (Malaysia)
     * PH	菲律宾 (Philippines)
     * TH	泰国 (Thailand)
     * VN	越南 (Vietnam)
     * @param channelRespVO
     * @param paymentVO
     * @param orderNo
     * @return
     *//*
    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/hfPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/hfPayAgentCallback";
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantId", channelRespVO.getMerNo());
        paramMap.put("payType", channelRespVO.getChannelCode());
        paramMap.put("orderId", orderNo);
        paramMap.put("amount", paymentVO.getAmount());
        paramMap.put("notifyUrl", notifyUrl);

        paramMap.put("userName", paymentVO.getDepositName());
        //paramMap.put("accNo", paymentVO.getBankCode());
        //paramMap.put("bankCode", paymentVO.getBankCode());
        paramMap.put("country", paymentVO.getCountryCode());
        //巴西
        if(CurrencyEnum.BRL.getCode().equals(paymentVO.getCurrency())){
            paramMap.put("country", "BR");
        }
        //泰铢
        if(CurrencyEnum.THB.getCode().equals(paymentVO.getCurrency())){
            paramMap.put("country", "TH");
        }


        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.getSignByMapObj(paramMap, key);
        paramMap.put("sign", sign.toUpperCase());
        String reqUrl=channelRespVO.getApiUrl() + "/api/pf/receiveorder/create";
        log.info("HFPay代收支付,请求地址:{},请求参数: {}",reqUrl, paramMap);
        String result = HttpClient4Util.doPostJson(reqUrl, JSONObject.toJSONString(paramMap));
        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("HFPay代收支付返回：{}", jsonObject);
        } catch (Exception e) {
           log.error("HFPay代收支付返回异常:{0}",e);
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage("三方发生异常");
           return paymentResponseVO;
        }

        String retCode=jsonObject.getString("code");
        String retMsg=jsonObject.getString("msg");
        if ("200".equals(retCode)) {
            JSONObject dataJson =jsonObject.getJSONObject("data");
            paymentResponseVO.setCode(CommonConstant.business_zero);
            paymentResponseVO.setOrderId(orderNo);
            paymentResponseVO.setAmount(paymentVO.getAmount());
            paymentResponseVO.setPaymentUrl(dataJson.getString("payUrl"));
        } else {
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage(retMsg);
        }

        return paymentResponseVO;
    }


    *//**
     * 发起提现请求
     * @param channelRespVO
     * @param withdrawalVO
     * @param orderNo
     * @return
     *//*
    @Override
    public WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, WithdrawalVO withdrawalVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/hfPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/hfPayoutAgentCallback";
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantId", channelRespVO.getMerNo());
        paramMap.put("payType", channelRespVO.getChannelCode());

        paramMap.put("price", withdrawalVO.getAmount());
        paramMap.put("notifyUrl", notifyUrl);
        paramMap.put("accountName", withdrawalVO.getBankUserName());
        paramMap.put("bankCode", withdrawalVO.getBankCode());
        paramMap.put("certificate", withdrawalVO.getBankBranch());
        paramMap.put("accountNo", withdrawalVO.getBankNo());
        paramMap.put("country", withdrawalVO.getCountryCode());
        //巴西
        if(CurrencyEnum.BRL.getCode().equals(withdrawalVO.getCurrency())){
            paramMap.put("country", "BR");
        }
        //泰铢
        if(CurrencyEnum.THB.getCode().equals(withdrawalVO.getCurrency())){
            paramMap.put("country", "TH");
        }
        paramMap.put("sourceOrderId", orderNo);

        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.getSignByMapObj(paramMap, key).toUpperCase();

        paramMap.put("sign", sign.toUpperCase());

        String reqUrl=channelRespVO.getApiUrl() + "/api/system/payOrder";
        log.info("HFPay代付请求地址:{},请求参数：{}",reqUrl, paramMap);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPostJson(reqUrl, JSONObject.toJSONString(paramMap));
            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("HFPay代付返回：{}", resultObj.toString());
            String status = resultObj.getString("code");
            withdrawalResponseVO.setMessage(resultObj.getString("msg"));
            withdrawalResponseVO.setOrderNo(orderNo);
            //code 此字段标识是否成功 200成功 其他失败
            if ("200".equals(status)) {
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                //withdrawalResponseVO.setWithdrawOrderId(resultObj.getString("orderNo"));
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
            }  else  {
                withdrawalResponseVO.setCode(CommonConstant.business_negative1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("HFPay代付返回渠道层：{}", withdrawalResponseVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return withdrawalResponseVO;
    }


    *//**
     * 查询订单
     * @param channelRespVO
     * @param orderNo
     * @return
     *//*
    @Override
    public PayOrderResponseVO queryPayOrder(SystemRechargeChannelBaseVO channelRespVO, String orderNo) {
        PayOrderResponseVO payOrderResponseVO = new PayOrderResponseVO();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantId", channelRespVO.getMerNo());
        paramMap.put("orderId", orderNo);

        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.getSignByMapObj(paramMap, key);
        paramMap.put("sign", sign.toUpperCase());
        String reqUrl=channelRespVO.getApiUrl() + "/api/pf/receiveorder/queryOrder";
        log.info("HFPay支付订单查询,请求地址:{},请求参数: {}",reqUrl, paramMap);
        String result = HttpClient4Util.doPostJson(reqUrl, JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("HFPay支付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("code");
            if ("200".equals(status)) {
                payOrderResponseVO.setCode(0);
                payOrderResponseVO.setMessage(jsonObject.getString("msg"));
                payOrderResponseVO.setOrderNo(orderNo);
                JSONObject dataJson=jsonObject.getJSONObject("data");
                //订单状态 1	待支付
                //2	交易中
                //5	已支付
                //6	已取消
                String orderStatus = dataJson.getString("status");
                BigDecimal orderAmt = dataJson.getBigDecimal("amount");
                BigDecimal payAmt = dataJson.getBigDecimal("amount");
                payOrderResponseVO.setThirdOrderNo(dataJson.getString("orderId"));
                if ("5".equals(orderStatus)) {
                    payOrderResponseVO.setAmount(payAmt.toString());
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                }else if ("6".equals(orderStatus)) {
                    payOrderResponseVO.setAmount(orderAmt.toString());
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                } else {
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                }
            }
            log.info("HFPay支付订单查询返回渠道层：{}", payOrderResponseVO);
        } catch (Exception e) {
            log.info("HFPay支付订单查询返回异常：{0}", e);
        }
        return payOrderResponseVO;
    }

    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantId", channelRespVO.getMerNo());
        paramMap.put("sourceOrderId", orderNo);

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.getSignByMapObj(paramMap, key);
        paramMap.put("sign", sign.toUpperCase());
        String reqUrl=channelRespVO.getApiUrl() + "/api/system/payOrder/queryOrder";
        log.info("HFPay代付订单查询,请求地址:{},请求参数: {}", reqUrl,paramMap);
        String result = HttpClient4Util.doPostJson(reqUrl, JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("HFPay代付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("code");
            withdrawalResponseVO.setOrderNo(orderNo);
            if ("200".equals(status)) {
                withdrawalResponseVO.setCode(0);
                withdrawalResponseVO.setOrderNo(orderNo);
                withdrawalResponseVO.setMessage(jsonObject.getString("message"));
                //订单状态:0	待处理
                //3	已成功
                //4	失败
                JSONObject dataJson=jsonObject.getJSONObject("data");
                String orderStatus = dataJson.getString("status");
                withdrawalResponseVO.setWithdrawOrderId(dataJson.getString("orderId"));
                String withdrawAmt=dataJson.getString("price");
                if ("3" .equals(orderStatus)) {
                    withdrawalResponseVO.setAmount(withdrawAmt);
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
                } else if ("4".equals(orderStatus)) {
                    withdrawalResponseVO.setAmount(withdrawAmt);
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
                } else {
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
                }
            } else {
                withdrawalResponseVO.setCode(-1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("HFPay代付订单查询返回渠道层：{}", withdrawalResponseVO);
        } catch (Exception e) {
            log.info("HFPay代付订单查询返回异常：{0}", e);
        }
        return withdrawalResponseVO;
    }*/

    @Autowired
    private DomainService domainService;

    public static void main(String[] args) {
        //CN 收  825314 付 430197
        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("8550103");
        //vnd币种
        // channelRespVO.setMerNo("wbdk_ovnd_zcsh");
        //channelRespVO.setSecretKey("a75907ccc74358b5c8b2324fcb4654ff");
        //MYR币种
        channelRespVO.setMerNo("1335656179078061025");
        channelRespVO.setSecretKey("oHXSCXvWrQTbFcpJ20nEOIyK_7PWIn6Dp0hJKNkOi1Y3wx4ZXdbBMTsTlFfbQ2OfyCYXYS07CxrKzg");

        channelRespVO.setApiUrl("https://xhf-uat-api-pay.imbillls.com");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setUserId("4547");
        paymentVO.setAmount("90000");
        paymentVO.setFirstName("SqTest");
        paymentVO.setDepositName("SqTest");
        paymentVO.setApplyIp("127.0.0.1");
        paymentVO.setCountryCode("VN");
        paymentVO.setEmail("11@gmail.com");
        paymentVO.setPhoneNum("13800000000");

        String orderNO = OrderUtil.getOrderNoNum("P", 10);
        channelRespVO.setThirdOrderNo("4202510096486015529");
        //new HFPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
        //new HFPayService().queryPayOrder(channelRespVO, "P4936031150");

        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = new SystemWithdrawChannelResponseVO();
        withdrawChannelResponseVO.setChannelCode("8551103");
        withdrawChannelResponseVO.setMerNo("1335656179078061025");
        withdrawChannelResponseVO.setSecretKey("oHXSCXvWrQTbFcpJ20nEOIyK_7PWIn6Dp0hJKNkOi1Y3wx4ZXdbBMTsTlFfbQ2OfyCYXYS07CxrKzg");
        withdrawChannelResponseVO.setApiUrl("https://xhf-uat-api-pay.imbillls.com");
        withdrawChannelResponseVO.setCallbackUrl("https://gw.playesoversea.store");
        withdrawChannelResponseVO.setExtParam("Alipay");

        orderNO = OrderUtil.getOrderNoNum("W", 10);


        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("200000.00");
        withdrawalVO.setBankUserName("Betty");
        withdrawalVO.setBankNo("54545454514");
        withdrawalVO.setBankCode("VN_ACB");
        withdrawalVO.setBankBranch("TCB");
        withdrawalVO.setCountryCode("VN");
        withdrawalVO.setUserName("betty");
        withdrawalVO.setBankName("BettyBank");

        withdrawChannelResponseVO.setThirdOrderNo("9202510095360676665");
        new HFPayService().creatPayoutOrder(withdrawChannelResponseVO, withdrawalVO, orderNO);
        //"code":0,"data":{"order_num":"9202510095360676665","merchant_id":1335656179078061025,"order_id":"W7284776392","pay_url":""},"message":"ok!"}
        //new HFPayService().queryPayoutOrder(withdrawChannelResponseVO, "W7284776392");
    }


    /**

     * @param channelRespVO
     * @param paymentVO
     * @param orderNo
     * @return
     */
    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/hfPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/hfPayAgentCallback";
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantId", Long.valueOf(channelRespVO.getMerNo()));
        paramMap.put("payType", Integer.valueOf(channelRespVO.getChannelCode()));
        paramMap.put("orderId", orderNo);
        paramMap.put("amount", paymentVO.getAmount());
        paramMap.put("notifyUrl", notifyUrl);

        paramMap.put("userName", paymentVO.getDepositName());
        if (ObjectUtil.isNotEmpty(paymentVO.getBankCode())) {
            paramMap.put("bankCode", paymentVO.getBankCode());
        }
        //paramMap.put("accNo", paymentVO.getBankCode());
        //paramMap.put("bankCode", paymentVO.getBankCode());
        paramMap.put("country", "VN");
        if(StringUtils.isNotEmpty(paymentVO.getCountryCode())){
            paramMap.put("country", paymentVO.getCountryCode());
        }

        paramMap.put("email", paymentVO.getEmail());
        paramMap.put("phone", paymentVO.getPhoneNum());
        String userName = paymentVO.getDepositName();
        if (StringUtils.isEmpty(userName)) {
            userName = paymentVO.getUserId();
        }
        paramMap.put("userName", userName);
        Map<String,Object> extra = new HashMap<>();
        extra.put("subject","shuangji66");

        paramMap.put("extra", JSONObject.toJSONString(extra));
        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.getSignByMapObj(paramMap, key);
        paramMap.put("sign", sign.toUpperCase());
        String reqUrl=channelRespVO.getApiUrl() + "/pay/collection/create";
        log.info("HFPay代收支付,请求地址:{},请求参数: {}",reqUrl, JSONObject.toJSONString(paramMap));
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        try {
            String result = HttpClient4Util.doPostJson(reqUrl, JSONObject.toJSONString(paramMap));
            //统一返回字段
            JSONObject jsonObject = new JSONObject();
            jsonObject = JSONObject.parseObject(result);
            log.info("HFPay代收支付返回：{}", jsonObject);
            String retCode=jsonObject.getString("code");
            String retMsg=jsonObject.getString("msg");
            if ("0".equals(retCode)) {
                JSONObject dataJson =jsonObject.getJSONObject("data");
                paymentResponseVO.setCode(CommonConstant.business_zero);
                paymentResponseVO.setOrderId(orderNo);
                paymentResponseVO.setAmount(paymentVO.getAmount());
                paymentResponseVO.setThirdOrderId(dataJson.getString("order_num"));
                paymentResponseVO.setPaymentUrl(dataJson.getString("pay_url"));
            } else {
                paymentResponseVO.setCode(-1);
                paymentResponseVO.setMessage(retMsg);
            }

            return paymentResponseVO;
        } catch (Exception e) {
            log.error("HFPay代收支付返回异常:{0}",e);
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage("三方发生异常");
            return paymentResponseVO;
        }

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
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/hfPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/hfPayoutAgentCallback";
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantId", Long.valueOf(channelRespVO.getMerNo()));
        paramMap.put("payType", Integer.valueOf(channelRespVO.getChannelCode()));
        paramMap.put("sourceOrderId", orderNo);
        String amountStr = withdrawalVO.getAmount();
        if (amountStr.contains(".")) {
            amountStr = amountStr.substring(0, amountStr.indexOf("."));
        }
        paramMap.put("price", amountStr);
        String extParam = channelRespVO.getExtParam();
        /*if(StringUtils.isNotEmpty(extParam)){
            if("Alipay".equalsIgnoreCase(extParam)){
                paramMap.put("bankName", "Alipay");
            }else{
                paramMap.put("bankName", extParam);
            }

        }*/
        //paramMap.put("bankName", "Alipay");
        paramMap.put("notifyUrl", notifyUrl);
        paramMap.put("country", withdrawalVO.getCountryCode());
        //人民币
        if(CurrencyEnum.CNY.getCode().equals(withdrawalVO.getCurrency())){
            paramMap.put("country", "CN");
        }
        //巴西
        if(CurrencyEnum.BRL.getCode().equals(withdrawalVO.getCurrency())){
            paramMap.put("country", "BR");
        }
        //泰铢
        if(CurrencyEnum.THB.getCode().equals(withdrawalVO.getCurrency())){
            paramMap.put("country", "TH");
        }
        if(CurrencyEnum.KVND.getCode().equals(withdrawalVO.getCurrency())){
            paramMap.put("country", "VN");
        }
        if(CurrencyEnum.VND.getCode().equals(withdrawalVO.getCurrency())){
            paramMap.put("country", "VN");
        }

        paramMap.put("bankName", withdrawalVO.getBankName());
        paramMap.put("bankCode", withdrawalVO.getBankCode());

   /*     paramMap.put("accountName", withdrawalVO.getBankUserName());
        paramMap.put("bankCode", withdrawalVO.getBankCode());
        paramMap.put("certificate", withdrawalVO.getBankBranch());*/
        paramMap.put("accountNo", withdrawalVO.getBankNo());
        paramMap.put("accountName", withdrawalVO.getBankUserName());

        Map<String,Object> extra = new HashMap<>();
        extra.put("subject","shuangji66");


        paramMap.put("extra", JSONObject.toJSONString(extra));


        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.getSignByMapObj(paramMap, key).toUpperCase();

        paramMap.put("sign", sign.toUpperCase());

        String reqUrl=channelRespVO.getApiUrl() + "/pay/create";
        log.info("HFPay代付请求地址:{},请求参数：{}",reqUrl, paramMap);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPostJson(reqUrl, JSONObject.toJSONString(paramMap));
            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("HFPay代付返回：{}", resultObj.toString());
            String status = resultObj.getString("code");
            withdrawalResponseVO.setMessage(resultObj.getString("msg"));
            withdrawalResponseVO.setOrderNo(orderNo);
            //code 此字段标识是否成功 200成功 其他失败
            if ("0".equals(status)) {
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                //withdrawalResponseVO.setWithdrawOrderId(resultObj.getString("orderNo"));
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
                JSONObject dataJson =resultObj.getJSONObject("data");
                withdrawalResponseVO.setAmount(withdrawalVO.getAmount());
                withdrawalResponseVO.setWithdrawOrderId(dataJson.getString("order_num"));

            }  else  {
                withdrawalResponseVO.setCode(CommonConstant.business_negative1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("HFPay代付返回渠道层：{}", withdrawalResponseVO);
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
        paramMap.put("merchant_id", Long.valueOf(channelRespVO.getMerNo()));
        paramMap.put("order_num", channelRespVO.getThirdOrderNo());
        paramMap.put("merchant_order_num", orderNo);

        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.getSignByMapObj(paramMap, key);
        paramMap.put("sign", sign.toUpperCase());
        String reqUrl=channelRespVO.getApiUrl() + "/pay/collection/query";
        log.info("HFPay支付订单查询,请求地址:{},请求参数: {}",reqUrl, paramMap);
        String result = HttpClient4Util.doPostJson(reqUrl, JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("HFPay支付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("code");
            if ("0".equals(status)) {
                payOrderResponseVO.setCode(0);
                payOrderResponseVO.setMessage(jsonObject.getString("msg"));
                payOrderResponseVO.setOrderNo(orderNo);
                JSONObject dataJson=jsonObject.getJSONObject("data");
                //订单状态 1	待支付
                //2	交易中
                //5	已支付
                //6	已取消
                String orderStatus = dataJson.getString("status");
                BigDecimal orderAmt = dataJson.getBigDecimal("amount");
                BigDecimal payAmt = dataJson.getBigDecimal("amount");
                payOrderResponseVO.setThirdOrderNo(dataJson.getString("orderId"));
                if ("5".equals(orderStatus)) {
                    payOrderResponseVO.setAmount(payAmt.toString());
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                }else if ("6".equals(orderStatus)) {
                    payOrderResponseVO.setAmount(orderAmt.toString());
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                } else {
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                }
            }
            log.info("HFPay支付订单查询返回渠道层：{}", payOrderResponseVO);
        } catch (Exception e) {
            log.info("HFPay支付订单查询返回异常：{0}", e);
        }
        return payOrderResponseVO;
    }

    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchant_id", Long.valueOf(channelRespVO.getMerNo()));
        paramMap.put("merchant_order_num", orderNo);
        paramMap.put("order_num", channelRespVO.getThirdOrderNo());
        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.getSignByMapObj(paramMap, key);
        paramMap.put("sign", sign.toUpperCase());
        String reqUrl=channelRespVO.getApiUrl() + "/pay/payment/query";
        log.info("HFPay代付订单查询,请求地址:{},请求参数: {}", reqUrl,paramMap);
        String result = HttpClient4Util.doPostJson(reqUrl, JSONObject.toJSONString(paramMap));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("HFPay代付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("code");
            withdrawalResponseVO.setOrderNo(orderNo);
            if ("0".equals(status)) {
                withdrawalResponseVO.setCode(0);
                withdrawalResponseVO.setOrderNo(orderNo);
                withdrawalResponseVO.setMessage(jsonObject.getString("message"));
                //订单状态:0	待处理
                //3	已成功
                //4	失败
                JSONObject dataJson=jsonObject.getJSONObject("data");
                String orderStatus = dataJson.getString("status");
                withdrawalResponseVO.setWithdrawOrderId(dataJson.getString("orderId"));
                String withdrawAmt=dataJson.getString("price");
                if ("3" .equals(orderStatus)) {
                    withdrawalResponseVO.setAmount(withdrawAmt);
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
                } else if ("1".equals(orderStatus)) {
                    withdrawalResponseVO.setAmount(withdrawAmt);
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
                } else if ("5".equals(orderStatus)) {
                    withdrawalResponseVO.setAmount(withdrawAmt);
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
                } else {
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
                }
            } else {
                withdrawalResponseVO.setCode(-1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("HFPay代付订单查询返回渠道层：{}", withdrawalResponseVO);
        } catch (Exception e) {
            log.info("HFPay代付订单查询返回异常：{0}", e);
        }
        return withdrawalResponseVO;
    }
}
