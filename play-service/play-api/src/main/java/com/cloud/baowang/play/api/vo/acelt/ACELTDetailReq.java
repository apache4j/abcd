package com.cloud.baowang.play.api.vo.acelt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ACELTDetailReq {

    private String betNum;

    private String orderNo;

    private BigDecimal tradeAmount;
}
