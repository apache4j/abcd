package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 代理信息变更记录表
 * </p>
 *
 * @author awei
 * @since 2023-10-10
 */
@Data
@TableName("agent_info_change_record")
@Schema(description = "代理信息变更记录表")
public class AgentInfoChangeRecordPO extends BasePO {
    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(description = "签约账号")
    private String agentAccount;

    @Schema(description = "代理类型")
    private Integer agentType;

    @Schema(description = "变更类型")
    private Integer changeType;

    @Schema(description = "变更前内容")
    private String changeBefore;

    @Schema(description = "变更后内容")
    private String changeAfter;

    @Schema(description = "操作人")
    private String operator;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "操作时间")
    private Long operatorTime;

    @Schema(description = "状态 0删除 1有效")
    private Integer status;
}
