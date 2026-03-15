package com.cloud.baowang.play.api.vo.spade.res;

import com.cloud.baowang.common.core.serializer.BigDecimalTwoDecimalSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SpadeTransferRes extends SpadeBaseRes {

    String transferId;
//    String merchantCode;
    String merchantTxId;
    String acctId;
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    BigDecimal balance;

    String serialNo;
}
