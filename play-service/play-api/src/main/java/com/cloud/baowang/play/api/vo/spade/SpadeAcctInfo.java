package com.cloud.baowang.play.api.vo.spade;

import com.cloud.baowang.common.core.serializer.BigDecimalTwoDecimalSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpadeAcctInfo {

    //必须项
    String acctId;
    String userName;
    String currency;
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    BigDecimal balance;


}
