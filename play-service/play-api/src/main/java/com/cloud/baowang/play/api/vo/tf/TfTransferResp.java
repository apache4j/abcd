package com.cloud.baowang.play.api.vo.tf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TfTransferResp {

    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message;
}
