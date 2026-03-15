package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2023/10/28 15:25
 * @description: 佣金审核请求VO
 */
@Data
@Schema(description = "佣金审核查询请求VO")
public class CommissionReviewReq extends PageVO implements Serializable {
    @Schema(description =  "申请开始时间")
    private Long applyStartTime;
    @Schema(description =  "申请结束时间")
    private Long applyEndTime;
    @Schema(description =  "审核开始时间")
    private Long auditStartTime;
    @Schema(description =  "审核结束时间")
    private Long auditEndTime;
    @Schema(description = "订单号")
    private String orderNo;
    @Schema(description = "锁单状态 0未锁 1已锁")
    private Integer lockStatus;
    @Schema(description =  "锁单员")
    private String locker;
    @Schema(description =  "审核员")
    private String oneReviewer;
    @Schema(description =  "代理账号")
    private String agentAccount;
    @Schema(description =  "佣金类型")
    private String commissionType;
    @Schema(description =  "结算周期")
    private String settleCycle;
    @Schema(description =  "审核状态")
    private Integer orderStatus;
    @Schema(description =  "审核状态", hidden = true)
    private List<Integer> orderStatusList;
    @Schema(description =  "siteCode", hidden = true)
    private String siteCode;
    @Schema(description =  "当前登录管理员", hidden = true)
    private String adminName;

    @Schema(description =  "订单状态 0-待一审 1-待二审")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer review;
}
