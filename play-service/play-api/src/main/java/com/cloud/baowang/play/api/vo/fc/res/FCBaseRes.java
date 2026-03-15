package com.cloud.baowang.play.api.vo.fc.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FCBaseRes {

    @JsonProperty("Result")
    Integer Result;

    @JsonProperty("ErrorText")
    String ErrorText;

    @JsonProperty("MainPoints")
    BigDecimal MainPoints;
}
