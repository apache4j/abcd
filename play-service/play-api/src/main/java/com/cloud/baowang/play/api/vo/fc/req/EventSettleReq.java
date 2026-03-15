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
public class EventSettleReq {

    @Schema(title = "游戏记录编号(唯一码)，长度 24 码")
    String eventID;

    @Schema(title = "玩家账号")
    String MemberAccount;

    @Schema(title = "游戏编号")
    Integer GameID;

    @Schema(title = "交易单号(唯一码)，长度 24 码")
    String BankID;

    @Schema(title = "奖励编号")
    String trsID;

    @Schema(title = "派彩金额")
    BigDecimal points;

    @Schema(title = "交易建立时间(年-月-日 时:分:秒)")
    Date CreateDate;

    @Schema(title = "发送请求当下的时间")
    Long Ts;

}
