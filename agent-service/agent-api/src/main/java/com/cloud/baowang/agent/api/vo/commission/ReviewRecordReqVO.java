package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2023/10/28 15:25
 * @description: 佣金审核记录查询
 */
@Data
@Schema(description = "佣金审核记录查询VO")
public class ReviewRecordReqVO implements Serializable {
    @Schema(description = "结算开始时间")
    private Long startTime;
    @Schema(description = "结算结束时间")
    private Long endTime;
    @Schema(description = "报表ID")
    private String reportId;
    @Schema(description = "订单号")
    private String orderNo;
    @Schema(description =  "代理Id")
    private String agentId;
    @Schema(description =  "佣金类型List")
    private List<String> commissionTypeList;
    @Schema(description =  "审核状态", hidden = true)
    private List<Integer> orderStatusList;
    @Schema(description =  "siteCode", hidden = true)
    private String siteCode;
}
