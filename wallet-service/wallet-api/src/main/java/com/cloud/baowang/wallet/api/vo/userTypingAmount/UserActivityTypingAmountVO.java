package com.cloud.baowang.wallet.api.vo.userTypingAmount;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityTypingAmountVO {

    /**
     * 会员ID
     */
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String userId;
    /**
     * 会员账号
     */

    private String userAccount;

    /**
     * 站点CODE
     */
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String siteCode;

    /**
     * 币种
     */
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String currency;

    /**
     * 限制游戏
     * 对应枚举 {@link VenueTypeEnum}
     */
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String limitGameType;


    /**
     * 打码量
     */
    private BigDecimal typingAmount = BigDecimal.ZERO;
}
