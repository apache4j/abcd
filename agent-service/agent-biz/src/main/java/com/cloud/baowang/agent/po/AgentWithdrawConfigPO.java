package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 代理提款设置
 *
 * @author kimi
 * @since 2024-06-12
 */
@Data
@Accessors(chain = true)
@TableName("agent_withdraw_config")
@Schema(description = "代理提款设置")
public class AgentWithdrawConfigPO extends SiteBasePO {

    @Schema(description = "状态 1开启 0关闭 -1删除")
    private Integer status;

    @Schema(description = "代理账号")
    private String agentAccount;
}
