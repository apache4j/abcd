package com.cloud.baowang.agent.api.vo.agentinfo;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "代理短链接查询VO")
@Data
public class AgentShortUrlManagerPageQueryVO extends PageVO {
    @Schema(hidden = true)
    private String siteCode;
    @Schema(description = "短链接")
    private String shortUrl;
    @Schema(description = "代理账号")
    private String agentAccount;
    @Schema(description = "创建人")
    private String bindShortUrlOperator;
}
