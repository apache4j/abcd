package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.util.StringUtils;

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
@TableName("agent_info")
@Schema(description = "代理基本信息")
public class AgentInfoPO extends SiteBasePO {

    @Schema(description = "代理编号")
    private String agentId;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "出生日期")
    private Long birthday;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "QQ")
    private String qq;

    @Schema(description = "telegram")
    private String telegram;

    @Schema(description = "代理头像code")
    private String avatarCode;

    @Schema(description = "siteCode")
    private String siteCode;

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

    @Schema(description = "加密盐")
    private String salt;

    @Schema(description = "登录密码")
    private String agentPassword;

    @Schema(description = "支付密码")
    private String payPassword;

    @Schema(description = "首存时间")
    private Long firstDepositTime;

    @Schema(description = "首存金额")
    private BigDecimal firstDepositAmount;

    @Schema(description = "代理类型 1正式 2测试 3合作")
    private Integer agentType;

    @Schema(description = "代理契约模式-佣金契约 1是 0否")
    private Integer contractModelCommission;

    @Schema(description = "代理契约模式-返点契约 1是 0否")
    private Integer contractModelRebate;

    @Schema(description = "账号状态 1正常 2登录锁定 3充提锁定(状态多选,用逗号分开)")
    private String status;

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

    @Schema(description = "注册ip")
    private String registerIp;

    @Schema(description = "最后登录时间")
    private Long lastLoginTime;

    @Schema(description = "离线天数")
    private Integer offlineDays;

    @Schema(description = "合营代码")
    private String inviteCode;

    @Schema(description = "代理标签id")
    private String agentLabelId;

    @Schema(description = "风控层级id")
    private String riskLevelId;

    @Schema(description = "是否有欠款  该代理对下级代理是否存在欠款 1有欠款 0无欠款")
    private Integer isAgentArrears;

    @Schema(description = "备注信息")
    private String remark;

    @Schema(description = "上级备注信息")
    private String superRemark;

    @Schema(description = "充值热钱包地址")
    private String walletAddress;

    @Schema(description = "短码链接")
    private String shortUrl;

    @Schema(description = "是否设置密保问题 1设置 0未设置")
    private Integer securitySet;

    @Schema(description = "谷歌验证秘钥")
    private String googleAuthKey;

    @Schema(description = "代理客户端 首页功能入口 PC端")
    private String homeButtonEntrance;

    @Schema(description = "代理客户端 首页功能入口 H5端")
    private String homeButtonEntranceH5;

    @Schema(description = "代理归属 1推广 2招商 3官资")
    private Integer agentAttribution;

    @Schema(description = "代理类别 1常规代理 2流量代理")
    private Integer agentCategory;

    @Schema(description = "IP白名单(只有流量代理需要)，使用英文逗号隔开")
    private String agentWhiteList;

    @Schema(description = "AES密钥(只有流量代理需要)，Base64编码的字符串")
    private String aesSecretKey;

    @Schema(description = "佣金方案")
    private String planCode;

    @Schema(description = "当前期佣金方案")
    private String currentPlanCode;

    @Schema(description = "会员福利 多个中间逗号分隔")
    private String userBenefit;
    /**
     * 绑定短链接时间
     */
    private Long bindShortUrlTime;
    /**
     * 绑定短链接操作人
     */
    private String bindShortUrlOperator;
    /**
     * 所属商务账号
     */
    private String merchantAccount;

    /**
     * 所属商务名称
     */
    private String merchantName;


    private String fbPixId;
    private String fbToken;
    private String googlePixId;
    private String googleToken;

    public String getFbPixId() {
        return StringUtils.hasText(this.fbPixId)?this.fbPixId:"";
    }

    public String getFbToken() {
        return StringUtils.hasText(this.fbToken)?this.fbToken:"";
    }

    public String getGooglePixId() {
        return StringUtils.hasText(this.googlePixId)?this.googlePixId:"";
    }

    public String getGoogleToken() {
        return StringUtils.hasText(this.googleToken)?this.googleToken:"";
    }
}
