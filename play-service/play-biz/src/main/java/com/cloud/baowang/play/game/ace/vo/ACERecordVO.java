package com.cloud.baowang.play.game.ace.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ACERecordVO {

    Long playerID;
    Long extPlayerID;
    String gameID;
    String methodType;
    String playSessionID;
    String referenceID;
    String status;
    Date created;
    Date updated;

    BigDecimal betAmount;
    BigDecimal winAmount;
    Long jackpotModule;
    BigDecimal jackpotContributionAmt;
    String currency;
    String resultUrl;
    String roundDetails;
    String platform;

}
