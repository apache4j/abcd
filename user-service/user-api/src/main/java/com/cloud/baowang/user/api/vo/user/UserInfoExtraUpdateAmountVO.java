package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "密码找回请求对象")
public class UserInfoExtraUpdateAmountVO implements Serializable {


    @Schema(description = "会员id")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "有效投注量")
    private BigDecimal validAmount;

    @Schema(description = "盈利")
    private BigDecimal winLoseAmount;

    @Schema(description = "站点编码")
    private String siteCode;

}
