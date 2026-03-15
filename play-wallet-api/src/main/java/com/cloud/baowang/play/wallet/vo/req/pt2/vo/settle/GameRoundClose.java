package com.cloud.baowang.play.wallet.vo.req.pt2.vo.settle;

import lombok.Data;

@Data
public class GameRoundClose {
    //游戏回合结束时间
    private String date;

    private String rngGeneratorId;

    private String rngSoftwareId;

    private GameRoundSummary gameRoundSummary;
}
