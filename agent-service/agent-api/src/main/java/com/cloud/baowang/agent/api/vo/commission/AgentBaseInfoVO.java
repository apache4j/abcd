package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/10/28 12:23
 * @description: 佣金审核代理基本信息
 */
@Data
@I18nClass
@Schema(title = "佣金审核代理基本信息", description = "佣金审核代理基本信息")
public class AgentBaseInfoVO {
    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理类型 1正式 2测试 3合作")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_TYPE)
    private Integer agentType;

    @Schema(description = "代理类型 文本")
    private String agentTypeText;

    @Schema(description = "代理类别 1常规代理 2流量代理")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_CATEGORY)
    private Integer agentCategory;

    @Schema(description = "代理类别名称")
    private String agentCategoryText;

    @Schema(description = "代理标签")
    private String agentLabelName;

    @Schema(description = "佣金方案Id")
    private String planId;

    @Schema(title = "佣金方案名称")
    private String planName;

    @Schema(description = "会员福利 多个中间逗号分隔")
    private String userBenefit;

    @Schema(description = "会员福利名称")
    private String userBenefitText;

    @Schema(description = "注册时间")
    private Long registerTime;

    @Schema(description = "注册IP")
    private String registerIp;

    @Schema(description = "最后登录时间")
    private Long lastLoginTime;

    @Schema(description = "最后登录IP")
    private String lastLoginIp;

    @Schema(description = "累计佣金所得")
    private BigDecimal commissionTotal;

    @Schema(title = "币种", description = "币种")
    private String currency;
}
