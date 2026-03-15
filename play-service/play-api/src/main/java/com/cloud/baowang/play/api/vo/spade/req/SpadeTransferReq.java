package com.cloud.baowang.play.api.vo.spade.req;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpadeTransferReq {
    String transferId;
    String acctId;
    String currency;
    BigDecimal amount;
    Integer type;
    String channel;
    String gameCode;
    String ticketId;
    String referenceId;
    String specialGame;
    String refTicketIds;
    String playerIp;
    String gameFeature;
    String transferTime;
    String merchantCode;
    String serialNo;
    String roundId;
    String siteId;
    String body;
    String digest;
}
