package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: fangfei
 * @createTime: 2024/09/22 15:39
 * @description:
 */
@Data
@I18nClass
@Schema(title = "代理信息")
public class AgentPlanInfoVO implements Serializable {
    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理类型 1正式 2测试 3合作")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_TYPE)
    private Integer agentType;

    @Schema(description = "代理类型 文本")
    private String agentTypeText;

    @Schema(description = "代理归属 1推广 2招商 3官资")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.AGENT_ATTRIBUTION)
    private Integer agentAttribution;

    @Schema(description = "代理归属名称")
    private Integer agentAttributionText;

    @Schema(description = "代理类别 1常规代理 2流量代理")
    @I18nField(type =I18nFieldTypeConstants.DICT,value = CommonConstant.AGENT_CATEGORY)
    private Integer agentCategory;

    @Schema(description = "代理类别名称")
    private String agentCategoryText;
}
