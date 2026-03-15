package com.cloud.baowang.play.api.vo.fc.req;

import com.alibaba.fastjson2.JSONArray;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettleReq {

    @Schema(title = "游戏记录编号(唯一码)，长度 24 码")
    String RecordID;

    @Schema(title = "交易单号(唯一码)，长度 24 码")
    String BankID;

    @Schema(title = "玩家账号")
    String MemberAccount;

    @Schema(title = "币别")
    String Currency;

    @Schema(title = "游戏编号")
    Integer GameID;

    @Schema(title = "游戏类型")
    Integer GameType;

    @Schema(title = "押注金额")
    BigDecimal Bet;

    @Schema(title = "有效投注")
    BigDecimal ValidBet;

    @Schema(title = "游戏赢分")
    BigDecimal Win;

    @Schema(title = "抽水")
    BigDecimal Commission;

    @Schema(title = "总输赢")
    BigDecimal NetWin;

    @Schema(title = "要返还给玩家的金额(永远为正数)")
    BigDecimal Refund;

    @Schema(title = "此次结算了哪些下注; 对应至下注的BetID")
    JSONArray SettleBetIDs;

    @Schema(title = "游戏时间(年-月-日 时:分:秒)")
    Date GameDate;

    @Schema(title = "交易建立时间(年-月-日 时:分:秒)")
    Date CreateDate;

    @Schema(title = "发送请求当下的时间")
    Long Ts;

}
