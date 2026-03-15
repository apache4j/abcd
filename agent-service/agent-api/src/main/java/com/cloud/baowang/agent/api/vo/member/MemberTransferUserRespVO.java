package com.cloud.baowang.agent.api.vo.member;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@I18nClass
@Schema(title ="会员转代申请用户查询响应")
public class MemberTransferUserRespVO {

    @Schema(title ="转代会员ID")
    private Integer userAccount;

    @Schema(title ="转代会员注册信息")
    private Integer userRegister;


    @Schema(description = "账号类型 1-测试 2-正式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;

    @Schema(description = "账号类型名称")
    private String accountTypeText;

    @Schema(description = "上级代理id")
    private String superAgentId;

    @Schema(description = "上级代理账号")
    private String superAgentAccount;

}
