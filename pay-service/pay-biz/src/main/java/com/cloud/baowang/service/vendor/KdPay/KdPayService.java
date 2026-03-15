package com.cloud.baowang.service.vendor.KdPay;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.wallet.PayoutStatusEnum;
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

import java.util.HashMap;
import java.util.Map;

/**
 * @author: wade
 * @createTime: 2025/07/01
 * @description: Gopay支付
 */
@Slf4j
@Service(value = "KdPay")
public class KdPayService implements BasePayService {

    @Autowired
    private DomainService domainService;

    public static void main(String[] args) {

        SystemRechargeChannelBaseVO channelRespVO = new SystemRechargeChannelBaseVO();
        channelRespVO.setChannelCode("3");
        //vnd币种
        // channelRespVO.setMerNo("wbdk_ovnd_zcsh");
        //channelRespVO.setSecretKey("a75907ccc74358b5c8b2324fcb4654ff");
        //MYR币种
        channelRespVO.setMerNo("250909526511");
        channelRespVO.setSecretKey("N2nU89HXGQ2dAsPGY2UUEEJAbfojH65C");

        channelRespVO.setApiUrl("https://api.kdpayment.com");
        channelRespVO.setCallbackUrl("https://gw.playesoversea.pro");

        PaymentVO paymentVO = new PaymentVO();
        paymentVO.setUserId("25054425");
        paymentVO.setAmount("1.9");
        paymentVO.setFirstName("HFTest");
        paymentVO.setDepositName("HFTest");
        paymentVO.setApplyIp("127.0.0.1");
        paymentVO.setCountryCode("CN");

        String orderNO = OrderUtil.getOrderNoNum("P", 10);

        //new KdPayService().creatPayOrder(channelRespVO, paymentVO, orderNO);
        //P4481386177
        //new KdPayService().queryPayOrder(channelRespVO, "P4481386177");

        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = new SystemWithdrawChannelResponseVO();
        withdrawChannelResponseVO.setChannelCode("194");
        withdrawChannelResponseVO.setMerNo("250909526511");
        withdrawChannelResponseVO.setSecretKey("N2nU89HXGQ2dAsPGY2UUEEJAbfojH65C");
        withdrawChannelResponseVO.setApiUrl("https://api.kdpayment.com");
        withdrawChannelResponseVO.setCallbackUrl("https://gw.playesoversea.store");
        //withdrawChannelResponseVO.setThirdOrderNo("20250915151533l6u0ID");

        orderNO = OrderUtil.getOrderNoNum("W", 10);


        WithdrawalVO withdrawalVO = new WithdrawalVO();
        withdrawalVO.setAmount("1");
        withdrawalVO.setBankUserName("Betty");
        withdrawalVO.setBankNo("ec60d399ff25d24b71b9bca48ea9be637b");
        withdrawalVO.setBankCode("TCB");
        withdrawalVO.setBankBranch("TCB");
        withdrawalVO.setCountryCode("BR");


        //new KdPayService().creatPayoutOrder(withdrawChannelResponseVO, withdrawalVO, orderNO);
        new KdPayService().queryPayoutOrder(withdrawChannelResponseVO, "TKCNY20250915021507PE");
    }


    /**
     * 国家编码	国家
     *
     * @return
     */
    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        // 回调地址
        String notifyUrl = channelRespVO.getCallbackUrl() + "/pay/callback/api/kdPayCallback";
        if (CommonConstant.business_one.equals(paymentVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl() + "/pay/callback/api/kdPayAgentCallback";
        }
        // 封装请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("userCode", channelRespVO.getMerNo());   // 商户号
        params.put("orderCode", orderNo);                   // 商户订单号
        params.put("amount", paymentVO.getAmount());        // 金额（字符串）
        params.put("payType", channelRespVO.getChannelCode());                         // 固定值3：K豆支付
        params.put("callbackUrl", notifyUrl);               // 回调地址

        // 拼接签名串：orderCode&amount&payType&userCode&key
        String key = channelRespVO.getSecretKey();
        String signStr = orderNo + "&" + paymentVO.getAmount() + "&" + channelRespVO.getChannelCode() + "&" + channelRespVO.getMerNo() + "&" + key;
        String sign;
        try {
            sign = SignUtil.goMd5(signStr).toUpperCase(); // 32位大写
        } catch (Exception e) {
            log.error("KdPay代收支付,签名异常:", e);
            PaymentResponseVO errorVO = new PaymentResponseVO();
            errorVO.setCode(-1);
            errorVO.setMessage("签名生成异常");
            return errorVO;
        }
        params.put("sign", sign);

        // 请求地址
        String reqUrl = channelRespVO.getApiUrl() + "/system/api/pay";
        log.info("kdPay代收支付,请求地址:{}, 请求参数: {}", reqUrl, params);

        String result;
        try (HttpResponse response = HttpRequest.post(reqUrl)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .timeout(30000)
                .form(params)
                .execute()) {
            result = response.body();
        } catch (Exception e) {
            log.error("kdPay代收支付请求异常:", e);
            PaymentResponseVO errorVO = new PaymentResponseVO();
            errorVO.setCode(-1);
            errorVO.setMessage("请求三方接口异常");
            return errorVO;
        }

        // 构建统一返回
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        try {
            JSONObject jsonObject = JSONObject.parseObject(result);
            log.info("kdPay代收支付返回：{}", jsonObject);

            String code = jsonObject.getString("code");
            String message = jsonObject.getString("message");

            if ("200".equals(code)) { // 成功标识为 200
                JSONObject dataJson = jsonObject.getJSONObject("data");

                paymentResponseVO.setCode(CommonConstant.business_zero);
                paymentResponseVO.setOrderId(orderNo);
                paymentResponseVO.setThirdOrderId(dataJson.getString("orderNo"));
                paymentResponseVO.setAmount(paymentVO.getAmount());
                paymentResponseVO.setPaymentUrl(dataJson.getString("url"));       // 支付跳转链接

            } else {
                paymentResponseVO.setCode(-1);
                paymentResponseVO.setMessage(message);
            }
        } catch (Exception e) {
            log.error("kdPay代收支付返回解析异常:", e);
            paymentResponseVO.setCode(-1);
            paymentResponseVO.setMessage("返回解析异常");
        }

        return paymentResponseVO;
    }


    /**
     * 发起提现请求（KdPay代付）
     *
     * @param channelRespVO 渠道信息
     * @param withdrawalVO  提现请求信息
     * @param orderNo       订单号
     * @return WithdrawalResponseVO
     */
    @Override
    public WithdrawalResponseVO creatPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO,
                                                 WithdrawalVO withdrawalVO,
                                                 String orderNo) {
        // 回调地址（可选）
        String notifyUrl = channelRespVO.getCallbackUrl() + "/pay/callback/api/kdPayoutCallback";
        if (CommonConstant.business_one.equals(withdrawalVO.getAccountType())) {
            notifyUrl = channelRespVO.getCallbackUrl() + "/pay/callback/api/kdPayoutAgentCallback";
        }
        // 组装参数
        Map<String, Object> params = new HashMap<>();
        params.put("userCode", channelRespVO.getMerNo());        // 商户号
        params.put("orderCode", orderNo);                        // 商户订单号
        params.put("amount", withdrawalVO.getAmount());          // 下发金额
        params.put("address", withdrawalVO.getBankNo());         // 钱包地址
        params.put("callbackUrl", notifyUrl);                    // 回调地址（可选）

        // 签名串：orderCode&amount&address&userCode&key
        String key = channelRespVO.getSecretKey();
        String signStr = orderNo + "&" + withdrawalVO.getAmount() + "&" + withdrawalVO.getBankNo()
                + "&" + channelRespVO.getMerNo() + "&" + key;
        String sign;
        try {
            sign = SignUtil.goMd5(signStr).toUpperCase(); // 32位大写
        } catch (Exception e) {
            log.error("KdPay代付提款, 签名异常:", e);
            WithdrawalResponseVO errorVO = new WithdrawalResponseVO();
            errorVO.setCode(CommonConstant.business_negative1);
            errorVO.setMessage("签名生成异常");
            return errorVO;
        }
        params.put("sign", sign);

        String reqUrl = channelRespVO.getApiUrl() + "/system/api/remit";
        log.info("kdPay代付提款请求地址:{}, 请求参数：{}", reqUrl, params);

        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        try (HttpResponse response = HttpRequest.post(reqUrl)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(Header.CACHE_CONTROL, "no-cache")
                .timeout(30000)
                .form(params)
                .execute()) {

            String result = response.body();
            JSONObject resultObj = JSONObject.parseObject(result);
            log.info("kdPay代付提款返回：{}", resultObj);

            String code = resultObj.getString("code");
            String message = resultObj.getString("message");
            withdrawalResponseVO.setMessage(message);
            withdrawalResponseVO.setOrderNo(orderNo);

            if ("200".equals(code)) {
                // data 里返回的是三方订单号
                JSONObject dataJson = resultObj.getJSONObject("data");

                withdrawalResponseVO.setCode(CommonConstant.business_zero);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
                withdrawalResponseVO.setWithdrawOrderId(dataJson.getString("orderNo"));
            } else {
                withdrawalResponseVO.setCode(CommonConstant.business_negative1);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
            }
            log.info("kdPay代付返回渠道层：{}", withdrawalResponseVO);

        } catch (Exception e) {
            log.error("kdPay代付请求异常:", e);
            withdrawalResponseVO.setCode(CommonConstant.business_negative1);
            withdrawalResponseVO.setMessage("请求三方接口异常");
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
        String reqUrl = channelRespVO.getApiUrl() + "/system/api/query/pay-order";
        log.info("kdPay支付订单查询，请求地址:{}，订单号:{}", reqUrl, orderNo);

        try {
            // 1. 准备请求参数
            String userCode = channelRespVO.getMerNo(); // 商户号
            // 三方订单号
            String orderCode = ObjectUtil.isNull(channelRespVO.getThirdOrderNo()) ? "" : channelRespVO.getThirdOrderNo(); // 支付接口返回的订单号，可为空字符串

            String customerOrderCode = orderNo;
            // 2. 生成签名 MD5(orderCode&customerOrderCode&userCode&key)
            String signStr = orderCode + "&" + customerOrderCode + "&" + userCode + "&" + channelRespVO.getSecretKey();
            String sign = SignUtil.goMd5(signStr).toUpperCase();

            Map<String, Object> params = new HashMap<>();
            params.put("userCode", userCode);
            params.put("orderCode", orderCode);
            params.put("customerOrderCode", customerOrderCode);
            params.put("sign", sign);

            // 3. 发起 POST 请求 (Hutool HttpRequest)
            try (HttpResponse response = HttpRequest.post(reqUrl)
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .header(Header.CACHE_CONTROL, "no-cache")
                    .timeout(30000)
                    .form(params)
                    .execute()) {

                String result = response.body();
                log.info("kdPay支付订单查询返回原始结果：{}", result);

                JSONObject jsonObject = JSONObject.parseObject(result);
                String code = jsonObject.getString("code");

                if ("200".equals(code)) {
                    payOrderResponseVO.setCode(0);
                    payOrderResponseVO.setMessage(jsonObject.getString("message"));

                    JSONObject dataJson = jsonObject.getJSONObject("data");
                    String status = dataJson.getString("status"); // 1 初始 2 待支付 3 已支付 4 失败
                    String amount = dataJson.getString("amount");

                    payOrderResponseVO.setOrderNo(dataJson.getString("customerOrderCode")); // 商户订单号
                    payOrderResponseVO.setThirdOrderNo(dataJson.getString("orderCode"));   // 第三方订单号
                    payOrderResponseVO.setAmount(amount);

                    // 状态映射
                    if ("3".equals(status)) {
                        payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                    } else if ("4".equals(status)) {
                        payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                    } else {
                        payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                    }
                } else {
                    payOrderResponseVO.setCode(-1);
                    payOrderResponseVO.setMessage(jsonObject.getString("message"));
                    payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                }
            }
        } catch (Exception e) {
            log.error("kdPay支付订单查询异常，订单号:{}，错误:{}", orderNo, e.getMessage(), e);
            payOrderResponseVO.setCode(-1);
            payOrderResponseVO.setMessage("查询异常");
            payOrderResponseVO.setPayOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
        }

        log.info("kdPay支付订单查询返回渠道层：{}", payOrderResponseVO);
        return payOrderResponseVO;
    }


    /**
     * 查询代付订单
     *
     * @param channelRespVO 渠道信息
     * @param orderNo       商户订单号
     * @return WithdrawalResponseVO
     */
    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();
        String reqUrl = channelRespVO.getApiUrl() + "/system/api/query/remit-order";
        log.info("kdPay代付订单查询，请求地址:{}，订单号:{}", reqUrl, orderNo);

        try {
            // 1. 组装参数
            String userCode = channelRespVO.getMerNo(); // 商户号
            String orderCode = channelRespVO.getThirdOrderNo(); // 下发接口返回的 orderNo，三方订单号
            if (orderCode == null) {
                orderCode = "";
            }
            String customerOrderCode = orderNo;
            // 2. 生成签名 MD5(orderCode&customerOrderCode&userCode&key)
            String signStr = orderCode + "&" + customerOrderCode + "&" + userCode + "&" + channelRespVO.getSecretKey();
            String sign = SignUtil.goMd5(signStr).toUpperCase();

            Map<String, Object> params = new HashMap<>();
            params.put("userCode", userCode);
            params.put("orderCode", orderCode);
            params.put("customerOrderCode", customerOrderCode);
            params.put("sign", sign);

            // 3. 发起请求
            try (HttpResponse response = HttpRequest.post(reqUrl)
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .header(Header.CACHE_CONTROL, "no-cache")
                    .timeout(30000)
                    .form(params)
                    .execute()) {

                String result = response.body();
                log.info("kdPay代付订单查询返回原始结果：{}", result);

                JSONObject jsonObject = JSONObject.parseObject(result);
                String code = jsonObject.getString("code");
                withdrawalResponseVO.setOrderNo(orderNo);

                if ("200".equals(code)) {
                    withdrawalResponseVO.setCode(0);
                    withdrawalResponseVO.setMessage(jsonObject.getString("message"));

                    JSONObject dataJson = jsonObject.getJSONObject("data");
                    String status = dataJson.getString("status"); // 1 初始 2 成功 3 失败
                    // 通道订单号
                    withdrawalResponseVO.setWithdrawOrderId(dataJson.getString("orderCode"));  // 平台订单号
                    withdrawalResponseVO.setAmount(dataJson.getString("amount"));
                    // 我方订单号
                    withdrawalResponseVO.setOrderNo(orderNo);

                    // 状态映射
                    if ("2".equals(status)) {
                        withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Success.getCode());
                    } else if ("3".equals(status)) {
                        withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Fail.getCode());
                    } else {
                        withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
                    }
                } else {
                    withdrawalResponseVO.setCode(-1);
                    withdrawalResponseVO.setMessage(jsonObject.getString("message"));
                    withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
                }
            }
        } catch (Exception e) {
            log.error("kdPay代付订单查询异常，订单号:{}，错误:{}", orderNo, e.getMessage(), e);
            withdrawalResponseVO.setCode(-1);
            withdrawalResponseVO.setMessage("查询异常");
            withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
        }

        log.info("kdPay代付订单查询返回渠道层：{}", withdrawalResponseVO);
        return withdrawalResponseVO;
    }

}
