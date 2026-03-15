package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 额度转账实体
 * </p>
 */
@Data
@TableName("agent_quota_transfer_record")
@Schema(title = "代理额度转账实体")
public class AgentQuotaTransferRecordPO extends BasePO {


    @Schema(title = "站点编码")
    private String siteCode;

    @Schema(title = "代理id")
    private String agentId;

    @Schema(title = "代理账户")
    private String agentAccount;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "转账金额")
    private BigDecimal amount;

    @Schema(title = "状态：1：转账成功；2：转账失败；3：转账中")
    private Integer status;




}
