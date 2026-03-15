package com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "会员账号修改审核详情返回对象")
@I18nClass
public class UserAccountUpdateReviewDetailsResVO {
    @Schema(title = "唯一主键ID")
    private String id;
    // 会员注册信息
    @Schema(title = "注册时间")
    private Long registrationTime;
    @Schema(title = "上次登录时间")
    private Long lastLoginTime;
    @Schema(title = "最后下注时间")
    private Long lastBetTime;
    @Schema(title = "注册端")
    private String registerTerminal;
    @Schema(title = "上级代理")
    private String superiorAgent;
    @Schema(title = "账号类型code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;
    @Schema(title = "账号类型name")
    private String accountTypeText;
    // 会员账号信息
    @Schema(title = "账号")
    private String account;
    @Schema(title = "账号状态")
    private String accountState;
    @Schema(title = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    private List<CodeValueVO> accountStatusName;
    @Schema(description = "会员段位信息")
    @I18nField
    private String vipRankNameI18nCode;
    @Schema(title = "VIP等级")
    private String vipLevel;
    @Schema(title = "存款次数")
    private String depositNumber;
    @Schema(title = "提款次数")
    private String withdrawNumber;
    @Schema(title = "主货币")
    private String mainCurrency;
    @Schema(title = "注册端")
    private String registry;
    @Schema(title = "风控层级")
    private String riskLevel;
    @Schema(title = "会员标签")
    private String memberLabel;
    @Schema(title = "备注信息")
    private String remark;
    // 申请信息
    @Schema(title = "申请人")
    private String applicant;
    @Schema(title = "申请时间")
    private Long applicationTime;
    @Schema(title = "审核申请类型")
    @I18nField
    private String reviewApplicationType;
    @Schema(title = "申请原因")
    private String reasonApplication;
    @Schema(title = "修改前")
    private String beforeFixing;
    @Schema(title = "修改后")
    private String afterModification;
    // 审核信息
    @Schema(title = "一审人")
    private String firstInstance;
    @Schema(title = "一审时间")
    private Long firstTrialTime;
    @Schema(title = "一审备注")
    private String firstRemark;

    @Schema(title = "变更类型")
    private String changeType;
}
