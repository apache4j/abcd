package com.cloud.baowang.agent.api.vo.agentreview;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(description = "代理审核详情 返回")
@I18nClass
public class AgentReviewDetailsVO {

    /**
     * 站点编码
     */
    private String siteCode;


    @Schema(description = "id")
    private String id;

    @Schema(description = "父节点")
    private String parentId;

    @Schema(description = "上级代理账号")
    private String parentAccount;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理归属 1推广 2招商 3官资")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.AGENT_ATTRIBUTION)
    private Integer agentAttribution;

    @Schema(description = "代理归属 1推广 2招商 3官资 - Name")
    private String agentAttributionText;

    @Schema(description = "代理类别 1常规代理 2流量代理")
    @I18nField(type =I18nFieldTypeConstants.DICT,value = CommonConstant.AGENT_CATEGORY)
    private Integer agentCategory;

    @Schema(description = "代理类别 1常规代理 2流量代理 - Name")
    private String agentCategoryText;

    @Schema(description = "IP白名单(只有流量代理需要)，使用英文逗号隔开")
    private String agentWhiteList;

    @Schema(description = "代理类型 1正式 2测试 3合作")
    @I18nField(type =I18nFieldTypeConstants.DICT,value = CommonConstant.AGENT_TYPE)
    private Integer agentType;

    @Schema(description = "代理类型 1正式 2测试 3合作")
    private String agentTypeText;

    @Schema(description = "代理层级")
    private Integer level;

    @Schema(description = "代理层级名称")
    private String levelName;

    @Schema(description = "代理线层级上限")
    private Integer maxLevel;

    @Schema(description = "申请信息")
    private String applyInfo;

    @Schema(description = "申请时间")
    private Long applyTime;

    @Schema(description = "申请人")
    private String applicant;

    @Schema(description = "一审完成时间")
    private Long oneReviewFinishTime;

    @Schema(description = "一审人")
    private String reviewer;

    @Schema(description = "一审备注")
    private String reviewRemark;

    @Schema(description = "佣金方案")
    private String planCode;

    @Schema(description = "佣金方案名称")
    private String planCodeName;

    @Schema(description = "会员福利 多个中间逗号分隔")
    @I18nField(type =I18nFieldTypeConstants.DICT,value = CommonConstant.AGENT_USER_BENEFIT)
    private String userBenefit;

    @Schema(description = "会员福利")
    private String userBenefitText;

    /**
     * 所属商务账号
     */
    @Schema(description = "商务账号")
    private String merchantAccount;

    /**
     * 所属商务名称
     */
    @Schema(description = "商务名称")
    private String merchantName;
}
