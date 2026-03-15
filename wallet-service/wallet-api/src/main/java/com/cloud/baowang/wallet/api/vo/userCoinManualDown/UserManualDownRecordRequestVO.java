package com.cloud.baowang.wallet.api.vo.userCoinManualDown;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(description = "会员人工扣除记录请求对象")
public class UserManualDownRecordRequestVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "操作开始时间")
    private Long creatorStartTime;

    @Schema(description = "操作结束时间")
    private Long creatorEndTime;

    @Schema(description = "修改开始时间")
    private Long updateStartTime;

    @Schema(description = "修改结束时间")
    private Long updateEndTime;


    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "会员姓名")
    private String userName;
    /**
     * {@link com.cloud.baowang.common.core.enums.ReviewStatusEnum}
     */
    @Schema(description = "审核状态")
    private Integer auditStatus;
    /**
     * {@link com.cloud.baowang.common.core.enums.manualDowmUp.ManualAdjustTypeEnum}
     */
    @Schema(description = "调整类型 字典code:")
    @NotNull(message = "调整类型不能为空")
    private Integer adjustType;

    @Schema(description = "账变状态 0.失败，1.成功 system_param balance_change_status code值")
    private Integer balanceChangeStatus;

    @Schema(description = "调整金额最小值")
    private BigDecimal minAdjustAmount;

    @Schema(description = "调整金额最大值")
    private BigDecimal maxAdjustAmount;

    @Schema(description = "是否导出 true 是 false 否")
    private Boolean exportFlag = false;

}
