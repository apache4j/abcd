package com.cloud.baowang.play.api.vo.fc.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BetNInfoReq {

    @Schema(title = "游戏记录编号(唯一码)，长度 24 码")
    String RecordID;

    @Schema(title = "交易单号(唯一码)，长度24码")
    String BankID;

    @Schema(title = "玩家账号")
    String MemberAccount;

    @Schema(title = "币别")
    String Currency;

    @Schema(title = "游戏编号")
    Integer GameID;

    @Schema(title = "游戏类型")
    Integer GameType;

    @Schema(title = "老虎机游戏是否有使用购买免费游戏功能")
    Boolean isBuyFeature;

    @Schema(title = "判断此局是否有使用道具卡片")
    Boolean isUsingProps;

    @Schema(title = "押注金额")
    BigDecimal Bet;

    @Schema(title = "游戏赢分")
    BigDecimal Win;

    @Schema(title = "彩金赢分")
    BigDecimal JPBet;

    @Schema(title = "押注金额")
    BigDecimal JPPrize;

    @Schema(title = "游戏彩金贡献")
    BigDecimal GameJPBet;

    @Schema(title = "游戏中奖彩金")
    BigDecimal GameJPPrize;

    @Schema(title = "游戏中奖彩金")
    BigDecimal NetWin;

    @Schema(title = "实际押注金额 (仅推币机与捕鱼机)")
    BigDecimal RequireAmt;

    @Schema(title = "游戏时间(年-月-日 时:分:秒)")
    Date GameDate;

    @Schema(title = "交易建立时间(年-月-日 时:分:秒)")
    Date CreateDate;

    @Schema(title = "交易建立时间(年-月-日 时:分:秒)")
    Long Ts;

}
