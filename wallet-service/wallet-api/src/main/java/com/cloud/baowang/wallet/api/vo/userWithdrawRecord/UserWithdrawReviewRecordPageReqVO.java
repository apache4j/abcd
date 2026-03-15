package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(title = "会员提款审核记录列表请求对象")
public class UserWithdrawReviewRecordPageReqVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "审核操作状态(可不传，默认为结单查看/流程结束数据)", defaultValue = "3")
    private Integer reviewOperation;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "申请时间-开始")
    private Long applyTimeStart;

    @Schema(description = "申请时间-结束")
    private Long applyTimeEnd;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "订单状态")
    private String status;

    @Schema(description = "收款账户")
    private String depositWithdrawAddress;

}
