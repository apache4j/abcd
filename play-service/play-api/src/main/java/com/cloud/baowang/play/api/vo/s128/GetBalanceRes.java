package com.cloud.baowang.play.api.vo.s128;


import com.cloud.baowang.play.api.enums.s128.S128GetBalanceErrorCodeEnum;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;


@Data
@JacksonXmlRootElement(localName = "get_balance")
public class GetBalanceRes extends S128Res {

    @JacksonXmlProperty(localName = "balance")
    private BigDecimal balance;


    public static GetBalanceRes success(BigDecimal balance) {
        GetBalanceRes getBalanceRes = new GetBalanceRes();
        getBalanceRes.setStatusCode(S128GetBalanceErrorCodeEnum.SUCCESS.getCode());
        getBalanceRes.setStatusText(S128GetBalanceErrorCodeEnum.SUCCESS.getMessage());
        getBalanceRes.setBalance(balance);
        return getBalanceRes;
    }

    public static GetBalanceRes fail(S128GetBalanceErrorCodeEnum errorCodeEnum,String errorMsg) {
        GetBalanceRes getBalanceRes = new GetBalanceRes();
        getBalanceRes.setStatusCode(errorCodeEnum.getCode());
        getBalanceRes.setStatusText(errorCodeEnum.getMessage());
        if (StringUtils.isNotEmpty(errorMsg)){
            getBalanceRes.setStatusText(errorMsg);
        }
        return getBalanceRes;
    }

}
