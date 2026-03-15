package com.cloud.baowang.agent.api.vo.agentreview.info;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 代理基本信息
 * </p>
 *
 * @author kimi
 * @since 2023-10-10
 */
@Data
@Schema(description = "代理fb google other信息")
public class AgentOtherVO {

    @Schema(description = "FaceBook PixId")
    private String FbPixId;
    @Schema(description = "FaceBook Token")
    private String FbToken;
    @Schema(description = "Google Ads PixId")
    private String GooglePixId;
    @Schema(description = "Google Ads Token")
    private String GoogleToken;

}
