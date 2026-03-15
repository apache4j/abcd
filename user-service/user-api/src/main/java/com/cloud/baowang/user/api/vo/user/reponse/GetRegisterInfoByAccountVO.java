package com.cloud.baowang.user.api.vo.user.reponse;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(title = "获取会员注册信息")
@I18nClass
public class GetRegisterInfoByAccountVO {

    @Schema(title = "注册时间")
    private Long registrationTime;

    @Schema(title = "会员Id")
    private Long memberId;

    @Schema(title = "会员账号")
    private String memberAccount;

    @Schema(title = "会员姓名")
    private String memberName;

    @Schema(title = "主货币")
    private String mainCurrency;

    @Schema(title = "会员类型")
    private String memberType;

    @Schema(title = "上级代理")
    private String superiorAgent;

    @Schema(title = "注册IP")
    private String registerIp;

    @Schema(title = "IP归属地")
    private String ipAttribution;

    @Schema(title = "终端设备号")
    private String terminalDeviceNumber;

    @Schema(title = "注册终端")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REGISTRY)
    private String registerTerminal;
    @Schema(description = "注册终端 - Text")
    private String registerTerminalText;

    @Schema(title = "会员域名")
    private String memberDomain;
}
