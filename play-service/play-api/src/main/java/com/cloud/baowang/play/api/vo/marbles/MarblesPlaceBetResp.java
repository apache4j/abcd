package com.cloud.baowang.play.api.vo.marbles;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarblesPlaceBetResp {

    @JsonProperty("Code")
    private int code;
    @JsonProperty("Message")
    private String message;

    @JsonProperty("Results")
    private List<PlaceBetBuilder> result;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
   public static class PlaceBetBuilder {
       @JsonProperty("Code")
       private int code;

       @JsonProperty("Message")
       private String message;

       @JsonProperty("Balance")
       private String balance;

       @JsonProperty("OperatorTransactionId")
       private String operatorTransactionId;

       @JsonProperty("TransactionId")
       private String transactionId;
   }
}
