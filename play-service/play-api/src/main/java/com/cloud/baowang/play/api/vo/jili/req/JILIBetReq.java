package com.cloud.baowang.play.api.vo.jili.req;

import com.cloud.baowang.play.api.vo.jili.JILIBaseReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

@Data
@NoArgsConstructor
public class JILIBetReq extends JILIBaseReq {

    String transactionId;
    String betId;
    String externalTransactionId;
    BigDecimal amount;
    String gameCode;
    String roundId;
    String token;
    Long timestamp;

    public boolean isValid() {
        if (transactionId == null || transactionId.isEmpty()) return false;
        if (betId == null || betId.isEmpty()) return false;
        if (externalTransactionId == null || externalTransactionId.isEmpty()) return false;
        if (amount == null ) return false;
        if (super.getCurrency() == null || super.getCurrency().isEmpty()) return false;
        if (gameCode == null || gameCode.isEmpty()) return false;
        if (roundId == null || roundId.isEmpty()) return false;
        if (timestamp == null || timestamp == 0) return false;
        if (super.getTraceId() == null || super.getTraceId().isEmpty()) return false;
        if (token == null || token.isEmpty()) return false;
        return super.getUsername() != null && !super.getUsername().isEmpty();
    }

    public LinkedHashMap<String, Object> getMap(){

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        map.put("traceId", this.getTraceId());
        map.put("username", this.getUsername());
        map.put("transactionId", this.getTransactionId());
        map.put("betId", this.getBetId());
        map.put("externalTransactionId", this.getExternalTransactionId());
        map.put("amount", this.getAmount());
        map.put("currency", this.getCurrency());
        map.put("token", this.getToken());
        map.put("gameCode", this.getGameCode());
        map.put("roundId", this.getRoundId());
        map.put("timestamp", this.getTimestamp());

        return map;

    }

}
