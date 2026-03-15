package com.cloud.baowang.play.api.vo.nextSpin;

import com.cloud.baowang.play.api.enums.nextSpin.NextSpinRespErrEnums;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class NextSpinBetResp {
    // 执行情况
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("serialNo")
    private String serialNo;
    @JsonProperty("merchantCode")
    private String merchantCode;
    @JsonProperty("transferId")
    private String transferId;
    @JsonProperty("merchantTxId")
    private String merchantTxId;
    @JsonProperty("acctId")
    private String acctId;
    @JsonProperty("balance")
    private BigDecimal balance;
    public BigDecimal getBalance() {
        if (balance == null) {
            return null;
        }
        // 保留两位小数，使用四舍五入
        return balance.setScale(2, RoundingMode.DOWN);
    }

    public static NextSpinBetResp success(NextSpinReq req,String merchantTxId,BigDecimal balance){
        NextSpinBetResp nextSpinResp = new NextSpinBetResp();
        nextSpinResp.setCode(NextSpinRespErrEnums.SUCCESS.getCode());
        nextSpinResp.setMsg(NextSpinRespErrEnums.SUCCESS.getDescription());
        nextSpinResp.setMerchantCode(req.getMerchantCode());
        nextSpinResp.setSerialNo(req.getSerialNo());
        nextSpinResp.setTransferId(req.getTransferId());
        nextSpinResp.setAcctId(req.getAcctId());
        nextSpinResp.setMerchantTxId(merchantTxId);
        nextSpinResp.setBalance(balance);
        return nextSpinResp;
    }
    public static NextSpinBetResp err(NextSpinRespErrEnums enums, NextSpinReq req, BigDecimal balance){
        NextSpinBetResp nextSpinResp = new NextSpinBetResp();
        nextSpinResp.setCode(enums.getCode());
        nextSpinResp.setMsg(enums.getDescription());
        nextSpinResp.setMerchantCode(req.getMerchantCode());
        nextSpinResp.setSerialNo(req.getSerialNo());
        nextSpinResp.setTransferId(req.getTransferId());
        nextSpinResp.setAcctId(req.getAcctId());
        nextSpinResp.setBalance(balance);
        return nextSpinResp;
    }



}
