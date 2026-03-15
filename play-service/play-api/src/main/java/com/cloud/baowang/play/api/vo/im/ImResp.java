package com.cloud.baowang.play.api.vo.im;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class ImResp {

    private String status;
    // user_not_found 用户未发现
    // fail_balance 金额不足
    private String error;
    private String login;
    private BigDecimal balance;
    private String currency;


    public static ImResp success(){
        ImResp imResp = new ImResp();
        imResp.setStatus("success");
        return imResp;
    }

    public static ImResp err( String msg){
        ImResp imResp = new ImResp();
        imResp.setStatus("fail");
        imResp.setError(msg);
        return imResp;
    }

    public boolean isOk(){
        return "success".equalsIgnoreCase(this.status);
    }

}
