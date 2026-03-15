package com.cloud.baowang.agent.api.vo.agentreview.info;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 代理基本信息
 * </p>
 *
 * @author kimi
 * @since 2023-10-10
 */
@Data
@I18nClass
@Schema(description = "代理基本信息")
public class AgentInfoBasicVO {

    @Schema(description = "代理id")
    private String id;

    @Schema(description = "姓名")
    private String name;


    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理层级")
    private Integer level;

    @Schema(description = "代理类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_TYPE)
    private Integer agentType;

    @Schema(description = "代理类型文本")
    private String agentTypeText;

    @Schema(description = "账号状态 1正常 2登录锁定 3充提锁定(状态多选,用逗号分开)")
    @I18nField(type= I18nFieldTypeConstants.DICT_CODE_TO_STR,value = CommonConstant.AGENT_STATUS)
    private String status;

    @Schema(description = "账号状态 文本")
    private String statusText;

    @Schema(description = "风控层级id")
    private String riskLevelId;

    @Schema(description = "风控层级文本")
    private String riskLevelText;

    @Schema(description = "商务账号")
    private String merchantAccount;

    @Schema(description = "商务名称")
    private String merchantName;

}
