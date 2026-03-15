package com.cloud.baowang.play.api.vo.jili.req;

import com.cloud.baowang.play.api.vo.jili.JILIBaseReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

@Data
@NoArgsConstructor
public class JILIBetResultReq extends JILIBaseReq {

    String transactionId;
    String betId;
    String externalTransactionId;
    String roundId;
    BigDecimal betAmount;
    BigDecimal winAmount;
    BigDecimal effectiveTurnover;
    BigDecimal winLoss;
    BigDecimal jackpotAmount;
    String resultType;
    String gameCode;
    String token;
    Long betTime;
    Long settledTime;
    Integer isFreespin;
    Integer isEndRound;


    public boolean isValid() {
        return transactionId != null && !transactionId.isEmpty() &&
                betId != null && !betId.isEmpty() &&
                externalTransactionId != null && !externalTransactionId.isEmpty() &&
                roundId != null && !roundId.isEmpty() &&
                betAmount != null  && // Assuming betAmount should be greater than 0
                winAmount != null  && // Assuming winAmount can be 0 or more
                effectiveTurnover != null  &&
                winLoss != null &&
                resultType != null && !resultType.isEmpty() &&
                gameCode != null && !gameCode.isEmpty() &&
                betTime != null &&
                isFreespin != null &&
                isEndRound != null &&
                super.getTraceId() != null && !super.getTraceId().isEmpty() &&
                token != null && !token.isEmpty() &&
                super.getCurrency() != null && !super.getCurrency().isEmpty() &&
                super.getUsername() != null && !super.getUsername().isEmpty();
    }


    public LinkedHashMap<String, Object> getMap(){

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("traceId", this.getTraceId());
        map.put("username", this.getUsername());
        map.put("transactionId", this.getTransactionId());
        map.put("betId", this.getBetId());
        map.put("externalTransactionId", this.getExternalTransactionId());
        map.put("roundId", this.getRoundId());
        map.put("betAmount", this.getBetAmount());
        map.put("winAmount", this.getWinAmount());
        map.put("effectiveTurnover", this.getEffectiveTurnover());
        map.put("winLoss", this.getWinLoss());
        if (this.getJackpotAmount()!=null){
            map.put("jackpotAmount", this.getJackpotAmount());
        }
        map.put("resultType", this.getResultType());
        map.put("isFreespin", this.getIsFreespin());
        map.put("isEndRound", this.getIsEndRound());
        map.put("currency", this.getCurrency());
        map.put("token", this.getToken());
        map.put("gameCode", this.getGameCode());
        map.put("betTime", this.getBetTime());
        if (this.getSettledTime()!=null){
            map.put("settledTime", this.getSettledTime());
        }
        return map;

    }


}
