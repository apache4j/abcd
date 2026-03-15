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
public class BetReq {

    @Schema(title = "游戏记录编号(唯一码)，长度 24 码")
    String RecordID;

    @Schema(title = "下注编号(唯一码)，长度24码")
    String BetID;

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

    @Schema(title = "交易建立时间(年-月-日 时:分:秒)")
    Date CreateDate;

    @Schema(title = "发送请求当下的时间")
    Long Ts;

}
