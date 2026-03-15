package com.cloud.baowang.agent.api.vo.agentinfo;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "添加短链接/修改")
@Data
public class AgentShortUrlManagerAddVO implements Serializable {
    @Schema(hidden = true)
    private String siteCode;

    @Schema(description = "短链接")
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String shortUrl;

    @Schema(description = "代理账号")
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String agentAccount;

    @Schema(description = "创建人",hidden = true)
    private String bindShortUrlOperator;

    @Schema(description = "创建时间",hidden = true)
    private Long bindShortUrlTime;
}
