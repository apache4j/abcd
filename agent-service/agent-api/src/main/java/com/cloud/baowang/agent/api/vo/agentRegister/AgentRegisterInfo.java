package com.cloud.baowang.agent.api.vo.agentRegister;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author : kimi
 * @Date : 10/10/23 5:51 PM
 * @Version : 1.0
 */
@Data
@Schema(description ="代理注册记录对象")
public class AgentRegisterInfo {

    /* 代理账号 */
    private String agentAccount;

    /* 代理类型 */
    private String agentType;

    /* 注册IP */
    private String registerIp;

    /* IP归属地 */
    private String ipAttribution;

    /* 注册终端 */
    private String registerDevice;

    /* 终端设备号 */
    private String deviceNumber;

    /* 注册时间 */
    private Long registerTime;

    /* 注册域名 */
    private String registerDomain;
}
