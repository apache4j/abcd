package com.cloud.baowang.play.game.v8.resp;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class V8OrderRespVO {
    @JsonProperty("m")
    private String m;
    @JsonProperty("d")
    private V8OrderDataRespVO d;
    @JsonProperty("s")
    private Integer s;

}
