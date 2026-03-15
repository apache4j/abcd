package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 代理备注表
 *
 * @author kimi
 * @since 2024-05-31
 */
@Data
@TableName("agent_remark_record")
@Schema(description = "代理备注表")
public class AgentRemarkRecordPO extends BasePO {
    @Schema(description = "siteCode")
    private String siteCode;
    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态 0删除 1有效")
    private Integer status;

    @Schema(description = "操作人")
    private String operator;
}
