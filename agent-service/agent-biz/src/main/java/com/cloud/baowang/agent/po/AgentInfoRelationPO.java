package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 代理关系闭包表：存储所有代理的祖先-子孙层级关系
 * </p>
 *
 * @author ford
 * @since 2025-08-27
 */
@Data
@TableName("agent_info_relation")
@Schema(description = "代理上下级关系")
public class AgentInfoRelationPO extends SiteBasePO {

    @Schema(description = "祖先代理编号")
    private String ancestorAgentId;

    @Schema(description = "子孙代理编号")
    private String descendantAgentId;

    @Schema(description = "层级深度 0 表示自己，1 表示直接父子关系，2 表示隔一层的祖孙，以此类推")
    private Integer agentDepth;
}
