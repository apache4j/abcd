package com.cloud.baowang.user.api.vo.user;

import com.cloud.baowang.user.api.enums.UserAccountTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 会员基本信息
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Accessors(chain = true)
@Schema(description = "会员基本信息")
public class GetByUserAccountVO {
    @Schema(description = "id")
    private String id;
    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "会员ID")
    private String userId;
    @Schema(description = "会员注册信息(手机号码或邮箱)")
    private String userRegister;

    @Schema(description = "姓名")
    private String userName;

    @Schema(description = "主货币")
    private String mainCurrency;

    /**
     * {@link  UserAccountTypeEnum}
     */
    @Schema(description = "账号类型 1测试 2正式 3商务 4置换")
    private String accountType;

    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    private String accountStatus;

    @Schema(description = "首存时间")
    private Long firstDepositTime;

    @Schema(description = "首存金额")
    private BigDecimal firstDepositAmount;

    @Schema(description = "次存时间")
    private Long secondDepositTime;

    @Schema(description = "次存金额")
    private BigDecimal secondDepositAmount;
    @Schema(description = "最后登录ip")
    private String lastLoginIp;
    @Schema(description = "最后登录时间")
    private Long lastLoginTime;
    @Schema(description = "最后登录设备号")
    private String lastDeviceNo;

    @Schema(description = "最后下注时间")
    private Long lastBetTime;

    @Schema(description = "上级代理id")
    private String superAgentId;

    @Schema(description = "上级代理账号")
    private String superAgentAccount;

    @Schema(description = "vip当前等级")
    private Integer vipGradeCode;
    @Schema(description = "vipRank")
    private Integer vipRank;

    @Schema(description = "会员标签id")
    private String userLabelId;

    @Schema(description = "账号备注")
    private String acountRemark;

    @Schema(description = "邀请码")
    private String friendInviteCode;

    @Schema(description = "注册时间")
    private Long registerTime;

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

    @Schema(description = "头像图标")
    private String avatarCode;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "vip升级后的等级")
    private Integer vipRankUp;
    @Schema(description = "绑定代理的时间")
    private Long bindingAgentTime;

    @Schema(description = "注册端")
    private Integer registry;

    @Schema(description = "代理对账号备注")
    private String agentRemark;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "出生日期")
    private String birthday;
    @Schema(description = "身份是否认证 0-未认证 1-已认证")
    private Integer authStatus;
}
