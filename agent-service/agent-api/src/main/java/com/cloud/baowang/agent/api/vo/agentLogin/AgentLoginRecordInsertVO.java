package com.cloud.baowang.agent.api.vo.agentLogin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "代理登录记录信息记录入参")
public class AgentLoginRecordInsertVO implements Serializable {

    @Schema(description = "登录状态 参考loginTypeEnum")
    private String loginStatus;

    @Schema(description = "代理Id")
    private String agentId;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理类型")
    private String agentType;

    @Schema(description = "登录IP")
    private String loginIp;

    @Schema(description = "IP归属地")
    private String ipAttribution;

    @Schema(description = "登录终端")
    private String loginDevice;

    @Schema(description = "终端设备号")
    private String deviceNumber;

    @Schema(description = "登录地址")
    private String loginAddress;

    @Schema(description = "设备版本")
    private String deviceVersion;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "登录时间")
    private Long loginTime;

    @Schema(description = "siteCode")
    private String siteCode;

    /* 标签 */
    @Schema(description = "标签")
    private String agentLabelId;

}
