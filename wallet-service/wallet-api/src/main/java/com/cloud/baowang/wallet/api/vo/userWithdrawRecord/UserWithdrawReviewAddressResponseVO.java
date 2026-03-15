package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(title = "会员地址详情列表返回对象")
@Data
public class UserWithdrawReviewAddressResponseVO {



    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "首次使用时间")
    private Long firstUsedTime;

    @Schema(description = "最后使用时间")
    private Long lastUsedTime;


    @Schema(description = "总使用次数")
    private Integer usedNums;

    @Schema(description = "币种")
    private String currencyCode;
}
