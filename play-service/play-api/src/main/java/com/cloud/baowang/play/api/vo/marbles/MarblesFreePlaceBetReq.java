package com.cloud.baowang.play.api.vo.marbles;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarblesFreePlaceBetReq {



    // 需要求取余额的产品钱包
    @JsonProperty("Transactions")
    private List<FreePlaceBet> transactions;

    // 会话
    @JsonProperty("SessionToken")
    private String sessionToken;

    // 所投注的产品钱包。
    @JsonProperty("ProductWallet")
    private String productWallet;



}
