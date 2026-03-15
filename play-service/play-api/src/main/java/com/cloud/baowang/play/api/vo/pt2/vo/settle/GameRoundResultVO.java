package com.cloud.baowang.play.api.vo.pt2.vo.settle;


import com.cloud.baowang.play.api.vo.pt2.PT2BaseVO;
import lombok.Data;

@Data
public class GameRoundResultVO extends PT2BaseVO {
    //支付交易详情
    private Pay pay;

    //彩金详情
    private Jackpot jackpot;

    //如果设置，表示游戏回合已结束
    private GameRoundClose gameRoundClose;

    //如果已配置，表示指向详细游戏回合历史的URL
    private String gameHistoryUrl;

    //有关游戏回合发生的现场游戏桌的附加信息
    private LiveTableDetails liveTableDetails;

}
