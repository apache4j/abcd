package com.cloud.baowang.activity.api.vo.task;

import com.cloud.baowang.activity.api.enums.task.TaskDistributionTypeEnum;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "任务领取记录信息-请求入参")
public class SiteTaskOrderRecordReqVO extends PageVO implements Serializable {

    /**
     * 领取开始时间
     */
    @Schema(description = "领取开始时间")
    private Long receiveStartTime;

    /**
     * 可领取结束时间
     */
    @Schema(description = "领取结束时间")
    private Long receiveEndTime;

    @Schema(description = "发放开始时间")
    private Long distributionStartTime;

    @Schema(description = "发放结束时间")
    private Long distributionEndTime;


    /**
     * 站点code
     */
    @Schema(description = "站点code", hidden = true)
    private String siteCode;

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNo;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    private String taskName;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称", hidden = true)
    private List<String> taskIds;

    /**
     * 会员账号
     */
    @Schema(description = "会员账号")
    private String userAccount;


    /**
     * 领取状态
     */
    @Schema(description = "领取状态")
    private Integer receiveStatus;


}



