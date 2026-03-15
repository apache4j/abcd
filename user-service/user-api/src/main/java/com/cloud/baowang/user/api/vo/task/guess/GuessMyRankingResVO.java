package com.cloud.baowang.user.api.vo.task.guess;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "我的排行信息响应")
public class GuessMyRankingResVO {

    @Schema(title = "用户头像")
    private String accountPicture;

    @Schema(title = "账号")
    private String account;

    @Schema(title = "我的排行位置")
    private Integer rank;

    @Schema(title = "下注金额")
    private BigDecimal totalBetAmount;

    @Schema(title = "还需投注金额")
    private BigDecimal BetAmountDistance;

}
