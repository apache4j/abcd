package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 代理额度钱包
 * </p>
 *
 * @author qiqi
 */
@Data
@TableName("agent_quota_coin")
@Schema(title = "代理额度钱包对象")
public class AgentQuotaCoinPO extends BasePO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "代理编号")
    private String agentId;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "代理名称")
    private String agentName;

    @Schema(description = "代理ID父节点")
    private String parentId;

    @Schema(description = "层次id逗号分隔")
    private String path;

    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "冻结金额")
    private BigDecimal freezeAmount;

    @Schema(description = "可用余额")
    private BigDecimal availableAmount;

}
