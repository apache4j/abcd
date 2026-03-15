package com.cloud.baowang.play.api.vo.ace.res;

import com.cloud.baowang.play.api.vo.ace.req.ACEBaseReq;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ACEAuthenticateRes extends ACEBaseRes implements Serializable {

    @JsonProperty("playerID")
    Long playerID;

}
