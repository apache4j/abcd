package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

/**
 * 会员银行卡管理
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("user_bank_card_manage")
@Schema(title = "会员银行卡管理")
public class UserBankCardManagePO extends BasePO {

    @Schema(title =  "银行卡号")
    private String bankCardNo;

    @Schema(title =  "银行名称")
    private String bankName;

    @Schema(title =  "银行支行")
    private String branchBankName;

    @Schema(title =  "黑名单状态 0禁用 1启用")
    private Integer blackStatus;

    @Schema(title =  "绑定状态 0未绑定 1绑定中")
    private Integer bindingStatus;

    @Schema(title =  "风控层级id")
    private Long riskControlLevelId;

    @Schema(title =  "绑定账号数量")
    private Integer bindingAccountTimes;

    @Schema(title =  "当前绑定会员id")
    private Long currentBindingUserId;

    @Schema(title =  "当前绑定会员账号")
    private String currentBindingUserAccount;

    @Schema(title =  "会员提款成功次数")
    private Integer userWithdrawSuccessTimes;

    @Schema(title =  "会员提款被拒次数")
    private Integer userWithdrawFailTimes;

    @Schema(title =  "会员提款总金额")
    private BigDecimal userWithdrawSumAmount;

    @Schema(title =  "代理提款成功次数")
    private Integer agentWithdrawSuccessTimes;

    @Schema(title =  "代理提款被拒次数")
    private Integer agentWithdrawFailTimes;

    @Schema(title =  "代理提款总金额")
    private BigDecimal agentWithdrawSumAmount;

    @Schema(title =  "银行卡新增时间")
    private Long firstUseTime;

    @Schema(title =  "最近提款时间")
    private Long lastWithdrawTime;

    @Schema(title =  "最近操作人")
    private String lastOperator;

    /**
     * 站点code
     */
    @TableField(value = "site_code")
    private String siteCode;
}
