package com.cloud.baowang.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.agent.api.api.AgentPayCallbackApi;
import com.cloud.baowang.agent.api.api.AgentWithdrawRecordApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentCallbackDepositParamVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentCallbackWithdrawParamVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalRecordResVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.pay.api.enums.PayChannelNameEnum;
import com.cloud.baowang.pay.api.vo.TradeNotifyVo;
import com.cloud.baowang.service.CallbackService;
import com.cloud.baowang.service.vendor.JZPay.vo.JZPayCallbackVO;
import com.cloud.baowang.service.vendor.JZPay.vo.JZPayoutCallbackVO;
import com.cloud.baowang.service.vendor.MIDPay.vo.MidPaymentBackVO;
import com.cloud.baowang.service.vendor.PAPay.vo.PaPayCallbackVO;
import com.cloud.baowang.service.vendor.PAPay.vo.PaPayoutCallbackVO;
import com.cloud.baowang.wallet.api.api.PayCallbackApi;
import com.cloud.baowang.wallet.api.api.UserWithdrawRecordApi;
import com.cloud.baowang.wallet.api.enums.wallet.OwnerUserTypeEnum;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.userCoin.UserDepositWithdrawalResVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.CallbackDepositParamVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.CallbackWithdrawParamVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cloud.baowang.common.core.constants.WalletConstants.*;

/**
 * @author: fangfei
 * @createTime: 2024/10/01 12:20
 * @description: 回调服务
 */
@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CallbackController {

    private final CallbackService callbackService;
    private final UserWithdrawRecordApi userWithdrawRecordApi;
    private final AgentWithdrawRecordApi agentWithdrawRecordApi;
    private Environment environment;
    private final PayCallbackApi payCallbackApi;
    private final AgentPayCallbackApi agentPayCallbackApi;



    //回调模拟函数
    @GetMapping(value = "testCallback")
    public String testCallback(@RequestParam Map<String, String> param) {
        log.info("手动代收回调：{}", JSONObject.toJSONString(param));
        String[] env = environment.getActiveProfiles();
        log.info("当前环境为{}", Arrays.toString(env));

        if (!env[0].equals("sit") && !env[0].equals("dev")) {
            return "当前环境不支持";
        }
        String status = param.get("status");
        String orderId = param.get("orderId");
        String type = param.get("userType");
        String amount = param.get("amount");
        if (amount == null) return "amount 不能为空";

        String channel = "";
        String userType = "";
        String paxId = null;
        if (type == null) {
            type = "0";
        }
        if (type.equals("1")) {
            AgentWithdrawalRecordResVO vo =agentWithdrawRecordApi.getRecordByOrderId(orderId);
            channel = vo.getDepositWithdrawChannelName();
            userType = OwnerUserTypeEnum.AGENT.getCode();
        } else {
            UserDepositWithdrawalResVO vo = userWithdrawRecordApi.getRecordByOrderId(orderId);
            channel = vo.getDepositWithdrawChannelName();
            userType = OwnerUserTypeEnum.USER.getCode();
            paxId = vo.getPayTxId();
        }

        if (status.equals("1")) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackDepositParamVO paramVO = new CallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderId);
                paramVO.setPayId(paxId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.userDepositCallback(paramVO);
            } else {
                AgentCallbackDepositParamVO paramVO = new AgentCallbackDepositParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderId);
                paramVO.setPayId(paxId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                agentPayCallbackApi.agentDepositCallback(paramVO);
            }
        }

        return "success";

    }
    @GetMapping(value = "testPayoutCallback")
    public String testPayoutCallback(@RequestParam Map<String, String> param) {
        log.info("手动代付回调：{}", JSONObject.toJSONString(param));
        String[] env = environment.getActiveProfiles();
        log.info("当前环境为{}", Arrays.toString(env));

        if (!env[0].equals("sit") && !env[0].equals("dev")) {
            return "当前环境不支持";
        }

        String status = param.get("status");
        String orderId = param.get("orderId");
        String type = param.get("userType");
        String channel = "";
        String amount = "";
        String userType = "";
        String paxId = null;
        if (type == null) type = "0";
        if (type.equals("1")) {
            AgentWithdrawalRecordResVO vo =agentWithdrawRecordApi.getRecordByOrderId(orderId);
            channel = vo.getDepositWithdrawChannelName();
            amount = vo.getApplyAmount().toString();
            userType = OwnerUserTypeEnum.AGENT.getCode();
        } else {
            UserDepositWithdrawalResVO vo = userWithdrawRecordApi.getRecordByOrderId(orderId);
            channel = vo.getDepositWithdrawChannelName();
            amount = vo.getApplyAmount().toString();
            userType = OwnerUserTypeEnum.USER.getCode();
            paxId = vo.getPayTxId();
        }

        if (status.equals("1")) {
            if (OwnerUserTypeEnum.USER.getCode().equals(userType)) {
                CallbackWithdrawParamVO paramVO = new CallbackWithdrawParamVO();
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setOrderNo(orderId);
                paramVO.setPayId(paxId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                payCallbackApi.withdrawCallback(paramVO);
            } else {
                AgentCallbackWithdrawParamVO paramVO = new AgentCallbackWithdrawParamVO();
                paramVO.setOrderNo(orderId);
                paramVO.setStatus(ThirdPayOrderStatusEnum.Success.getCode());
                paramVO.setAmount(new BigDecimal(amount));
                paramVO.setPayId(paxId);
                agentPayCallbackApi.withdrawCallback(paramVO);
            }
        }


        return "success";
    }

    @PostMapping(value = "jvPayCallback")
    public ResponseVO jvPayCallback(@RequestBody List<TradeNotifyVo> rechargeTradeNotifyVoList,HttpServletRequest request) {
        String signVal=request.getHeader(HEAD_SIGN_NAME);
        String timestamp=request.getHeader(HEAD_TIMESTAMP);
        String random=request.getHeader(HEAD_RANDOM);
        log.info("{}:{},{}:{}.{}:{}", HEAD_SIGN_NAME,signVal,HEAD_TIMESTAMP,timestamp,HEAD_RANDOM,random);
        log.info("JVPay接收到的通知参数:{}", JSON.toJSONString(rechargeTradeNotifyVoList));
        return callbackService.jvPayCallback(rechargeTradeNotifyVoList,signVal,timestamp,random);
    }

    /**
     * PGPAY 代收回调
     * @param param
     * @return
     */
    @PostMapping(value = "pgPayCallback")
    public String pGPayCallback(@RequestParam Map<String, Object> param) {
        log.info("PGPay支付回调参数：" + param);
        if (param != null) {
            return callbackService.pGPayCallback(param);
        }
        JSONObject result = new JSONObject();
        result.put("code", "0");
        result.put("description", "Success");

        return result.toJSONString();
    }

    /**
     * midPay代收回调 ---会员
     */
    @PostMapping(value = "midPayCallback")
    public String midPayCallback(@RequestBody MidPaymentBackVO backVO) {
        log.info("MidPay支付回调参数：{}", JSONObject.toJSONString(backVO));
        return callbackService.midPayCallback(backVO, OwnerUserTypeEnum.USER.getCode());
    }

    /**
     * midPay代付回调 ---会员
     */
    @PostMapping(value = "midPayoutCallback")
    public String midPayoutCallback(@RequestBody MidPaymentBackVO backVO) {
        log.info("MidPay代付回调参数：{}", JSONObject.toJSONString(backVO));
        return callbackService.midPayoutCallback(backVO, OwnerUserTypeEnum.USER.getCode());
    }

    /**
     * midPay代收回调 ---代理
     */
    @PostMapping(value = "midPayAgentCallback")
    public String midPayAgentCallback(@RequestBody MidPaymentBackVO backVO) {
        log.info("MidPay支付回调参数：{}", JSONObject.toJSONString(backVO));
        return callbackService.midPayCallback(backVO, OwnerUserTypeEnum.AGENT.getCode());
    }

    /**
     * midPay代付回调 ---代理
     */
    @PostMapping(value = "midPayoutAgentCallback")
    public String midPayoutAgentCallback(@RequestBody MidPaymentBackVO backVO) {
        log.info("MidPay代付回调参数：{}", JSONObject.toJSONString(backVO));
        return callbackService.midPayoutCallback(backVO, OwnerUserTypeEnum.AGENT.getCode());
    }


    @PostMapping(value = "paPayCallback")
    public String paPayCallback(@RequestBody PaPayCallbackVO backVO) {
        log.info("PAPay支付会员回调参数：{}", JSONObject.toJSONString(backVO));
        return callbackService.paPayCallback(backVO, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "paPayoutCallback")
    public String paPayoutCallback(@RequestBody PaPayoutCallbackVO backVO) {
        log.info("PAPay代付会员回调参数：{}", JSONObject.toJSONString(backVO));
        return callbackService.paPayoutCallback(backVO, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "paPayAgentCallback")
    public String paPayAgentCallback(@RequestBody PaPayCallbackVO backVO) {
        log.info("PAPay支付代理回调参数：{}", JSONObject.toJSONString(backVO));
        return callbackService.paPayCallback(backVO, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "paPayoutAgentCallback")
    public String paPayoutAgentCallback(@RequestBody PaPayoutCallbackVO backVO) {
        log.info("PAPay代付代理回调参数：{}", JSONObject.toJSONString(backVO));
        return callbackService.paPayoutCallback(backVO, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "mtPayPostBack")
    public void mtPayPostBack(HttpServletRequest request) {

        String body = readBodyAsString(request);
        JSONObject jsonObject = JSONObject.parseObject(body);
        log.info("MTPay支付回调参数 流方式: {}", body);
        String orderId = jsonObject.get("TransNum").toString();
        //判断是代理还是会员
        String code = OwnerUserTypeEnum.USER.getCode();
        AgentWithdrawalRecordResVO vo =agentWithdrawRecordApi.getRecordByOrderId(orderId);
        if (vo != null) {
            code = OwnerUserTypeEnum.AGENT.getCode();
        }
        callbackService.mtPayCallback(jsonObject, code);
    }

    @GetMapping(value = "mtPayCallback")
    public void mtPayCallback(@RequestParam Map<String, Object> param) {
        log.info("MTPay支付回调参数：{}", JSONObject.toJSONString(param));
    }

    @PostMapping(value = "mtPayoutCallback")
    public void mtPayoutCallback(@RequestParam Map<String, Object> param, HttpServletRequest request) {
        log.info("MTPay代付回调参数：{}", JSONObject.toJSONString(param));
        if(param.size() == 0) {
            String body = readBodyAsString(request);
            JSONObject jsonObject = JSONObject.parseObject(body);
            log.info("MTPay代付回调参数, 流方式：{}", jsonObject.toString());
            param = new HashMap<>();
            param.put("Result", jsonObject.getString("jsonObject"));
            if (jsonObject.get("MerchantTransNum") == null) {
                param.put("TransNum", jsonObject.getString("TransNum"));
            } else {
                param.put("MerchantTransNum", jsonObject.getString("MerchantTransNum"));
            }
            param.put("MerchantCode", jsonObject.getString("MerchantCode"));
            param.put("CheckString2", jsonObject.getString("CheckString2"));
            param.put("CurrencyTypeId", jsonObject.getString("CurrencyTypeId"));
            param.put("PayoutId", jsonObject.getString("PayoutId"));
            param.put("ToBankId", jsonObject.getString("ToBankId"));
            param.put("ToBankAccountNum", jsonObject.getString("ToBankAccountNum"));
            param.put("Amount", jsonObject.getString("Amount"));
        }
        //判断是代理还是会员
        String orderId = param.get("MerchantTransNum").toString();
        String code = OwnerUserTypeEnum.USER.getCode();
        AgentWithdrawalRecordResVO vo =agentWithdrawRecordApi.getRecordByOrderId(orderId);
        if (vo != null) {
            code = OwnerUserTypeEnum.AGENT.getCode();
        }
        callbackService.mtPayoutCallback(param, code);
    }

    @PostMapping(value = "luckyPayCallback")
    public void luckyPayCallback(@RequestParam Map<String, Object> param) {
        log.info("luckPay支付回调参数：{}", JSONObject.toJSONString(param));
        callbackService.luckyPayCallback(param, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "luckyPayoutCallback")
    public void luckyPayoutCallback(@RequestParam Map<String, Object> param) {
        log.info("luckPay代付回调参数：{}", JSONObject.toJSONString(param));
        callbackService.luckyPayoutCallback(param, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "luckyPayAgentCallback")
    public void luckyPayAgentCallback(@RequestParam Map<String, Object> param) {
        log.info("luckPay支付回调参数：{}", JSONObject.toJSONString(param));
        callbackService.luckyPayCallback(param, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "luckyPayoutAgentCallback")
    public void luckyPayoutAgentCallback(@RequestParam Map<String, Object> param) {
        log.info("luckPay代付回调参数：{}", JSONObject.toJSONString(param));
        callbackService.luckyPayoutCallback(param, OwnerUserTypeEnum.AGENT.getCode());
    }

    @GetMapping(value = "mhdGoPayCallback")
    public String mhdGoPayCallback(@RequestParam Map<String, Object> param) {
        log.info("MhdGoPay充值回调参数：{}", param);
        return callbackService.mhdPayCallback(PayChannelNameEnum.MhdGoPay.getName(), param, OwnerUserTypeEnum.USER.getCode());
    }

    @GetMapping(value = "mhdGoPayoutCallback")
    public String mhdGoPayoutCallback(@RequestParam Map<String, Object> param) {
        log.info("MhdGoPay代付回调参数：{}", param);
        return callbackService.mhdPayoutCallback(PayChannelNameEnum.MhdGoPay.getName(), param, OwnerUserTypeEnum.USER.getCode());
    }

    @GetMapping(value = "mhdGoPayAgentCallback")
    public String mhdGoPayAgentCallback(@RequestParam Map<String, Object> param) {
        log.info("MhdGoPay充值回调参数：{}", param);
        return callbackService.mhdPayCallback(PayChannelNameEnum.MhdGoPay.getName(), param, OwnerUserTypeEnum.AGENT.getCode());
    }

    @GetMapping(value = "mhdGoPayoutAgentCallback")
    public String mhdGoPayoutAgentCallback(@RequestParam Map<String, Object> param) {
        log.info("MhdGoPay代付回调参数：{}", param);
        return callbackService.mhdPayoutCallback(PayChannelNameEnum.MhdGoPay.getName(), param, OwnerUserTypeEnum.AGENT.getCode());
    }

    @GetMapping(value = "mhdGcPayCallback")
    public String mhdGcPayCallback(@RequestParam Map<String, Object> param) {
        log.info("MhdGcPay充值回调参数：{}", param);
        return callbackService.mhdPayCallback(PayChannelNameEnum.MhdGcPay.getName(), param, OwnerUserTypeEnum.USER.getCode());
    }

    @GetMapping(value = "mhdGcPayoutCallback")
    public String mhdGcPayoutCallback(@RequestParam Map<String, Object> param) {
        log.info("MhdGcPay代付回调参数：{}", param);
        return callbackService.mhdPayoutCallback(PayChannelNameEnum.MhdGcPay.getName(), param, OwnerUserTypeEnum.USER.getCode());
    }

    @GetMapping(value = "mhdGcPayAgentCallback")
    public String mhdGcPayAgentCallback(@RequestParam Map<String, Object> param) {
        log.info("MhdGcPay充值回调参数：{}", param);
        return callbackService.mhdPayCallback(PayChannelNameEnum.MhdGcPay.getName(), param, OwnerUserTypeEnum.AGENT.getCode());
    }

    @GetMapping(value = "mhdGcPayoutAgentCallback")
    public String mhdGcPayoutAgentCallback(@RequestParam Map<String, Object> param) {
        log.info("MhdGcPay代付回调参数：{}", param);
        return callbackService.mhdPayoutCallback(PayChannelNameEnum.MhdGcPay.getName(), param, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "jzPayCallback")
    public String jzPayCallback(@RequestBody JZPayCallbackVO backVO) {
        log.info("JZPay支付回调参数：{}", JSONObject.toJSONString(backVO));
        return callbackService.jzPayCallback(backVO, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "jzPayoutCallback")
    public String jzPayoutCallback(@RequestBody JZPayoutCallbackVO backVO) {
        log.info("JZPay代付回调参数：{}", JSONObject.toJSONString(backVO));
        return callbackService.jzPayoutCallback(backVO, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "jzPayAgentCallback")
    public String jzPayAgentCallback(@RequestBody JZPayCallbackVO backVO) {
        log.info("JZPay支付回调参数：{}", JSONObject.toJSONString(backVO));
        return callbackService.jzPayCallback(backVO, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "jzPayoutAgentCallback")
    public String jzPayoutAgentCallback(@RequestBody JZPayoutCallbackVO backVO) {
        log.info("JZPay代付回调参数：{}", JSONObject.toJSONString(backVO));
        return callbackService.jzPayoutCallback(backVO, OwnerUserTypeEnum.AGENT.getCode());
    }






    public static String readBodyAsString(HttpServletRequest request) {
        InputStream is = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = request.getInputStream();
            byte[] b = new byte[4096];
            for (int n; (n = is.read(b)) != -1;) {
                sb.append(new String(b, 0, n));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }


    // 钱进来 begin
    @PostMapping(value = "qjlPayCallback")
    public String qjlPayCallback(@RequestParam Map<String,Object> params) {
        log.info("QLPay充值回调参数：{}", params);
        return callbackService.qjlPayCallback(params, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "qjlPayoutCallback")
    public String qjlPayoutCallback(@RequestParam Map<String,Object> params) {
        log.info("QLPay代付回调参数：{}", params);
        return callbackService.qjlPayoutCallback(params, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "qjlPayAgentCallback")
    public String qjlPayAgentCallback(@RequestParam Map<String,Object> params) {
        log.info("QLPay代理充值回调参数：{}", params);
        return callbackService.qjlPayCallback(params, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "qjlPayoutAgentCallback")
    public String qjlPayoutAgentCallback(@RequestParam Map<String,Object> params) {
        log.info("QLPay代理代付回调参数：{}", params);
        return callbackService.qjlPayoutCallback(params, OwnerUserTypeEnum.AGENT.getCode());
    }
    // 钱进来 end



    // DoPay begin
    @PostMapping(value = "doPayCallback")
    public String doPayCallback(@RequestBody JSONObject reqJson) {
        log.info("doPay充值回调参数：{}", reqJson);
        return callbackService.doPayCallback(reqJson, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "doPayoutCallback")
    public String doPayoutCallback(@RequestBody JSONObject reqJson) {
        log.info("doPay代付回调参数：{}", reqJson);
        return callbackService.doPayOutCallback(reqJson, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "doPayAgentCallback")
    public String doPayAgentCallback(@RequestBody JSONObject reqJson) {
        log.info("doPay代理充值回调参数：{}", reqJson);
        return callbackService.doPayCallback(reqJson, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "doPayoutAgentCallback")
    public String doPayoutAgentCallback(@RequestBody JSONObject reqJson) {
        log.info("doPay代理代付回调参数：{}", reqJson);
        return callbackService.doPayOutCallback(reqJson, OwnerUserTypeEnum.AGENT.getCode());
    }
    // DoPay end


    // FPay begin
    @PostMapping(value = "fPayCallback")
    public String fPayCallback(@RequestBody JSONObject reqJson) {
        log.info("fPay回调参数：{}", reqJson);
        return callbackService.fPayCallback(reqJson);
    }
    // FPay end


    // TopPay begin
    @PostMapping(value = "topPayCallback")
    public String topPayCallback(@RequestBody com.alibaba.fastjson.JSONObject reqJson) {
        log.info("topPay充值回调参数：{}", reqJson);
        com.alibaba.fastjson.JSONObject dataJson=reqJson.getJSONObject("data");
        return callbackService.topPayCallback(dataJson, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "topPayoutCallback")
    public String topPayoutCallback(@RequestBody com.alibaba.fastjson.JSONObject reqJson) {
        log.info("topPay代付回调参数：{}", reqJson);
        com.alibaba.fastjson.JSONObject dataJson=reqJson.getJSONObject("data");
        return callbackService.topPayoutCallback(dataJson, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "topPayAgentCallback")
    public String topPayAgentCallback(@RequestBody com.alibaba.fastjson.JSONObject reqJson) {
        log.info("topPay代理充值回调参数：{}", reqJson);
        com.alibaba.fastjson.JSONObject dataJson=reqJson.getJSONObject("data");
        return callbackService.topPayCallback(dataJson, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "topPayoutAgentCallback")
    public String topPayoutAgentCallback(@RequestBody com.alibaba.fastjson.JSONObject reqJson) {
        log.info("topPay代理代付回调参数：{}", reqJson);
        com.alibaba.fastjson.JSONObject dataJson=reqJson.getJSONObject("data");
        return callbackService.topPayoutCallback(dataJson, OwnerUserTypeEnum.AGENT.getCode());
    }
    // DoPay end




    //EzPay Begin
    @PostMapping(value = "ezPayCallback")
    public String ezPayCallback(@RequestParam Map<String, String> param) {
        log.info("EZPay支付回调参数：{}", param);
        return callbackService.ezPayCallback(param, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "ezPayoutCallback")
    public String ezPayoutCallback(@RequestParam Map<String, String> param) {
        log.info("EZPay代付回调参数：{}", param);
        return callbackService.ezPayoutCallback(param, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "ezPayAgentCallback")
    public String ezPayAgentCallback(@RequestParam Map<String, String> param) {
        log.info("EZPay支付代理回调参数：{}", param);
        return callbackService.ezPayCallback(param, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "ezPayoutAgentCallback")
    public String ezPayoutAgentCallback(@RequestParam Map<String, String> param) {
        log.info("EZPay代付代理回调参数：{}", param);
        return callbackService.ezPayoutCallback(param, OwnerUserTypeEnum.AGENT.getCode());
    }
    //EzPay end


    // HYPay begin
    @PostMapping(value = "hyPayCallback")
    public String hyPayCallback(@RequestBody JSONObject reqJson) {
        log.info("hyPay充值回调参数：{}", reqJson);
        return callbackService.hyPayCallback(reqJson, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "hyPayoutCallback")
    public String hyPayoutCallback(@RequestBody JSONObject reqJson) {
        log.info("hyPay代付回调参数：{}", reqJson);
        return callbackService.hyPayOutCallback(reqJson, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "hyPayAgentCallback")
    public String hyPayAgentCallback(@RequestBody JSONObject reqJson) {
        log.info("hyPay代理充值回调参数：{}", reqJson);
        return callbackService.hyPayCallback(reqJson, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "hyPayoutAgentCallback")
    public String hyPayoutAgentCallback(@RequestBody JSONObject reqJson) {
        log.info("hyPay代理代付回调参数：{}", reqJson);
        return callbackService.hyPayOutCallback(reqJson, OwnerUserTypeEnum.AGENT.getCode());
    }
    // HYPay end

    // LemonPay begin
    @PostMapping(value = "lemonPayCallback")
    public String lemonPayCallback(@RequestParam Map<String, Object> param) {
        log.info("lemonPay充值回调参数：{}", param);
        return callbackService.lemonPayCallback(param, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "lemonPayoutCallback")
    public String lemonPayoutCallback(@RequestParam Map<String, Object> param) {
        log.info("lemonPay代付回调参数：{}", param);
        return callbackService.lemonPayOutCallback(param, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "lemonPayAgentCallback")
    public String lemonPayAgentCallback(@RequestParam Map<String, Object> param) {
        log.info("lemonPay代理充值回调参数：{}", param);
        return callbackService.lemonPayCallback(param, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "lemonPayoutAgentCallback")
    public String lemonPayoutAgentCallback(@RequestParam Map<String, Object> param) {
        log.info("lemonPay代理代付回调参数：{}", param);
        return callbackService.lemonPayOutCallback(param, OwnerUserTypeEnum.AGENT.getCode());
    }
    // LemonPay end

    // TSpay begin
    @PostMapping(value = "tsPayCallback")
    public String tsPayCallback(@RequestBody JSONObject param) {
        log.info("tsPay充值回调参数：{}", param);
        return callbackService.tsPayCallback(param, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "tsPayoutCallback")
    public String tsPayoutCallback(@RequestBody JSONObject param) {
        log.info("tsPay提现回调参数：{}", param);
        return callbackService.tsPayOutCallback(param, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "tsPayAgentCallback")
    public String tsPayAgentCallback(@RequestBody JSONObject param) {
        log.info("tsPay代理充值回调参数：{}", param);
        return callbackService.tsPayCallback(param, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "tsPayoutAgentCallback")
    public String tsPayoutAgentCallback(@RequestBody JSONObject param) {
        log.info("tsPay代理提现回调参数：{}", param);
        return callbackService.tsPayOutCallback(param, OwnerUserTypeEnum.AGENT.getCode());
    }
    // TSpay end



    // EbPay begin
    @PostMapping(value = "ebPayCallback")
    public String ebPayCallback(@RequestParam Map<String, Object> param) {
        log.info("ebPay 充值回调参数：{}", param);
        JSONObject reqJson=JSONObject.parseObject(JSON.toJSONString(param));
        return callbackService.ebPayCallback(reqJson, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "ebPayoutCallback")
    public String ebPayoutCallback(@RequestParam Map<String, Object> param) {
        log.info("ebPay 代付回调参数：{}", param);
        JSONObject reqJson=JSONObject.parseObject(JSON.toJSONString(param));
        return callbackService.ebPayOutCallback(reqJson, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "ebPayAgentCallback")
    public String ebPayAgentCallback(@RequestParam Map<String, Object> param ){
        log.info("ebPay 代理充值回调参数：{}", param);
        JSONObject reqJson=JSONObject.parseObject(JSON.toJSONString(param));
        return callbackService.ebPayCallback(reqJson, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "ebPayoutAgentCallback")
    public String ebPayoutAgentCallback(@RequestParam Map<String, Object> param) {
        log.info("ebPay 代理代付回调参数：{}", param);
        JSONObject reqJson=JSONObject.parseObject(JSON.toJSONString(param));
        return callbackService.ebPayOutCallback(reqJson, OwnerUserTypeEnum.AGENT.getCode());
    }
    // EbPay end

    // FIXPay begin
    @PostMapping(value = "fixPayCallback")
    public String fixPayCallback(@RequestBody JSONObject reqJson) {
        log.info("fixPay 充值回调参数：{}", reqJson);
        return callbackService.fixPayCallback(reqJson, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "fixPayoutCallback")
    public String fixPayoutCallback(@RequestBody JSONObject reqJson) {
        log.info("fixPay 代付回调参数：{}", reqJson);
        return callbackService.fixPayOutCallback(reqJson, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "fixPayAgentCallback")
    public String fixPayAgentCallback(@RequestBody JSONObject reqJson ){
        log.info("fixPay 代理充值回调参数：{}", reqJson);
        return callbackService.fixPayCallback(reqJson, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "fixPayoutAgentCallback")
    public String fixPayoutAgentCallback(@RequestBody JSONObject reqJson) {
        log.info("fixPay 代理代付回调参数：{}", reqJson);
        return callbackService.fixPayOutCallback(reqJson, OwnerUserTypeEnum.AGENT.getCode());
    }
    // FIXPay


    // HFPay begin
    @PostMapping(value = "hfPayCallback")
    public String hfPayCallback(@RequestBody JSONObject reqJson) {
        log.info("hfPay 充值回调参数：{}", reqJson);
        return callbackService.hfPayCallback(reqJson, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "hfPayoutCallback")
    public String hfPayoutCallback(@RequestBody JSONObject reqJson) {
        log.info("hfPay 代付回调参数：{}", reqJson);
        return callbackService.hfPayOutCallback(reqJson, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "hfPayAgentCallback")
    public String hfPayAgentCallback(@RequestBody JSONObject reqJson ){
        log.info("hfPay 代理充值回调参数：{}", reqJson);
        return callbackService.hfPayCallback(reqJson, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "hfPayoutAgentCallback")
    public String hfPayoutAgentCallback(@RequestBody JSONObject reqJson) {
        log.info("hfPay 代理代付回调参数：{}", reqJson);
        return callbackService.hfPayOutCallback(reqJson, OwnerUserTypeEnum.AGENT.getCode());
    }
    // HFPay end

    // SQPay begin
    @PostMapping(value = "sqPayCallback")
    public String sqPayCallback(@RequestBody JSONObject reqJson) {
        log.info("sqPay 充值回调参数：{}", reqJson);
        return callbackService.sqPayCallback(reqJson, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "sqPayoutCallback")
    public String sqPayoutCallback(@RequestBody JSONObject reqJson) {
        log.info("sqPay 代付回调参数：{}", reqJson);
        return callbackService.sqPayOutCallback(reqJson, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "sqPayAgentCallback")
    public String sqPayAgentCallback(@RequestBody JSONObject reqJson ){
        log.info("sqPay 代理充值回调参数：{}", reqJson);
        return callbackService.sqPayCallback(reqJson, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "sqPayoutAgentCallback")
    public String sqPayoutAgentCallback(@RequestBody JSONObject reqJson) {
        log.info("sqPay 代理代付回调参数：{}", reqJson);
        return callbackService.sqPayOutCallback(reqJson, OwnerUserTypeEnum.AGENT.getCode());
    }
    // SQPay end
    @PostMapping(value = "goPayCallback")
    public String goPayCallback(@RequestBody JSONObject reqJson) {
        log.info("goPay充值回调参数：{}", reqJson);
        return callbackService.goPayCallback(reqJson, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "goPayoutCallback")
    public String goPayoutCallback(@RequestBody JSONObject reqJson) {
        log.info("sgoPay代付回调参数：{}", reqJson);
        return callbackService.goPayOutCallback(reqJson, OwnerUserTypeEnum.USER.getCode());
    }
    @PostMapping(value = "goPayAgentCallback")
    public String goPayAgentCallback(@RequestBody JSONObject reqJson) {
        log.info("goPay 代理充值回调参数：{}", reqJson);
        return callbackService.goPayCallback(reqJson, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "goPayoutAgentCallback")
    public String goPayoutAgentCallback(@RequestBody JSONObject reqJson) {
        log.info("sgoPay 代理代付回调参数：{}", reqJson);
        return callbackService.goPayOutCallback(reqJson, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "kdPayCallback")
    public String kdPayCallback(@RequestParam Map<String, Object> request) {
        log.info("kdPay 充值回调参数：{}", JSON.toJSONString(request));
        return callbackService.kdPayCallback(request, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "kdPayoutCallback")
    public String kdPayoutCallback(@RequestParam Map<String, Object> reqJson) {
        log.info("kdPay 代付回调参数：{}", reqJson);
        return callbackService.kdPayOutCallback(reqJson, OwnerUserTypeEnum.USER.getCode());
    }

    @PostMapping(value = "kdPayAgentCallback")
    public String kdPayAgentCallback(@RequestParam Map<String, Object> request) {
        log.info("kdPay 代理充值回调参数：{}", JSON.toJSONString(request));
        return callbackService.kdPayCallback(request, OwnerUserTypeEnum.AGENT.getCode());
    }

    @PostMapping(value = "kdPayoutAgentCallback")
    public String kdPayoutAgentCallback(@RequestParam Map<String, Object> reqJson) {
        log.info("kdPay 代理代付回调参数：{}", reqJson);
        return callbackService.kdPayOutCallback(reqJson, OwnerUserTypeEnum.AGENT.getCode());
    }
}
