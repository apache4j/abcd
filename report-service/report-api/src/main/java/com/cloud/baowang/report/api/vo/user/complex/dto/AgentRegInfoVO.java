package com.cloud.baowang.report.api.vo.user.complex.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @auther amos
 * @create 2024-11-04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "代理下注册人数")
public class AgentRegInfoVO  {
    private String date;
    private Integer count;
    private Integer registry;
    private String siteCode;
    /**币种*/
    private String currency;


    /** 代理注册人数 */
    private Integer agentRegistrationCount;
    /** 代理注册人数:后台 */
    private Integer agentRegistrationCountBacked;
    /** 代理注册人数:PC */
    private Integer agentRegistrationCountPc;
    /** 代理注册人数:android app */
    private Integer agentRegistrationCountAndroidApp;
    /** 代理注册人数:android h5 */
    private Integer agentRegistrationCountAndroidH5;
    /** 代理注册人数:ios app */
    private Integer agentRegistrationCountIosApp;
    /** 代理注册人数:ios h5 */
    private Integer agentRegistrationCountIosH5;
}
