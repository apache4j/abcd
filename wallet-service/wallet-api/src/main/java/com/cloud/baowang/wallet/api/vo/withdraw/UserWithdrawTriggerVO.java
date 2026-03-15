package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "会员提现触发类")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserWithdrawTriggerVO extends MessageBaseVO implements Serializable {


    @Schema(description = "会员id")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;

    /**
     * 提现订单号
     */
    @Schema(description = "提现单号")
    private String orderNumber;

    @Schema(description = "提现时间")
    private Long withdrawTime;

    @Schema(description = "提现金额")
    private BigDecimal withdrawAmount;

    @Schema(description = "币种代码")
    private String currencyCode;

    /**
     * 是否手动申请 true:手动申请 false: 开始派发
     * */
    private boolean applyFlag=false;

    @Schema(description = "时区")
    private String timezone;

    @Schema(description = "注册时间")
    private Long registerTime;
}
