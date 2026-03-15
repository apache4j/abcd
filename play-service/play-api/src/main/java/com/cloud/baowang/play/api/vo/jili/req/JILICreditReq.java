package com.cloud.baowang.play.api.vo.jili.req;

import com.cloud.baowang.play.api.vo.jili.JILIBaseReq;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

@Data
@NoArgsConstructor
public class JILICreditReq extends JILIBaseReq {

    String transactionId;
    String betId;
    Integer isRefund;
    BigDecimal amount;
    BigDecimal betAmount;
    BigDecimal winAmount;
    String roundId;
    BigDecimal effectiveTurnover;
    BigDecimal winLoss;
    BigDecimal jackpotAmount;
    String token;
    String gameCode;
    Long betTime;
    Long settledTime;
    Long timestamp;

    public boolean isValid() {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            return false; // Invalid if transactionId is null or empty
        }
        if (isRefund == null || isRefund > 1 || isRefund < 0) {
            return false; // Invalid if isRefund is null or empty
        }
        if (token == null || token.trim().isEmpty()) {
            return false; // Invalid if token is null or empty
        }
        if (gameCode == null || gameCode.trim().isEmpty()) {
            return false; // Invalid if gameCode is null or empty
        }
        if (betTime == null ) {
            return false; // Invalid if betTime is null or empty
        }
        // Check if BigDecimal fields are null or negative (if needed)
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            return false; // Invalid if amount is null or negative
        }
        if (betAmount == null || betAmount.compareTo(BigDecimal.ZERO) < 0) {
            return false; // Invalid if betAmount is null or negative
        }
        if (winAmount == null || winAmount.compareTo(BigDecimal.ZERO) < 0) {
            return false; // Invalid if winAmount is null or negative
        }
        if (effectiveTurnover == null ) {
            return false; // Invalid if effectiveTurnover is null or negative
        }
        if (winLoss == null) {
            return false; // Invalid if winLoss is null
        }if (timestamp == null) {
            return false; // Invalid if winLoss is null
        }
        if (super.getTraceId() == null || super.getTraceId().isEmpty()) return false;
        if (super.getCurrency() == null || super.getCurrency().isEmpty()) return false;
        return super.getUsername() != null && !super.getUsername().isEmpty();
    }

    public LinkedHashMap<String, Object> getMap(){

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("traceId", this.getTraceId());
        map.put("username", this.getUsername());
        map.put("transactionId", this.getTransactionId());
        if (this.getBetId()!=null){

            map.put("betId", this.getBetId());
        }
        map.put("roundId", this.getRoundId());

        map.put("isRefund", this.getIsRefund());

        map.put("amount", this.getAmount());
        map.put("betAmount", this.getBetAmount());

        map.put("winAmount", this.getWinAmount());
        map.put("effectiveTurnover", this.getEffectiveTurnover());
        map.put("winLoss", this.getWinLoss());
        if (this.getJackpotAmount()!=null){
            map.put("jackpotAmount", this.getJackpotAmount());
        }
        map.put("currency", this.getCurrency());
        map.put("token", this.getToken());
        map.put("gameCode", this.getGameCode());
        map.put("betTime", this.getBetTime());
        if (this.getSettledTime()!=null){
            map.put("settledTime", this.getSettledTime());
        }
        map.put("timestamp", this.getTimestamp());
        return map;

    }

}
