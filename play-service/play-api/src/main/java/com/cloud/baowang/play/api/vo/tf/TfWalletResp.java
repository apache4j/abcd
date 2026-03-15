package com.cloud.baowang.play.api.vo.tf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TfWalletResp {

    @JsonProperty("balance")
    private BigDecimal balance;
}
