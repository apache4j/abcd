package com.cloud.baowang.play.api.vo.pt2.vo.rps;

import com.cloud.baowang.play.api.vo.pt2.vo.Balance;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PT2RspVO {
    private String requestId;
    private String username;
    private String permanentExternalToken;
    private String currencyCode;
    private String countryCode;
    private String error;

    //运营商钱包中的交易代码
    private String externalTransactionCode;

    //“yyyy-mm-dd hh24:mi:ss.SSS”，格林威治时间时区
    private String externalTransactionDate;

    private Balance balance;
}
