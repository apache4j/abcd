package com.cloud.baowang.play.api.vo.marbles;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarblesRefundResp {

    @JsonProperty("Code")
    private int code;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("Results")
    private List<RefundBuilder> result;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
   public static class RefundBuilder {
       private int Code;

       private String Message;

       private String Balance;

       private String OperatorTransactionId;

       private String TransactionId;
   }
}
