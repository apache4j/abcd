package com.cloud.baowang.agent.api.vo.agentRegister;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 10/10/23 9:44 PM
 * @Version : 1.0
 */
@Data
@Schema(description ="代理注册记录传入参数")
public class AgentRegisterRecordParam extends PageVO implements Serializable {
    @Schema(description ="站点code")
    private String siteCode;

    @Schema(description ="代理类型 下拉框类型: agent_type ")
    private String agentType;

    @Schema(description ="代理账号")
    private String agentAccount;

    @Schema(description ="注册IP")
    private String registerIp;

    @Schema(description ="IP归属地")
    private String ipAttribution;

    @Schema(description ="注册终端 下拉框类型:registry")
    private String registerDevice;

    @Schema(description ="注册开始时间")
    private Long startTime;

    @Schema(description ="注册结束时间")
    private Long endTime;

}
