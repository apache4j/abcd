package com.cloud.baowang.play.api.vo.marbles;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundReq {

    @JsonProperty("TransactionId")
    private String transactionId;

    @JsonProperty("RefTransactionId")
    private String refTransactionId;

    @JsonProperty("TransactionType")
    private String transactionType;

    @JsonProperty("PlayerId")
    private String playerId;

    @JsonProperty("ProviderPlayerId")
    private String providerPlayerId;

    @JsonProperty("Provider")
    private String provider;

    @JsonProperty("TimeStamp")
    private String timeStamp;
}
