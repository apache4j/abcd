package com.cloud.baowang.play.api.vo.omg;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OmgReq {

    // 商户的唯一标识
    @JsonProperty("app_id")
    private String appId;

    // 时间戳(秒)
    private Integer timestamp ;

    // 接入方从ingame接口传递上来的token参数
    @JsonProperty("operator_player_session")
    private String operatorPlayerSession;

    @JsonProperty("game_id")
    private Integer gameId;

    private String ip;
    @JsonProperty("custom_parameter")
    private String customParameter;

    @JsonProperty("uname")
    private String uname;

    @JsonProperty("player_login_token")
    private String playerLoginToken;
    // 余额变动(正,负)
    @JsonProperty("money")
    private BigDecimal money;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("bet")
    private BigDecimal bet;

    // 1:游戏下注; 2:取消下注; 3:游戏返奖; 4:验证对局结束; 5:LuckWin游戏宝箱下发奖励；6:Future游戏持仓费用扣减
    private Integer type;

    // 当前局是否结束
    @JsonProperty("end_round")
    private Boolean endRound;

    // 仅当type=2时有值，表示取消的是哪一笔订单
    @JsonProperty("cancel_order_id")
    private Integer cancelOrderId;

    // 仅当type=3时有值，表示这一轮所有注单的派奖金额
    @JsonProperty("award_order_ids")
    private Object award_order_ids;

    @JsonProperty("round_id")
    private String roundId;

}
