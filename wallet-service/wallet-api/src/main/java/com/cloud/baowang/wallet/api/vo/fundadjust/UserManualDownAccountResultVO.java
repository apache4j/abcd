package com.cloud.baowang.wallet.api.vo.fundadjust;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(title = "会员资金调整减额 账号信息对象")
public class UserManualDownAccountResultVO {

    /**
     * 会员账号
     */
    @Schema(description = "会员账号")
    private String userAccount;

    /**
     * 调整金额
     */
    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;


}
