package com.cloud.baowang.play.game.zf.openApi.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZFRespVO<T> implements Serializable {
    @JsonProperty("ErrorCode")
    private String errorCode;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("Data")
    private T data;

}
