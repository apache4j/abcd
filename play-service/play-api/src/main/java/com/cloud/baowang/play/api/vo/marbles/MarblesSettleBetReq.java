package com.cloud.baowang.play.api.vo.marbles;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文档参考的是Sample Input (彩票 Lottery) – Commission 示例
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarblesSettleBetReq {



    // 需要求取余额的产品钱包
    @JsonProperty("ProductWallet")
    private String productWallet;

    // 玩家账号。此账号必须与营运商创建于IMOne 端的玩家账号相同。
    @JsonProperty("Transactions")
    private List<SettleBetReq> transactions;



}
