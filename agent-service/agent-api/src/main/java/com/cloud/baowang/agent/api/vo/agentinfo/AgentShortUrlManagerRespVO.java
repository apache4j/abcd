package com.cloud.baowang.agent.api.vo.agentinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "代理短链接视图VO")
@Data
public class AgentShortUrlManagerRespVO implements Serializable {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "短链接")
    private String shortUrl;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "创建人")
    private String bindShortUrlOperator;

    @Schema(description = "创建时间")
    private Long bindShortUrlTime;
}
