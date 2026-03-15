package com.cloud.baowang.agent.api.vo.winloss;


import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 25/10/23 2:36 PM
 * @Version : 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代理盈亏请求对象")
public class AgentUserWinLossParam extends SitePageVO implements Serializable {

    @Schema(description ="代理id")
    private String agentId;

//     @Schema(description ="代理账号")
    private String agentAccount;

    private String underAccount;

    @Schema(description ="统计时间")
    private Integer dateNum;

    @Schema(description ="排序")
    private Integer sort;

    @Schema(description ="开始时间")
    private Long startTime;

    @Schema(description ="结束时间")
    private Long endTime;

    private List<String> underAgentAccount;

    // 代理层级 agent_info中的level
    private Integer level;
}
