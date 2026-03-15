package com.cloud.baowang.common.kafka.vo;


import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(title = "会员累计充值添加请求对象")
public class ValidInviteUserRechargeMqVO extends MessageBaseVO {


    /**
     * 会员id
     */
    @Schema(title = "会员id")
    private String userId;

    /**
     * 会员账号
     */
    @Schema(title = "会员账号")
    private String userAccount;


    @Schema(title = "充值提款金额")
    private BigDecimal amount;

    @Schema(title = "是否首存")
    private int isFirstDeposit;

    private String siteCode;



}
