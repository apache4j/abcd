package com.cloud.baowang.play.api.vo.marbles;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarblesReq {



    // 需要求取余额的产品钱包
    @JsonProperty("ProductWallet")
    private String productWallet;

    // 玩家账号。此账号必须与营运商创建于IMOne 端的玩家账号相同。
    @JsonProperty("PlayerId")
    private String playerId;

    // 玩家注册于 IMOne 端的币别。
    @JsonProperty("Currency")
    private String currency;


}
