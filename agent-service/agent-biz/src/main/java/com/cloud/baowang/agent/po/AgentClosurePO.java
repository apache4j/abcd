package com.cloud.baowang.agent.po;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("agent_closure")
@Schema(description = "代理层级关系（Closure Table）")
public class AgentClosurePO implements Serializable {

    @Schema(description = "父节点ID")
    private String parentNodeId;

    @Schema(description = "后代节点ID")
    private String childNodeId;

    @Schema(description = "层级距离：0=自己，1=直接下级，2=二级下级...")
    private Integer distance;
}