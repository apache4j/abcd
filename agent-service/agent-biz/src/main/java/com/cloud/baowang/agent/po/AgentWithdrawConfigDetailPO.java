package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 代理提款金额设置
 */
@Data
@Accessors(chain = true)
@TableName("agent_withdraw_config_detail")
@Schema(description = "代理提款金额设置")
public class AgentWithdrawConfigDetailPO extends BasePO {
    @Schema(description = "配置表id")
    private String configId;

    @Schema(description = "币种")
    private String currency;

    @Schema(title = "大额提款标记金额")
    @NotNull(message = "大额提款标记金额不能为空")
    private BigDecimal largeWithdrawMarkAmount;

    @Schema(title = "单次提款最低限额")
    @NotNull(message = "单次提款最低限额不能为空")
    private BigDecimal withdrawMinQuotaSingle;

    @Schema(title = "单次提款最高限额")
    @NotNull(message = "单次提款最高限额不能为空")
    private BigDecimal withdrawMaxQuotaSingle;

    @Schema(title = "单日最高提款次数")
    @NotNull(message = "单日最高提款次数不能为空")
    private Integer withdrawMaxCountDay;

    @Schema(title = "单日最高提款总额")
    @NotNull(message = "单日最高提款总额不能为空")
    private BigDecimal withdrawMaxQuotaDay;

    @Schema(title = "提款方式ID")
    @NotNull(message = "提款方式不能为空")
    private String withdrawWayId;

    @Schema(description = "提款方式- i18")
    private String withdrawWayI18;


    @Schema(title = "手续费率类型")
    @NotNull(message = "手续费率类型 0百分比 1固定金额")
    private Integer feeType;


    @Schema(title = "费率")
    @NotBlank(message = "费率不能为空")
    private BigDecimal feeRate;
}
