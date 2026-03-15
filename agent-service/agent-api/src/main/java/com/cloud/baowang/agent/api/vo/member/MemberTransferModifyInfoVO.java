package com.cloud.baowang.agent.api.vo.member;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "会员转代详情-代理详情")
@I18nClass
public class MemberTransferModifyInfoVO {

    @Schema(title = "代理账号")
    private String agentName;

    @Schema(title = "账号状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_STATUS)
    private String status;

    @Schema(title = "账号状态")
    private String statusText;

    @Schema(description = "代理风控层级")
    private String agentRiskLevel;

    @Schema(description = "代理标签")
    private String agentLabel;

    @Schema(description = "代理层级")
    private Integer level;

    @Schema(description = "备注")
    private String remark;

}
