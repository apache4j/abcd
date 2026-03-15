package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: fangfei
 * @createTime: 2023/10/25 12:02
 * @description: 佣金方案查询VO
 */
@Data
@Schema(description = "佣金方案查询VO")
public class CommissionPlanReqVO extends PageVO implements Serializable {
    /** 代理账号 */
    @Schema(title = "方案名称")
    private String planName;
    @Schema(title = "方案code")
    private String planCode;
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "创建 开始时间 ")
    private Long startTime;

    @Schema(description = "创建 结束时间")
    private Long endTime;

    @Schema(description = "创建人")
    private String createAccountNo;


    @Schema(description = "修改人")
    private String updaterAccountNo;



    @Schema(description = "修改 开始时间 ")
    private Long updateStartTime;

    @Schema(description = "修改 结束时间")
    private Long updateEndTime;
}
