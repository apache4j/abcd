package com.cloud.baowang.agent.api.vo.agentinfo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 代理列表 Request
 *
 * @author kimi
 */
@Data
@Schema(title = "代理列表 Request")
public class AgentInfoPageVO extends SitePageVO {

    @Schema(title = "注册时间-开始")
    private Long registerTimeStart;

    @Schema(title = "注册时间-结束")
    private Long registerTimeEnd;

    @Schema(title = "代理账号id - 前端不传递",hidden = true)
    private String agentId;

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "直属下级:1/全部下级:2")
    private Integer agentSubType;

    @Schema(title = "账号类型 下拉框类型:agent_type ")
    private List<Integer> agentType;

    @Schema(title = "账号状态 下拉框类型:agent_status")
    private String status;

    @Schema(title = "风控层级id common里的getRiskDownBox")
    private String riskLevelId;

    @Schema(title = "代理标签id common里的getAgentLabelList")
    private String agentLabelId;

    @Schema(title = "注册方式 下拉框类型:register_way ")
    private Integer registerWay;

    @Schema(description = "商务名称")
    private String merchantName;

    @Schema(title = "代理层级")
    private Integer level;

    @Schema(title = "方案名称")
    private String planName;

    @Schema(title = "邀请码")
    private String inviteCode;
}
