package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author qiqi
 */
@Data
@Schema(title = "会员提款审核列表请求对象")
public class UserWithdrawReviewPageReqVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "operator", hidden = true)
    private String operator;

    @Schema(description = "页签标记 0。待一审 1.挂单审核，2.待出款 user_withdraw_review_operation code")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer reviewOperation;

    @Schema(description = "申请时间-开始")
    private Long applyTimeStart;

    @Schema(description = "申请时间-结束")
    private Long applyTimeEnd;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "锁单状态 -字典code:lock_status")
    private String lockStatus;

    @Schema(description = "币种代码")
    private String currencyCode;

    @Schema(description = "审核员/操作人")
    private String auditUser;

    @Schema(description = "会员ID")
    private String userAccount;


}
