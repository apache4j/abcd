package com.cloud.baowang.play.game.omg.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OmgOrderDataRespVO {

    // 玩家昵称
    private String account;
    // 玩家id
    @JsonProperty("account_id")
    private String accountId;
    // 游戏id
    @JsonProperty("game_id")
    private String gameId;
    // 牌局id
    @JsonProperty("round_id")
    private String roundId;
    // 牌局id(字符串格式)
    @JsonProperty("round_id_str")
    private String roundIdStr;
    // 游戏初始金额
    @JsonProperty("enter_money")
    private BigDecimal enterMoney;
    // 结算之后玩家身上的钱
    @JsonProperty("after_settlement_money")
    private BigDecimal afterSettlementMoney;
    // 下注金额
    private BigDecimal bet;
    // 派奖金额
    private BigDecimal win;
    // 时间戳
    @JsonProperty("create_time")
    private Long createTime;
    // 注单记录id
    private String id;
    // 注单记录id
    @JsonProperty("id_str")
    private String idStr;
    // 上级牌局id
    @JsonProperty("parent_id")
    private String parentId;
    // 上级牌局id(字符串格式）
    @JsonProperty("parent_id_str")
    private String parentIdStr;
    // 0.正常 1.连消 2.免费旋转 3.重转 4.高倍
    @JsonProperty("small_game_type")
    private Integer smallGameType;
    // 是否是购买小游戏 0不是 1是
    private Integer fb;
    // 1:spribe
    // 2:PG
    // 3:JILI
    // 4:PP_MAX  (支持全货币)
    // 5:OMG
    // 6:Mini Game
    // 8:hacksaw
    // 9:PP（支持巴西币）
    private Integer platform;

}
