package com.cloud.baowang.user.api.vo.task.guess;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "竞赛信息响应")
public class GuessDailyResVO {

    @Schema(title = "规则说明")
    private String rule;

    @Schema(title = "比赛奖池")
    private BigDecimal prizePool;

    @Schema(title = "上届冠军")
    private GuessLastChampionResVO guessLastChampion;

    @Schema(title = "我的排行信息")
    private GuessMyRankingResVO myRankingInfo;

    @Schema(title = "排行榜列表")
    private GuessRankingListResVO rankingList;

}
