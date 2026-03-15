package com.cloud.baowang.report.api.vo.rechagerwithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Builder
@Schema(title = "会员存取款日报表对象")
public class ReportUserDepositWithdrawVO {

    /**
     * id
     */
    @Schema(description = "ID")
    private Long id;


    /**
     * 日期
     */
    @Schema(description ="日期")
    private Long day;

    /**
     * 币种
     */
    @Schema(description ="币种")
    private String currencyCode;

    @Schema(description ="平台币币种")
    private String platformCurrencyCode;


    /**
     * 存款人数
     */
    @Schema(description ="存款人数")
    private Integer depositorsNums;

    /**
     * 存款次数
     */
    @Schema(description ="存款次数")
    private Integer depositTimes;

    /**
     * 存款总金额
     */
    @Schema(description ="存款总金额")
    private BigDecimal depositTotalAmount;



    /**
     * 上级转入人数
     */
    @Schema(description ="上级转入人数")
    private Integer depositSubordinatesNums;

    @Schema(description ="上级转入次数")
    private Integer depositSubordinatesTimes;

    /**
     * 上级转入总额
     */
    @Schema(description ="上级转入总额")
    private BigDecimal depositSubordinatesAmount;

    /**
     * 取款人数
     */
    @Schema(description ="取款人数")
    private Integer withdrawalsNums;

    /**
     * 大额取款人数
     */
    @Schema(description ="大额取款人数")
    private Integer bigMoneyWithdrawalsNums;

    /**
     * 大额取款次数
     */
    @Schema(description ="取款次数")
    private Integer withdrawTimes;

    /**
     * 大额取款次数
     */
    @Schema(description ="大额取款次数")
    private Integer bigMoneyWithdrawTimes;

    /**
     * 取款总金额
     */
    @Schema(description ="取款总金额")
    private BigDecimal withdrawTotalAmount;

    /**
     * 大额取款总金额
     */
    @Schema(description ="大额取款总金额")
    private BigDecimal bigMoneyWithdrawAmount;

    /**
     * 存取款差额
     */
    @Schema(description ="存取款差额")
    private BigDecimal depositWithdrawalDifference;

    /**
     * 创建时间
     */
    private Long createdTime;
}
