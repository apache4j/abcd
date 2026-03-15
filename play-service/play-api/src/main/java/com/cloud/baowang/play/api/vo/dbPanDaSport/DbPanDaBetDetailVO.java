package com.cloud.baowang.play.api.vo.dbPanDaSport;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbPanDaBetDetailVO {


    /**
     * 下注金额
     */
    private BigDecimal amount;

    /**
     * 注单号
     */
    private String orderNo;

}
