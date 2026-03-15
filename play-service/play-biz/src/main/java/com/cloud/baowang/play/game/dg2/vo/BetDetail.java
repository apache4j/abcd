package com.cloud.baowang.play.game.dg2.vo;
import lombok.Data;
import java.util.Map;

@Data
public class BetDetail {
    private String info; // 附加信息
    private Map<String, Object> bets; // 各下注项，比如 player:100, tie:50
}

