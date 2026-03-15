package com.cloud.baowang.wallet.api.vo.rebate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 24/6/23 10:35 AM
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "用户订单返水搜集对象")
public class UserRebateVO implements Serializable {

    /* 站点code */
    @Schema(title = "站点code")
    private String siteCode;

    /* 会员id */
    @Schema(title = "会员id")
    private String userId;

    /* 币种 */
    @Schema(title = "币种")
    private String currency;

    /* 有效投注金额 */
    @Schema(title = "有效投注金额")
    private BigDecimal validBetAmount;

}
