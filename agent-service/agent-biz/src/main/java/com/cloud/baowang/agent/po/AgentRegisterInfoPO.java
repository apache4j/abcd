package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author : 小智
 * @Date : 10/10/23 5:51 PM
 * @Version : 1.0
 */
@Data
@TableName("agent_register_info")
@Schema(description ="代理注册记录对象")
public class AgentRegisterInfoPO extends SiteBasePO {

    // 代理编号
    private String agentId;

    /* 代理账号 */
    private String agentAccount;

    /* 代理类型 */
    private String agentType;

    /* 注册人 */
    private String registrant;

    /* 注册IP */
    private String registerIp;
    /* 注册域名 */
    private String registerDomain;

    /* IP风控层级 */
    private String registerIpControlId;

    /* IP归属地 */
    private String ipAttribution;

    /* 注册终端 */
    private String registerDevice;

    /* 终端设备号 */
    private String deviceNumber;

    /* 终端设备风控层级 */
    private String deviceControlId;

    /* 注册时间 */
    private Long registerTime;



}
