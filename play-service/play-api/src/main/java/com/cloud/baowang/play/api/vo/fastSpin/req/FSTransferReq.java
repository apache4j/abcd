package com.cloud.baowang.play.api.vo.fastSpin.req;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FSTransferReq {

    String acctId;
    String transferId;
    String merchantCode;
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
    String serialNo;

    String body;

}
