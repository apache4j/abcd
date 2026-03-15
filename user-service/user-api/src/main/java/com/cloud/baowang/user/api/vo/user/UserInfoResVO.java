package com.cloud.baowang.user.api.vo.user;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.cloud.baowang.user.api.enums.UserAccountTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "会员信息返回实体")
public class UserInfoResVO {

    @Schema(description = "会员id")
    private String userId;
    @Schema(description = "会员账号")
    private String userAccount;


    @Schema(description = "密码")
    private String password;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "加密盐")
    private String salt;

    @Schema(description = "姓名")
    private String userName;

    @Schema(description = "性别")
    private Integer gender;

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

    @Schema(description = "主货币")
    private String mainCurrency;

    /**
     * {@link UserAccountTypeEnum}
     */
    @Schema(description = "账号类型 1-测试 2-正式")
    private String accountType;

    @Schema(description = "账号状态 1正常 2登录锁定 3游戏锁定 4充提锁定")
    private String accountStatus;

    @Schema(description = "风控层级id")
    private String riskLevelId;

    @Schema(description = "首存时间")
    private Long firstDepositTime;

    @Schema(description = "首存金额")
    private BigDecimal firstDepositAmount;

    @Schema(description = "次存时间")
    private Long secondDepositTime;

    @Schema(description = "次存金额")
    private BigDecimal secondDepositAmount;

    public BigDecimal getFirstDepositAmount() {
        if (ObjectUtils.isEmpty(firstDepositAmount)) {
            firstDepositAmount = BigDecimal.valueOf(0);
        }
        return firstDepositAmount;
    }

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

    @Schema(description = "注册域名")
    private String memberDomain;

    @Schema(description = "注册ip")
    private String registerIp;

    @Schema(description = "注册端")
    private Integer registry;

    @Schema(description = "上级代理id")
    private String superAgentId;

    @Schema(description = "上级代理账号")
    private String superAgentAccount;

    @Schema(description = "绑定代理的时间")
    private Long bindingAgentTime;

    @Schema(description = "会员标签id")
    private String userLabelId;

    @Schema(description = "转代次数")
    private Integer transAgentTime;

    @Schema(description = "vip当前等级")
    private Integer vipGradeCode;

    @Schema(description = "vip段位")
    private Integer vipRank;

    @Schema(description = "vip升级后的等级")
    private Integer vipGradeUp;

    @Schema(description = "取款密码")
    private String withdrawPwd;

    @Schema(description = "账号备注")
    private String acountRemark;

    @Schema(description = "代理对账号备注")
    @Length(max = 400,message = "备注不能超过超过400个字符！")
    private String agentRemark;

    @Schema(description = "用户头像编号")
    private String avatarCode;

    @Schema(description = "用户头像标识|img相对路径")
    private String avatar;

    @Schema(description = "设备号")
    private String deviceNo;

    @Schema(description = "设备风控层级id")
    private String deviceControlId;

    @Schema(description = "最后登录设备号")
    private String lastDeviceNo;

    @Schema(description = "充值热钱包地址")
    private String walletAddress;

    @Schema(description = "代理归属修改次数")
    private Integer changeAgentCount;

    @Schema(description = "邀请码")
    private String friendInviteCode;

    @Schema(description = "邀请人")
    private String inviter;

    @Schema(description = "邀请人Id")
    private String inviterId;

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "用户语言")
    private String language;


    @Schema(description = "IP归属地")
    private String ipAddress;
}
