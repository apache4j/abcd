package com.cloud.baowang.play.api.vo.marbles;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 彩票
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarblesPlaceBetReq {



    // 需要求取余额的产品钱包
    @JsonProperty("ProductWallet")
    private String productWallet;

    // 玩家账号。此账号必须与营运商创建于IMOne 端的玩家账号相同。
    @JsonProperty("SessionToken")
    private String sessionToken;

    // 玩家注册于 IMOne 端的币别。
    @JsonProperty("Transactions")
    private List<PlaceBet> transactions;


}
