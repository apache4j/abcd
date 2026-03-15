package com.cloud.baowang.wallet.api.vo.fundrecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(description = "会员注册信息")
public class GetRegisterInfoVO {

    @Schema(description = "注册时间")
    private Long registrationTime;

    @Schema(description = "最后登陆时间")
    private Long lastLoginTime;

    @Schema(description = "最后下注时间")
    private Long lastBetTime;

    @Schema(description = "注册端")
    private String registerTerminal;

    @Schema(description = "注册IP")
    private String registerIp;

    @Schema(description = "终端设备号")
    private String terminalDeviceNumber;

    @Schema(description = "账号类型")
    private String memberType;

    @Schema(description = "注册域名")
    private String memberDomain;

    @Schema(description = "上级代理")
    private String superiorAgent;
}
