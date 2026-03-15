package com.cloud.baowang.service.vendor.EzPay;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.wallet.PayoutStatusEnum;
import com.cloud.baowang.common.core.utils.HttpClient4Util;
import com.cloud.baowang.common.core.utils.MD5Util;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.pay.api.vo.PayOrderResponseVO;
import com.cloud.baowang.pay.api.vo.PaymentResponseVO;
import com.cloud.baowang.pay.api.vo.PaymentVO;
import com.cloud.baowang.pay.api.vo.WithdrawalResponseVO;
import com.cloud.baowang.pay.api.vo.WithdrawalVO;
import com.cloud.baowang.service.vendor.BasePayService;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.util.SignUtil;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: ford
 * @createTime: 2025-02-17 13:07
 * @description: EzPay
 */
@Slf4j
@Service(value = "EzPay")
public class EzPayService implements BasePayService {

    @Autowired
    private SystemDictConfigApi systemDictConfigApi;

    //支付产品(Deposit Product Id):18:GCASH5（1000~50000）19:Gcash2 100-5000021:Gcash3 100-50000
    public static void main(String[] args) {
        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("18");
        channelRespVO.setMerNo("20000098");
        channelRespVO.setSecretKey("JGGYPATHETYFFAA1LKQDFF2ILSBMVOU2CQMMV4UBDVRBILGNXDP9VUMHIYTQ8FGBYEXMHENLOFAHPRWK0AQ3J6S2JFN0QHETR8UVG7GEMTZKLWCKB4GEOAGAQDAUBSQA");
        channelRespVO.setApiUrl("https://pay.ez-pay.xyz"); //http://api.pingan.mom
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setAmount("1000");
        paymentVO.setApplyIp("127.0.0.1");
        paymentVO.setCurrency("PHP");

        String orderNO = OrderUtil.getOrderNoNum("EZ", 32);

        //new EzPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
        new EzPayService().queryPayOrder(channelRespVO, "EZ49088708474899132060579259798713");

        SystemWithdrawChannelResponseVO withResponseVO = new SystemWithdrawChannelResponseVO();
        withResponseVO.setChannelCode("2012");
        withResponseVO.setMerNo("20000098");
        withResponseVO.setSecretKey("JGGYPATHETYFFAA1LKQDFF2ILSBMVOU2CQMMV4UBDVRBILGNXDP9VUMHIYTQ8FGBYEXMHENLOFAHPRWK0AQ3J6S2JFN0QHETR8UVG7GEMTZKLWCKB4GEOAGAQDAUBSQA");
        withResponseVO.setApiUrl("https://pay.ez-pay.xyz"); //http://api.pingan.mom
        withResponseVO.setCallbackUrl("https://gw.playesoversea.store");

        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("100");
        withdrawalVO.setBankNo("5555556666666");
        withdrawalVO.setBankName("CTS");
        withdrawalVO.setBankUserName("Betty");

        // new EzPayService().creatPayoutOrder(withResponseVO, withdrawalVO, orderNO);
        new EzPayService().queryPayoutOrder(withResponseVO, "EZ31325919206924995652458318390599");
    }

    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/ezPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/ezPayAgentCallback";
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mchId", channelRespVO.getMerNo());
        paramMap.put("productId", channelRespVO.getChannelCode());
        paramMap.put("mchOrderId", orderNo);
        paramMap.put("amount", paymentVO.getAmount());
        paramMap.put("clientIp", paymentVO.getApplyIp());
        paramMap.put("callbackUrl", notifyUrl);
        paramMap.put("time", System.currentTimeMillis()/1000);
        paramMap.put("remark", "gcash");

        String key = channelRespVO.getSecretKey();
        String sign = SignUtil.paramSignsPay(paramMap, key);
        paramMap.put("method", "URL");
        paramMap.put("sign", sign);

        log.info("EzPay 支付请求参数: {}", paramMap);
        String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/api/deposit/create-order", paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("EzPay 支付返回：{}", jsonObject);
        } catch (Exception e) {
          log.error("EzPay 支付返回异常:{0}",e);
        }

        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        if (jsonObject.getString("code").equals("1")) {
            JSONObject data = jsonObject.getJSONObject("data");
            paymentResponseVO.setCode(CommonConstant.business_zero);
            paymentResponseVO.setOrderId(orderNo);
            //String mchOrderId=data.getString("mchOrderId"); //对方返回的
            //String amount=data.getString("amount");
            paymentResponseVO.setAmount(paymentVO.getAmount());
            paymentResponseVO.setPaymentUrl(data.getString("url"));

        } else {
            paymentResponseVO.setCode(jsonObject.getIntValue("code"));
            paymentResponseVO.setMessage(jsonObject.getString("msg"));
        }

        return paymentResponseVO;
    }

    /**
     * 代付操作
     * @param channelRespVO
     * @param withdrawalVO
     * @param orderNo
     * @return
     */
    @Override
    public WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, WithdrawalVO withdrawalVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/ezPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/ezPayoutAgentCallback";
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mchId", channelRespVO.getMerNo());
        paramMap.put("type", "5");//3: USDT-trc20 5: PHP

        paramMap.put("cardHolderName", withdrawalVO.getBankUserName());
        paramMap.put("cardNumber", withdrawalVO.getBankNo());
        paramMap.put("bankName", withdrawalVO.getBankCode());
        paramMap.put("amount", withdrawalVO.getAmount());
        SystemDictConfigRespVO systemDictConfigRespVO= systemDictConfigApi.getByCode(DictCodeConfigEnums.EZPAY_FUND_PASSWORD.getCode(), CommonConstant.ADMIN_CENTER_SITE_CODE).getData();
       String fundPassword=systemDictConfigRespVO.getConfigParam();
        paramMap.put("password", MD5Util.md5(fundPassword));
        paramMap.put("callbackUrl", notifyUrl);
        paramMap.put("time", System.currentTimeMillis()/1000);
        paramMap.put("mchOrderId", orderNo);

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.paramSignsPay(paramMap, key);
        paramMap.put("sign", sign);
        paramMap.put("passageId", channelRespVO.getChannelCode());//代付通道(Withdrawal passage Id):2014:Pesonet代付(PHP)2012:Instapay代付(PHP)2002:USDT TRC 代付

        log.info("EzPay 代付请求：{}", paramMap);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/api/withdrawal/create-order", paramMap);
            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("EzPay 代付返回：{}", resultObj.toString());
            Integer code = resultObj.getIntValue("code");
            withdrawalResponseVO.setCode(code);
            withdrawalResponseVO.setMessage(resultObj.getString("msg"));

            if (code == 1) {
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                withdrawalResponseVO.setOrderNo(orderNo);
                withdrawalResponseVO.setWithdrawOrderId(resultObj.getString("orderId"));
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
            }  else  {
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
        } catch (Exception e) {
            log.error("EzPay 代付发生异常:{0}", e);
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
        paramMap.put("mchId", channelRespVO.getMerNo());
        paramMap.put("mchOrderId", orderNo);
        paramMap.put("time", System.currentTimeMillis()/1000);

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.paramSignsPay(paramMap, key);
        paramMap.put("sign", sign);

        log.info("EzPay 支付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/api/deposit/order-info", paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("EzPay 支付订单查询返回：{}", jsonObject);
            Integer code = jsonObject.getIntValue("code");
            payOrderResponseVO.setOrderNo(orderNo);
            payOrderResponseVO.setMessage(jsonObject.getString("msg"));
            payOrderResponseVO.setCode(0);
            payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
            if (1 == code) {
                payOrderResponseVO.setOrderNo(orderNo);
                JSONObject data = jsonObject.getJSONObject("data");
                String amount = data.getString("amount");
                payOrderResponseVO.setAmount(String.valueOf(amount));
                int status = data.getIntValue("status");
                if(status==2 || status==3){
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                }
                if(status==-3 || status==-4){
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                }
            }
        } catch (Exception e) {
           log.error("EzPay 支付订单查询异常:{0}",e);
        }
        return payOrderResponseVO;
    }

    /**
     * 代付订单查询
     * @param channelRespVO
     * @param orderNo
     * @return
     */
    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mchId", channelRespVO.getMerNo());
        paramMap.put("mchOrderId", orderNo);
        paramMap.put("time", System.currentTimeMillis()/1000);

        String key = channelRespVO.getSecretKey();

        String sign = SignUtil.paramSignsPay(paramMap, key);
        paramMap.put("sign", sign);

        log.info("EzPay 代付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/api/withdrawal/order-info", paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("EzPay 代付订单查询返回：{}", jsonObject);
            Integer code = jsonObject.getIntValue("code");
            withdrawalResponseVO.setOrderNo(orderNo);
            withdrawalResponseVO.setCode(0);
            withdrawalResponseVO.setMessage(jsonObject.getString("msg"));
            withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
            if (1 == code) {
                withdrawalResponseVO.setOrderNo(orderNo);
                JSONObject dataJson = jsonObject.getJSONObject("data");
                JSONObject cashInfoData = dataJson.getJSONObject("cashInfo");
                String amount = cashInfoData.getString("payAmount");
                withdrawalResponseVO.setAmount(String.valueOf(amount));
                //订单状态(Status):
                //0：待处理(Pending)，此为处理中的状态。
                //1：代付中(Processing)，此为处理中的状态。
                //2：成功(Success)，此为最终状态。
                //4：审核通过(Approved)，此为处理中的状态。
                //5：审核不通过(Rejected)，此为最终状态。
                int status = dataJson.getIntValue("status");
                if(status==2){
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
                }
                if(status==5){
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
                }
            }
        } catch (Exception e) {
            log.error("EzPay 代付订单查询异常:{0}",e);
        }
        return withdrawalResponseVO;
    }

}
