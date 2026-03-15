package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "会员注册信息")
@TableName(value = "user_registration_info")
public class UserRegistrationInfoPO extends BasePO {

    @Schema(title ="注册时间")
    private Long registrationTime;

    @Schema(title ="会员Id")
    private String memberId;

    @Schema(title ="会员账号")
    private String memberAccount;

    @Schema(title ="会员姓名")
    private String memberName;

    @Schema(title ="主货币")
    private String mainCurrency;

    @Schema(title ="会员类型")
    private String memberType;

    @Schema(title ="上级代理")
    private String superiorAgent;

    @Schema(title ="上级代理id")
    private String agentId;

    @Schema(title ="注册IP")
    private String registerIp;

    @Schema(title ="IP归属地")
    private String ipAttribution;

    @Schema(title ="终端设备号")
    private String terminalDeviceNumber;

    @Schema(title ="注册终端")
    private String registerTerminal;

    @Schema(title ="会员域名")
    private String memberDomain;

    @Schema(title ="邮箱")
    private String email;

    @Schema(title ="电话号码")
    private String phone;

    /**
     * 站点code
     */
    @TableField(value = "site_code")
    private String siteCode;

}
