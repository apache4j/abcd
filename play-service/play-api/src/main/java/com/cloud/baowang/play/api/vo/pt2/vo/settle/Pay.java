package com.cloud.baowang.play.api.vo.pt2.vo.settle;

import lombok.Data;

import java.util.List;

@Data
public class Pay {
    private String transactionCode;

    private String transactionDate;

    private String amount;

    //WIN: 玩家赢钱. REFUND: 退款给玩家
    private String type;

    // MAIN_BET: 主要下注. SIDE_BET: 边注
    private String betType;

    //资金变更
    private List<FundChange> internalFundChanges;

    private String relatedTransactionCode;
}
