package com.cloud.baowang.service.vendor.GoPay;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.wallet.PayoutStatusEnum;
import com.cloud.baowang.common.core.utils.HttpClient4Util;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.pay.api.vo.*;
import com.cloud.baowang.service.DomainService;
import com.cloud.baowang.service.vendor.BasePayService;
import com.cloud.baowang.util.SignUtil;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wade
 * @createTime: 2025/07/01
 * @description: Gopay支付
 */
@Slf4j
@Service(value = "GoPay")
public class GoPayService implements BasePayService {

    @Autowired
    private DomainService domainService;

    public static void main(String[] args) {

        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("193");
        //vnd币种
        // channelRespVO.setMerNo("wbdk_ovnd_zcsh");
        //channelRespVO.setSecretKey("a75907ccc74358b5c8b2324fcb4654ff");
        //MYR币种
        channelRespVO.setMerNo("954a8d80-e0fd-4734-9d38-d07ee6c76557");
        channelRespVO.setSecretKey("6489614db7a7461a8ef90f612d43abd5");

        channelRespVO.setApiUrl("https://dj3.go3888pay.com");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.pro");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setUserId("4547");
        paymentVO.setAmount("2");
        paymentVO.setFirstName("HFTest");
        paymentVO.setDepositName("HFTest");
        paymentVO.setApplyIp("127.0.0.1");
        paymentVO.setCountryCode("CN");

        String orderNO = OrderUtil.getOrderNoNum("P", 10);

        new GoPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
        //channelRespVO.setThirdOrderNo("2743e8bc-49db-497c-be8e-f4bf289af7b1");
        //new GoPayService().queryPayOrder(channelRespVO, "P6950236307");

        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = new SystemWithdrawChannelResponseVO();
        withdrawChannelResponseVO.setChannelCode("194");
        withdrawChannelResponseVO.setMerNo("954a8d80-e0fd-4734-9d38-d07ee6c76557");
        withdrawChannelResponseVO.setSecretKey("6489614db7a7461a8ef90f612d43abd5");
        withdrawChannelResponseVO.setApiUrl("https://dj3.go3888pay.com/");
        withdrawChannelResponseVO.setCallbackUrl("https://gw.playesoversea.pro");
        //withdrawChannelResponseVO.setThirdOrderNo("f198b9b430d941188cbbf22a90b95931");
        withdrawChannelResponseVO.setThirdOrderNo("989d0303ae28ea19de4ce5aa911263ef");
        orderNO = OrderUtil.getOrderNoNum("W", 10);


        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("101");
        withdrawalVO.setBankUserName("Betty");
        withdrawalVO.setBankNo("a053aea7e463eac3");
        withdrawalVO.setBankCode("TCB");
        withdrawalVO.setBankBranch("TCB");
        withdrawalVO.setCountryCode("BR");

        //new GoPayService().creatPayoutOrder(withdrawChannelResponseVO, withdrawalVO, orderNO);
        new GoPayService().queryPayoutOrder(withdrawChannelResponseVO, "TKCNY20250915033824AT");
    }


    /**
     * 国家编码	国家
     *
     * @return
     */
    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl() + "/pay/callback/api/goPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl() + "/pay/callback/api/goPayAgentCallback";
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("recvid", channelRespVO.getMerNo());       // 商户号
        paramMap.put("orderid", orderNo);                      // 商户订单号
        paramMap.put("amount", paymentVO.getAmount());         // 金额
        paramMap.put("notifyurl", notifyUrl);                  // 回调地址
        paramMap.put("note", "createpay");                     // 备注
        String key = channelRespVO.getSecretKey();
        String signStr = channelRespVO.getMerNo() + orderNo + paymentVO.getAmount() + key;
        String sign = "";
        try {
            sign = SignUtil.goMd5(signStr);
        } catch (Exception e) {
            log.error("GOPay代收支付,签名异常:{0}", e);
        }

        paramMap.put("sign", sign);
        String reqUrl = channelRespVO.getApiUrl() + "/createpay";
        log.info("goPay代收支付,请求地址:{},请求参数: {}", reqUrl, paramMap);
        String result = HttpClient4Util.doPostJson(reqUrl, JSONObject.toJSONString(paramMap));
        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("goPay代收支付返回：{}", jsonObject);
        } catch (Exception e) {
            log.error("goPay代收支付返回异常:{0}", e);
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage("三方发生异常");
            return paymentResponseVO;
        }

        String code = jsonObject.getString("code");
        String retMsg = jsonObject.getString("msg");
        String dataStr = jsonObject.getString("data");
        if ("1".equals(code)) { // 成功标识为 1
            // data 是 JSON 字符串，需要解析
            JSONObject dataJson = JSONObject.parseObject(dataStr);
            paymentResponseVO.setCode(CommonConstant.business_zero); // 设置业务成功码
            paymentResponseVO.setOrderId(orderNo);
            paymentResponseVO.setAmount(paymentVO.getAmount());
            paymentResponseVO.setPaymentUrl(dataJson.getString("navurl")); // 支付二维码 URL
            String thirdId = dataJson.getString("id");
            paymentResponseVO.setThirdOrderId(thirdId);
        } else {
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage(retMsg);
        }
        return paymentResponseVO;
    }


    /**
     * 发起提现请求
     *
     * @param channelRespVO
     * @param withdrawalVO
     * @param orderNo
     * @return
     */
    @Override
    public WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, WithdrawalVO withdrawalVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl() + "/pay/callback/api/goPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl() + "/pay/callback/api/goPayoutAgentCallback";
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sendid", channelRespVO.getMerNo());
        paramMap.put("orderid", orderNo);
        paramMap.put("amount", withdrawalVO.getAmount());
        paramMap.put("address", withdrawalVO.getBankNo());
        paramMap.put("note", "withdrawal"); // 示例里有 note，可加上
        paramMap.put("notifyurl", notifyUrl);
        String key = channelRespVO.getSecretKey();
        String signStr = channelRespVO.getMerNo() + orderNo + withdrawalVO.getAmount() + key;
        String sign = "";
        try {
            sign = SignUtil.goMd5(signStr);
        } catch (Exception e) {
            log.error("GOPay代收支付,签名异常:{0}", e);
        }

        paramMap.put("sign", sign);

        String reqUrl = channelRespVO.getApiUrl() + "/createwd";
        log.info("goPay代付提款请求地址:{},请求参数：{}", reqUrl, paramMap);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPostJson(reqUrl, JSONObject.toJSONString(paramMap));
            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("goPay代付提款返回：{}", resultObj.toString());
            String status = resultObj.getString("code");
            withdrawalResponseVO.setMessage(resultObj.getString("msg"));
            withdrawalResponseVO.setOrderNo(orderNo);
            //code 此字段标识是否成功 200成功 其他失败
            if ("1".equals(status)) { // 返回1表示成功
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
                withdrawalResponseVO.setWithdrawOrderId(orderNo);

            } else {
                withdrawalResponseVO.setCode(CommonConstant.business_negative1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("goPay代付返回渠道层：{}", withdrawalResponseVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return withdrawalResponseVO;
    }


    /**
     * 查询订单
     *
     * @param channelRespVO
     * @param orderNo
     * @return
     */
    @Override
    public PayOrderResponseVO queryPayOrder(SystemRechargeChannelBaseVO channelRespVO, String orderNo) {
        PayOrderResponseVO payOrderResponseVO = new PayOrderResponseVO();

        String reqUrl = channelRespVO.getApiUrl() + "/getpay?id=" + channelRespVO.getThirdOrderNo();
        log.info("GoPay支付订单查询,请求地址:{}", reqUrl);
        try {
            String result = HttpClient4Util.get(reqUrl);
            JSONObject jsonObject = new JSONObject();
            jsonObject = JSONObject.parseObject(result);
            log.info("GoPay支付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("code");
            if ("1".equals(status)) {
                payOrderResponseVO.setCode(0);
                // 这里 data 是字符串，需要二次解析
                String dataStr = jsonObject.getString("data");
                JSONObject dataJson = JSONObject.parseObject(dataStr);
                payOrderResponseVO.setMessage(jsonObject.getString("msg"));
                payOrderResponseVO.setOrderNo(dataJson.getString("orderid"));


                // 订单状态
                String orderStatus = dataJson.getString("state"); // 注意返回字段是 state，不是 status
                BigDecimal orderAmt = dataJson.getBigDecimal("amount");
                BigDecimal payAmt = dataJson.getBigDecimal("amount");
                payOrderResponseVO.setThirdOrderNo(dataJson.getString("id"));
                //Created  	 1       //已创建
                //Transed	4      //已转币
                //Canceled    	8      //已取消
                //Error  	 	99      //错误
                payOrderResponseVO.setAmount(payAmt.toString());
                if ("4".equals(orderStatus)) {
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                } else if ("1".equals(orderStatus)) {
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                } else if ("8".equals(orderStatus)) {
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                } else if ("99".equals(orderStatus)) {
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                } else {
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                }
                log.info("goPay支付订单查询返回渠道层：{}", payOrderResponseVO);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return payOrderResponseVO;
    }

    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        String reqUrl = channelRespVO.getApiUrl() + "/getpay?id=" + channelRespVO.getThirdOrderNo();
        log.info("GoPay支付订单查询,请求地址:{}", reqUrl);

        JSONObject jsonObject = new JSONObject();
        try {
            String result = HttpClient4Util.get(reqUrl);
            //"code": 1,"msg": "success"
            jsonObject = JSONObject.parseObject(result);
            log.info("GOPay代付订单查询返回：{}", jsonObject);
            String status = jsonObject.getString("code");
            withdrawalResponseVO.setOrderNo(orderNo);
            if ("1".equals(status)) {
                withdrawalResponseVO.setCode(0);
                withdrawalResponseVO.setOrderNo(orderNo);
                withdrawalResponseVO.setMessage(jsonObject.getString("msg"));
                //订单状态:0	待处理
                //3	已成功
                //4	失败
                JSONObject dataJson = jsonObject.getJSONObject("data");
                String orderStatus = dataJson.getString("status");
                withdrawalResponseVO.setWithdrawOrderId(dataJson.getString("orderId"));
                String withdrawAmt = dataJson.getString("price");
                withdrawalResponseVO.setAmount(withdrawAmt);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
            } else {
                withdrawalResponseVO.setCode(-1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("GoPay代付订单查询返回渠道层：{}", withdrawalResponseVO);
        } catch (Exception e) {
            log.info("GoPay代付订单查询返回异常：{0}", e);
        }
        return withdrawalResponseVO;
    }
}
