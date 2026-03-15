package com.cloud.baowang.agent.api.vo.agentRegister;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 10/10/23 9:44 PM
 * @Version : 1.0
 */
@Data
@Schema(description ="代理注册记录返回参数")
public class AgentRegisterRecordVO implements Serializable {

    @Schema(description ="代理类型code")
    private String agentType;

    @Schema(description ="代理类型名称")
    private String agentTypeName;

    @Schema(description ="代理账号")
    private String agentAccount;

    @Schema(description ="注册人")
    private String registrant;

    @Schema(description ="注册IP")
    private String registerIp;

    @Schema(description ="注册IP风控层级id")
    private Long registerIpControlId;

    @Schema(description ="注册IP风控层级名称")
    private String registerIpControlName;

    @Schema(description ="IP归属地")
    private String ipAttribution;

    @Schema(description ="注册终端")
    private String registerDevice;

    @Schema(description ="注册终端名称")
    private String registerDeviceName;

    @Schema(description ="终端设备号")
    private String deviceNumber;

    @Schema(description ="注册时间")
    private Long registerTime;

    @Schema(description ="终端设备号风控层级id")
    private Long deviceControlId;

    @Schema(description ="终端设备号风控层级名称")
    private String deviceControlName;

}
