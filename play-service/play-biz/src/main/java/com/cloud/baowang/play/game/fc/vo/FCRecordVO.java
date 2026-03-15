package com.cloud.baowang.play.game.fc.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FCRecordVO {

    @Schema(title = "游戏记录编号(唯一码)，长度 24 码")
    String recordID;

    @Schema(title = "玩家账号")
    String account;

    @Schema(title = "游戏编号")
    Integer gameID;

    @Schema(title = "游戏类型")
    Integer gametype;

    @Schema(title = "押注金额")
    BigDecimal bet;

    @Schema(title = "净输赢点数")
    BigDecimal winlose;

    @Schema(title = "赢分点数")
    BigDecimal prize;

    @Schema(title = "退还金额 (除 Lucky 9 游戏，其余游戏 refund = win)")
    BigDecimal refund;

    @Schema(title = "有效投注 (除 Lucky9 游戏，其余游戏 Bet = validBet)")
    BigDecimal validBet;

    @Schema(title = "抽水金额 (除 Lucky9 游戏，其余游戏为 0)")
    BigDecimal commission;

    @Schema(title = "游戏内彩金模式")
    Integer jpmode;

    @Schema(title = "彩金模式")
    Integer inGameJpmode;

    @Schema(title = "彩金点数")
    BigDecimal jppoints;

    @Schema(title = "彩金抽水(支持到小数第六位)")
    BigDecimal jptax;

    @Schema(title = "游戏内彩金贡献")
    BigDecimal inGameJptax;

    @Schema(title = "游戏内中奖彩金")
    BigDecimal inGameJppoints;

    @Schema(title = "下注前点数")
    BigDecimal before;

    @Schema(title = "下注后点数")
    BigDecimal after;

    @Schema(title = "交易建立时间(年-月-日 时:分:秒)")
    String bdate;

    @Schema(title = "(老虎机游戏)是否有使用购买免费游戏功能")
    Boolean isBuyFeature;

    @Schema(title = "交易建立时间(年-月-日 时:分:秒)")
    Integer gameMode;

}
