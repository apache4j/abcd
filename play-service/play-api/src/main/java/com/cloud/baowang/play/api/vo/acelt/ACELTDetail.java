package com.cloud.baowang.play.api.vo.acelt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ACELTDetail {

    private Integer transferType;    // 交易类型
    private String orderNo;          // 订单号
    private String gameCode;         // 彩种编号
    private String playCode;         // 玩法编号
    private BigDecimal tradeAmount; // 交易金额
    private Integer inOut;           // 收入或支出

}
