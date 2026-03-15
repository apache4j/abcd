package com.cloud.baowang.play.wallet.vo.req.pt2.vo.settle;

import com.cloud.baowang.play.wallet.vo.req.pt2.PT2BaseVO;
import lombok.Data;

@Data
public class TransferFundsVO  extends PT2BaseVO {


    private String requestId;

    private String username;

    //交易代码。
    private String transactionCode;
    //交易时间
    private String transactionDate;

    private String amount;

    //枚举值： //BONUS
    /**
     * FREESPIN: 免费旋转奖金
     * GOLDENCHIP: 黄金筹码奖金
     * PREWAGER: 预赌奖金
     * AFTERWAGER: 赌注奖金
     * CASH: 现金奖金
     * FREESPIN2: 免费旋转奖金2
     * POKER: 扑克奖金
     */
    private String type;

    //奖金资金来源的附加信息
    private BonusInfo bonusInfo;
}
