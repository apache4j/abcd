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
public class UserActivityTypingChangeVO {

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
     * 打码量
     */
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal typingAmount;

    /**
     * 领取优惠的时间
     */
    //@NotNull(message = ConstantsCode.PARAM_ERROR)
    private Long startTime;

    //@NotNull(message = ConstantsCode.PARAM_ERROR)
    private String limitGameType;

    /**
     * 订单编号
     */
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String  orderNo;
}
