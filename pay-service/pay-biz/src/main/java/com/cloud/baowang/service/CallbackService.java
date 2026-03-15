package com.cloud.baowang.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.agent.api.api.AgentPayCallbackApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentCallbackDepositParamVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentCallbackWithdrawParamVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentVirtualCurrencyPayCallbackVO;
import com.cloud.baowang.common.core.constants.BigDecimalConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.ECDSAUtil;
import com.cloud.baowang.common.core.utils.Encrypt;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.pay.api.enums.PayChannelNameEnum;
import com.cloud.baowang.pay.api.enums.VirtualCurrencyPayTypeEnum;
import com.cloud.baowang.pay.api.vo.TradeNotifyVo;
import com.cloud.baowang.service.vendor.JZPay.vo.JZPayCallbackVO;
import com.cloud.baowang.service.vendor.JZPay.vo.JZPayoutCallbackVO;
import com.cloud.baowang.service.vendor.MIDPay.vo.MidPaymentBackVO;
import com.cloud.baowang.service.vendor.PAPay.vo.PaPayCallbackVO;
import com.cloud.baowang.service.vendor.PAPay.vo.PaPayoutCallbackVO;
import com.cloud.baowang.service.vendor.TSPay.TSOrderStatusEnum;
import com.cloud.baowang.service.vendor.TSPay.TSSignUtils;
import com.cloud.baowang.service.vendor.TopPay.TopPaySignUtils;
import com.cloud.baowang.util.SignUtil;
import com.cloud.baowang.wallet.api.api.DepositWithdrawOrderQueryApi;
import com.cloud.baowang.wallet.api.api.PayCallbackApi;
import com.cloud.baowang.wallet.api.api.SystemRechargeChannelApi;
import com.cloud.baowang.wallet.api.api.SystemWithdrawChannelApi;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.OwnerUserTypeEnum;
import com.cloud.baowang.wallet.api.vo.DepositWithdrawOrderQueryResponseVO;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.recharge.ChannelQueryReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.userCoin.VirtualCurrencyPayCallbackVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.CallbackDepositParamVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.CallbackWithdrawParamVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author: fangfei
 * @createTime: 2024/10/06 15:18
 * @description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CallbackService {
    private final PayCallbackApi payCallbackApi;
    private final AgentPayCallbackApi agentPayCallbackApi;
    private final SystemRechargeChannelApi systemRechargeChannelApi;
    private final SystemWithdrawChannelApi systemWithdrawChannelApi;
    private final DepositWithdrawOrderQueryApi depositWithdrawOrderQueryApi;
    @Value("${common.config.jvPayPubKey}")
    private String jvPayPubKey;

    public static void main(String[] args) {
        //{amount=1.90000000, sign=7E2D51D07AEBEFC85E994E1C19F3714E, orderCode=P8096379431, userCode=250909526511, status=3}
        String orderCode = "P8096379431";
        String amount = "1.90000000";
        String userCode = "250909526511";
        String status = "3";
        String key = "N2nU89HXGQ2dAsPGY2UUEEJAbfojH65C";
        String rawStr = orderCode + "&" + amount + "&" + userCode + "&" + status + "&" + key;
        String mySign = DigestUtil.md5Hex(rawStr).toUpperCase();
        String sign = "7E2D51D07AEBEFC85E994E1C19F3714E";
        System.out.println(mySign);
        System.out.println(mySign.equals(sign));

        String str1 = "3b079128922c450a0c178127dcc6a151";
        String str2 = "1dc7aca11895f929b33c396db742e1f6";
        String signStr = str1 + "6489614db7a7461a8ef90f612d43abd5";
        String mySign2 = SignUtil.goMd5(signStr);
        System.out.println(mySign2);
        System.out.println(str2.equals(mySign2));

    }

    public ResponseVO jvPayCallback(List<TradeNotifyVo> rechargeTradeNotifyVoList, String signVal, String timestamp, String random) {
        //请求头校验
        JSONArray bodyJsonArray = JSONArray.parseArray(JSON.toJSONString(rechargeTradeNotifyVoList));
        boolean validFlag = ECDSAUtil.verifySign(timestamp, random, bodyJsonArray, signVal, jvPayPubKey);
        if (!validFlag) {
            log.info("JVPAY通知回调结果校验失败");
            return ResponseVO.fail(ResultCode.SIGN_ERROR);
        }
        log.info("签名验证成功");
        for (TradeNotifyVo vo : rechargeTradeNotifyVoList) {
            //会员
            if (OwnerUserTypeEnum.USER.getCode().equals(vo.getOwnerUserType())) {
                VirtualCurrencyPayCallbackVO virtualCurrencyPayCallbackVO = ConvertUtil.entityToModel(vo, VirtualCurrencyPayCallbackVO.class);
                if (VirtualCurrencyPayTypeEnum.WITHDRAW.getCode().equals(vo.getOrderType())) {
                    CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                    paramVO.setOrderNo(virtualCurrencyPayCallbackVO.getOrderNo());
                    paramVO.setPayId(virtualCurrencyPayCallbackVO.getTradeHash());
                    paramVO.setUserAccount(virtualCurrencyPayCallbackVO.getOwnerUserId());
                    if (ThirdPayOrderStatusEnum.Pending.getCode().equals(virtualCurrencyPayCallbackVO.getTradeStatus())) {
                        paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                    } else {
                        paramVO.setStatus(virtualCurrencyPayCallbackVO.getTradeStatus());
                    }
                    paramVO.setAmount(virtualCurrencyPayCallbackVO.getTradeAmount());
                    paramVO.setRemark(vo.getChainRespMsg());
                    payCallbackApi.withdrawCallback(paramVO);
                } else {
                    payCallbackApi.virtualCurrencyDepositCallback(virtualCurrencyPayCallbackVO);
                }
            } else if (OwnerUserTypeEnum.AGENT.getCode().equals(vo.getOwnerUserType())) {
                AgentVirtualCurrencyPayCallbackVO virtualCurrencyPayCallbackVO = ConvertUtil.entityToModel(vo, AgentVirtualCurrencyPayCallbackVO.class);
                if (VirtualCurrencyPayTypeEnum.WITHDRAW.getCode().equals(vo.getOrderType())) {
                    AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                    paramVO.setOrderNo(virtualCurrencyPayCallbackVO.getOrderNo());
                    paramVO.setPayId(virtualCurrencyPayCallbackVO.getTradeHash());
                    paramVO.setAgentAccount(virtualCurrencyPayCallbackVO.getOwnerUserId());
                    paramVO.setAmount(virtualCurrencyPayCallbackVO.getTradeAmount());
                    if (ThirdPayOrderStatusEnum.Pending.getCode().equals(virtualCurrencyPayCallbackVO.getTradeStatus())) {
                        paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                    } else {
                        paramVO.setStatus(virtualCurrencyPayCallbackVO.getTradeStatus());
                    }
                    paramVO.setRemark(vo.getChainRespMsg());
                    agentPayCallbackApi.withdrawCallback(paramVO);
                } else {
                    agentPayCallbackApi.virtualCurrencyDepositCallback(virtualCurrencyPayCallbackVO);
                }
            }
        }

        return ResponseVO.success();
    }

    public String pGPayCallback(Map<String, Object> param) {
        String orderId = param.get("orderId").toString();
        String amount = param.get("amount").toString();
        String status = param.get("status").toString();

        JSONObject result = new JSONObject();
        result.put("code", "0");
        result.put("description", "Success");

        if ("20".equals(status)) {
            CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
            paramVO.setAmount(new BigDecimal(amount));
            paramVO.setOrderNo(orderId);
            paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
            payCallbackApi.userDepositCallback(paramVO);
        }

        return result.toJSONString();

    }

    public String midPayCallback(MidPaymentBackVO backVO, String userType) {
        String status = backVO.getStatus();

        ChannelQueryReqVO reqVO = new ChannelQueryReqVO();
        reqVO.setChannelName(PayChannelNameEnum.MidPay.getName());
        //reqVO.setCurrencyCode(backVO.getCurrency());
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelByCode(reqVO);

        if ("SUCCESS".equals(status)) {
            String amount = new BigDecimal(backVO.getAmount()).setScale(2, RoundingMode.DOWN).toString();
            String signStr = "secretKey=" + baseVO.getSecretKey() + "&amount=" + amount +
                    "&currency=" + backVO.getCurrency() + "&id=" + backVO.getId() +
                    "&orderId=" + backVO.getOrderId() + "&status=" + backVO.getStatus();
            log.info("MidPay加密前： {}", signStr);

            String sign = Encrypt.getSHA256(signStr).toUpperCase();
            log.info("MidPay加密后： {}", sign);
            if (!sign.equals(backVO.getHash())) {
                log.info("MidPay代收验签失败");
                return "SUCCESS";
            } else {
                log.info("MidPay代付验签成功");
            }
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(backVO.getAmount()));
                paramVO.setOrderNo(backVO.getOrderId());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(backVO.getAmount()));
                paramVO.setOrderNo(backVO.getOrderId());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }

        } else if ("FAIL".equals(status) || "REJECTED".equals(status) || "REVERSED".equals(status)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(backVO.getAmount()));
                paramVO.setOrderNo(backVO.getOrderId());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(backVO.getAmount()));
                paramVO.setOrderNo(backVO.getOrderId());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        }

        return "SUCCESS";
    }

    public String midPayoutCallback(MidPaymentBackVO backVO, String userType) {
        ChannelQueryReqVO reqVO = new ChannelQueryReqVO();
        reqVO.setChannelName(PayChannelNameEnum.MidPay.getName());
        //reqVO.setCurrencyCode(backVO.getCurrency());
        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelByCode(reqVO);
        //验签 secretKey=xxx&id=xx&orderId=xx&status=xx
        String signStr = "secretKey=" + baseVO.getSecretKey() + "&id=" + backVO.getId() +
                "&orderId=" + backVO.getOrderId() + "&status=" + backVO.getStatus();
        log.info("MidPay代付加密前： {}", signStr);

        String sign = Encrypt.getSHA256(signStr).toUpperCase();
        log.info("MidPay代付加密后： {}", sign);
        if (!sign.equals(backVO.getHash())) {
            log.info("MidPay代付验签失败");
            return "SUCCESS";
        } else {
            log.info("MidPay代付验签成功");
        }

        String status = backVO.getStatus();
        if ("SUCCESS".equals(status)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(backVO.getAmount()));
                paramVO.setOrderNo(backVO.getOrderId());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setOrderNo(backVO.getOrderId());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                paramVO.setAmount(new BigDecimal(backVO.getAmount()));
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        } else if ("REJECTED".equals(status) || "REVERSED".equals(status) || "FAIL".equals(status)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setOrderNo(backVO.getOrderId());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setOrderNo(backVO.getOrderId());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        }

        return "SUCCESS";
    }

    public String paPayCallback(PaPayCallbackVO backVO, String userType) {
        ChannelQueryReqVO reqVO = new ChannelQueryReqVO();
        reqVO.setChannelName(PayChannelNameEnum.PAPay.getName());
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelByCode(reqVO);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mchId", backVO.getMchId());
        paramMap.put("outTradeNo", backVO.getOutTradeNo());
        paramMap.put("payAmount", backVO.getPayAmount());
        paramMap.put("transactionId", backVO.getTransactionId());
        paramMap.put("nonceStr", backVO.getNonceStr());
        paramMap.put("success", backVO.getSuccess());
        paramMap.put("attach", backVO.getAttach());
        boolean checkFlag = SignUtil.checkPaPaySign(paramMap, backVO.getSign(), baseVO.getPubKey());
        if (!checkFlag) {
            log.info("PaPay支付验签失败");
            return "CHECK_SIGN_ERROR";
        } else {
            log.info("PaPay支付验签成功:{}", paramMap);
        }

        String status = backVO.getSuccess();
        if ("true".equals(status)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(backVO.getPayAmount()));
                paramVO.setOrderNo(backVO.getOutTradeNo());
                paramVO.setPayId(backVO.getTransactionId());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(backVO.getPayAmount()));
                paramVO.setOrderNo(backVO.getOutTradeNo());
                paramVO.setPayId(backVO.getTransactionId());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        }
        return "OK";
    }

    /**
     * 提款通知
     *
     * @param backVO   通知参数
     * @param userType 会员类型
     * @return
     */
    public String paPayoutCallback(PaPayoutCallbackVO backVO, String userType) {
        ChannelQueryReqVO reqVO = new ChannelQueryReqVO();
        reqVO.setChannelName(PayChannelNameEnum.PAPay.getName());
        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelByCode(reqVO);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mchId", backVO.getMchId());
        paramMap.put("outTradeNo", backVO.getOutTradeNo());
        paramMap.put("payAmount", backVO.getPayAmount());
        paramMap.put("status", backVO.getStatus());
        boolean checkFlag = SignUtil.checkPaPaySign(paramMap, backVO.getSign(), baseVO.getPubKey());
        if (!checkFlag) {
            log.info("PaPay代付验签失败");
            return "CHECK_SIGN_ERROR";
        } else {
            log.info("PaPay代付验签成功:{}", paramMap);
        }

        String status = backVO.getStatus();
        if ("2".equals(status)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(backVO.getPayAmount()));
                paramVO.setOrderNo(backVO.getOutTradeNo());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setOrderNo(backVO.getOutTradeNo());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                paramVO.setAmount(new BigDecimal(backVO.getPayAmount()));
                agentPayCallbackApi.withdrawCallback(paramVO);
            }

        } else if ("3".equals(status)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setOrderNo(backVO.getOutTradeNo());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setOrderNo(backVO.getOutTradeNo());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                paramVO.setAmount(new BigDecimal(backVO.getPayAmount()));
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        } else if ("4".equals(status)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(backVO.getPayAmount()));
                paramVO.setOrderNo(backVO.getOutTradeNo());
                paramVO.setRemark("冲正");
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setOrderNo(backVO.getOutTradeNo());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                paramVO.setRemark("冲正");
                paramVO.setAmount(new BigDecimal(backVO.getPayAmount()));
                agentPayCallbackApi.withdrawCallback(paramVO);
            }

        }
        return "OK";
    }

    public void mtPayCallback(JSONObject jsonObject, String userType) {
        String result = jsonObject.get("Result").toString();
        String orderId = jsonObject.get("TransNum").toString();
        String sign = jsonObject.get("CheckString2").toString();

        String currency = jsonObject.getString("Currency");
        String merNo = jsonObject.getString("MerchantCode");
        ChannelQueryReqVO reqVO = new ChannelQueryReqVO();
        reqVO.setChannelName(PayChannelNameEnum.MTPay.getName());
        //reqVO.setCurrencyCode(currency);
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelByCode(reqVO);

        String reOrderId = new StringBuilder(orderId).reverse().toString();
        String reMerNo = new StringBuilder(merNo).reverse().toString();
        String signStr = reMerNo + reOrderId + baseVO.getSecretKey();

        log.info("MTPay代收加密前： {}", signStr);
        String mySign = Encrypt.getSHA256(signStr).toLowerCase();

        log.info("MTPay代收加密后： {}", mySign);
        if (!sign.equals(mySign)) {
            log.info("MTPay代收验签失败");
            return;
        } else {
            log.info("MTPay代收验签成功");
        }

        if ("10001".equals(result)) {
            String amount = jsonObject.get("Amount").toString();
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }

        }
    }

    public void mtPayoutCallback(Map<String, Object> param, String userType) {
        String result = param.get("Result").toString();
        String orderId = param.get("MerchantTransNum").toString();
        String sign = param.get("CheckString2").toString();

        String currencyTypeId = param.get("CurrencyTypeId").toString();
        String merNo = param.get("MerchantCode").toString();
        String payoutId = param.get("PayoutId").toString();
        String toBankId = param.get("ToBankId").toString();
        String toBankAccountNum = param.get("ToBankAccountNum").toString();
        String amount = param.get("Amount").toString();

        ChannelQueryReqVO reqVO = new ChannelQueryReqVO();
        reqVO.setChannelName(PayChannelNameEnum.MTPay.getName());
        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelByCode(reqVO);
        String signStr = merNo + baseVO.getSecretKey() + orderId + payoutId + toBankId
                + toBankAccountNum + currencyTypeId + amount;

        log.info("MTPay代付加密前： {}", signStr);
        String mySign = Encrypt.getSHA256(signStr).toLowerCase();

        log.info("MTPay代付加密后： {}", mySign);
        if (!sign.equals(mySign)) {
            log.info("MTPay代付验签失败");
            return;
        } else {
            log.info("MTPay代付验签成功");
        }

        if ("35002".equals(result)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setOrderNo(orderId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                paramVO.setAmount(new BigDecimal(amount));
                agentPayCallbackApi.withdrawCallback(paramVO);
            }

        }
    }

    public void luckyPayCallback(Map<String, Object> param, String userType) {
        String status = param.get("status").toString();
        String orderId = param.get("clientNo").toString();
        String sign = param.get("sign").toString();
        String thirdOrderId = param.get("orderNo").toString();

        ChannelQueryReqVO reqVO = new ChannelQueryReqVO();
        reqVO.setChannelName(PayChannelNameEnum.LuckyPay.getName());
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelByCode(reqVO);

        //clientCode&clientNo&orderNo&payAmount&status&txid+privateKey
        String clientCode = param.get("clientCode").toString();
        String amount = param.get("payAmount").toString();
        String txId = param.get("txid").toString();
        String signStr = clientCode + "&" +
                orderId + "&" +
                thirdOrderId + "&" +
                amount + "&" +
                status + "&" +
                txId + baseVO.getSecretKey();

        log.info("luckyPay代收加密前： {}", signStr);
        String mySign = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();
        log.info("luckyPay代收加密后： {}", mySign);
        if (!sign.equals(mySign)) {
            log.info("luckyPay代收验签失败");
            return;
        } else {
            log.info("luckyPay代收验签成功");
        }

        if ("PAID".equals(status) || "FINISH".equals(status)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderId);
                paramVO.setPayId(thirdOrderId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderId);
                paramVO.setPayId(thirdOrderId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }

        } else if ("CANCEL".equals(status)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setOrderNo(orderId);
                paramVO.setPayId(thirdOrderId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setOrderNo(orderId);
                paramVO.setPayId(thirdOrderId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }

        }
    }

    public void luckyPayoutCallback(Map<String, Object> param, String userType) {
        String status = param.get("status").toString();
        String orderId = param.get("clientNo").toString();
        String sign = param.get("sign").toString();
        String thirdOrderId = param.get("orderNo").toString();

        //clientCode&clientNo&orderNo&payAmount&status&txid+privateKey
        ChannelQueryReqVO reqVO = new ChannelQueryReqVO();
        reqVO.setChannelName(PayChannelNameEnum.LuckyPay.getName());
        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelByCode(reqVO);

        //clientCode&clientNo&orderNo&payAmount&status&txid+privateKey
        String clientCode = param.get("clientCode").toString();
        String amount = param.get("payAmount").toString();
        String txId = param.get("txid").toString();
        String signStr = clientCode + "&" +
                orderId + "&" +
                thirdOrderId + "&" +
                amount + "&" +
                status + "&" +
                txId + baseVO.getSecretKey();

        log.info("luckyPay代付加密前： {}", signStr);
        String mySign = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();
        log.info("luckyPay代付加密后： {}", mySign);
        if (!sign.equals(mySign)) {
            log.info("luckyPay代付验签失败");
            return;
        } else {
            log.info("luckyPay代付验签成功");
        }

        if ("PAID".equals(status) || "FINISH".equals(status)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderId);
                paramVO.setPayId(thirdOrderId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setOrderNo(orderId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setPayId(thirdOrderId);
                agentPayCallbackApi.withdrawCallback(paramVO);
            }

        }
    }

    public String mhdPayCallback(String channelName, Map<String, Object> param, String userType) {
        String opstate = param.get("opstate").toString();
        String orderId = param.get("orderid").toString();
        String parter = param.get("parter").toString();
        String sign = param.get("sign").toString();
        String remark = param.getOrDefault("remark", "").toString();

        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelInfoByMerNo(channelName, parter);

        //opstate值&orderid=orderid值&ovalue=ovalue值&parter=商家接口调用ID&key=商家接口调用密钥
        String amount = param.get("ovalue").toString();
        String signStr = "opstate=" + opstate + "&" +
                "orderid=" + orderId + "&" +
                "ovalue=" + amount + "&" +
                "parter=" + parter;
        if (StringUtils.isNotBlank(remark)) {
            signStr = signStr + "&remark=" + remark;
        }
        signStr = signStr + "&key=" + baseVO.getSecretKey();

        log.info("MhdPay代收加密前： {}", signStr);
        String mySign = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();
        log.info("MhdPay代收加密后： {}", mySign);

        if (!sign.equals(mySign)) {
            log.info("MhdPay代收验签失败");
            return "success";
        } else {
            log.info("MhdPay代收验签成功");
        }

        if ("1".equals(opstate)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }

        }

        return "success";
    }

    public String mhdPayoutCallback(String channelName, Map<String, Object> param, String userType) {
        String opstate = param.get("opstate").toString();
        String orderId = param.get("orderid").toString();
        String sign = param.get("sign").toString();
        String parter = param.get("parter").toString();

        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelInfoByMerNo(channelName, parter);

        //opstate=opstate值&orderid=orderid值&ovalue=ovalue值&parter=商家接口调用ID&key=商家接口调用密钥
        String amount = param.get("ovalue").toString();
        String signStr = "opstate=" + opstate + "&" +
                "orderid=" + orderId + "&" +
                "ovalue=" + amount + "&" +
                "parter=" + parter + "&" +
                "key=" + baseVO.getSecretKey();

        log.info("MhdPay代付加密前： {}", signStr);
        String mySign = DigestUtils.md5DigestAsHex(signStr.getBytes()).toLowerCase();
        log.info("MhdPay代付加密后： {}", mySign);

        if (!sign.equals(mySign)) {
            log.info("MhdPay代付验签失败");
            return "success";
        } else {
            log.info("MhdPay代付验签成功");
        }

        if ("1".equals(opstate)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setOrderNo(orderId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                paramVO.setAmount(new BigDecimal(amount));
                agentPayCallbackApi.withdrawCallback(paramVO);
            }

        }

        return "success";
    }

    public String jzPayCallback(JZPayCallbackVO backVO, String userType) {
        String returnCode = backVO.getReturncode();

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("mer_id", backVO.getMer_id());
        paramMap.put("mer_no", backVO.getMer_no());
        paramMap.put("applyamount", backVO.getApplyamount());
        paramMap.put("amount", backVO.getAmount());
        paramMap.put("transaction_id", backVO.getTransaction_id());
        paramMap.put("timestamp", backVO.getTimestamp());
        paramMap.put("returncode", backVO.getReturncode());

        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.JZPay.getName(), backVO.getMer_id());
        String key = baseVO.getSecretKey();

        String mySign = SignUtil.getSignByMap(paramMap, key);

        log.info("JZPay代收加密后： {}", mySign);

        if (!backVO.getSign().equals(mySign)) {
            log.info("JZPay代收验签失败");
            return "OK";
        } else {
            log.info("JZPay代收验签成功");
        }

        if ("00".equals(returnCode)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(backVO.getAmount()));
                paramVO.setOrderNo(backVO.getMer_no());
                paramVO.setPayId(backVO.getTransaction_id());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(backVO.getAmount()));
                paramVO.setOrderNo(backVO.getMer_no());
                paramVO.setPayId(backVO.getTransaction_id());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }

        }

        return "OK";
    }

    public String jzPayoutCallback(JZPayoutCallbackVO backVO, String userType) {
        String returnCode = backVO.getReturncode();

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("mer_id", backVO.getMer_id());
        paramMap.put("mer_no", backVO.getMer_no());
        paramMap.put("amount", backVO.getAmount());
        paramMap.put("transaction_id", backVO.getTransaction_id());
        paramMap.put("timestamp", backVO.getTimestamp());
        paramMap.put("returncode", backVO.getReturncode());

        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.JZPay.getName(), backVO.getMer_id());
        String key = baseVO.getSecretKey();

        String mySign = SignUtil.getSignByMap(paramMap, key);

        log.info("JZPay代收加密后： {}", mySign);

        if (!backVO.getSign().equals(mySign)) {
            log.info("JZPay代收验签失败");
            return "OK";
        } else {
            log.info("JZPay代收验签成功");
        }

        if ("00".equals(returnCode)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(backVO.getAmount()));
                paramVO.setOrderNo(backVO.getMer_no());
                paramVO.setPayId(backVO.getTransaction_id());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setOrderNo(backVO.getMer_no());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                paramVO.setAmount(new BigDecimal(backVO.getAmount()));
                paramVO.setPayId(backVO.getTransaction_id());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }

        } else if ("01".equals(returnCode)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setOrderNo(backVO.getMer_no());
                paramVO.setPayId(backVO.getTransaction_id());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setOrderNo(backVO.getMer_no());
                paramVO.setPayId(backVO.getTransaction_id());
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }

        }

        return "OK";
    }

    public String xPayCallback(Map<String, Object> param, String userType) {
        ChannelQueryReqVO reqVO = new ChannelQueryReqVO();
        reqVO.setChannelName(PayChannelNameEnum.XPay.getName());
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelByCode(reqVO);

        String payStatus = param.get("payStatus").toString();
        if (!"SUCCESS".equals(payStatus)) {
            return "SUCCESS";
        }

        String outTradeNo = param.get("outTradeNo").toString();
        String orderNo = param.get("orderNo").toString();
        String amountTrue = param.get("amountTrue").toString();
        String sign = param.get("sign").toString();
        String mySign = SignUtil.paramSignsPay(param, baseVO.getSecretKey());
        log.info("XPay支付加密后： {}", mySign);
        if (!mySign.equals(sign)) {
            log.info("XPay支付验签失败");
            return "OK";
        } else {
            log.info("XPay支付验签成功");
        }

        if ("SUCCESS".equals(payStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amountTrue));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(orderNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amountTrue));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(orderNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        }

        return "SUCCESS";
    }

    public String xPayoutCallback(Map<String, Object> param, String userType) {
        ChannelQueryReqVO reqVO = new ChannelQueryReqVO();
        reqVO.setChannelName(PayChannelNameEnum.XPay.getName());
        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelByCode(reqVO);

        String orderStatus = param.get("orderStatus").toString();
        String outTradeNo = param.get("outOrderNo").toString();
        String orderNo = param.get("orderNo").toString();
        String amount = param.get("amount").toString();
        String sign = param.get("sign").toString();
        String mySign = SignUtil.paramSignsPay(param, baseVO.getSecretKey());
        log.info("XPay代付加密后： {}", sign);
        if (!mySign.equals(sign)) {
            log.info("XPay代付验签失败");
            return "SUCCESS";
        } else {
            log.info("XPay代付验签成功");
        }

        if ("1".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(orderNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(orderNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                paramVO.setAmount(new BigDecimal(amount));
                agentPayCallbackApi.withdrawCallback(paramVO);
            }

        } else if ("2".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(orderNo);
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(orderNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                paramVO.setAmount(new BigDecimal(amount));
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        }

        return "SUCCESS";
    }

    public String qjlPayCallback(Map<String, Object> param, String userType) {
        String merId = (String) param.get("app_id");
        if (!org.springframework.util.StringUtils.hasText(merId)) {
            log.info("QLPay代收通知参数异常");
            return "VERIFY_ERROR";
        }
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.QLPay.getName(), merId);
        if (baseVO == null) {
            log.info("QLPay代收配置参数异常");
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) param.remove("sign");
        log.info("QLPay代收通知加密前： {}", param);
        String mySign = SignUtil.getSignByMapObj(param, key).toLowerCase();
        log.info("QLPay代收通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            log.info("QLPay代收通知验签失败");
            return "VERIFY_ERROR";
        } else {
            log.info("QLPay代收通知验签成功");
        }
        String returnCode = (String) param.get("trade_status");
        if ("1".equals(returnCode)) {
            String amount = (String) param.get("amount");
            String tradeNo = (String) param.get("trade_no");
            String outTradeNo = (String) param.get("out_trade_no");
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        } else if ("2".equals(returnCode)) {
            String amount = (String) param.get("amount");
            String tradeNo = (String) param.get("trade_no");
            String outTradeNo = (String) param.get("out_trade_no");
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        }
        return "success";
    }

    public String qjlPayoutCallback(Map<String, Object> param, String userType) {
        String merId = (String) param.get("app_id");
        if (!org.springframework.util.StringUtils.hasText(merId)) {
            log.info("QLPay代付通知参数异常");
            return "VERIFY_ERROR";
        }
        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.QLPay.getName(), merId);
        if (baseVO == null) {
            log.info("QLPay代付配置参数异常");
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) param.remove("sign");
        log.info("QLPay代付通知加密前： {}", param);
        String mySign = SignUtil.getSignByMapObj(param, key).toLowerCase();
        log.info("QLPay代付通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            log.info("QLPay代付通知验签失败");
            return "VERIFY_ERROR";
        } else {
            log.info("QLPay代付通知验签成功");
        }
        String returnCode = (String) param.get("trade_status");
        if ("1".equals(returnCode)) {
            String amount = (String) param.get("amount");
            String tradeNo = (String) param.get("trade_no");
            String outTradeNo = (String) param.get("out_trade_no");
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                paramVO.setAmount(new BigDecimal(amount));
                agentPayCallbackApi.withdrawCallback(paramVO);
            }

        } else if ("2".equals(returnCode)) {
            String amount = (String) param.get("amount");
            String tradeNo = (String) param.get("trade_no");
            String outTradeNo = (String) param.get("out_trade_no");
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                paramVO.setAmount(new BigDecimal(amount));
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        }
        return "success";
    }

    public String doPayCallback(JSONObject reqJson, String userType) {
        String merId = (String) reqJson.get("mchNo");
        if (!org.springframework.util.StringUtils.hasText(merId)) {
            log.info("doPay代收通知参数异常");
            return "VERIFY_ERROR";
        }
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.DoPay.getName(), merId);
        // baseVO=new SystemRechargeChannelBaseVO();
        // baseVO.setSecretKey("946D510CF0B101EC2B95B02D6A103A18");//VND
        //baseVO.setSecretKey("595987743DAFF1FF2BF924C097D650A1"); //PHP
        if (baseVO == null) {
            log.info("DoPay代收配置参数异常");
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) reqJson.remove("sign");
        log.info("DoPay代收通知加密前： {}", reqJson);
        String mySign = SignUtil.getSignByMapObj(reqJson, key).toLowerCase();
        log.info("DoPay代收通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            log.info("DoPay代收通知验签失败");
            return "VERIFY_ERROR";
        } else {
            log.info("DoPay代收通知验签成功");
        }
        //订单状态:未支付=0;支付中=10;支付成功=20;支付失败=30。一般成功、失败时才会回调
        Integer orderStatus = (Integer) reqJson.get("status");
        String retMsg = (String) reqJson.get("msg");
        if (20 == orderStatus) {
            String amount = (String) reqJson.get("fee");
            String tradeNo = (String) reqJson.get("ordernum");
            String outTradeNo = (String) reqJson.get("mchOrdernum");
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        } else if (30 == orderStatus) {
            String amount = (String) reqJson.get("fee");
            String tradeNo = (String) reqJson.get("ordernum");
            String outTradeNo = (String) reqJson.get("mchOrdernum");
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                paramVO.setRemark(retMsg);
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setRemark(retMsg);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        }
        return "success";
    }

    public String doPayOutCallback(JSONObject reqJson, String userType) {
        String merId = (String) reqJson.get("mchNo");
        if (!org.springframework.util.StringUtils.hasText(merId)) {
            log.info("doPay代付通知参数异常");
            return "VERIFY_ERROR";
        }
        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.DoPay.getName(), merId);
        // baseVO=new SystemWithdrawChannelResponseVO();
        // baseVO.setSecretKey("946D510CF0B101EC2B95B02D6A103A18");//VND
        // baseVO.setSecretKey("595987743DAFF1FF2BF924C097D650A1");//PHP
        if (baseVO == null) {
            log.info("DoPay代付配置参数异常");
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) reqJson.remove("sign");
        log.info("DoPay代付通知加密前： {}", reqJson);
        String mySign = SignUtil.getSignByMapObj(reqJson, key).toLowerCase();
        log.info("DoPay代付通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            log.info("DoPay代付通知验签失败");
            return "VERIFY_ERROR";
        } else {
            log.info("DoPay代付通知验签成功");
        }
        //订单状态:未支付=0;支付中=10;支付成功=20;支付失败=30。一般成功、失败时才会回调
        Integer orderStatus = (Integer) reqJson.get("status");
        String retMsg = (String) reqJson.get("msg");
        if (20 == orderStatus) {
            String amount = (String) reqJson.get("fee");
            String tradeNo = (String) reqJson.get("ordernum");
            String outTradeNo = (String) reqJson.get("mchOrdernum");
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        } else if (30 == orderStatus) {
            String amount = (String) reqJson.get("fee");
            String tradeNo = (String) reqJson.get("ordernum");
            String outTradeNo = (String) reqJson.get("mchOrdernum");
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                paramVO.setRemark(retMsg);
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setRemark(retMsg);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        }
        return "success";
    }

    public String fPayCallback(JSONObject reqJson) {
        ChannelQueryReqVO reqVO = new ChannelQueryReqVO();
        reqVO.setChannelName(PayChannelNameEnum.FPay.getName());
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelInfoByMerNo(
                PayChannelNameEnum.FPay.getName(), (String) reqJson.remove("username"));
        //  baseVO=new SystemRechargeChannelBaseVO();
        //  baseVO.setSecretKey("H79ezn6a28i3mV8");//VND
        if (baseVO == null) {
            log.info("FPay支付通道配置参数异常");
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) reqJson.remove("token");
        log.info("FPay支付通道通知加密前： {}", reqJson);
        String orderId = reqJson.getString("order_id");
//        log.info("FPay秘钥:{},orderId:{}", key, orderId);
        String mySign = SignUtil.encryptionMD5(key.concat(orderId)).toLowerCase();
        log.info("FPay支付通道通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            log.info("FPay支付通道通知验签失败");
            return "VERIFY_ERROR";
        } else {
            log.info("FPay支付通道通知验签成功");
        }
        if (reqJson.getBoolean("status")) {
            String orderStatus = reqJson.getString("order_status");
            String amount = (String) reqJson.get("amount");
            //String tradeNo= (String) reqJson.get("ordernum");
            String outTradeNo = (String) reqJson.get("order_id");
            Integer thirdOrderStatus = 0;
            if ("completed".equals(orderStatus)) {
                thirdOrderStatus = ThirdPayOrderStatusEnum.Success.getCode();
            } else if ("fail".equals(orderStatus)) {
                thirdOrderStatus = ThirdPayOrderStatusEnum.Fail.getCode();
            } else {
                log.info("FPay支付通道通知状态不合法:{},无须处理", reqJson);
                return "NO!";
            }
            DepositWithdrawOrderQueryResponseVO depositWithdrawOrderQueryResponseVO = depositWithdrawOrderQueryApi.queryOrderByOrderNo(outTradeNo);
            Integer orderType = depositWithdrawOrderQueryResponseVO.getOrderType();
            String ownerUserType = depositWithdrawOrderQueryResponseVO.getOwnerUserType();
            log.info("FPay支付通道根据订单号:{},查询结果:{}", outTradeNo, depositWithdrawOrderQueryResponseVO);
            if (Objects.equals(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode(), orderType)) {
                if (OwnerUserTypeEnum.USER.getCode().equals(ownerUserType)) {
                    CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                    paramVO.setAmount(new BigDecimal(amount));
                    paramVO.setOrderNo(outTradeNo);
                    // paramVO.setPayId(String.valueOf(tradeNo));
                    paramVO.setStatus(thirdOrderStatus);
                    payCallbackApi.userDepositCallback(paramVO);
                } else {
                    AgentCallbackDepositParamVO agentCallbackDepositParamVO = new AgentCallbackDepositParamVO();
                    agentCallbackDepositParamVO.setAmount(new BigDecimal(amount));
                    agentCallbackDepositParamVO.setOrderNo(outTradeNo);
                    // paramVO.setPayId(String.valueOf(tradeNo));
                    agentCallbackDepositParamVO.setStatus(thirdOrderStatus);
                    agentPayCallbackApi.agentDepositCallback(agentCallbackDepositParamVO);
                }

            } else {
                if (OwnerUserTypeEnum.USER.getCode().equals(ownerUserType)) {
                    CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                    paramVO.setAmount(new BigDecimal(amount));
                    paramVO.setOrderNo(outTradeNo);
                    //   paramVO.setPayId(String.valueOf(tradeNo));
                    paramVO.setStatus(thirdOrderStatus);
                    payCallbackApi.withdrawCallback(paramVO);
                } else {
                    AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                    paramVO.setAmount(new BigDecimal(amount));
                    paramVO.setOrderNo(outTradeNo);
                    //paramVO.setPayId(String.valueOf(tradeNo));
                    paramVO.setStatus(thirdOrderStatus);
                    agentPayCallbackApi.withdrawCallback(paramVO);
                }
            }

        }
        return "success";
    }

    /**
     * 代收
     *
     * @param reqJson
     * @param userType
     * @return
     */
    public String topPayCallback(com.alibaba.fastjson.JSONObject reqJson, String userType) {
        String merId = reqJson.getString("merchantCode");
        String signVal = (String) reqJson.remove("signature");
        String originReqStr = TopPaySignUtils.createStrParam(reqJson);
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.TopPay.getName(), merId);
        //  baseVO=new SystemWithdrawChannelResponseVO();
        //  baseVO.setPubKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgntKXkBIwCUO9+fywKj94p4yB8FXviOXhH0cEmhpWCRAbtxJvibd8BLaUH2FpNFVSQQh/WrNv4+46O0m1f7c/UDnTfAMLYOsUhkxe16f1XHK7jEfFCIHGXIbeUmcAShBy4F1W4SsFjZX4NGTpep1sRD8SnRp5rwyZd2ACw91xdJStF70uWQEs9rSZsvFVZAwXVafzLt4wSzZ4d/KrD4JALc+xrIor3hBEDN4jnr0HLtOSxNaonNy1J7cvZgQ9tLM9Z/3RR7DGnzeTSMLJxDoPOcQ3zu8Cye9ofLyxMIbJ2gTZZUj8Ghu0NoTj+7NpvQyANo0nyzP8+ylbGbiGGRJzQIDAQAB");//VND
        if (!TopPaySignUtils.verifySign(originReqStr, signVal, baseVO.getPubKey())) {
            log.info("TopPay支付通道通知验签失败");
            return "VERIFY_ERROR";
        } else {
            log.info("TopPay支付通道通知验签成功");
        }
        /**
         * {"merchantCode":"M1001621","tradeNo":"PKR202503050841365613196652432","errorDesc":"Account error",
         * "signature":"YlgAR6+UiWXjyrUp1L0bwRSLKYEjq1/tEINwQnz2RDgDwTrSVHTR7aI3Ke0Mkitu827keHfjzHtDkRPpzCVz88QWFgCPjX793Z0fIwqX9BoCJU7jpww1vNELvlqoKPX/hh/teEk9gMExEZbAX0OPWgygZ/j7E+hkZIPf5uk2hRj5m1vdgAi385SE3Oewxnqpd/XNtSN9YkmbGTDTh3I7i2WVQ5ItyAjItFHP60Zh1GrEESypT303/LSvkt2TYgNTN1mQgrPMx/iTF6WrvuW1VrciRZ4QvUKnGrdxMrzkAsfRCZba4oGu21UQe6jP8IV33kLpNMmAGJVItYNiELPrUA==",
         * "payProductCode":"PAK639390","paymentType":"PAYMENT","feeAmount":"2.625",
         * "payCategory":"BANK","payAmount":"105.000","payType":"PAYMENT","outTradeNo":"W9133336377","status":"FAILED"}
         */

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

        String orderStatus = (String) reqJson.get("status");
        String retMsg = (String) reqJson.get("msg");
        String amount = (String) reqJson.get("payAmount");
        BigDecimal payAmount = new BigDecimal(amount);
        // String feeAmount= (String) reqJson.get("feeAmount");
        //  BigDecimal actAmount=new BigDecimal(amount).subtract(new BigDecimal(feeAmount));
        String tradeNo = (String) reqJson.get("tradeNo");
        String outTradeNo = (String) reqJson.get("outTradeNo");
        if ("PAYMENT_DONE".equals(orderStatus) || "TRANSFER_SUCCESS".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(payAmount);
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(payAmount);
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        } else if ("FAILED".equals(orderStatus) || "REJECT".equals(orderStatus) || "TRANSFER_FAILED".equals(orderStatus) || "PAYMENT_CLOSE".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(payAmount);
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                paramVO.setRemark(retMsg);
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(payAmount);
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setRemark(retMsg);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        }
        return "SUCCESS";
    }

    /**
     * 代付
     *
     * @param reqJson
     * @param userType
     * @return
     */
    public String topPayoutCallback(com.alibaba.fastjson.JSONObject reqJson, String userType) {
        String merId = reqJson.getString("merchantCode");
        String signVal = (String) reqJson.remove("signature");
        String originReqStr = TopPaySignUtils.createStrParam(reqJson);
        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.TopPay.getName(), merId);
        //  baseVO=new SystemWithdrawChannelResponseVO();
        // baseVO.setPubKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgntKXkBIwCUO9+fywKj94p4yB8FXviOXhH0cEmhpWCRAbtxJvibd8BLaUH2FpNFVSQQh/WrNv4+46O0m1f7c/UDnTfAMLYOsUhkxe16f1XHK7jEfFCIHGXIbeUmcAShBy4F1W4SsFjZX4NGTpep1sRD8SnRp5rwyZd2ACw91xdJStF70uWQEs9rSZsvFVZAwXVafzLt4wSzZ4d/KrD4JALc+xrIor3hBEDN4jnr0HLtOSxNaonNy1J7cvZgQ9tLM9Z/3RR7DGnzeTSMLJxDoPOcQ3zu8Cye9ofLyxMIbJ2gTZZUj8Ghu0NoTj+7NpvQyANo0nyzP8+ylbGbiGGRJzQIDAQAB");//VND

        if (baseVO == null) {
            log.info("TopPay代付配置参数异常");
            return "INNER_ERROR";
        }
        if (!TopPaySignUtils.verifySign(originReqStr, signVal, baseVO.getPubKey())) {
            log.info("TopPay代付通知验签失败");
            return "VERIFY_ERROR";
        } else {
            log.info("TopPay代付通知验签成功");
        }
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
        String orderStatus = (String) reqJson.get("status");
        String retMsg = (String) reqJson.get("msg");
        String amount = (String) reqJson.get("payAmount");
        BigDecimal payAmount = new BigDecimal(amount);
        //String feeAmount= (String) reqJson.get("feeAmount");
        //BigDecimal actAmount=new BigDecimal(amount).add(new BigDecimal(feeAmount));
        String tradeNo = (String) reqJson.get("tradeNo");
        String outTradeNo = (String) reqJson.get("outTradeNo");
        if ("PAYMENT_DONE".equals(orderStatus) || "TRANSFER_SUCCESS".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(payAmount);
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(payAmount);
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        } else if ("FAILED".equals(orderStatus) || "REJECT".equals(orderStatus) || "TRANSFER_FAILED".equals(orderStatus) || "PAYMENT_CLOSE".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(payAmount);
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                paramVO.setRemark(retMsg);
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(payAmount);
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setRemark(retMsg);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        }
        return "SUCCESS";
    }

    public String ezPayCallback(Map<String, String> param, String userType) {
        String signVal = param.remove("sign");
       /* Map<String, String> paramMap = new HashMap<>();
        paramMap.put("mchOrderId", backVO.getMchOrderId());
        paramMap.put("orderId", backVO.getOrderId());
        paramMap.put("amount", backVO.getAmount());
        paramMap.put("time", backVO.getTime());
        paramMap.put("status", backVO.getStatus());
        paramMap.put("remark", backVO.getRemark());*/

        ChannelQueryReqVO reqVO = new ChannelQueryReqVO();
        reqVO.setChannelName(PayChannelNameEnum.EZPay.getName());
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelByCode(reqVO);
        String key = baseVO.getSecretKey();
        String mySign = SignUtil.getSignByMap(param, key);

        log.info("EZPay代收加密后： {}", mySign);

        if (!signVal.toUpperCase().equals(mySign)) {
            log.info("EZPay代收验签失败");
            return "FAIL";
        } else {
            log.info("EZPay代收验签成功:{}", param);
        }
        String orderStatus = param.get("status");

        if ("2".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(param.get("amount")));
                paramVO.setOrderNo(param.get("mchOrderId"));
                paramVO.setPayId(param.get("orderId"));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(param.get("amount")));
                paramVO.setOrderNo(param.get("mchOrderId"));
                paramVO.setPayId(param.get("orderId"));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        }
        return "SUCCESS";
    }

    public String ezPayoutCallback(Map<String, String> param, String userType) {

        String signVal = param.remove("sign");

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("orderId", param.get("orderId"));
        paramMap.put("amount", param.get("amount"));
        paramMap.put("status", param.get("status"));
        paramMap.put("time", param.get("time"));

        ChannelQueryReqVO reqVO = new ChannelQueryReqVO();
        reqVO.setChannelName(PayChannelNameEnum.EZPay.getName());
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelByCode(reqVO);
        String key = baseVO.getSecretKey();
        String mySign = SignUtil.getSignByMap(paramMap, key);

        log.info("EZPay代付加密后： {}", mySign);

        if (!signVal.toUpperCase().equals(mySign)) {
            log.info("EZPay代付验签失败");
            return "FAIL";
        } else {
            log.info("EZPay代付验签成功:{}", param);
        }
        String orderStatus = param.get("status");

        if ("success".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(param.get("amount")));
                paramVO.setOrderNo(param.get("mchOrderId"));
                paramVO.setPayId(param.get("orderId"));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                paramVO.setAmount(new BigDecimal(param.get("amount")));
                paramVO.setOrderNo(param.get("mchOrderId"));
                paramVO.setPayId(param.get("orderId"));
                agentPayCallbackApi.withdrawCallback(paramVO);
            }

        } else if ("refuse".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setOrderNo(param.get("mchOrderId"));
                paramVO.setPayId(param.get("orderId"));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setOrderNo(param.get("mchOrderId"));
                paramVO.setPayId(param.get("orderId"));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        }

        return "SUCCESS";
    }

    public String hyPayCallback(JSONObject reqJson, String userType) {
        String merId = (String) reqJson.get("merchantNo");
        if (!org.springframework.util.StringUtils.hasText(merId)) {
            log.info("hyPay代收通知参数异常");
            return "VERIFY_ERROR";
        }
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.HyPay.getName(), merId);
        if (baseVO == null) {
            log.info("HyPay代收配置参数异常");
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) reqJson.remove("sign");
        log.info("HyPay代收通知加密前： {}", reqJson);
        String mySign = SignUtil.getSignByMapObj(reqJson, key).toLowerCase();
        log.info("HyPay代收通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            log.info("HyPay代收通知验签失败");
            return "VERIFY_ERROR";
        } else {
            log.info("HyPay代收通知验签成功");
        }
        //订单状态:未支付=0;支付中=10;支付成功=20;支付失败=30。一般成功、失败时才会回调
        String orderStatus = (String) reqJson.get("status");
        String amount = (String) reqJson.get("orderAmt");
        String tradeNo = (String) reqJson.get("orderNo");
        String outTradeNo = (String) reqJson.get("merOrderNo");
        if ("100".equals(orderStatus)) {
            String payAmt = (String) reqJson.get("payAmt");
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(payAmt));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(payAmt));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        } else if ("-100".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                // paramVO.setRemark(retMsg);
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                // paramVO.setRemark(retMsg);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        }
        JSONObject retJson = new JSONObject();
        retJson.put("code", 1);
        retJson.put("message", "成功");
        return retJson.toString();
    }

    public String hyPayOutCallback(JSONObject reqJson, String userType) {
        String merId = (String) reqJson.get("merchantNo");
        if (!org.springframework.util.StringUtils.hasText(merId)) {
            log.info("hyPay代付通知参数异常");
            return "VERIFY_ERROR";
        }
        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.HyPay.getName(), merId);
        if (baseVO == null) {
            log.info("HyPay代付配置参数异常");
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) reqJson.remove("sign");
        log.info("HyPay代付通知加密前： {}", reqJson);
        String mySign = SignUtil.getSignByMapObj(reqJson, key).toLowerCase();
        log.info("HyPay代付通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            log.info("HyPay代付通知验签失败");
            return "VERIFY_ERROR";
        } else {
            log.info("HyPay代付通知验签成功");
        }
        //订单状态:未支付=0;支付中=10;支付成功=20;支付失败=30。一般成功、失败时才会回调
        Integer orderStatus = reqJson.getInteger("status");
        String amount = (String) reqJson.get("orderAmt");
        String tradeNo = (String) reqJson.get("orderNo");
        String outTradeNo = (String) reqJson.get("merOrderNo");
        if (100 == orderStatus) {
            String payAmt = (String) reqJson.get("payAmt");
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(payAmt));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(payAmt));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        } else if (-100 == orderStatus) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                //paramVO.setRemark(retMsg);
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                // paramVO.setRemark(retMsg);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        }
        JSONObject retJson = new JSONObject();
        retJson.put("code", 1);
        retJson.put("message", "成功");
        return retJson.toString();
    }

    public String lemonPayCallback(Map<String, Object> param, String userType) {
        String merId = (String) param.get("mchId");
        if (!org.springframework.util.StringUtils.hasText(merId)) {
            log.info("lemonPay代收通知参数异常");
            return "VERIFY_ERROR";
        }
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.LemonPay.getName(), merId);
        if (baseVO == null) {
            log.info("lemonPay代收配置参数异常");
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) param.remove("sign");
        log.info("lemonPay代收通知加密前： {}", param);
        String mySign = SignUtil.getSignByMapObj(param, key);
        log.info("lemonPay代收通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            log.info("lemonPay代收通知验签失败");
            return "VERIFY_ERROR";
        } else {
            log.info("lemonPay代收通知验签成功");
        }
        //订单状态:未支付=0;支付中=10;支付成功=20;支付失败=30。一般成功、失败时才会回调
        String orderStatus = (String) param.get("status");
        String payAmt = (String) param.get("amount");
        String tradeNo = (String) param.get("payOrderId");
        String outTradeNo = (String) param.get("mchOrderNo");
        if ("2".equals(orderStatus) || "3".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(payAmt).divide(BigDecimalConstants.HUNDRED, 2, RoundingMode.FLOOR));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(payAmt).divide(BigDecimalConstants.HUNDRED, 2, RoundingMode.FLOOR));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        } else if ("-1".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(payAmt).divide(BigDecimalConstants.HUNDRED, 2, RoundingMode.FLOOR));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                // paramVO.setRemark(retMsg);
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(payAmt).divide(BigDecimalConstants.HUNDRED, 2, RoundingMode.FLOOR));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                // paramVO.setRemark(retMsg);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        }
//        JSONObject retJson=new JSONObject();
//        retJson.put("code",1);
//        retJson.put("message","成功");
        return "success";
    }

    public String lemonPayOutCallback(Map<String, Object> param, String userType) {
//        String merId=(String) param.get("mchId");
//        if(!org.springframework.util.StringUtils.hasText(merId)){
//            log.info("LemonPay代付通知参数异常");
//            return "VERIFY_ERROR";
//        }
        // 因为此通道特殊，代付不传商户号，所以这里写死拿秘钥
        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.LemonPay.getName(), "1071");
        if (baseVO == null) {
            log.info("LemonPay代付配置参数异常");
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) param.remove("sign");
        log.info("LemonPay代付通知加密前： {}", param);
        String mySign = SignUtil.getSignByMapObj(param, key);
        log.info("LemonPay代付通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            log.info("LemonPay代付通知验签失败");
            return "VERIFY_ERROR";
        } else {
            log.info("LemonPay代付通知验签成功");
        }
        //状态:0-待处理,1-处理中,2-成功,3-失败,6-冲正(成功后退款变成失败了)
        String orderStatus = (String) param.get("status");
        String amount = (String) param.get("amount");
        // 代付订单号
        String tradeNo = (String) param.get("agentpayOrderId");
        // 商户订单号
        String outTradeNo = (String) param.get("mchOrderNo");
        if ("2".equals(orderStatus) || "6".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount).divide(BigDecimalConstants.HUNDRED, 2, RoundingMode.FLOOR));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount).divide(BigDecimalConstants.HUNDRED, 2, RoundingMode.FLOOR));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        } else if ("3".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount).divide(BigDecimalConstants.HUNDRED, 2, RoundingMode.FLOOR));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                //paramVO.setRemark(retMsg);
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount).divide(BigDecimalConstants.HUNDRED, 2, RoundingMode.FLOOR));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                // paramVO.setRemark(retMsg);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        }
//        JSONObject retJson=new JSONObject();
//        retJson.put("code",1);
//        retJson.put("message","成功");
        return "success";
    }

    public String tsPayCallback(JSONObject reqJson, String userType) {

        //1. check args
        BigDecimal amount = reqJson.getBigDecimal("amount");
        //Integer status =  reqJson.getInteger("status");
        String mchOrderNo = reqJson.getString("mchOrderNo");
        String platOrderNo = reqJson.getString("platOrderNo");
        String merId = reqJson.getString("memberId");
        if (amount == null || mchOrderNo == null || platOrderNo == null || merId == null) {
            log.error("TSPay代收回调参数异常");
            return "FAIL";
        }
        //2. check this mchId is ours
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.TSPay.getName(), merId);
        if (baseVO == null) {
            log.error("TSPay充值回调参数异常, 无对应支付通道数据");
            return "INNER_ERROR";
        }
        //3. signature check
        try {
            String signStr = reqJson.getString("sign");
            if (!TSSignUtils.verifySign(reqJson, baseVO.getPubKey())) {
                log.error("TSPay充值回调验证签名false, 参数: {}, 原签名 {}", reqJson, signStr);
                return "INNER_ERROR";
            }
        } catch (Exception e) {
            log.error("TSPay充值回调验证签名失败, Exception:", e);
            return "INNER_ERROR";
        }

        //4. check the recharge order record
        boolean process = false;
        boolean orderStatus = false;
        switch (TSOrderStatusEnum.valueOf(reqJson.getString("orderStatus"))) {
            case SUCCESS, PART_SUC:
                process = true;
                orderStatus = true;
                break;
            case PENDING:
                break;
            case FAILED, REFUND:
                process = true;
                break;
        }
        if (process) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(amount);
                paramVO.setOrderNo(mchOrderNo);
                paramVO.setPayId(platOrderNo);
                paramVO.setStatus(orderStatus ? ThirdPayOrderStatusEnum.Success.getCode() : ThirdPayOrderStatusEnum.Fail.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(amount);
                paramVO.setOrderNo(mchOrderNo);
                paramVO.setPayId(platOrderNo);
                paramVO.setStatus(orderStatus ? ThirdPayOrderStatusEnum.Success.getCode() : ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        }
        return "SUCCESS";
    }

    public String tsPayOutCallback(JSONObject reqJson, String userType) {

        //1. check args
        Integer status = reqJson.getInteger("status");
        BigDecimal amount = reqJson.getBigDecimal("amount");
        String mchOrderNo = reqJson.getString("mchOrderNo");
        String platOrderNo = reqJson.getString("platOrderNo");
        String merId = reqJson.getString("memberId");
        // || status == null || !Objects.equals(TSPayResponseCodeEnum.SUCCESS.getCode(), status)
        if (amount == null || mchOrderNo == null || platOrderNo == null || merId == null) {
            log.info("TSPay代付回调参数异常");
            return "FAIL";
        }
        //2. check this mchId is ours
        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.TSPay.getName(), merId);
        if (baseVO == null) {
            log.info("TSPay代付回调参数异常, 无对应支付通道数据");
            return "INNER_ERROR";
        }
        //3. signature check
        try {
            String signStr = reqJson.getString("sign");
            if (!TSSignUtils.verifySign(reqJson, baseVO.getPubKey())) {
                log.error("TSPay提现回调验证签名false, 参数: {}, 原签名 {}", reqJson, signStr);
                return "INNER_ERROR";
            }
        } catch (Exception e) {
            log.error("TSPay提现回调验证签名失败, Exception:", e);
            return "INNER_ERROR";
        }

        boolean process = false;
        boolean orderStatus = false;
        //4. check the recharge order record
        switch (TSOrderStatusEnum.valueOf(reqJson.getString("orderStatus"))) {
            case SUCCESS, PART_SUC:
                process = true;
                orderStatus = true;
                break;
            case PENDING:
                break;
            case FAILED, REFUND:
                process = true;
                break;
        }
        if (process) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(amount);
                paramVO.setOrderNo(mchOrderNo);
                paramVO.setPayId(platOrderNo);
                paramVO.setStatus(orderStatus ? ThirdPayOrderStatusEnum.Success.getCode() : ThirdPayOrderStatusEnum.Fail.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(amount);
                paramVO.setOrderNo(mchOrderNo);
                paramVO.setPayId(platOrderNo);
                paramVO.setStatus(orderStatus ? ThirdPayOrderStatusEnum.Success.getCode() : ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        }
        return "SUCCESS";
    }

    public String ebPayCallback(JSONObject reqJson, String userType) {
        String merId = (String) reqJson.get("merchantNo");
        JSONObject retJson = new JSONObject();
        retJson.put("code", "99");
        if (!org.springframework.util.StringUtils.hasText(merId)) {
            retJson.put("msg", "通知参数异常");
            return retJson.toString();
        }
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.EbPay.getName(), merId);
        if (baseVO == null) {
            retJson.put("msg", "内部异常");
            return retJson.toString();
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) reqJson.remove("sign");
        log.info("EbPayPay代收通知加密前： {}", reqJson);
        // 签名原串：orderNo=ICBC2220516124055745b46700&merchantOrderId=819273559624282180&merchantNo=666666&payTypeId=912 &orderStatus=1&orderAmount=2.12&paidAmount=0.2&key=2rk9b1trxwfzohku
        String queryBuffer = "orderNo" + "=" + reqJson.get("orderNo") + "&" +
                "merchantOrderId" + "=" + reqJson.get("merchantOrderId") + "&" +
                "merchantNo" + "=" + reqJson.get("merchantNo") + "&" +
                "payTypeId" + "=" + reqJson.get("payTypeId") + "&" +
                "orderStatus" + "=" + reqJson.get("orderStatus") + "&" +
                "orderAmount" + "=" + reqJson.get("orderAmount") + "&" +
                "paidAmount" + "=" + reqJson.get("paidAmount") + "&" +
                "key" + "=" + key;

        String mySign = SignUtil.signOriginStr(queryBuffer);

        log.info("EbPay代收通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            retJson.put("msg", "验证签名失败");
            return retJson.toString();
        } else {
            log.info("EbPay代收通知验签成功");
        }
        //订单状态:0 待支付；1. 成功；2 失败
        String orderStatus = (String) reqJson.get("orderStatus");
        String amount = (String) reqJson.get("orderAmount");
        String tradeNo = (String) reqJson.get("orderNo");
        String outTradeNo = (String) reqJson.get("merchantOrderId");
        if ("1".equals(orderStatus)) {
            String payAmt = (String) reqJson.get("paidAmount");
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(payAmt));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(payAmt));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        } else if ("2".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                // paramVO.setRemark(retMsg);
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                // paramVO.setRemark(retMsg);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        }
        retJson.put("code", 200);
        retJson.put("message", "成功");
        return retJson.toString();
    }

    public String ebPayOutCallback(JSONObject reqJson, String userType) {
        String merId = (String) reqJson.get("merchantNo");
        JSONObject retJson = new JSONObject();
        retJson.put("code", "99");
        if (!org.springframework.util.StringUtils.hasText(merId)) {
            retJson.put("msg", "通知参数异常");
            return retJson.toString();
        }
        // 因为此通道特殊，代付不传商户号，所以这里写死拿秘钥
        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.EbPay.getName(), merId);
        if (baseVO == null) {
            retJson.put("msg", "INNER_ERROR");
            return retJson.toString();
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) reqJson.remove("sign");
        log.info("EbPay代付通知加密前： {}", reqJson);

        String queryBuffer = "orderNo" + "=" + reqJson.get("orderNo") + "&" +
                "merchantOrderId" + "=" + reqJson.get("merchantOrderId") + "&" +
                "merchantNo" + "=" + reqJson.get("merchantNo") + "&" +
                "payTypeId" + "=" + reqJson.get("payTypeId") + "&" +
                "orderStatus" + "=" + reqJson.get("orderStatus") + "&" +
                "orderAmount" + "=" + reqJson.get("orderAmount") + "&" +
                "paidAmount" + "=" + reqJson.get("paidAmount") + "&" +
                "key" + "=" + key;

        String mySign = SignUtil.signOriginStr(queryBuffer);

        log.info("EbPay代付通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            log.info("EbPay代付通知验签失败");
            retJson.put("msg", "VERIFY_ERROR");
            return retJson.toString();
        } else {
            log.info("EbPay代付通知验签成功");
        }
        //订单状态 0 待支付；1 成功；2 失败
        String orderStatus = (String) reqJson.get("orderStatus");
        String amount = (String) reqJson.get("orderAmount");
        // 代付订单号
        String tradeNo = (String) reqJson.get("orderNo");
        // 商户订单号
        String outTradeNo = (String) reqJson.get("merchantOrderId");
        if ("1".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        } else if ("2".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                //paramVO.setRemark(retMsg);
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                // paramVO.setRemark(retMsg);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        }
        retJson.put("code", 200);
        retJson.put("message", "成功");
        return retJson.toString();
    }

    public String fixPayCallback(JSONObject reqJson, String userType) {
        String merId = (String) reqJson.get("merchantNo");
        if (!org.springframework.util.StringUtils.hasText(merId)) {
            return "PARAM_ERROR";
        }
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.FIXPay.getName(), merId);
        if (baseVO == null) {
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) reqJson.remove("sign");
        log.info("FIXPayPay代收通知加密前： {}", reqJson);

        String mySign = SignUtil.genFixSignByMapObj(reqJson, key);

        log.info("FIXPay代收通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            return "VERIFY_ERROR";
        } else {
            log.info("FIXPay代收通知验签成功");
        }
        //订单状态:0 待支付；1. 成功；2 失败
        String orderStatus = (String) reqJson.get("orderStatus");
        String amount = (String) reqJson.get("amount");
        String tradeNo = (String) reqJson.get("platOrderNo");
        String outTradeNo = (String) reqJson.get("merchantOrderNo");
        if ("SUCCESS".equals(orderStatus)) {
            String payAmt = (String) reqJson.get("factAmount");
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(payAmt));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(payAmt));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        } /*else if ("2".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                // paramVO.setRemark(retMsg);
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                // paramVO.setRemark(retMsg);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        }*/
        return "SUCCESS";
    }

    public String fixPayOutCallback(JSONObject reqJson, String userType) {
        String merId = (String) reqJson.get("merchantNo");
        if (!org.springframework.util.StringUtils.hasText(merId)) {
            return "ERROR";
        }
        // 因为此通道特殊，代付不传商户号，所以这里写死拿秘钥
        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.FIXPay.getName(), merId);
        if (baseVO == null) {
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) reqJson.remove("sign");
        log.info("FIXPay代付通知加密前： {}", reqJson);


        String mySign = SignUtil.genFixSignByMapObj(reqJson, key);

        log.info("FIXPay代付通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            log.info("FIXPay代付通知验签失败");
            return "VERIFY_ERROR";
        } else {
            log.info("FIXPay代付通知验签成功");
        }
        //订单状态 PENDING	订单处理中
        //SUCCESS	成功
        //FAILED	失败
        String orderStatus = (String) reqJson.get("orderStatus");
        String amount = (String) reqJson.get("amount");
        // 代付订单号
        String tradeNo = (String) reqJson.get("platOrderNo");
        // 商户订单号
        String outTradeNo = (String) reqJson.get("merchantOrderNo");
        String retMsg = reqJson.getString("orderMessage");
        if ("SUCCESS".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        } else if ("FAILED".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                paramVO.setRemark(retMsg);
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setRemark(retMsg);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        }
        return "SUCCESS";
    }
   // hfPay -start
   public String hfPayCallback(JSONObject reqJson, String userType) {
       String merId = String.valueOf(reqJson.get("merchantId"));
       if (!org.springframework.util.StringUtils.hasText(merId)) {
           return "PARAM_ERROR";
       }
       SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.HFPay.getName(), merId);
       if (baseVO == null) {
           return "INNER_ERROR";
       }
       String key = baseVO.getSecretKey();
       String reqSignVal = (String) reqJson.remove("sign");
       log.info("HFPay代收通知加密前： {}", reqJson);

       String mySign = SignUtil.getSignByMapObj(reqJson, key);

       log.info("HFPay代收通知加密后： {}", mySign);
       if (!reqSignVal.equalsIgnoreCase(mySign)) {
           return "VERIFY_ERROR";
       } else {
           log.info("HFPay代收通知验签成功");
       }
       //订单状态: 支付状态, 999-订单失败, 200-订单成功
       String orderStatus = String.valueOf(reqJson.get("status"));
       String amount = String.valueOf(reqJson.get("amount"));
       // 代付订单号(三方订单号)
       String tradeNo = String.valueOf(reqJson.get("orderId"));
       // 商户订单号
       String outTradeNo = String.valueOf(reqJson.get("merchantOrderId"));
       if ("200".equals(orderStatus)) {
           String payAmt = (String) reqJson.get("amount");
           if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
               CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
               paramVO.setAmount(new BigDecimal(payAmt));
               paramVO.setOrderNo(outTradeNo);
               paramVO.setPayId(String.valueOf(tradeNo));
               paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
               payCallbackApi.userDepositCallback(paramVO);
           } else {
               AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
               paramVO.setAmount(new BigDecimal(payAmt));
               paramVO.setOrderNo(outTradeNo);
               paramVO.setPayId(String.valueOf(tradeNo));
               paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
               agentPayCallbackApi.agentDepositCallback(paramVO);
           }
       } else if ("999".equals(orderStatus)) {
           if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
               CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
               paramVO.setAmount(new BigDecimal(amount));
               paramVO.setOrderNo(outTradeNo);
               paramVO.setPayId(tradeNo);
               paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
               // paramVO.setRemark(retMsg);
               payCallbackApi.userDepositCallback(paramVO);
           } else {
               AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
               paramVO.setAmount(new BigDecimal(amount));
               paramVO.setOrderNo(outTradeNo);
               paramVO.setPayId(tradeNo);
               // paramVO.setRemark(retMsg);
               paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
               agentPayCallbackApi.agentDepositCallback(paramVO);
           }
       }
       return "success";
   }

    public String hfPayOutCallback(JSONObject reqJson, String userType) {
        String merId = String.valueOf(reqJson.get("merchantId"));
        if (!org.springframework.util.StringUtils.hasText(merId)) {
            return "PARAM_ERROR";
        }
        // 因为此通道特殊，代付不传商户号，所以这里写死拿秘钥
        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.HFPay.getName(), merId);
        if (baseVO == null) {
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) reqJson.remove("sign");
        log.info("HFPay代付通知加密前： {}", reqJson);


        String mySign = SignUtil.getSignByMapObj(reqJson, key);

        log.info("HFPay代付通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            log.info("HFPay代付通知验签失败");
            return "VERIFY_ERROR";
        } else {
            log.info("HFPay代付通知验签成功");
        }
        //订单状态 999-订单失败, 200-订单成功
        String orderStatus = String.valueOf(reqJson.get("status"));
        String amount = String.valueOf(reqJson.get("amount"));
        // 代付订单号
        String tradeNo = String.valueOf(reqJson.get("orderId"));
        // 商户订单号
        String outTradeNo = String.valueOf(reqJson.get("merchantOrderId"));
        //String retMsg=reqJson.getString("orderMessage");
        if ("200".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        } else if ("999".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                // paramVO.setRemark(retMsg);
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                // paramVO.setRemark(retMsg);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        }
        return "success";
    }

    // hfPay -end
    /*public String hfPayCallback(JSONObject reqJson, String userType) {
        String merId = (String) reqJson.get("merchantId");
        if (!org.springframework.util.StringUtils.hasText(merId)) {
            return "PARAM_ERROR";
        }
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.HFPay.getName(), merId);
        if (baseVO == null) {
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) reqJson.remove("sign");
        log.info("HFPay代收通知加密前： {}", reqJson);

        String mySign = SignUtil.getSignByMapObj(reqJson, key);

        log.info("HFPay代收通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            return "VERIFY_ERROR";
        } else {
            log.info("HFPay代收通知验签成功");
        }
        //订单状态: 支付状态, 999-订单失败, 200-订单成功
        String orderStatus = (String) reqJson.get("status");
        String amount = (String) reqJson.get("amount");
        String tradeNo = (String) reqJson.get("orderId");
        String outTradeNo = (String) reqJson.get("merchantOrderId");
        if ("200".equals(orderStatus)) {
            String payAmt = (String) reqJson.get("amount");
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(payAmt));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(payAmt));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        } else if ("999".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                // paramVO.setRemark(retMsg);
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                // paramVO.setRemark(retMsg);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        }
        return "SUCCESS";
    }

    public String hfPayOutCallback(JSONObject reqJson, String userType) {
        String merId = (String) reqJson.get("merchantId");
        if (!org.springframework.util.StringUtils.hasText(merId)) {
            return "ERROR";
        }
        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.HFPay.getName(), merId);
        if (baseVO == null) {
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) reqJson.remove("sign");
        log.info("HFPay代付通知加密前： {}", reqJson);


        String mySign = SignUtil.getSignByMapObj(reqJson, key);

        log.info("HFPay代付通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            log.info("HFPay代付通知验签失败");
            return "VERIFY_ERROR";
        } else {
            log.info("HFPay代付通知验签成功");
        }
        //订单状态 999-订单失败, 200-订单成功
        String orderStatus = (String) reqJson.get("status");
        String amount = (String) reqJson.get("amount");
        // 代付订单号
        String tradeNo = (String) reqJson.get("orderId");
        // 商户订单号
        String outTradeNo = (String) reqJson.get("merchantOrderId");
        //String retMsg=reqJson.getString("orderMessage");
        if ("200".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        } else if ("999".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                // paramVO.setRemark(retMsg);
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                // paramVO.setRemark(retMsg);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        }
        return "SUCCESS";
    }*/

    public String sqPayCallback(JSONObject reqJson, String userType) {
        String merId = String.valueOf(reqJson.get("merchantId"));
        if (!org.springframework.util.StringUtils.hasText(merId)) {
            return "PARAM_ERROR";
        }
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.SQPay.getName(), merId);
        if (baseVO == null) {
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) reqJson.remove("sign");
        log.info("SQPay代收通知加密前： {}", reqJson);

        String mySign = SignUtil.getSignByMapObj(reqJson, key);

        log.info("SQPay代收通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            return "VERIFY_ERROR";
        } else {
            log.info("SQPay代收通知验签成功");
        }
        //订单状态: 支付状态, 999-订单失败, 200-订单成功
        String orderStatus = String.valueOf(reqJson.get("status"));
        String amount = String.valueOf(reqJson.get("amount"));
        // 代付订单号(三方订单号)
        String tradeNo = String.valueOf(reqJson.get("orderId"));
        // 商户订单号
        String outTradeNo = String.valueOf(reqJson.get("merchantOrderId"));
        if ("200".equals(orderStatus)) {
            String payAmt = (String) reqJson.get("amount");
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(payAmt));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(payAmt));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        } else if ("999".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                // paramVO.setRemark(retMsg);
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                // paramVO.setRemark(retMsg);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        }
        return "success";
    }

    public String sqPayOutCallback(JSONObject reqJson, String userType) {
        String merId = String.valueOf(reqJson.get("merchantId"));
        if (!org.springframework.util.StringUtils.hasText(merId)) {
            return "PARAM_ERROR";
        }
        // 因为此通道特殊，代付不传商户号，所以这里写死拿秘钥
        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.SQPay.getName(), merId);
        if (baseVO == null) {
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) reqJson.remove("sign");
        log.info("SQPay代付通知加密前： {}", reqJson);


        String mySign = SignUtil.getSignByMapObj(reqJson, key);

        log.info("SQPay代付通知加密后： {}", mySign);
        if (!reqSignVal.equalsIgnoreCase(mySign)) {
            log.info("SQPay代付通知验签失败");
            return "VERIFY_ERROR";
        } else {
            log.info("SQPay代付通知验签成功");
        }
        //订单状态 999-订单失败, 200-订单成功
        String orderStatus = String.valueOf(reqJson.get("status"));
        String amount = String.valueOf(reqJson.get("amount"));
        // 代付订单号
        String tradeNo = String.valueOf(reqJson.get("orderId"));
        // 商户订单号
        String outTradeNo = String.valueOf(reqJson.get("merchantOrderId"));
        //String retMsg=reqJson.getString("orderMessage");
        if ("200".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        } else if ("999".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(String.valueOf(tradeNo));
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                // paramVO.setRemark(retMsg);
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(outTradeNo);
                paramVO.setPayId(tradeNo);
                // paramVO.setRemark(retMsg);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        }
        return "success";
    }

    public String goPayCallback(JSONObject reqJson, String userType) {
        // 收款方商户号就是第三方
        String merId = (String) reqJson.get("recvid");
        if (!org.springframework.util.StringUtils.hasText(merId)) {
            return "PARAM_ERROR";
        }
        SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.GOPay.getName(), merId);
        if (baseVO == null) {
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String retsign = (String) reqJson.get("retsign");
        log.info("GoPay代收通知加密前： {}", reqJson);

        String sign = (String) reqJson.get("sign");

        String mySign = SignUtil.goMd5(sign + key);

        log.info("GoPay代收通知加密后： {}", retsign);
        if (!retsign.equalsIgnoreCase(mySign)) {
            return "VERIFY_ERROR";
        } else {
            log.info("GOPay代收通知验签成功");
        }
        //成功才回调

        String amount = (String) reqJson.get("amount");
        String orderid = (String) reqJson.get("orderid");
        // 三方订单号
        String id = (String) reqJson.get("id");
        String state = (String) reqJson.get("state");
        if ("4".equals(state)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();

                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderid);
                paramVO.setPayId(id);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);

            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderid);
                paramVO.setPayId(id);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);

            }
        } else {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();

                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderid);
                paramVO.setPayId(id);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                payCallbackApi.userDepositCallback(paramVO);

            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderid);
                paramVO.setPayId(id);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);

            }
        }

        return "SUCCESS";
    }

    public String goPayOutCallback(JSONObject reqJson, String userType) {
        String merId = (String) reqJson.get("sendid");
        String orderNo = (String) reqJson.get("orderid");

        if (!org.springframework.util.StringUtils.hasText(merId)) {
            return "PARAM_ERROR";
        }
        // 因为此通道特殊，代付不传商户号，所以这里写死拿秘钥
        SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelInfoByMerNo(PayChannelNameEnum.GOPay.getName(), merId);
        if (baseVO == null) {
            return "INNER_ERROR";
        }
        String key = baseVO.getSecretKey();
        String reqSignVal = (String) reqJson.get("retsign");
        log.info("SQPay代付通知加密前： {}", reqJson);

        String sign = (String) reqJson.remove("sign");
        //      String signStr = channelRespVO.getMerNo() + orderNo + withdrawalVO.getAmount() + key;
        String signStr = sign + key;
        String mySign = SignUtil.goMd5(signStr);

        log.info("SQPay代付通知加密后： {}", mySign);
        if (ObjectUtil.isNotEmpty(reqSignVal)) {
            if (!reqSignVal.equalsIgnoreCase(mySign)) {
                log.info("SQPay代付通知验签失败");
                return "VERIFY_ERROR";
            } else {
                log.info("SQPay代付通知验签成功");
            }
        }
        //订单状态
        String orderStatus = (String) reqJson.get("state");
        String amount = (String) reqJson.get("amount");
        // 代付订单号
        String orderid = (String) reqJson.get("orderid");
        // 商户订单号
        String id = (String) reqJson.get("id");
        //String retMsg=reqJson.getString("orderMessage");
        if ("4".equals(orderStatus)) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderid);
                paramVO.setPayId(id);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderid);
                paramVO.setPayId(id);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
            return "SUCCESS";
        } else {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderid);
                paramVO.setPayId(id);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderid);
                paramVO.setPayId(id);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        }
        return "SUCCESS";
    }


    public String kdPayCallback(@RequestParam Map<String, Object> request, String userType) {
        try {
            // 1. 获取参数
            String userCode = (String) request.get("userCode");
            String orderCode = (String) request.get("orderCode");//我方订单号
            String amount = (String) request.get("amount");
            String status = (String) request.get("status");
            String sign = (String) request.get("sign");

            log.info("收到支付回调: userCode={}, orderCode={}, amount={}, status={}, sign={}",
                    userCode, orderCode, amount, status, sign);

            if (StringUtils.isBlank(userCode)) {
                return "PARAM_ERROR";
            }

            // 2. 查询商户秘钥
            SystemRechargeChannelBaseVO baseVO = systemRechargeChannelApi.getChannelInfoByMerNo(
                    PayChannelNameEnum.KDPay.getName(), userCode);
            if (baseVO == null) {
                return "INNER_ERROR";
            }
            String key = baseVO.getSecretKey();

            // 3. 生成签名（必须大写）
            String rawStr = orderCode + "&" + amount + "&" + userCode + "&" + status + "&" + key;
            String mySign = DigestUtil.md5Hex(rawStr).toUpperCase();

            log.info("签名原串: {}", rawStr);
            log.info("生成签名: {}, 回调签名: {}", mySign, sign);

            if (!mySign.equals(sign)) {
                log.error("签名校验失败, mySign={}, sign={}", mySign, sign);
                return "VERIFY_ERROR";
            }

            log.info("签名校验成功");

            // 4. 处理支付状态（这里只演示已支付成功的回调）
            if ("3".equals(status)) {
                if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                    CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                    paramVO.setAmount(new BigDecimal(amount));
                    paramVO.setOrderNo(orderCode);
                    paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                    payCallbackApi.userDepositCallback(paramVO);

                    return "success"; // 平台要求返回 success
                } else {
                    AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                    paramVO.setAmount(new BigDecimal(amount));
                    paramVO.setOrderNo(orderCode);
                    paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                    agentPayCallbackApi.withdrawCallback(paramVO);

                    return "success"; // 平台要求返回 success
                }

            } else if("4".equals(status)) {
                if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                    CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                    paramVO.setAmount(new BigDecimal(amount));
                    paramVO.setOrderNo(orderCode);
                    paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                    payCallbackApi.userDepositCallback(paramVO);


                } else {
                    AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                    paramVO.setAmount(new BigDecimal(amount));
                    paramVO.setOrderNo(orderCode);
                    paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                    agentPayCallbackApi.withdrawCallback(paramVO);


                }
                log.warn("支付状态非成功: status={}", status);
                return "success";
            }

        } catch (Exception e) {
            log.error("支付回调处理异常", e);
            return "ERROR";
        }
        return "success";
    }

    public String kdPayOutCallback(Map<String, Object> request, String userType) {
        try {
            // 1. 获取参数
            String orderCode = request.get("orderCode").toString();//通道订单号
            String customerOrderCode = request.get("customerOrderCode").toString();//我方订单号
            String amount = (String) request.get("amount");
            String userCode = (String) request.get("userCode");
            String status = (String) request.get("status");
            String remitTime = (String) request.get("remitTime");
            String sign = (String) request.get("sign");

            log.info("收到代付回调: orderCode={}, customerOrderCode={}, amount={}, userCode={}, status={}, remitTime={}, sign={}",
                    orderCode, customerOrderCode, amount, userCode, status, remitTime, sign);

            if (!org.springframework.util.StringUtils.hasText(userCode)) {
                return "PARAM_ERROR";
            }

            // 2. 查询商户秘钥
            SystemWithdrawChannelResponseVO baseVO = systemWithdrawChannelApi.getChannelInfoByMerNo(
                    PayChannelNameEnum.KDPay.getName(), userCode);
            if (baseVO == null) {
                return "INNER_ERROR";
            }
            String key = baseVO.getSecretKey();

            // 3. 生成签名
            String rawStr = orderCode + "&" + customerOrderCode + "&" + amount + "&" + userCode + "&" + status + "&" + key;
            String mySign = DigestUtil.md5Hex(rawStr).toUpperCase();

            log.info("签名原串: {}", rawStr);
            log.info("生成签名: {}, 回调签名: {}", mySign, sign);

            if (!mySign.equals(sign)) {
                log.error("代付回调签名校验失败, mySign={}, sign={}", mySign, sign);
                return "VERIFY_ERROR";
            }

            log.info("代付回调签名校验成功");

            // 4. 处理代付状态（2=成功, 3=失败）
            if ("2".equals(status)) {
                if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                    CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                    paramVO.setAmount(new BigDecimal(amount));
                    paramVO.setOrderNo(customerOrderCode); // 商户业务订单号
                    paramVO.setPayId(orderCode);           // 平台代付订单号
                    paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                    payCallbackApi.withdrawCallback(paramVO);
                } else {
                    AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                    paramVO.setAmount(new BigDecimal(amount));
                    paramVO.setOrderNo(customerOrderCode);
                    paramVO.setPayId(orderCode);
                    paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                    agentPayCallbackApi.withdrawCallback(paramVO);
                }
            } else if ("3".equals(status)) {
                if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                    CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                    paramVO.setAmount(new BigDecimal(amount));
                    paramVO.setOrderNo(customerOrderCode);
                    paramVO.setPayId(orderCode);
                    paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                    payCallbackApi.withdrawCallback(paramVO);
                } else {
                    AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                    paramVO.setAmount(new BigDecimal(amount));
                    paramVO.setOrderNo(customerOrderCode);
                    paramVO.setPayId(orderCode);
                    paramVO.setStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                    agentPayCallbackApi.withdrawCallback(paramVO);
                }
            } else {
                log.warn("未知状态回调: status={}", status);
            }

            // 必须返回 success，否则三方会认为失败
            return "success";

        } catch (Exception e) {
            log.error("代付回调处理异常", e);
            return "ERROR";
        }
    }
}
