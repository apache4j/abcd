package com.cloud.baowang.play.api.vo.tf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TfTransferReq {

    @JsonProperty("loginName")
    private String loginName;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("placeBet")
    private Boolean placeBet;

    @JsonProperty("ticketNum")
    private String ticketNum;

    @JsonProperty("description")
    private String description;

    @JsonProperty("ticketDetail")
    private TfOrderInfoVO ticketDetail;

}
