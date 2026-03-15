package com.cloud.baowang.play.api.vo.nextSpin;

import com.cloud.baowang.play.api.enums.nextSpin.NextSpinRespErrEnums;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NextSpinResp {
    // 执行情况
    @JsonProperty("msg")
    private String msg;
    // 编码
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("serialNo")
    private String serialNo;
    @JsonProperty("merchantCode")
    private String merchantCode;
    // 用户信息
    private AcctInfo acctInfo;

    public static NextSpinResp success(String merchantCode,String serialNo,AcctInfo acctInfo){
        NextSpinResp nextSpinResp = new NextSpinResp();
        nextSpinResp.setCode(NextSpinRespErrEnums.SUCCESS.getCode());
        nextSpinResp.setMsg(NextSpinRespErrEnums.SUCCESS.getDescription());
        nextSpinResp.setMerchantCode(merchantCode);
        nextSpinResp.setSerialNo(serialNo) ;
        nextSpinResp.setAcctInfo(acctInfo);
        return nextSpinResp;
    }
    public static NextSpinResp err(NextSpinRespErrEnums enums, String merchantCode, String serialNo){
        NextSpinResp nextSpinResp = new NextSpinResp();
        nextSpinResp.setCode(enums.getCode());
        nextSpinResp.setMsg(enums.getDescription());
        nextSpinResp.setMerchantCode(merchantCode);
        nextSpinResp.setSerialNo(serialNo) ;
        return nextSpinResp;
    }

}
