package com.cloud.baowang.play.api.vo.acelt;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ACELTAccountCheangeRes extends ACELTBaseReq {


    /**
     * 用户名
     */
    private String username;

    /**
     * 交易凭证
     */
    private String transferReference;

    /**
     * 收入或支出（总）
     */
    private Integer inOut;

    /**
     * currencyCode
     */
    private String currencyCode;

    /**
     * 交易金额
     */
    private BigDecimal tradeAmount;


    /**
     * 交易前金额
     */
    private BigDecimal beforeAmount;


    /**
     * 交易后金额
     */
    private BigDecimal afterAmount;



}
