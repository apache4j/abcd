package com.cloud.baowang.wallet.api.vo.platformCoinAdjust;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会员平台币上分审核记录-列表 Request
 *
 * @author qiqi
 */
@Data
@Schema(title = "会员平台币上分审核记录-列表 Request")
public class UserPlatformCoinManualUpReviewRecordPageVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(title = "申请时间-开始")
    private Long applyTimeStart;

    @Schema(title = "申请时间-结束")
    private Long applyTimeEnd;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "订单状态")
    private Integer auditStatus;
}
