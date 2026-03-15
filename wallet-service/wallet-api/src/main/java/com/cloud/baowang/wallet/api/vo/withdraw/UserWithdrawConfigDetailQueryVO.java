package com.cloud.baowang.wallet.api.vo.withdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(title = "会员提款配置详情查询VO")
public class UserWithdrawConfigDetailQueryVO {

    @Schema(description = "站点编码",hidden = true)
    private String siteCode;

    @Schema(description = "会员账号")
    @NotNull(message = "userAccount can not be empty")
    private String userAccount;

    @Schema(description = "会员id",hidden = true)
    private String userId;

    @Schema(description ="货币代码",hidden = true)
    private String currencyCode;

    @Schema(description ="VIP段位code",hidden = true)
    private Integer vipRankCode;




}
