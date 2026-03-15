package com.cloud.baowang.play.game.fastSpin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BetInfo {

    String ticketId;
    String acctId;
    String ticketTime;
    String categoryId;
    String gameCode;
    String currency;
    BigDecimal betAmount;
    String result;
    BigDecimal winLoss;
    BigDecimal jackpotAmount;
    String betIp;
    Long luckyDrawId;
    Integer roundId;
    Integer sequence;

    String channel;

    BigDecimal Balance;
    BigDecimal jpWin;
    String referenceId;
    String gameFeature;
    Boolean completed;
}
