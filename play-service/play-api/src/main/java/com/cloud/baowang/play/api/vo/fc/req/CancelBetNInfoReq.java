package com.cloud.baowang.play.api.vo.fc.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelBetNInfoReq {

    @Schema(title = "交易单号(唯一值)，长度24码")
    String BankID;

    @Schema(title = "玩家账号")
    String MemberAccount;

    @Schema(title = "币别")
    String Currency;

    @Schema(title = "游戏编号")
    Integer GameID;

    @Schema(title = "发送请求当下的时间")
    Long Ts;

    @Schema(title = "游戏记录编号(唯一码)，长度 24 码")
    String RecordID;

    @Schema(title = "游戏中奖彩金")
    BigDecimal NetWin;
}
