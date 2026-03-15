package com.cloud.baowang.wallet.api.vo.fundrecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 会员账号信息
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Accessors(chain = true)
@Schema(description = "会员账号信息")
public class GetByUserInfoVO {

    @Schema(description = "会员ID")
    private String userId;

    @Schema(description = "会员注册信息")
    private String userAccount;

    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    private String accountStatus;

    @Schema(description = "vip当前等级")
    private Integer vipGradeCode;
    @Schema(description = "vip当前等级-Name")
    private String vipGradeCodeName;

    @Schema(description = "会员标签id")
    private String userLabelId;
    @Schema(description = "会员标签")
    private String userLabel;
    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "账号备注")
    private String acountRemark;

    @Schema(description = "累计存款次数")
    private Integer allDepositTimes;

    @Schema(description = "累计提款次数")
    private Integer allWithdrawTimes;

    @Schema(description = "累计总存款金额")
    private BigDecimal allDepositAmount;

    @Schema(description = "累计总提款金额")
    private BigDecimal allWithdrawAmount;

    @Schema(description = "累计总存提款差额")
    private BigDecimal differenceAmount;

    @Schema(description = "站点code")
    private String siteCode;

}
