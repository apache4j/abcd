package com.cloud.baowang.agent.api.vo.depositWithdraw;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(title = "代理注册信息")
@I18nClass
public class AgentRegisterInfoVO {

    @Schema(description="注册时间")
    private Long registrationTime;

    @Schema(description="最后登陆时间")
    private Long lastLoginTime;

    @Schema(description="注册端")
    @I18nField
    private String registerTerminal;

    @Schema(description="注册IP")
    private String registerIp;

    @Schema(description="终端设备号")
    private String terminalDeviceNumber;

    @Schema(description="账号类型")
    private String memberType;

    @Schema(description="注册域名")
    private String memberDomain;

    @Schema(description="上级代理")
    private String parentAgentName;

}
