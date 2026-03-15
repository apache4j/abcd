package com.cloud.baowang.user.api.vo.ads;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员基本信息VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "广告埋点vo")
public class UserRechargeEventVO implements Serializable {

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "会员id")
    private String userId;

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


    @Schema(description = "邮箱")
    private String email;



    @Schema(description = "风控层级id")
    private String riskLevelId;

    @Schema(description = "风控层级")
    private String riskLevel;

    @Schema(description = "首存时间")
    private Long firstDepositTime;

    @Schema(description = "次存时间")
    private Long secondDepositTime;


    @Schema(description = "最后登录时间")
    private Long lastLoginTime;

    @Schema(description = "最后登录IP")
    private String lastLoginIp;


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


    @Schema(description = "主货币")
    private String mainCurrency;

    @Schema(description = "邀请人")
    private String inviter;
    @Schema(description = "邀请人Id")
    private String inviterId;


    @Schema(description = "用户语言")
    private String language;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "充值金额")
    private BigDecimal amount;

    @Schema(description = "平台来源 1-fb 2-google")
    private String platformSource;

    private String eventId;


    private Integer deviceType;

    private String reqIp;


}
