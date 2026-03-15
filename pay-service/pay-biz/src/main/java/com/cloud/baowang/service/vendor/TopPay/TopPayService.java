package com.cloud.baowang.service.vendor.TopPay;

import com.alibaba.fastjson.JSONObject;
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
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author: ford
 * @createTime: 2025/03/03
 * @description:  TopPay支付
 */
@Slf4j
@Service(value = "TopPay")
public class TopPayService implements BasePayService {

    private static final String privateConfigKey="-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCgB8SQ3rJSqv89\n" +
            "8ddUHYYbDL4qFi4KYjSv95i+bc5aSZAj2VGeXHDfXD5SWbl/8CflZn2S3BPeE/Tk\n" +
            "BkuDdihhX+s9LT73UoggL8EDWK6LTgz605flgk7XYVolYr5VC2YUF7Gcuyft5DHD\n" +
            "k97WLWCs4cwt6fhTzQ6HFIOUJG0Kf2v7NxTe8ISY8WQndG+I9DK7iB6H1MEzoKAB\n" +
            "RVEXVs3ps5RM6nKlEzPDS3VNDJk+YRniJqD2qrOtiGT71HgfpuYge0gGWOnhq70e\n" +
            "ENDmb6DA/FVvzbfEtVepTQ+P36l5tBByIqzzqiirxcTcouq3r9TdReEd5R283/fH\n" +
            "JoFjhkGHAgMBAAECggEADQv1E5aEVUcJniQ8WOg7SbNFQasjP8d34iy+cDAFlcjP\n" +
            "joymIdIJhbxAfHhCLlMG/aXYcQOuPvnDil+VAne8nQuz4v3wVzuMS4t5Ieu19zd6\n" +
            "yWw8wwJXqSmPruKY/9f5D4RK/6smezeScw54KV/ztMSH3+pmS3a5S3v5qjc6CJPv\n" +
            "HadvdGnjxb0iX2JwEq/4i/B67Y33lIjicE+6oqEzF1hnXx55ui6PCkzUuV/QPZgo\n" +
            "Tm+5+yWZtxEa1LFny42ovYqP4AA3F3t0r7FLd8PD0oE5nYYQNKzYyLTTUGvKgKx7\n" +
            "W0V4veRa5OXM7As15DKSVvasnfR8SfI8ryZF2ilIaQKBgQD54f7teLeX8i3bZGCm\n" +
            "MjI6jtKuqgBh6lONrROJ0Gj+k3qsCPlPcI5Y+SF7F2FSlBDZpcQFTCtMBgVd+nek\n" +
            "C/ywMgTGu3Dd60Uh7wh765jW1x5Di1vkkeLplqlrAKhdinPn4lneZWpQLCD+/tYp\n" +
            "yXdthpfHdyoKG/R9OjYRT04TDQKBgQCj8qu3GR0T9UAB3JOAQ+z79s4eXVTebH61\n" +
            "/ae+QJB9Czbok2OoRsKapAgBe4xwO/+R5jxadlcd1gtMGxSl17Qpm7PQXsk78CVX\n" +
            "I9JfwegzdAv3Bm4yUZRoiUp5tph32UJcal9ZcEBJg33HvhMwZ6hyYYH+Be3mnhFZ\n" +
            "Cm0DDkWR4wKBgDo9xXg9+bStmqiXBbvW2UQyw8I4F2y6/Ax6eaJWdBJZ926fjNRK\n" +
            "LLNqJbaV5XYk0qSX8U82yKSohpm35ijTLms2zUwwpgbjbjKRjw2rBO2WKBSgJ6Pn\n" +
            "qNa+6e/psFRVD9zNMhsW0ierkWk9plcJARADHd881AyVJXAGoFF6hX0FAoGAEdvm\n" +
            "bqySqVDm5w0DGE7N6uiIrXAXRZ9l8icudaAM3UIyegVMLtF+c1/460htFPDENz1w\n" +
            "R55qgPqHAoRUrHghzQKEBOe+XgQb3TEuwnWZ+LjkcMVHjb2rP/Pso/OLCkslWnHP\n" +
            "7ys2JYvoULDY4EX6aHNMuMImlx+S3jMvisysMWcCgYB2ZzPObqqb3fBNnD/zNDyj\n" +
            "hCGUXL2WQ1n8kCvGTb9XOHPqnhofmc1b8ogT6S3VnKSj9+F4PLtOf9q6W0tSQnHw\n" +
            "BfCGiIerY/NZlx0dNM+uQQBkspHZbCA6j4s0vStnIvPdejpC7SNV7c1wF/lT2oqW\n" +
            "cME25+tW73YooXtEvuMBjQ==\n" +
            "-----END RSA PRIVATE KEY-----";


    public static void main(String[] args) {
        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("PAKEASYPAISA");
        channelRespVO.setMerNo("M1001621");
        channelRespVO.setPrivateKey(privateConfigKey);

        channelRespVO.setApiUrl("https://api.v2.toppay.cc");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setAmount("101");
        paymentVO.setFirstName("HYTest");
        paymentVO.setApplyIp("127.0.0.1");

        String orderNO = OrderUtil.getOrderNoNum("P", 10);



      // new TopPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
      //  channelRespVO.setApiUrl("https://api.query.toppay.cc/");
      //  new TopPayService().queryPayOrder(channelRespVO, orderNO);

        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = new SystemWithdrawChannelResponseVO();
        withdrawChannelResponseVO.setChannelCode("PAK639390");
        withdrawChannelResponseVO.setMerNo("M1001621");
        withdrawChannelResponseVO.setPrivateKey(privateConfigKey);
        withdrawChannelResponseVO.setApiUrl("https://api.v2.toppay.cc");
        withdrawChannelResponseVO.setCallbackUrl("https://gw.playesoversea.store");

        orderNO = OrderUtil.getOrderNoNum("W", 10);

        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("105");
        withdrawalVO.setBankUserName("Betty");
        withdrawalVO.setBankNo("45457487878");
        //withdrawalVO.setBankCode("MSB");
        //withdrawalVO.setBankName("Maritime Bank");
        //{
        //			"bankCode":"ONELINK",
        //			"bankName":"ONELINK",
        //			"countryCode":"PAK",
        //			"payCategoryCode":"BANK"
        //		},
        //		{
        //			"bankCode":"PAK639530",
        //			"bankName":"Al Baraka Bank Limited",
        //			"countryCode":"PAK",
        //			"payCategoryCode":"BANK"
        //		},
        //		{
        //			"bankCode":"PAK589430",
        //			"bankName":"Allied Bank Limited",
        //			"countryCode":"PAK",
        //			"payCategoryCode":"BANK"
        //		}

        new TopPayService().bankList(withdrawChannelResponseVO);

        //  new TopPayService().creatPayoutOrder(withdrawChannelResponseVO, withdrawalVO, orderNO);
       // channelRespVO.setApiUrl("https://api.query.toppay.cc/");
        // new TopPayService().queryPayoutOrder(withdrawChannelResponseVO, orderNO);
    }

    public String bankList(SystemWithdrawChannelResponseVO channelRespVO){
        JSONObject paramJson= new JSONObject();
        paramJson.put("merchantCode", channelRespVO.getMerNo());
        paramJson.put("requestNo", OrderUtil.getOrderNo("B",18));
        paramJson.put("version", "v1");
        //类目码	类目描述
        //BRA	巴西
        //PHL	菲律宾
        //MEX	墨西哥
        //NGA	尼日利亚
        //PAK	巴基斯坦
        //AUS	澳大利亚
        paramJson.put("countryCode", "PAK");

        String privateKey = channelRespVO.getPrivateKey();

        String signStr = TopPaySignUtils.createStrParam(paramJson);
        String signature = TopPaySignUtils.getSign(signStr, privateKey);
        paramJson.put("signature", signature);

        log.info("TopPay代银行列表请求参数: {}", paramJson);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/gateway/online/banks", JSONObject.toJSONString(paramJson));
        log.info("TopPay 银行列表: {}", result);
        return result;

    }


    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/topPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/topPayAgentCallback";
        }
        JSONObject paramJson= new JSONObject();
        paramJson.put("merchantCode", channelRespVO.getMerNo());
        paramJson.put("outTradeNo", orderNo);
        paramJson.put("requestNo", orderNo);
        paramJson.put("payAmount", paymentVO.getAmount());
        paramJson.put("payType", "COLLECTION");
        paramJson.put("payCategory", "WALLET");
        paramJson.put("payProductCode", channelRespVO.getChannelCode());
        paramJson.put("userName", paymentVO.getFirstName());
        paramJson.put("notifyUrl", notifyUrl);
        paramJson.put("version", "v1");
        paramJson.put("description", "OkSport");
       // paramMap.put("mobile", "1234567890");   //可不填：默认随机
       // paramMap.put("email", "333@qq.com");    //可不填：默认随机
        paramJson.put("payMethod", "PAYLINK");

        String privateKey = channelRespVO.getPrivateKey();

        String signStr = TopPaySignUtils.createStrParam(paramJson);
        String signature = TopPaySignUtils.getSign(signStr, privateKey);
        paramJson.put("signature", signature);

        log.info("TopPay代收支付请求参数: {}", paramJson);
        String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/gateway/online/transaction", JSONObject.toJSONString(paramJson));
        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("TopPay代收支付返回：{}", jsonObject);
        } catch (Exception e) {
           log.error("TopPay代收支付返回异常:{0}",e);
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage("三方发生异常");
           return paymentResponseVO;
        }

        if ("0000".equals(jsonObject.getString("code"))) {
            JSONObject dataJson=jsonObject.getJSONObject("data");
           // String orderStatus=dataJson.getString("status");
            paymentResponseVO.setCode(CommonConstant.business_zero);
            paymentResponseVO.setOrderId(orderNo);
            paymentResponseVO.setAmount(paymentVO.getAmount());
            paymentResponseVO.setThirdOrderId(dataJson.getString("tradeNo"));
            paymentResponseVO.setPaymentUrl(dataJson.getString("paymentValue"));
        } else {
            paymentResponseVO.setCode(CommonConstant.business_negative1);
            paymentResponseVO.setMessage(jsonObject.getString("msg"));
        }

        log.info("TopPay代收返回渠道层：{}", paymentResponseVO);
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
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/topPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/topPayoutAgentCallback";
        }
        JSONObject paramJson = new JSONObject();
        paramJson.put("merchantCode", channelRespVO.getMerNo());
        paramJson.put("outTradeNo", orderNo);
        paramJson.put("requestNo", orderNo);
        paramJson.put("payAmount", withdrawalVO.getAmount());
        paramJson.put("payType", "PAYMENT");
        paramJson.put("payCategory", "BANK");
//        paramJson.put("payProductCode", channelRespVO.getChannelCode());
        // toppay代付产品code需要传银行code update by xiaozhi 2025-03-25
        paramJson.put("payProductCode", withdrawalVO.getBankCode());

        paramJson.put("accountNo",withdrawalVO.getBankNo());
       // paramJson.put("accountType", "");
        if(StringUtils.hasText(withdrawalVO.getBankUserName())){
            paramJson.put("userName",withdrawalVO.getBankUserName());
        }
        // mobile和cnic 固定写死
        paramJson.put("mobile","03352684231");
        paramJson.put("cnic","0002014856412");

        paramJson.put("version", "v1");
        paramJson.put("notifyUrl", notifyUrl);


        String privateKey = channelRespVO.getPrivateKey();
        String signStr = TopPaySignUtils.createStrParam(paramJson);
        String signature = TopPaySignUtils.getSign(signStr, privateKey);
        paramJson.put("signature", signature);

        log.info("TopPay代付请求：{}", paramJson);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "/gateway/online/transaction", JSONObject.toJSONString(paramJson));
            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("TopPay代付返回：{}", resultObj.toString());
            String retCode = resultObj.getString("code");
            withdrawalResponseVO.setMessage(resultObj.getString("msg"));
            withdrawalResponseVO.setOrderNo(orderNo);
            //订单状态:未支付=0;支付中=10;支付成功=20;支付失败=30。
            if ("0000".equals(retCode)) {
                JSONObject dataJson=resultObj.getJSONObject("data");
             //   int orderStatus=dataJson.getIntValue("status");
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                withdrawalResponseVO.setWithdrawOrderId(dataJson.getString("tradeNo"));
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
            }  else  {
                withdrawalResponseVO.setCode(CommonConstant.business_negative1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("TopPay代付返回渠道层：{}", withdrawalResponseVO);
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
        JSONObject paramJson = new JSONObject();
        paramJson.put("merchantCode", channelRespVO.getMerNo());
        paramJson.put("outTradeNo", orderNo);
        paramJson.put("requestNo", orderNo);
        paramJson.put("version", "v1");

        String privateKey = channelRespVO.getPrivateKey();

        String signStr = TopPaySignUtils.createStrParam(paramJson);
        String signature = TopPaySignUtils.getSign(signStr, privateKey);
        paramJson.put("signature", signature);

        log.info("TopPay支付订单查询请求参数: {}", paramJson);
       // String result = HttpClient4Util.doPostJson(channelRespVO.getApiUrl() + "https://api.query.toppay.cc/selectMs/query", JSONObject.toJSONString(paramMap));
        String result = HttpClient4Util.doPostJson("https://api.query.toppay.cc/selectMs/query", JSONObject.toJSONString(paramJson));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("TopPay支付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("code");
            payOrderResponseVO.setCode(CommonConstant.business_negative1);
            payOrderResponseVO.setMessage(jsonObject.getString("msg"));
            if ("0000".equals(status)) {
                payOrderResponseVO.setCode(CommonConstant.business_zero);
                payOrderResponseVO.setOrderNo(orderNo);
                JSONObject dataJson = jsonObject.getJSONObject("data");
                payOrderResponseVO.setThirdOrderNo(dataJson.getString("tradeNo"));
                String amount = dataJson.getString("payAmount");
                payOrderResponseVO.setAmount(String.valueOf(amount));
                //PAYMENT_PROGRESS	付款进行中：交易发起没有完成支付，等待用户付款
                //BECONFIRMED	待确认：向用户付款金额，等待确认是否对用户付款
                //AUDIT	待审核：受到风控或者金额受限等一些外部因数，需要进行审核
                //APPROED	审核通过待打款
                //PAYMENT_DONE	付款完成：收到用户的付款或已经向用户完成付款
                //PAYMENT_CLOSE	付款关闭：订单超时未支付，平台主动进行关闭处理
                //FAILED	交易失败：交易过程中发生失败，具体可查看失败原因
                //ABNORMAL	交易异常：银行方订单状态变化存在异常（如处理中到失败到成功）
                //REJECT	交易拒绝：向用户付款金额或提现时，确认拒绝付款
                //COLOSE	关闭：人工或者自动进行交易关闭
                //TRANS_MANUAL	转人工：订单处于异常状态，进行人工处理
                //WAIT_CONFIRMED	待确认：向对方进行转账，等待确认是否对对方转账
                //TRANSFER_FAILED	转账失败：转账过程中发生失败，具体可查看失败原因
                //TRANSFER_SUCCESS	转账成功：收到对方的转账或已经向对方完成转账
                //TRANSFERRING	转账中：转账发起没有完成转账
                String orderStatus = dataJson.getString("status");
                if ("PAYMENT_DONE".equals(orderStatus)) {
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                }else if ("PAYMENT_CLOSE".equals(orderStatus)||"REJECT".equals(orderStatus)||"FAILED".equals(orderStatus)||"COLOSE".equals(orderStatus)) {
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                } else {
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                }
            }
            log.info("TopPay支付订单查询返回渠道层: {}", payOrderResponseVO);
        } catch (Exception e) {
            log.info("TopPay支付订单查询返回异常：{0}", e);
        }
        return payOrderResponseVO;
    }

    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        JSONObject paramJson = new JSONObject();
        paramJson.put("merchantCode", channelRespVO.getMerNo());
        paramJson.put("outTradeNo", orderNo);
        paramJson.put("requestNo", orderNo);
        paramJson.put("version", "v1");

        String privateKey = channelRespVO.getPrivateKey();

        String signStr = TopPaySignUtils.createStrParam(paramJson);
        String signature = TopPaySignUtils.getSign(signStr, privateKey);
        paramJson.put("signature", signature);
        log.info("TopPay代付订单查询请求参数: {}", paramJson);

        String result = HttpClient4Util.doPostJson("https://api.query.toppay.cc/selectMs/query", JSONObject.toJSONString(paramJson));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("TopPay代付订单查询返回：{}", jsonObject);
            String retCode = jsonObject.getString("code");
            withdrawalResponseVO.setCode(CommonConstant.business_negative1);
            withdrawalResponseVO.setMessage(jsonObject.getString("msg"));
            withdrawalResponseVO.setOrderNo(orderNo);
            if ("0000".equals(retCode)) {
                withdrawalResponseVO.setMessage(jsonObject.getString("msg"));
                JSONObject data = jsonObject.getJSONObject("data");
                JSONObject dataJson = jsonObject.getJSONObject("data");
                withdrawalResponseVO.setWithdrawOrderId(dataJson.getString("tradeNo"));
                String amount = dataJson.getString("payAmount");
                withdrawalResponseVO.setAmount(String.valueOf(amount));
                //PAYMENT_PROGRESS	付款进行中：交易发起没有完成支付，等待用户付款
                //BECONFIRMED	待确认：向用户付款金额，等待确认是否对用户付款
                //AUDIT	待审核：受到风控或者金额受限等一些外部因数，需要进行审核
                //APPROED	审核通过待打款
                //PAYMENT_DONE	付款完成：收到用户的付款或已经向用户完成付款
                //PAYMENT_CLOSE	付款关闭：订单超时未支付，平台主动进行关闭处理
                //FAILED	交易失败：交易过程中发生失败，具体可查看失败原因
                //ABNORMAL	交易异常：银行方订单状态变化存在异常（如处理中到失败到成功）
                //REJECT	交易拒绝：向用户付款金额或提现时，确认拒绝付款
                //COLOSE	关闭：人工或者自动进行交易关闭
                //TRANS_MANUAL	转人工：订单处于异常状态，进行人工处理
                //WAIT_CONFIRMED	待确认：向对方进行转账，等待确认是否对对方转账
                //TRANSFER_FAILED	转账失败：转账过程中发生失败，具体可查看失败原因
                //TRANSFER_SUCCESS	转账成功：收到对方的转账或已经向对方完成转账
                //TRANSFERRING	转账中：转账发起没有完成转账
                String orderStatus = dataJson.getString("status");
                if ("PAYMENT_DONE".equals(orderStatus)) {
                    withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                }else if ("PAYMENT_CLOSE".equals(orderStatus)||"REJECT".equals(orderStatus)||"FAILED".equals(orderStatus)||"COLOSE".equals(orderStatus)) {
                    withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                } else {
                    withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                }
            } else {
                withdrawalResponseVO.setCode(-1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("TopPay代付订单查询返回渠道层: {}", withdrawalResponseVO);
        } catch (Exception e) {
            log.info("TopPay代付订单查询返回异常：{0}", e);
        }
        return withdrawalResponseVO;
    }


}
