package com.cloud.baowang.agent.api.vo.agentinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: kimi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(title = "返回代理树")
public class AgentListTreeVO {

/*    @Schema(title = "代理id")
    private String id;*/

    @Schema(title = "代理编号")
    private String agentId;

    @Schema(title = "父级id")
    private String parentId;

    @Schema(title = "层级结果 用逗号拼接")
    private String path;

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "直属下级代理")
    private Long directDownAgentNum;

    @Schema(title = "全部下级代理")
    private Long allDownAgentNum;

    @Schema(title = "下级对象")
    private List<AgentListTreeVO> children;

    // ------------------------------------------------
    @Schema(title = "代理层级")
    private Integer level;

    @Schema(title = "注册时间")
    private Long registerTime;
}
