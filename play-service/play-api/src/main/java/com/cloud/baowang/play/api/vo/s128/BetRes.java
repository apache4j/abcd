package com.cloud.baowang.play.api.vo.s128;


import com.cloud.baowang.play.api.enums.s128.S128BetErrorCodeEnum;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;


@Data
@JacksonXmlRootElement(localName = "bet")
public class BetRes extends S128Res {

    @JacksonXmlProperty(localName = "balance")
    private BigDecimal balance;

    @JacksonXmlProperty(localName = "ref_id")
    private String refId;


    public static BetRes success() {
        BetRes getBalanceRes = new BetRes();
        getBalanceRes.setStatusCode(S128BetErrorCodeEnum.SUCCESS.getCode());
        getBalanceRes.setStatusText(S128BetErrorCodeEnum.SUCCESS.getMessage());
        return getBalanceRes;
    }

    public static BetRes fail(S128BetErrorCodeEnum errorCodeEnum, String errorMsg) {
        BetRes getBalanceRes = new BetRes();
        getBalanceRes.setStatusCode(errorCodeEnum.getCode());
        getBalanceRes.setStatusText(errorCodeEnum.getMessage());
        if (StringUtils.isNotEmpty(errorMsg)){
            getBalanceRes.setStatusText(errorMsg);
        }
        return getBalanceRes;
    }

}
