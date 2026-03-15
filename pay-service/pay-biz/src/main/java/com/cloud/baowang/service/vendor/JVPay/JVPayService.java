package com.cloud.baowang.service.vendor.JVPay;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.wallet.api.enums.wallet.PayoutStatusEnum;
import com.cloud.baowang.common.core.utils.HttpClientUtil;
import com.cloud.baowang.pay.api.vo.PayOrderResponseVO;
import com.cloud.baowang.pay.api.vo.PaymentResponseVO;
import com.cloud.baowang.pay.api.vo.PaymentVO;
import com.cloud.baowang.pay.api.vo.TradeNotifyVo;
import com.cloud.baowang.pay.api.vo.WithdrawalResponseVO;
import com.cloud.baowang.pay.api.vo.WithdrawalVO;
import com.cloud.baowang.common.core.constants.WalletConstants;
import com.cloud.baowang.pay.api.vo.OrderNoVO;
import com.cloud.baowang.pay.api.vo.VirtualCurrencyPayRequestVO;
import com.cloud.baowang.service.vendor.BasePayService;
import com.cloud.baowang.common.core.utils.ECDSAUtil;
import com.cloud.baowang.wallet.api.vo.pay.ThirdPayOrderStatusEnum;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelBaseVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawChannelResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: fangfei
 * @createTime: 2024/10/09 17:12
 * @description: 内部虚拟币支付通道 支持TRC20 ERC20
 */
@Slf4j
@Service(value = "JVPay")
public class JVPayService implements BasePayService {

    @Value("${common.config.jvPayDomain}")
    private String jvPayDomainUrl;

    @Value("${common.config.jvPayPrivateKey}")
    private String jvPayPrivateKey;


   /* public static void main(String[] args) {
        String orderNO = OrderUtil.getOrderNo("B", 10);
        SystemWithdrawChannelResponseVO withdrawChannelResponseVO = new SystemWithdrawChannelResponseVO();
        withdrawChannelResponseVO.setChannelCode("Maya");
        withdrawChannelResponseVO.setMerNo("MN000000000619");
        withdrawChannelResponseVO.setPrivateKey("MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCCg/2QFGR6j1YxmEL22ogtzbB0MtiNo+zpm8tJRSf71Vg==");
        withdrawChannelResponseVO.setApiUrl("http://192.168.27.122:8800");
        withdrawChannelResponseVO.setCallbackUrl("https://gw.playesoversea.store");
         new JVPayService().queryPayoutOrder(withdrawChannelResponseVO, "TKVND20241106035607BH");
    }
*/
    @Override
    public PaymentResponseVO creatPayOrder(SystemRechargeChannelBaseVO channelRespVO, PaymentVO paymentVO, String orderNo) {
        return null;
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
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();

        final String apiUrl = jvPayDomainUrl + "api/trade/withdraw"; //http://192.168.27.122:8800
        VirtualCurrencyPayRequestVO requestVO = new VirtualCurrencyPayRequestVO();
        requestVO.setPlatNo(withdrawalVO.getSiteCode());
        requestVO.setOrderNo(withdrawalVO.getOrderNo());
        requestVO.setChainType(withdrawalVO.getChainType());
        requestVO.setOwnerUserId(withdrawalVO.getUserId());
        requestVO.setOwnerUserType(withdrawalVO.getOwnerUserType());
        requestVO.setToAddress(withdrawalVO.getToAddress());
        requestVO.setWithdrawAmt(withdrawalVO.getAmount());
        String paramJson = JSON.toJSONString(requestVO);
        Map<String, String> headerMap = buildHeadMap(paramJson);
        try{
            String result = HttpClientUtil.doPostJson(apiUrl,paramJson , headerMap);
            log.info("提现结果："+result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            withdrawalResponseVO.setMessage(jsonObject.getString("code"));
            if((boolean) jsonObject.get("success")){
                withdrawalResponseVO.setCode(0);
                withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Pending.getCode());
                return withdrawalResponseVO;
            }
        }catch (Exception e){
            log.error("http request error:{0}",e);
        }
        withdrawalResponseVO.setCode(-1);
        withdrawalResponseVO.setWithdrawOrderStatus(PayoutStatusEnum.Abnormal.getCode());
        return withdrawalResponseVO;
    }

    /**
     * 报文加密
     * @param paramJson 请求参数
     * @return
     */
    private Map<String, String> buildHeadMap(String paramJson) {
        String  timestamp=System.currentTimeMillis()+"";
        String   random= RandomStringUtils.randomAlphabetic(6);
        String signVal= ECDSAUtil.signParam(timestamp,random, JSONObject.parse(paramJson),jvPayPrivateKey);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(WalletConstants.HEAD_TIMESTAMP,timestamp);
        headerMap.put(WalletConstants.HEAD_RANDOM,random);
        headerMap.put(WalletConstants.HEAD_SIGN_NAME,signVal);
        return headerMap;
    }

    /**
     * 订单查询
     * @param channelRespVO 支付通道信息
     * @param orderNo 订单号
     * @return
     */
    @Override
    public PayOrderResponseVO queryPayOrder(SystemRechargeChannelBaseVO channelRespVO, String orderNo) {
        PayOrderResponseVO payOrderResponseVO = new PayOrderResponseVO();

        final String apiUrl = jvPayDomainUrl + "api/trade/queryByOrderNo";

        OrderNoVO orderNoVO = new OrderNoVO();
        orderNoVO.setOrderNo(orderNo);
        String paramJson = JSON.toJSONString(orderNoVO);
        Map<String, String> headerMap = buildHeadMap(paramJson);
        try{
            String result = HttpClientUtil.doPostJson(apiUrl,paramJson , headerMap);
            log.info("查询订单结果："+result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if((boolean) jsonObject.get("success")){
                TradeNotifyVo tradeNotifyVo = JSONObject.parseObject(jsonObject.get("data").toString(), TradeNotifyVo.class);
                payOrderResponseVO.setCode(1);
                payOrderResponseVO.setTradeNotifyVo(tradeNotifyVo);
                return payOrderResponseVO;
            }
        }catch (Exception e){
            log.error("http request error:{0}",e);

        }
        payOrderResponseVO.setCode(-1);
        payOrderResponseVO.setTradeNotifyVo(null);
        return payOrderResponseVO;
    }

    @Override
    public WithdrawalResponseVO queryPayoutOrder(SystemWithdrawChannelResponseVO channelRespVO, String orderNo) {
        WithdrawalResponseVO withdrawalResponseVO = new WithdrawalResponseVO();

        final String apiUrl = jvPayDomainUrl + "api/trade/queryByOrderNo";
        OrderNoVO orderNoVO = new OrderNoVO();
        orderNoVO.setOrderNo(orderNo);
        String paramJson = JSON.toJSONString(orderNoVO);
        Map<String, String> headerMap = buildHeadMap(paramJson);
        try{
            String result = HttpClientUtil.doPostJson(apiUrl,paramJson , headerMap);
            log.info("queryByOrderNo查询订单结果："+result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if((boolean) jsonObject.get("success")){
                TradeNotifyVo tradeNotifyVo = JSONObject.parseObject(jsonObject.get("data").toString(), TradeNotifyVo.class);
                withdrawalResponseVO.setCode(1);
                withdrawalResponseVO.setOrderNo(orderNo);
                withdrawalResponseVO.setWithdrawOrderId(tradeNotifyVo.getTradeHash());
                if(tradeNotifyVo.getTradeStatus() == 1){
                    withdrawalResponseVO.setAmount(String.valueOf(tradeNotifyVo.getTradeAmount()));
                    withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Success.getCode());
                } else if (tradeNotifyVo.getTradeStatus() == 0) {
                    withdrawalResponseVO.setAmount(String.valueOf(tradeNotifyVo.getTradeAmount()));
                    withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Fail.getCode());
                } else {
                    withdrawalResponseVO.setAmount(String.valueOf(tradeNotifyVo.getTradeAmount()));
                    withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Pending.getCode());
                }
                withdrawalResponseVO.setTradeNotifyVo(tradeNotifyVo);
                return withdrawalResponseVO;
            }
        }catch (Exception e){
            log.error("http request error:{0}",e);

        }
        withdrawalResponseVO.setCode(-1);
        withdrawalResponseVO.setOrderNo(orderNo);
        withdrawalResponseVO.setWithdrawOrderStatus(ThirdPayOrderStatusEnum.Abnormal.getCode());
        withdrawalResponseVO.setTradeNotifyVo(null);
        return withdrawalResponseVO;
    }
}
