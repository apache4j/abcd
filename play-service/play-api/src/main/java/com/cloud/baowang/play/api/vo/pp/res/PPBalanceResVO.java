package com.cloud.baowang.play.api.vo.pp.res;


import com.cloud.baowang.common.core.serializer.BigDecimalTwoDecimalSerializer;
import com.cloud.baowang.play.api.vo.pp.PPBaseResVO;
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
public class PPBalanceResVO extends PPBaseResVO {

    String currency;
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class)
    BigDecimal cash;
    BigDecimal bonus;

}
