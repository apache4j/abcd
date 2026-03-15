package com.cloud.baowang.agent.api.vo.agentreview.info;


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
@Schema(description = "代理基本信息")
public class AgentInfoVO {

    @Schema(description = "代理id")
    private String id;

    @Schema(description = "代理id-短")
    private String agentId;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "出生日期")
    private Long birthday;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "代理头像code")
    private String avatarCode;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "QQ")
    private String qq;

    @Schema(description = "telegram")
    private String telegram;

    @Schema(description = "代理当前语言")
    private String language;

    @Schema(description = "父节点")
    private String parentId;

    @Schema(description = "上级代理账号")
    private String parentAccount;

    @Schema(description = "层次id 逗号分隔")
    private String path;

    @Schema(description = "代理层级")
    private Integer level;

    @Schema(description = "代理线层级上限")
    private Integer maxLevel;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "首存时间")
    private Long firstDepositTime;

    @Schema(description = "首存金额")
    private BigDecimal firstDepositAmount;

    @Schema(description = "代理类型 1正式 2测试 3合作")
    private Integer agentType;

    @Schema(description = "代理类型 文本")
    private String agentTypeText;

    @Schema(description = "代理契约模式-佣金契约 1是 0否")
    private Integer contractModelCommission;

    @Schema(description = "代理契约模式-返点契约 1是 0否")
    private Integer contractModelRebate;

    @Schema(description = "账号状态 1正常 2登录锁定 3充提锁定(状态多选,用逗号分开)")
    private String status;
    @Schema(description = "账号状态 文本")
    private String statusText;


    @Schema(description = "契约状态 1已签约 0未签约")
    private Integer contractStatus;

    @Schema(description = "入口权限 1开启 0关闭")
    private Integer entrancePerm;

    @Schema(description = "强制编辑契约生效 1开启 0关闭")
    private Integer forceContractEffect;

    @Schema(description = "解除充值限制 1被限制 0解除")
    private Integer removeRechargeLimit;

    @Schema(description = "注册方式 1手动 2自动")
    private Integer registerWay;

    @Schema(description = "注册端")
    private Integer registerDeviceType;

    @Schema(description = "注册时间")
    private Long registerTime;

    @Schema(description = "最后登录时间")
    private Long lastLoginTime;

    @Schema(description = "离线天数")
    private Integer offlineDays;

    @Schema(description = "合营代码")
    private String inviteCode;

    @Schema(description = "代理标签id")
    private String agentLabelId;
    @Schema(description = "代理标签文本")
    private String agentLabelText;

    @Schema(description = "风控层级id")
    private String riskLevelId;

    @Schema(description = "风控层级文本")
    private String riskLevelText;

    @Schema(description = "是否有欠款  该代理对下级代理是否存在欠款 1有欠款 0无欠款")
    private Integer isAgentArrears;

    @Schema(description = "备注信息")
    private String remark;

    @Schema(description = "上级备注信息")
    private String superRemark;

    @Schema(description = "代理归属 1推广 2招商 3官资")
    private Integer agentAttribution;

    @Schema(description = "代理类别 1常规代理 2流量代理")
    private Integer agentCategory;

    // ----------------------------------------------------------------
    @Schema(description = "是否有支付密码(0:有支付密码,1:无支付密码)")
    private Integer isPayPassword;

    @Schema(description = "是否设置密保问题 1设置 0未设置")
    private Integer securitySet;

    @Schema(description = "是否是新IP登录  0 不是  1 是")
    private Integer isNewIp;

    @Schema(description = "谷歌验证秘钥")
    private String googleAuthKey;

    @Schema(title = "站点Code")
    private String siteCode;

    @Schema(description = "佣金方案")
    private String planCode;

    @Schema(description = "当前期佣金方案")
    private String currentPlanCode;

    @Schema(description = "会员福利 多个中间逗号分隔")
    private String userBenefit;
    /**
     * 绑定短链接时间
     */
    @Schema(description = "绑定短链接时间")
    private Long bindShortUrlTime;
    /**
     * 绑定短链接操作人
     */
    @Schema(description = "绑定短链接操作人")
    private String bindShortUrlOperator;

    @Schema(description = "商务账号")
    private String merchantAccount;

    @Schema(description = "商务名称")
    private String merchantName;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "FaceBook PixId")
    private String FbPixId;
    @Schema(description = "FaceBook Token")
    private String FbToken;
    @Schema(description = "Google Ads PixId")
    private String GooglePixId;
    @Schema(description = "Google Ads Token")
    private String GoogleToken;

}
