package com.cloud.baowang.play.api.vo.jili.req;

import com.cloud.baowang.play.api.vo.jili.JILIBaseReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

@Data
@NoArgsConstructor
public class JILIDebitReq extends JILIBaseReq {

    String transactionId;
    String roundId;
    Integer takeAll;
    BigDecimal amount;
    String gameCode;
    String token;
    Long timestamp;

    public boolean isValid() {
        if (transactionId == null || transactionId.isEmpty()) return false;
        if (gameCode == null || gameCode.isEmpty()) return false;
        if (roundId == null || roundId.isEmpty()) return false;
        if (timestamp == null || timestamp == 0) return false;
        if (amount == null) return false;
        if (takeAll == null || takeAll > 1 || takeAll < 0) return false;
        if (super.getTraceId() == null || super.getTraceId().isEmpty()) return false;
        if (super.getCurrency() == null || super.getCurrency().isEmpty()) return false;
        return super.getUsername() != null && !super.getUsername().isEmpty();
    }

    public LinkedHashMap<String, Object> getMap(){

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("traceId", this.getTraceId());
        map.put("username", this.getUsername());
        map.put("transactionId", this.getTransactionId());
        map.put("roundId", this.getRoundId());
        if (this.getTakeAll()!=null){
            map.put("takeAll", this.getTakeAll());
        }
        map.put("amount", this.getAmount());
        map.put("currency", this.getCurrency());
        map.put("gameCode", this.getGameCode());
        if (this.getToken()!=null){
            map.put("token", this.getToken());
        }
        map.put("timestamp", this.getTimestamp());
        return map;

    }
}
