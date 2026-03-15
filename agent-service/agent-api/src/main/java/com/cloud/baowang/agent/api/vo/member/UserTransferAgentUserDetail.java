package com.cloud.baowang.agent.api.vo.member;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "会员转代-转代会员信息")
@Data
@I18nClass
public class UserTransferAgentUserDetail implements Serializable {

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private Integer accountType;

    @Schema(description = "账号类型")
    private String accountTypeText;

    @Schema(description = "账号状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_STATUS)
    private Integer accountStatus;

    @Schema(description = "账号状态")
    private String accountStatusText;

    @Schema(description = "风控层级")
    private String riskLevel;

    @Schema(description = "会员标签")
    private String userLabel;

    @Schema(description = "vip等级")
    private String vipGradeName;

    @Schema(description = "注册端")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REGISTRY)
    private Integer registry;

    @Schema(description = "注册端")
    private String registryText;

    @Schema(description = "注册时间")
    private Long registerTime;

    @Schema(description = "上次登录时间")
    private Long lastLoginTime;

    @Schema(description = "当前上级代理")
    private String currentAgentName;

    @Schema(description = "绑定时间")
    private Long bindingAgentTime;

    @Schema(description = "备注信息")
    private String acountRemark;


}
