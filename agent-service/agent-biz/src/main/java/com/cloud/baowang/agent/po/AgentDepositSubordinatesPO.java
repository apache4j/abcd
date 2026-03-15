package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 代理代存记录
 * </p>
 *
 * @author qiqi
 * @since 2023-10-24
 */
@Getter
@Setter
@TableName("agent_deposit_subordinates")
@Schema(name = "AgentDepositSubordinatesPO对象", description = "代理代存记录")
public class AgentDepositSubordinatesPO extends BasePO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(description = "代理ID")
    private String agentId;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理名称")
    private String agentName;

    @Schema(description = "代理ID父节点")
    private String parentId;

    @Schema(description = "层次id逗号分隔")
    private String path;

    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "代存类型（1 佣金代存 2额度代存）")
    private String depositSubordinatesType;

    @Schema(description = "代存会员账号")
    private String userAccount;

    @Schema(description = "账号类型 1测试 2正式")
    private String accountType;

    @Schema(description = "代存会员Id")
    private String userId;

    @Schema(description = "会员名称")
    private String userName;

    @Schema(description = "代存金额")
    private BigDecimal amount;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "代存时间")
    private Long depositTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "币种")
    private String currencyCode;

    /**
     * 平台币金额
     * platform_amount
     */
    @Schema(description = "平台币金额")
    private BigDecimal platformAmount;

    /**
     * 汇率 transfer_rate
     */
    private BigDecimal transferRate;

    @Schema(description = "流水倍数")
    private BigDecimal runningWaterMultiple;


}
