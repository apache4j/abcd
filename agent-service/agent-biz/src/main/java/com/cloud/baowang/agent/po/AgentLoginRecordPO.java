package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author : kimi
 */
@Data
@TableName("agent_login_record")
@Schema(description = "代理登录日志")
public class AgentLoginRecordPO extends SiteBasePO implements Serializable {

    /* 登录状态 */
    private Integer loginStatus;

    private String agentId;

    /* 代理账号 */
    private String agentAccount;

    /* 代理类型 */
    private Integer agentType;

    /* 登录IP */
    private String loginIp;

    /* ip风控层级id */
    private String IpControlId;

    /* IP归属地 */
    private String ipAttribution;

    /* 登录终端 */
    private Integer loginDevice;

    /* 终端设备号 */
    private String deviceNumber;

    /* 终端设备号风控层级id */
    private String deviceControlId;

    /* 登录地址 */
    private String loginAddress;

    /* 设备版本 */
    private String deviceVersion;

    /* 登录时间 */
    private Long loginTime;

    /* 备注 */
    private String remark;

    /* 标签 */
    private String agentLabelId;


}
