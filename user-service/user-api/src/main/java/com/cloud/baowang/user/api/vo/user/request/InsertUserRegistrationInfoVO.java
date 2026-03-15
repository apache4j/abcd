package com.cloud.baowang.user.api.vo.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "插入-会员注册信息")
public class InsertUserRegistrationInfoVO {

    @Schema(description = "会员Id")
    private String memberId;

    @Schema(description = "会员注册信息(手机号/邮箱)")
    private String memberAccount;

    @Schema(description = "会员姓名")
    private String memberName;
    @Schema(description = "手机号码")
    private String phone;
    @Schema(description = "邮箱")
    private String email;

    @Schema(title ="主货币")
    private String mainCurrency;

    @Schema(description = "会员类型")
    private String memberType;

    @Schema(description = "上级代理")
    private String superiorAgent;

    @Schema(title ="上级代理id")
    private String agentId;

    @Schema(description = "注册IP")
    private String registerIp;

    @Schema(description = "IP归属地")
    private String ipAttribution;

    @Schema(description = "终端设备号")
    private String terminalDeviceNumber;

    @Schema(description = "注册终端")
    private String registerTerminal;

    @Schema(description = "注册域名")
    private String memberDomain;

    @Schema(description = "创建人id")
    private String creator;

    @Schema(description = "更新人id")
    private String updater;

    @Schema(title = "站点code")
    private String siteCode;
}
