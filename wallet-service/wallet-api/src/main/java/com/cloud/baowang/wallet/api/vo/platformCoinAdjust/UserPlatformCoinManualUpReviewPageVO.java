package com.cloud.baowang.wallet.api.vo.platformCoinAdjust;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会员平台币上分审核-列表 Request
 *
 * @author qiqi
 */
@Data
@Schema(title = "会员平台币上分审核-列表 Request")
public class UserPlatformCoinManualUpReviewPageVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "operator")
    private String operator;
    @Schema(description = "reviewOperation", hidden = true)
    private Integer reviewOperation;
    /**
     * {@link com.cloud.baowang.common.core.enums.manualDowmUp.PlatformCoinReviewStatusEnum}
     */
    @Schema(title = "1.待审核，2.处理中，3.审核通过，4.审核驳回，同system_param platform_coin_review_status code")
    private Integer auditStatus;

    @Schema(title = "申请时间-开始")
    private Long applyStartTime;

    @Schema(title = "申请时间-结束")
    private Long applyEndTime;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(title = "订单号")
    private String orderNo;
    @Schema(title = "锁单人")
    private String locker;

    @Schema(title = "锁单状态 0未锁 1已锁")
    private Integer lockStatus;

}
