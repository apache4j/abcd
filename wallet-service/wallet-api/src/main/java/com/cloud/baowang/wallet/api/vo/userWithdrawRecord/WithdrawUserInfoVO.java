package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@I18nClass
@Schema(title = "提款会员账号信息")
public class WithdrawUserInfoVO {


    @Schema(description = "会员ID")
    private String userAccount;

    @Schema(description = "会员注册信息")
    private String userRegister;

    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_STATUS)
    private String accountStatus;

    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    private String accountStatusText;

    @Schema(description = "vip当前等级")
    private Integer vipGradeCode;
    @Schema(description = "vip当前等级-Name")
    private String vipGradeCodeName;
    @Schema(description = "vip段位信息")
    private Integer vipRankCode;
    @I18nField
    @Schema(description = "vip段位名称")
    private String vipRankCodeName;

    @Schema(description = "会员标签id")
    private String userLabelId;

    @Schema(description = "会员标签")
    private String userLabel;

    @Schema(description = "会员备注")
    private String userRemark;


    @Schema(description = "累计总存款金额")
    private BigDecimal totalDepositAmount;

    @Schema(description = "累计总存款次数")
    private Integer totalDepositNum;

    @Schema(description = "累计总提款金额")
    private BigDecimal totalWithdrawAmount;

    @Schema(description = "累计总提款次数")
    private Integer totalWithdrawNum;

    @Schema(description = "累计总存提款差额")
    private BigDecimal totalDepositWithdrawDifference;

    @Schema(description = "站点code")
    private String siteCode;

}
