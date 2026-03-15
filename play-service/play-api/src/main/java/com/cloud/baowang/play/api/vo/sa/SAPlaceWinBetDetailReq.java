package com.cloud.baowang.play.api.vo.sa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SAPlaceWinBetDetailReq {

    /**
     * 投注编号
     */
    private Long betid;

    /**
     * 投注类型
     */
    private Integer bettype;

    /**
     * 投注金额
     */
    private BigDecimal betamount;

    /**
     * 输赢金额
     */
    private BigDecimal resultamount;

    /**
     * 独一的点数转移编号
     */
    private String txnid;

    /**
     * 投注来源
     */
    private Integer betsource;

    /**
     * 有效投注额 / 洗码量
     */
    private BigDecimal rolling;

}
