package com.cloud.baowang.play.api.vo.jili.req;

import com.cloud.baowang.play.api.vo.jili.JILIBaseReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

@Data
@NoArgsConstructor
public class JILIAdjustmentReq extends JILIBaseReq {

    String transactionId;
    String externalTransactionId;
    String roundId;
    BigDecimal amount;
    String gameCode;
    Long timestamp;


    public boolean isValid() {
        if (transactionId == null || transactionId.isEmpty()) return false;
        if (externalTransactionId == null || externalTransactionId.isEmpty()) return false;
        if (gameCode == null || gameCode.isEmpty()) return false;
        if (roundId == null || roundId.isEmpty()) return false;
        if (timestamp == null || timestamp == 0) return false;
        if (amount == null )  return false;
        if (super.getTraceId() == null || super.getTraceId().isEmpty()) return false;
        if (super.getCurrency() == null || super.getCurrency().isEmpty()) return false;
        return super.getUsername() != null && !super.getUsername().isEmpty();
    }

    public LinkedHashMap<String, Object> getMap(){

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("traceId", this.getTraceId());
        map.put("username", this.getUsername());
        map.put("transactionId", this.getTransactionId());
        map.put("externalTransactionId", this.getExternalTransactionId());
        map.put("roundId", this.getRoundId());
        map.put("amount", this.getAmount());
        map.put("currency", this.getCurrency());
        map.put("gameCode", this.getGameCode());
        map.put("timestamp", this.getTimestamp());
        return map;

    }

}
