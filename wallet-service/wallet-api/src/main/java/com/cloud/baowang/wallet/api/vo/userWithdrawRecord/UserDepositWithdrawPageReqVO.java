package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author ford
 * @since  2024-12-06
 */
@Data
@Schema(title = "会员充值提币请求对象")
public class UserDepositWithdrawPageReqVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "更新时间-开始")
    private Long startTime;

    @Schema(description = "更新时间-结束")
    private Long endTime;

    private Integer userAccountType;

    private String status;


}
