package com.cloud.baowang.play.api.vo.ldg;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LgdDataResp {

    // 币别
    private String currency;
    // 线注
    private String lb = "L0";
    // 玩家余额
    private String playerPrice;
    // 玩家账号
    private String playerName;
    // 授权令牌
    private String token;
    // betId
    private String id;
    // 签名
    private String sign;
    // 请求内容
    private String method;
    // 处理状态
    private String processStatus;

}
