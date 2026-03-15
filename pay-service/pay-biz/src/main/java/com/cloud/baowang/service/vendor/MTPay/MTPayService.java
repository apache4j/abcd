package com.cloud.baowang.service.vendor.MTPay;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.agent.api.api.AgentWithdrawRecordApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalRecordResVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.wallet.PayoutStatusEnum;
import com.cloud.baowang.common.core.utils.Encrypt;
import com.cloud.baowang.common.core.utils.HttpClient4Util;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.pay.api.vo.*;
import com.cloud.baowang.service.vendor.BasePayService;
import com.cloud.baowang.service.vendor.MTPay.vo.BankIdEnum;
import com.cloud.baowang.service.vendor.MTPay.vo.CodeEnum;
import com.cloud.baowang.wallet.api.api.UserWithdrawRecordApi;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserDepositWithdrawalResVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: fangfei
 * @createTime: 2024/09/30 14:55
 * @description: MTPay
 */
@Slf4j
@Service(value = "MTPay")
public class MTPayService implements BasePayService {
    private static final String queryUrl = "https://job1.mxpay.asia/WebForm/Deposit_StatusCheck.aspx";
    private static final String payoutQueryUrl = "https://job1.metapay.net.co/WebForm/Deposit_P2pPayout2_Check_B2b.aspx";
    @Autowired
    private  UserWithdrawRecordApi userWithdrawRecordApi;
    @Autowired
    private  AgentWithdrawRecordApi agentWithdrawRecordApi;

    public static void main(String[] args) {
        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("10");
        channelRespVO.setMerNo("M2370124");
        channelRespVO.setSecretKey("74bddf4e-48c3-472d-b7ae-cde85a783945");
        //channelRespVO.setApiUrl("https://api-demo.metapay.net.co");
        channelRespVO.setApiUrl("https://job1-demo.metapay.net.co");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");
        channelRespVO.setCurrencyCode("MYR");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setAmount("100");
        paymentVO.setApplyIp("127.0.0.1");
        paymentVO.setCurrency("MYR");
        paymentVO.setCountryCode(CodeEnum.getCountryCode(paymentVO.getCurrency()));

        String orderNO = OrderUtil.getOrderNo("B", 10);

        //PaymentResponseVO responseVO = creatPayOrder(channelRespVO, paymentVO, orderNO);
        //new MTPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
        new MTPayService().queryPayOrder(channelRespVO, "CKMYR20250114025439JE");


        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("100");
        withdrawalVO.setCurrency("MYR");
        withdrawalVO.setBankCode("PBBB");
        withdrawalVO.setBankNo("106099253455");
        withdrawalVO.setBankUserName("Chua Chin Hui");

        //new MTPayService().creatPayOrder(channelRespVO, withdrawalVO, orderNO);
        //new MTPayService().queryPayoutOrder(channelRespVO, "BJgXbBeFdQd");
    }

    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String firstName = paymentVO.getFirstName() == null ? "John" : paymentVO.getFirstName();
        String countryCode = CodeEnum.getCountryCode(paymentVO.getCurrency());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("MerchantCode", channelRespVO.getMerNo());
        paramMap.put("TransNum", orderNo);
        paramMap.put("Currency", channelRespVO.getCurrencyCode());
        paramMap.put("Amount", paymentVO.getAmount());
        paramMap.put("PaymentDesc", "");
        paramMap.put("FirstName", firstName);
        paramMap.put("LastName", "");
        paramMap.put("EmailAddress", "test@gmail.com");
        paramMap.put("PhoneNum", "");
        paramMap.put("Address", "");
        paramMap.put("City", "");
        paramMap.put("State", "");
        paramMap.put("Country", countryCode);
        paramMap.put("Postcode", "");
        paramMap.put("MerchantRemark", "");

        String signStr = channelRespVO.getMerNo() + channelRespVO.getSecretKey() +
                orderNo +
                channelRespVO.getCurrencyCode() +
                paymentVO.getAmount() ;

        String sign = Encrypt.getSHA256(signStr).toLowerCase();
        paramMap.put("Signature", sign);

        log.info("MTPay支付请求参数: {}", paramMap);

        //组装表单
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <script>\n" +
                "        // 使用DOMContentLoaded事件，确保DOM已完全加载\n" +
                "        document.addEventListener('DOMContentLoaded', function () {\n" +
                "            var form = document.getElementById('autoSubmitForm');\n" +
                "\n" +
                "           form.submit();\n" +
                "          \n" +
                "        });\n" +
                "    </script>\n" +
                "<style>\n" +
                  ".hidden-form {\n" +
                  "display: none;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <form id=\"autoSubmitForm\" action=\"" + channelRespVO.getApiUrl() + "/WebForm/Deposit.aspx\" method=\"POST\" class=\"hidden-form\">\n" +
                "        <input name=\"MerchantCode\" id=\"\" value=\"" + channelRespVO.getMerNo() + "\"><br>\n" +
                "        <input name=\"TransNum\" id=\"\" value=" + "\"" + orderNo + "\"><br>\n" +
                "        <input name=\"Currency\" id=\"\" value=" + "\"" + channelRespVO.getCurrencyCode() + "\"><br>\n" +
                "        <input name=\"Amount\" id=\"\" value=" + "\"" + paymentVO.getAmount() + "\"><br>\n" +
                "        <input name=\"FirstName\" id=\"\" value=" + "\"" + firstName + "\"><br>\n" +
                "        <input name=\"LastName\" id=\"\" value=\"\"><br>\n" +
                "        <input name=\"EmailAddress\" id=\"\" value=\"test@gmail.com\"><br>\n" +
                "        <input name=\"MerchantRemark\" id=\"\" value=\"\"><br>\n" +
                "        <input name=\"State\" id=\"\" value=\"\"><br>\n" +
                "        <input name=\"City\" id=\"\" value=\"\"><br>\n" +
                "        <input name=\"Postcode\" id=\"\" value=\"\"><br>\n" +
                "        <input name=\"Country\" id=\"\" value=" + "\"" + countryCode + "\"><br>\n" +
                "        <input name=\"Signature\" id=\"\" value=" + "\"" + sign + "\"><br>\n" +
                "    </form>\n" +
                "</body>\n" +
                "</html>";

        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();


        paymentResponseVO.setCode(CommonConstant.business_zero);
        paymentResponseVO.setOrderId(orderNo);
        paymentResponseVO.setAmount(paymentVO.getAmount());
        paymentResponseVO.setPaymentUrl(html);

        return paymentResponseVO;
    }

    @Override
    public WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, WithdrawalVO withdrawalVO, String orderNo) {
        String bankId = BankIdEnum.getBankIdBySource(withdrawalVO.getBankCode());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("MerchantCode", channelRespVO.getMerNo());
        paramMap.put("MerchantTransNum", orderNo);
        paramMap.put("CurrencyTypeId", "1");
        paramMap.put("Amount", withdrawalVO.getAmount());
        paramMap.put("ToBankId", bankId);
        paramMap.put("ToBankAccountName", withdrawalVO.getBankUserName());
        paramMap.put("ToBankAccountNum", withdrawalVO.getBankNo());

        //Merchant Code + Merchant Secret Key
        //+ Merchant Transaction Number + ToBankId + ToBankAccountNum + CurrencyTypeId + Amount.
        String signStr = channelRespVO.getMerNo() + channelRespVO.getSecretKey() +
                orderNo +
                bankId +
                withdrawalVO.getBankNo() + "1" +  withdrawalVO.getAmount();

        String sign = Encrypt.getSHA256(signStr).toLowerCase();
        paramMap.put("Signature", sign);

        log.info("MTPay代付请求：{}", paramMap);
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try {
            String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/WebForm/Deposit_P2pPayout2_B2b.aspx", paramMap);

            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("MTPay代付返回：{}", resultObj.toString());
            String code = resultObj.getString("Result");

            if ("35001".equals(code)) {
                String payoutId = resultObj.getString("PayoutId");
                withdrawalResponseVO.setOrderNo(orderNo);
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                withdrawalResponseVO.setWithdrawOrderId(payoutId);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
            }  else  {
                withdrawalResponseVO.setCode(CommonConstant.business_negative1);
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
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("MerchantCode", channelRespVO.getMerNo());
        paramMap.put("MerchantTransNum", orderNo);

        String signStr = channelRespVO.getMerNo() + channelRespVO.getSecretKey() +
                orderNo;
        String sign = Encrypt.getSHA256(signStr).toLowerCase();
        paramMap.put("CheckString", sign);

        log.info("MT订单查询请求参数: {}, url:{}", paramMap, queryUrl);
        String result = HttpClient4Util.doPost(queryUrl, paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("MT订单查询返回：{}", jsonObject);
            String code = jsonObject.getString("Result");
            payOrderResponseVO.setOrderNo(orderNo);
            if ("2002".equals(code) || "2001".equals(code)) {
                payOrderResponseVO.setMessage(jsonObject.getString("Message"));
                payOrderResponseVO.setOrderNo(orderNo);
                payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
            } else if (("2101".equals(code))) {
                String amount = jsonObject.getString("Amount");
                payOrderResponseVO.setCode(0);
                payOrderResponseVO.setAmount(String.valueOf(amount));
                payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
            } else if ("2102".equals(code) || "2203".equals(code)) {
                String amount = jsonObject.getString("Amount");
                payOrderResponseVO.setAmount(String.valueOf(amount));
                payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
            } else if ("1203".equals(code)) { //订单不存在
                payOrderResponseVO.setCode(-1);
                payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
            } else  {
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
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();

        String payoutId = "";
        UserDepositWithdrawalResVO userVO = userWithdrawRecordApi.getRecordByOrderId(orderNo);
        if (userVO != null) {
            payoutId = userVO.getPayTxId();
        } else {
            AgentWithdrawalRecordResVO agentVO =agentWithdrawRecordApi.getRecordByOrderId(orderNo);
            payoutId = agentVO.getPayTxId();
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("PayoutId", payoutId);
        paramMap.put("MerchantCode", channelRespVO.getMerNo());
        paramMap.put("MerchantTransNum", orderNo);

        //of Merchant Code +
        //Merchant Secret Key + Merchant Payout
        //Number.
        String signStr = channelRespVO.getMerNo() + channelRespVO.getSecretKey() +
                orderNo;
        String sign = Encrypt.getSHA256(signStr).toLowerCase();
        paramMap.put("Signature", sign);

        log.info("MT代付订单查询请求参数: {}, url:{}", paramMap, payoutQueryUrl);
        String result = HttpClient4Util.doPost(payoutQueryUrl, paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("MT代付订单查询返回：{}", jsonObject);
            String code = jsonObject.getString("Result");
            withdrawalResponseVO.setOrderNo(orderNo);
            if ("35001".equals(code) || "35004".equals(code)) {
                withdrawalResponseVO.setMessage(jsonObject.getString("Message"));
                withdrawalResponseVO.setOrderNo(orderNo);
                withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
            } else if (("35002".equals(code))) {
                String amount = jsonObject.getString("Amount");
                withdrawalResponseVO.setCode(0);
                withdrawalResponseVO.setAmount(String.valueOf(amount));
                withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
            } else if ("35003".equals(code)) {
                String amount = jsonObject.getString("Amount");
                withdrawalResponseVO.setAmount(String.valueOf(amount));
                withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
            } else  {
                withdrawalResponseVO.setCode(-1);
                withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Abnormal.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return withdrawalResponseVO;
    }
}
