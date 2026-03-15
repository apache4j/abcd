package com.cloud.baowang.agent.api.vo.agentRegister;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 11/10/23 12:25 PM
 * @Version : 1.0
 */
@Data
@Schema(description ="代理注册记录信息表入参")
public class AgentRegisterRecordInsertVO implements Serializable {

    @Schema(description ="代理类型 参考agentType")
    private Integer agentType;

    @Schema(description ="代理编号")
    private String agentId;

    @Schema(description ="代理账号")
    private String agentAccount;

    @Schema(description ="注册IP")
    private String registerIp;

    @Schema(description ="IP归属地")
    private String ipAttribution;

    @Schema(description ="注册终端 参考deviceType")
    private Integer registerDevice;

    @Schema(description ="终端设备号")
    private String deviceNumber;

    @Schema(description ="注册时间")
    private Long registerTime;

    @Schema(description ="注册人")
    private String registrant;

    @Schema(description = "站点编码")
    private String siteCode;
}
