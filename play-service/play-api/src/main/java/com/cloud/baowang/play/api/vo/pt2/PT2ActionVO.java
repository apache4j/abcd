package com.cloud.baowang.play.api.vo.pt2;


import com.cloud.baowang.play.api.vo.pt2.vo.BonusChange;
import com.cloud.baowang.play.api.vo.pt2.vo.GameMetadata;
import com.cloud.baowang.play.api.vo.pt2.vo.JackpotContribution;
import lombok.Data;

import java.util.List;

@Data
public class PT2ActionVO extends PT2BaseVO{

    //服务器中的交易代码。

    private String amount;

    //资金变动
    private List<String> internalFundChanges;


    private String betDetails;

    //投注所在的真人游戏桌的额外信息。
    private String liveTableDetails;

    // 当前投注扣除的玩家余额奖励
    private BonusChange bonusChanges;

    //正在玩的游戏的额外信息
    private GameMetadata gameMetadata;

    //多个累积奖金的贡献信息
    private List<JackpotContribution> jackpotContributions;
}
