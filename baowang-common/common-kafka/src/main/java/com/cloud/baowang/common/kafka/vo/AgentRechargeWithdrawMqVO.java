package com.cloud.baowang.common.kafka.vo;


import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "代理累计充值添加请求对象")
@SuperBuilder
public class AgentRechargeWithdrawMqVO extends MessageBaseVO {



    /**
     * 站点编码
     */
    @Schema(title = "站点编码")
    private String siteCode;


    /**
     * 代理id
     */
    @Schema(title = "代理id")
    private String agentId;

    /**
     * 代理账号
     */
    @Schema(title = "代理账号")
    private String agentAccount;

    /**
     * 日期小时维度
     */
    @Schema(title = "日期小时维度")
    private Long dayHourMillis;

    /**
     * 站点日期 当天起始时间戳
     */
    /*@Schema(title = "日期小时维度")
    private Long dayMillis;
*/
    @Schema(title = "类型1存款 2取款")
    private String type;

    @Schema(title = "币种")
    private String currency;

    @Schema(title = "存取款方式ID")
    private String depositWithdrawWayId;

    @Schema(title = "充值提款 方式手续费")
    private BigDecimal wayFeeAmount;

    @Schema(title = "充值提款金额")
    private BigDecimal amount;

    @Schema(title = "充值提款代理手续费")
    private BigDecimal feeAmount;

    @Schema(title = "充值提款结算手续费")
    private BigDecimal settlementFeeAmount;

    @Schema(title = "大额存取款金额")
    private BigDecimal largeAmount;

}
