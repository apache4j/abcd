package com.cloud.baowang.wallet.api.vo.platformCoinAdjust;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author qiqi
 */
@Data
@Schema(title = "会员平台币上分记录 Request")
@Builder
public class UserPlatformCoinManualUpRecordVO {

    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(description = "userId")
    private String userId;
    @Schema(description = "orderStatus")
    private Integer orderStatus;

    @Schema(title = "申请时间-开始")
    private Long applyStartTime;

    @Schema(title = "申请时间-结束")
    private Long applyEndTime;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "会员注册信息")
    private String userAccount;

    @Schema(title = "审核状态 system_param platform_coin_review_status code值")
    private Integer auditStatus;

    @Schema(title = "调整类型 system_param platform_coin_manual_adjust_up_type code值")
    private Integer adjustType;

    @Schema(title = "调整金额-最小值")
    private String adjustAmountMin;

    @Schema(title = "调整金额-最大值")
    private String adjustAmountMax;

}
