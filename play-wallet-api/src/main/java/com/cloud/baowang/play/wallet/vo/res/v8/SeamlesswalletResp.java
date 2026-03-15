package com.cloud.baowang.play.wallet.vo.res.v8;


import com.cloud.baowang.play.wallet.enums.MarblesRespErrEnums;
import com.cloud.baowang.play.wallet.enums.V8RespErrEnums;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeamlesswalletResp {

    private String m = "/channelHandle";

    private String s;

    private D d;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class D{
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private Integer code;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String account;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private Float money;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private Integer status;
    }

    public static SeamlesswalletResp success(V8RespErrEnums errEnums, String s, String account, Float money) {
        SeamlesswalletResp resp = new SeamlesswalletResp();
        D d = new D();
        d.setCode(errEnums.getCode());
        d.setAccount(account);
        d.setMoney(money);
        resp.setD(d);
        resp.setS(s);
        return resp;
    }

    public static SeamlesswalletResp success() {
        SeamlesswalletResp s = new SeamlesswalletResp();
        D d = new D();
        d.setCode(1);
        s.setD(d);
        return s;
    }

    public static SeamlesswalletResp success(V8RespErrEnums errEnums, Integer status) {
        SeamlesswalletResp s = new SeamlesswalletResp();
        D d = new D();
        d.setCode(errEnums.getCode());
        d.setStatus(status);
        s.setD(d);
        return s;
    }


    /**
     * 查询状态
     */
    public static SeamlesswalletResp successStatus(V8RespErrEnums errEnums,Integer status, String agent) {
        SeamlesswalletResp s = new SeamlesswalletResp();
        D d = new D();
        s.setS(agent);
        d.setCode(errEnums.getCode());
        d.setStatus(status);
        s.setD(d);
        return s;
    }

    /**
     * 返回余额
     */
    public static SeamlesswalletResp successBet(V8RespErrEnums errEnums,String agent, String account, Float money) {
        SeamlesswalletResp s = new SeamlesswalletResp();
        D d = new D();
        s.setS(agent);
        d.setCode(errEnums.getCode());
        d.setAccount(account);
        d.setMoney(money);
        s.setD(d);
        return s;
    }

    public static SeamlesswalletResp successCancel(V8RespErrEnums errEnums,String agent, Integer status) {
        SeamlesswalletResp s = new SeamlesswalletResp();
        D d = new D();
        s.setS(agent);
        d.setCode(errEnums.getCode());
        d.setStatus(status);
        s.setD(d);
        return s;
    }




}
