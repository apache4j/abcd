package com.cloud.baowang.user.api.vo.task.guess;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "竞猜排行榜响应")
public class GuessRankingListResVO {

    @Schema(title = "等级")
    private Integer level;

    @Schema(title = "玩家账号")
    private String account;

    @Schema(title = "玩家账号昵称")
    private String accountNickName;

    @Schema(title = "下注金额")
    private BigDecimal totalBetAmount;

    @Schema(title = "奖金")
    private BigDecimal prizePool;

}
