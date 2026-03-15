package com.cloud.baowang.play.api.vo.jili.req;

import com.cloud.baowang.play.api.vo.jili.JILIBaseReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;

@Data
@NoArgsConstructor
public class JILIRollbackReq extends JILIBaseReq {

    String transactionId;
    String betId;
    String externalTransactionId;
    String roundId;
    String gameCode;
    Long timestamp;

    public boolean isValid() {
        return isNotNullOrEmpty(transactionId) &&
                isNotNullOrEmpty(betId) &&
                isNotNullOrEmpty(externalTransactionId) &&
                isNotNullOrEmpty(roundId) &&
                isNotNullOrEmpty(gameCode) &&
                timestamp!=null &&
                isNotNullOrEmpty(super.getTraceId()) &&
                isNotNullOrEmpty(super.getCurrency()) &&
                isNotNullOrEmpty(super.getUsername());
    }

    // Helper method to check if a string is not null or empty
    private boolean isNotNullOrEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public LinkedHashMap<String, Object> getMap(){

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("traceId", this.getTraceId());
        map.put("transactionId", this.getTransactionId());
        map.put("betId", this.getBetId());
        map.put("externalTransactionId", this.getExternalTransactionId());
        map.put("roundId", this.getRoundId());
        map.put("gameCode", this.getGameCode());
        map.put("username", this.getUsername());
        map.put("currency", this.getCurrency());
        map.put("timestamp", this.getTimestamp());
        return map;

    }

}
