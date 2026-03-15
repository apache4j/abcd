package com.cloud.baowang.agent.api.vo.withdrawConfig;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@I18nClass
@Schema(title = "代理提款配置 分页查询")
public class AgentWithdrawConfigPageVO {

    @Schema(title = "id")
    private String id;

    @Schema(title = "状态 1开启 0关闭")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SWITCH_STATUS)
    private Integer status;
    @Schema(title = "状态 1开启 0关闭 -1删除")
    private String statusText;

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "代理姓名")
    private String name;

    @Schema(title = "代理类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_TYPE)
    private Integer agentType;
    @Schema(title = "代理类型 文本")
    private String agentTypeText;

    @Schema(title = "代理状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_STATUS)
    private String agentStatus;
    @Schema(title = "代理状态 文本")
    private String agentStatusText;

    @Schema(title = "风控层级id")
    private String riskLevelId;
    @Schema(title = "风控层级名称")
    private String riskLevelName;
}
