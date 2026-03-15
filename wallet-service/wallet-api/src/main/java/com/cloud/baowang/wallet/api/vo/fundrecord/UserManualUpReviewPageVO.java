package com.cloud.baowang.wallet.api.vo.fundrecord;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 会员人工加额审核-列表 Request
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Schema(title = "会员人工加额审核-列表 Request")
public class UserManualUpReviewPageVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "reviewOperation", hidden = true)
    private Integer reviewOperation;
    @Schema(description = "operator")
    private String operator;
    /**
     * {@link com.cloud.baowang.common.core.enums.ReviewStatusEnum}
     */
    @Schema(title = "1.待审核，2.处理中，3.审核通过，4.审核驳回，同system_param review_status code")
    private Integer auditStatus;

    @Schema(title = "申请时间-开始")
    private Long applyTimeStart;

    @Schema(title = "申请时间-结束")
    private Long applyTimeEnd;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(title = "订单号")
    private String orderNo;
    @Schema(title = "锁单人")
    private String locker;

    @Schema(title = "锁单状态 0未锁 1已锁")
    private Integer lockStatus;

}
