package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(title = "会员人工存款0审核列表请求对象")
public class UserManualDepositPageReqVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "operator", hidden = true)
    private String operator;

/*    @Schema(description = "页签标记 1.待一审 3.待出款")
    @NotNull(message = "页签标记不能为空")
    private Integer reviewOperation;*/

    @Schema(description = "申请时间-开始")
    private Long applyTimeStart;

    @Schema(description = "申请时间-结束")
    private Long applyTimeEnd;

    @Schema(description = "审核时间-开始时间")
    private Long auditTimeStart;

    @Schema(description = "审核时间-结束时间")
    private Long auditTimeEnd;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "锁单状态 -字典code:lock_status")
    private String lockStatus;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "审核员账号")
    private String auditUser;

    @Schema(description = "三方消息状态 -字典code:deposit_withdraw_pay_process_status")
    private String payProcessStatus;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "存款方式")
    private String depositWayId;

}
