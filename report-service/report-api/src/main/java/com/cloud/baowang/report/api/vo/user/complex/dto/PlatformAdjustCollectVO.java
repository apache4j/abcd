package com.cloud.baowang.report.api.vo.user.complex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
    平台币上下分汇总
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "会员投注信息")
public class PlatformAdjustCollectVO {
    private String date;
    private String siteCode;
    private String currency;
    /** 上下分总额*/
    private BigDecimal platformTotalAdjust;

    /** 上分总额*/
    private BigDecimal platformAddAmount ;

    /** 上分人数*/
    private Integer platformAddPeopleNum;

    /** 下分总额*/
    private BigDecimal platformReduceAmount ;

    /** 下分人数*/
    private Integer platformReducePeopleNums ;


}
