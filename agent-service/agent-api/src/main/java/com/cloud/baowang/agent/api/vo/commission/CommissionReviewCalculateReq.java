package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: fangfei
 * @createTime: 2023/10/28 15:25
 * @description: 佣金审核请求VO
 */
@Data
@Schema(description = "佣金审核查询请求VO")
public class CommissionReviewCalculateReq  implements Serializable {
    @Schema(description =  "审核开始时间")
    private Long auditStartTime;
    @Schema(description =  "审核结束时间")
    private Long auditEndTime;
    @Schema(description =  "代理账号")
    private String agentAccount;
    @Schema(description =  "siteCode", hidden = true)
    private String siteCode;
}
