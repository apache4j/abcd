package com.cloud.baowang.user.api.vo.user;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Schema(description = "会员注册信息 查询返回对象")
@I18nClass
public class UserRegistrationInfoResVO implements Serializable {

    @Schema(description = "注册时间")
    private Long registrationTime;

    @Schema(title = "会员Id")
    private String memberId;

    @Schema(description = "账号类型 1测试 2正式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private String memberType;
    @Schema(description = "账号类型 1测试 2正式 - Text")
    private String memberTypeText;

    @Schema(description = "主货币")
    private String mainCurrency;

    @Schema(description = "上级代理")
    private String superiorAgent;

    @Schema(description = "上级代理Id")
    private String agentId;

    @Schema(description = "注册信息")
    private String memberAccount;

    @Schema(description = "注册IP")
    private String registerIp_$_registerIpLevel;

    public String getRegisterIp_$_registerIpLevel() {
        return registerIp;
    }

    @Schema(description = "注册IP", hidden = true)
    private String registerIp;

    @Schema(description = "注册IP风控层级", hidden = true)
    private String registerIpLevel;

    @Schema(description = "IP归属地")
    private String ipAttribution;

    @Schema(description = "注册终端")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REGISTRY)
    private String registerTerminal;
    @Schema(description = "注册终端 - Text")
    private String registerTerminalText;

    @Schema(description = "终端设备号")
    private String terminalDeviceNumber_$_terminalDeviceNumberLevel;

    public String getTerminalDeviceNumber_$_terminalDeviceNumberLevel() {
        return terminalDeviceNumber;
    }

    @Schema(description = "终端设备号")
    private String terminalDeviceNumber;

    @Schema(description = "终端设备号 风控层级")
    private String terminalDeviceNumberLevel;
    @Schema(description = "区号")
    private String areaCode;

    @Schema(description = "区号")
    private String areaCode_$_phone;

    public String getAreaCode_$_phone() {
        return areaCode;
    }

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    private String siteCode;
}
