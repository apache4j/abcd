package com.cloud.baowang.wallet.api.vo.user;


import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 会员基本信息VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "钱包会员基本信息")
public class WalletUserInfoVO implements Serializable {

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "会员id")
    private String userId;

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "会员注册信息(手机号码或邮箱)")
    private String userRegister;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "加密盐")
    private String salt;

    @Schema(description = "姓名")
    private String userName;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "性别名称")
    private String genderName;

    @Schema(description = "出生日期")
    private String birthday;

    @Schema(description = "区号")
    private String areaCode;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "手机号码验证状态 0 未验证  1已验证")
    private Integer phoneStatus;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "邮箱验证状态 0 未验证  1已验证")
    private Integer mailStatus;

    @Schema(description = "账号类型 1测试 2正式")
    private String accountType;

    @Schema(description = "账号类型名称 1测试 2正式")
    private String accountTypeName;

    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    private String accountStatus;

    @Schema(description = "账号状态名称 1正常 2登录锁定 3游戏锁定 4充提锁定")
    private List<CodeValueVO> accountStatusName;

    @Schema(description = "风控层级id")
    private String riskLevelId;

    @Schema(description = "风控层级")
    private String riskLevel;

    @Schema(description = "首存时间")
    private Long firstDepositTime;

    @Schema(description = "次存时间")
    private Long secondDepositTime;

    @Schema(description = "次存金额")
    private BigDecimal secondDepositAmount;


    @Schema(description = "首存金额")
    private BigDecimal firstDepositAmount;

    @Schema(description = "最后登录时间")
    private Long lastLoginTime;

    @Schema(description = "最后登录IP")
    private String lastLoginIp;

    @Schema(description = "离线天数")
    private Integer offlineDays;

    @Schema(description = "累计登录天数")
    private Integer onlineDays;

    @Schema(description = "注册时间")
    private Long registerTime;

    @Schema(description = "会员域名")
    private String memberDomain;

    @Schema(description = "注册ip")
    private String registerIp;

    @Schema(description = "注册ip风控层级")
    private String registerIpRiskLevel;

    @Schema(description = "注册端")
    private Integer registry;

    @Schema(description = "注册端名称")
    private String registryName;

    @Schema(description = "上级代理id")
    private String superAgentId;

    @Schema(description = "上级代理账号")
    private String superAgentAccount;

    @Schema(description = "上级代理名称")
    private String superAgentName;

    @Schema(description = "会员标签id")
    private String userLabelId;

    @Schema(description = "会员标签名称")
    private String userLabelName;

    @Schema(description = "转代次数")
    private Integer transAgentTime;

    @Schema(description = "vip当前等级code")
    private Integer vipGradeCode;

    @Schema(description = "vip段位")
    private Integer vipRank;

    @Schema(description = "vip升级后的等级code")
    private Integer vipGradeUp;

    @Schema(description = "设备号")
    private String deviceNo;

    @Schema(description = "最后登录设备号")
    private String lastDeviceNo;

    @Schema(description = "设备风控层级")
    private String deviceControlId;

    @Schema(description = "取款密码")
    private String withdrawPwd;

    @Schema(description = "账号备注")
    private String acountRemark;

    private String id;

    private String creator;

    private Long createdTime;

    private String updater;

    private Long updatedTime;

    @Schema(description = "充值热钱包地址")
    private String walletAddress;

    @Schema(description = "是否需要验证 0 不需要  1需要")
    private Integer needVerify;

    @Schema(description = "绑定代理的时间")
    private Long bindingAgentTime;

    @Schema(description = "用户头像code")
    private String avatarCode;

    @Schema(description = "用户头像")
    private String avatar;


    @Schema(description = "主货币")
    private String mainCurrency;

    @Schema(description = "邀请人")
    private String inviter;
    @Schema(description = "邀请人Id")
    private String inviterId;


    @Schema(description = "用户语言")
    private String language;

    @Schema(description = "登录状态 是否第一次登录")
    private Boolean firstLogin;

    @Schema(description = "新手指引步骤")
    private Integer step;

    @Schema(description = "任务领取状态 0-可领取 1-已领取 2-奖励已过期")
    private Integer receiveStatus;

    @Schema(description = "身份是否认证 0-未认证 1-已认证")
    private Integer authStatus;
}
