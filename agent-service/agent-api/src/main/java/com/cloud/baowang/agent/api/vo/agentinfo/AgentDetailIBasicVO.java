package com.cloud.baowang.agent.api.vo.agentinfo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 12/10/23 2:19 PM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "代理基本信息 返回对象")
@I18nClass
public class AgentDetailIBasicVO implements Serializable {

    /* 概要信息 */
    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "账号状态code")
    private String status;

    @Schema(title = "账号状态名称codes")
    private String statusNames;

    @Schema(title = "代理风控层级id")
    private String riskLevelId;

    @Schema(title = "代理风控层级名称")
    private String riskLevelName;

    @Schema(title = "代理标签id")
    private String agentLabelId;

    @Schema(title = "代理标签名称")
    private String agentLabelName;

    @Schema(title = "合营代码")
    private String inviteCode;

    @Schema(title = "直属上级")
    private String superAgent;

    @Schema(title = "代理层级")
    private Integer level;

    @Schema(title = "代理类型code")
    private String agentType;

    @Schema(title = "代理类型中文名称")
    private String agentTypeName;

    @Schema(title = "代理归属 1推广 2招商 3官资")
    private Integer agentAttribution;

    @Schema(title = "代理类别 1常规代理 2流量代理")
    private Integer agentCategory;
    @Schema(title = "代理类别 1常规代理 2流量代理 - Name")
    private String agentCategoryName;

    @Schema(title = "佣金方案")
    private String planCode;
    @Schema(title = "佣金方案名称")
    private String planCodeName;
    @Schema(title = "会员福利，多个")
    @I18nField(type = I18nFieldTypeConstants.DICT_CODE_TO_STR, value = CommonConstant.AGENT_USER_BENEFIT)
    private String userBenefit;
    @Schema(title = "会员福利名称")
    private String userBenefitText;

    @Schema(title = "入口权限code")
    private Integer entrancePerm;

    @Schema(title = "入口权限")
    private String entrancePermName;

    @Schema(title = "充值限制code")
    private Integer removeRechargeLimit;

    @Schema(title = "充值限制名称")
    private String removeRechargeLimitName;

    @Schema(title = "离线天数")
    private Integer offlineDays;

    @Schema(title = "IP白名单(只有流量代理需要)，使用英文逗号隔开")
    private String agentWhiteList;

    @Schema(title = "注册时间")
    private Long registerTime;

    @Schema(title = "注册端code")
    private Integer registerDeviceType;

    @Schema(title = "注册端名称")
    private String registerDeviceName;

    @Schema(title = "注册IP")
    private String registerIp;

    @Schema(title = "ip风控层级id")
    private String ipControlId;

    @Schema(title = "ip风控层级名称")
    private String ipControlName;

    @Schema(title = "最后登录时间")
    private Long lastLoginTime;


    /* 个人资料 */
    @Schema(title = "姓名")
    private String name;

    @Schema(title = "性别")
    private String gender;

    @Schema(title = "出生日期")
    private Long birthday;

    @Schema(title = "出生日期 格式化 YYYY-MM-DD")
    private String birthdayText;

    @Schema(title = "手机号码")
    private String phone;

    @Schema(title = "邮箱")
    private String email;

    @Schema(title = "QQ")
    private String qq;

    @Schema(title = "支付密码")
    private String payPassword;

    @Schema(title = "telegram")
    private String telegram;

    @Schema(title = "代理是否有修改审核的内容(0:无,1:有)")
    private Integer agentReviewStatus;

    @Schema(title = "支付密码审核中(0:无,1:有)")
    private Integer payPasswordStatus;

    @Schema(description = "商务账号")
    private String merchantAccount;

    @Schema(description = "商务名称")
    private String merchantName;

    @Schema(description = "FaceBook PixId")
    private String fbPixId;
    @Schema(description = "FaceBook Token")
    private String fbToken;
    @Schema(description = "Google Ads PixId")
    private String googlePixId;
    @Schema(description = "Google Ads Token")
    private String googleToken;


}
