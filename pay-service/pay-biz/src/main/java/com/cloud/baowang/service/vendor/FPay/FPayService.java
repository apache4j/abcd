package com.cloud.baowang.service.vendor.FPay;

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
 * @description: FPay
 */
@Slf4j
@Service(value = "FPay")
public class FPayService implements BasePayService {

    public static void main(String[] args) {
       // JSONObject retJson=new FPayService().withdrawBankList("https://liveapi.fpay.support","OkSportG");
      //  System.err.println(retJson);
       // String authToken=new FPayService().genAuthToken("https://liveapi.fpay.support","OkSportG","knvVEIuvZYdSzBCjSMQG");
        //System.err.println("authToken:"+authToken);

        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        //channelRespVO.setChannelCode("3");
       // channelRespVO.setMerNo("OkSportG");
       // channelRespVO.setPubKey("knvVEIuvZYdSzBCjSMQG");

        channelRespVO.setMerNo("OkSportD");
        channelRespVO.setPubKey("MxHPMYPsV5xKtFLAa0I2");

        channelRespVO.setApiUrl("https://liveapi.fpay.support");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.store");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setAmount("100");
       // paymentVO.setEmail("test01@gmail.com");
        //paymentVO.setPhoneNum("601012456789");
        paymentVO.setAmount("100");
        paymentVO.setCurrency("MYR");

        String orderNO = OrderUtil.getOrderNo("P", 16);

        new FPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
        new FPayService().queryPayOrder(channelRespVO, orderNO);


        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = new SystemWithdrawChannelResponseVO();
        withdrawChannelResponseVO.setMerNo("OkSportG");
        withdrawChannelResponseVO.setPubKey("knvVEIuvZYdSzBCjSMQG");
        withdrawChannelResponseVO.setApiUrl("https://liveapi.fpay.support");
        withdrawChannelResponseVO.setCallbackUrl("https://gw.playesoversea.store");

        //{"status":true,"data":[{"currency":"MYR","bank_name":"MAYBANK","id":"246"},{"currency":"MYR","bank_name":"CIMB","id":"261"},
        // {"currency":"MYR","bank_name":"HLB","id":"264"},
        // {"currency":"MYR","bank_name":"PUBLIC BANK","id":"273"},
        // {"currency":"MYR","bank_name":"RHB","id":"274"},
        // {"currency":"MYR","bank_name":"AMBANK","id":"250"},
        // {"currency":"MYR","bank_name":"Standard Chartered Bank","id":"275"},
        // {"currency":"MYR","bank_name":"BANK ISLAM","id":"251"},{"currency":"MYR","bank_name":"BSN","id":"257"},
        // {"currency":"MYR","bank_name":"AI-RAHJI BANK","id":"248"},{"currency":"MYR","bank_name":"UOB","id":"277"},{"currency":"MYR","bank_name":"HSBC","id":"265"},{"currency":"MYR","bank_name":"ALIIANCE BANK","id":"249"},{"currency":"MYR","bank_name":"CITI BANK","id":"262"},{"currency":"MYR","bank_name":"AFFIN BANK","id":"247"},{"currency":"MYR","bank_name":"Bank Rakyat","id":"252"},{"currency":"MYR","bank_name":"TOUCH N GO","id":"278"},{"currency":"MYR","bank_name":"GXBANK","id":"279"}]}
        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("1");
        withdrawalVO.setCurrency("MYR");
        withdrawalVO.setBankCode("246");
       // withdrawalVO.setBankBranch("Kentucky");
        withdrawalVO.setBankUserName("Betty");
        withdrawalVO.setBankNo("12332343432");

        //new FPayService().creatPayoutOrder(withdrawChannelResponseVO, withdrawalVO, orderNO);
        // new FPayService().queryPayoutOrder(withdrawChannelResponseVO, "PPbKXqn5pAMThpHav");
    }

    /**
     * 获取授权认证码接口
     * @return
     */
    private JSONObject withdrawBankList(String apiUrl,String merNo){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("username", merNo);
        paramMap.put("currency", "MYR");
        String result = HttpClient4Util.doPost(apiUrl + "/wallet/withdraw_bank_list", paramMap);
        JSONObject retJson = JSONObject.parseObject(result);
        return retJson;
    }

    /**
     * 获取授权认证码接口
     * @return
     */
    private String genAuthToken(String apiUrl,String merNo,String apiKey){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("username", merNo);
        paramMap.put("api_key", apiKey);
        String result = HttpClient4Util.doPost(apiUrl + "/merchant/auth", paramMap);
        JSONObject retJson = JSONObject.parseObject(result);
        if(retJson.getBoolean("status")){
            return retJson.getString("auth");
        }
        return null;
    }


    /**
     * 付款请求
     * @param channelRespVO
     * @param paymentVO
     * @param orderNo
     * @return
     */
    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/fPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/fPayAgentCallback";
        }
        //统一返回字段
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        String authToken=genAuthToken(channelRespVO.getApiUrl(),channelRespVO.getMerNo(), channelRespVO.getPubKey());
        if(!StringUtils.hasText(authToken)){
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage("AUTH_FAIL");
            return paymentResponseVO;
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("auth",authToken);
        paramMap.put("username",channelRespVO.getMerNo());
        paramMap.put("amount", paymentVO.getAmount());
        paramMap.put("currency",paymentVO.getCurrency());
        paramMap.put("orderid", orderNo);
        paramMap.put("email", "abc@gmail.com");
        if(StringUtils.hasText(paymentVO.getEmail())){
            paramMap.put("email", paymentVO.getEmail());
        }
        paramMap.put("phone_number", "00000000");
        if(StringUtils.hasText(paymentVO.getPhoneNum())){
            paramMap.put("phone_number", paymentVO.getPhoneNum());
        }
       // String key = channelRespVO.getSecretKey();

        //String sign = SignUtil.getSignByMapObj(paramMap, key);
       // paramMap.put("sign", sign);

        log.info("FPay代收支付请求参数: {}", paramMap);
        String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/merchant/generate_orders", paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("FPay代收支付返回：{}", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonObject.getBoolean("status")) {
            paymentResponseVO.setCode(CommonConstant.business_zero);
            paymentResponseVO.setOrderId(orderNo);
            paymentResponseVO.setAmount(paymentVO.getAmount());
           // paymentResponseVO.setThirdOrderId(jsonObject.getString("transaction_id"));
            paymentResponseVO.setPaymentUrl(jsonObject.getString("p_url"));
        } else {
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage(jsonObject.getString("message"));
        }

        return paymentResponseVO;
    }

    /**
     * 提款请求
     * @param channelRespVO
     * @param withdrawalVO
     * @param orderNo
     * @return
     */
    @Override
    public WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, WithdrawalVO withdrawalVO, String orderNo) {
        String notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/fPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl()+"/pay/callback/api/fPayoutAgentCallback";
        }

        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        String authToken=genAuthToken(channelRespVO.getApiUrl(),channelRespVO.getMerNo(), channelRespVO.getPubKey());
        if(!StringUtils.hasText(authToken)){
            withdrawalResponseVO.setCode(-1);
            withdrawalResponseVO.setMessage("AUTH_FAIL");
            return withdrawalResponseVO;
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("auth",authToken);
        paramMap.put("currency", withdrawalVO.getCurrency());
        paramMap.put("amount", withdrawalVO.getAmount());
        paramMap.put("orderid", orderNo);
        paramMap.put("bank_id", withdrawalVO.getBankCode());
        paramMap.put("holder_name", withdrawalVO.getBankUserName());
        paramMap.put("account_no", withdrawalVO.getBankNo());
        if(StringUtils.hasText(withdrawalVO.getBankBranch())){
            paramMap.put("bank_branch", withdrawalVO.getBankBranch());
        }

       // String key = channelRespVO.getSecretKey();

        //String sign = SignUtil.getSignByMap(paramMap, key).toUpperCase();
        //paramMap.put("sign", sign);

        log.info("FPay提款请求参数：{}", paramMap);

        try {
            String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/merchant/withdraw_orders", paramMap);

            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("FPay代付返回：{}", resultObj.toString());
            withdrawalResponseVO.setMessage(resultObj.getString("message"));
            if (resultObj.getBoolean("status")) {
                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                withdrawalResponseVO.setOrderNo(orderNo);
               // withdrawalResponseVO.setWithdrawOrderId(resultObj.getString("transaction_id"));
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
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
        String authToken=genAuthToken(channelRespVO.getApiUrl(),channelRespVO.getMerNo(), channelRespVO.getPubKey());
        if(!StringUtils.hasText(authToken)){
            payOrderResponseVO.setCode(-1);
            payOrderResponseVO.setMessage("AUTH_FAIL");
            return payOrderResponseVO;
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("auth",authToken);
        paramMap.put("username", channelRespVO.getMerNo());
        paramMap.put("id", orderNo);

        String key = channelRespVO.getSecretKey();

        //String sign = SignUtil.getSignByMap(paramMap, key);
       // paramMap.put("sign", sign);

        log.info("FPay支付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/merchant/check_status", paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("FPay支付订单查询返回：{}", jsonObject);
            if (jsonObject.getBoolean("status")) {
                payOrderResponseVO.setOrderNo(orderNo);
                //未支付、处理中状态=（1或者2） 成功状态=（4或者5或者6或者7）
                String orderStatus = jsonObject.getString("order_status");
                String amount = jsonObject.getString("amount");
                payOrderResponseVO.setAmount(amount);
                // payOrderResponseVO.setThirdOrderNo(data.getString("order"));
                 if ("completed".equals(orderStatus)) {
                     payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                } else  if ("fail".equals(orderStatus)) {
                     payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                }else  if ("pending".equals(orderStatus)) {
                     payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                 }
            } else {
                payOrderResponseVO.setCode(-1);
                payOrderResponseVO.setPayOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("FPay支付订单查询返回渠道层：{}", payOrderResponseVO);
        } catch (Exception e) {
          log.error("FPay支付订单查询异常:{0}",e);
        }
        return payOrderResponseVO;
    }

    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        String authToken=genAuthToken(channelRespVO.getApiUrl(),channelRespVO.getMerNo(), channelRespVO.getPubKey());
        if(!StringUtils.hasText(authToken)){
            withdrawalResponseVO.setCode(-1);
            withdrawalResponseVO.setMessage("AUTH_FAIL");
            return withdrawalResponseVO;
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("auth",authToken);
        paramMap.put("username", channelRespVO.getMerNo());
        paramMap.put("id", orderNo);

       // String key = channelRespVO.getSecretKey();

       // String sign = SignUtil.getSignByMap(paramMap, key);
      //  paramMap.put("sign", sign);

        log.info("FPay代付订单查询请求参数: {}", paramMap);
        String result = HttpClient4Util.doPost(channelRespVO.getApiUrl() + "/merchant/check_withdraw_status", paramMap);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(result);
            log.info("FPay代付订单查询返回：{}", jsonObject);
            withdrawalResponseVO.setOrderNo(orderNo);
            if (jsonObject.getBoolean("status")) {
               // withdrawalResponseVO.setMessage(jsonObject.getString("msg"));
                //处理中状态：（1或者3或者21） ；成功状态=（8或者9或者10或者11）； 失败状态=（12或者13或者19或者20）；
                String orderStatus = jsonObject.getString("order_status");
                String amount = jsonObject.getString("amount");
                withdrawalResponseVO.setAmount(String.valueOf(amount));
                withdrawalResponseVO.setCommissionAmount(null);
                if ("completed".equals(orderStatus)) {
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
                } else if ("fail".equals(orderStatus)){
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
                }else if ("rejected".equals(orderStatus)){
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
                }else if ("pending".equals(orderStatus)){
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
                }
            } else {
                withdrawalResponseVO.setCode(-1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("FPay代付订单查询返回渠道层：{}", withdrawalResponseVO);
        } catch (Exception e) {
           log.info("FPay代付订单查询返回异常:{0}",e);
        }
        return withdrawalResponseVO;
    }

}
