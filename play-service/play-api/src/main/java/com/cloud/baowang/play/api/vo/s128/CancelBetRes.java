package com.cloud.baowang.play.api.vo.s128;


import com.cloud.baowang.play.api.enums.s128.S128BetErrorCodeEnum;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;


@Data
@JacksonXmlRootElement(localName = "bet")
public class CancelBetRes extends S128Res {

    public static CancelBetRes success() {
        CancelBetRes getBalanceRes = new CancelBetRes();
        getBalanceRes.setStatusCode(S128BetErrorCodeEnum.SUCCESS.getCode());
        getBalanceRes.setStatusText(S128BetErrorCodeEnum.SUCCESS.getMessage());
        return getBalanceRes;
    }

    public static CancelBetRes fail(S128BetErrorCodeEnum errorCodeEnum, String errorMsg) {
        CancelBetRes getBalanceRes = new CancelBetRes();
        getBalanceRes.setStatusCode(errorCodeEnum.getCode());
        getBalanceRes.setStatusText(errorCodeEnum.getMessage());
        if (StringUtils.isNotEmpty(errorMsg)){
            getBalanceRes.setStatusText(errorMsg);
        }
        return getBalanceRes;
    }

}
