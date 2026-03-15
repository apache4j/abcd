package com.cloud.baowang.report.api.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "会员累计充值查询返回对象")
public class ReportUserRechargeResponseVO implements Serializable {


    /**
     * 会员id
     */
    @Schema(title = "会员id")
    private String userId;

    /**
     * 站点编码
     */
    @Schema(title = "站点编码")
    private String siteCode;

    /**
     * 会员账号
     */
    @Schema(title = "会员账号")
    private String userAccount;

    /**
     * 币种
     */
    @Schema(title = "币种")
    private String currency;

    /**
     * 日期小时维度
     */
    @Schema(title = "日期小时维度")
    private Long dayHourMillis;
    /**
     * 站点日期 当天起始时间戳
     */
    @Schema(title = "站点日期 当天起始时间戳")
    private Long dayMillis;

    /**
     * 充值金额
     */
    @Schema(title = "充值金额")
    private BigDecimal rechargeAmount;
}
