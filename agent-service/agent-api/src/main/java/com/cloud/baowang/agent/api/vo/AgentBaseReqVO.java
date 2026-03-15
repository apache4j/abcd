package com.cloud.baowang.agent.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/9/23 11:35
 * @Version: V1.0
 **/
@Data
public class AgentBaseReqVO {
    @Schema(description = "当前代理id",hidden = true)
    private String currentId;
    @Schema(description = "当前代理账号",hidden = true)
    private String currentAgent;
    @Schema(description = "站点编号",hidden = true)
    private String siteCode;
}
