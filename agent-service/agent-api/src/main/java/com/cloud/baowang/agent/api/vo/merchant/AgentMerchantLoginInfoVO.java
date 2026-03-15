package com.cloud.baowang.agent.api.vo.merchant;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "商务登录信息")
@I18nClass
public class AgentMerchantLoginInfoVO implements Serializable {

    /**
     * 站点
     */
    private String siteCode;

    @Schema(description = "登录时间")
    private Long createdTime;

    /**
     * 登录状态
     */
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.LOGIN_TYPE)
    @Schema(description = "登录状态")
    private Integer loginType;

    private String loginTypeText;

    /**
     * 商务账号
     */
    @Schema(description = "商务账号")
    private String merchantAccount;

    /**
     * 商务名称
     */
    @Schema(description = "商务名称")
    private String merchantName;

    @Schema(description = "登录ip")
    private String loginIp;

    /**
     * ip归属地
     */
    @Schema(description = "ip归属地")
    private String ipAddress;

    /**
     * 登录IP风控层级
     */
    @Schema(description = "ip风控层级")
    private String riskIpLevel;

    /**
     * 登录网址
     */
    @Schema(description = "登录网址")
    private String loginAddress;

    /**
     * 登录终端
     */
    @Schema(description = "登录终端")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.DEVICE_TYPE)
    private String loginTerminal;

    @Schema(description = "登录终端")
    private String loginTerminalText;

    /**
     * 终端设备号
     */
    @Schema(description = "终端设备号")
    private String terminalDeviceNo;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;


}
