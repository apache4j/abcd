package com.cloud.baowang.agent.api.vo.agent;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "充提信息")
public class AgentUserDepositWithdrawVO {

    @Schema(description ="存款总额")
    private BigDecimal depositTotalAmount;

    @Schema(description ="存款次数")
    private Integer depositTotalNum;

    @Schema(description ="取款总额")
    private BigDecimal withdrawTotalAmount;

    @Schema(description ="取款次数")
    private Integer withdrawTotalNum;

    @Schema(description ="普通取款次数")
    private Integer normalWithdrawNum;

    @Schema(description ="大额取款次数")
    private Integer bigMoneyWithdrawNum;

    @Schema(description = "主货币币种")
    private String currency;

    @Schema(description = "客户端存款金额")
    private BigDecimal clientDepositAmount;

    @Schema(description = "客户端提款金额")
    private BigDecimal clientWithdrawAmount;

    @Schema(description = "人工加额(后台存款)")
    private BigDecimal manualUpAmount;

    @Schema(description = "人工减额(后台提款)")
    private BigDecimal manualDownAmount;

    @Schema(description = "上级代存")
    private BigDecimal depositAubordinatesAmount;

}
