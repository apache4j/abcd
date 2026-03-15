package com.cloud.baowang.agent.api.vo.manualup;

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
@Schema(description = "代理注册信息")
@I18nClass
public class GetAgentRegisterInfoVO {
    @Schema(description = "站点Code")
    private String siteCode;

    @Schema(description = "注册时间")
    private Long registerTime;

    @Schema(description = "最后登陆时间")
    private Long lastLoginTime;
    @Schema(description = "注册端")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REGISTRY)
    private Integer registerDevice;
    @Schema(description = "注册端")
    private String registerDeviceText;
    @Schema(description = "注册IP")
    private String registerIp;

    @Schema(description = "终端设备号")
    private String deviceNumber;

    @Schema(description = "账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_TYPE)
    private Integer agentType;

    @Schema(description = "账号类型")
    private String agentTypeText;

    @Schema(description = "注册域名")
    private String registerDomain;
}
