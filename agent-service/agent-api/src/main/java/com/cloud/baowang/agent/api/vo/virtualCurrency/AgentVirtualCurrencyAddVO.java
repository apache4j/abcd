package com.cloud.baowang.agent.api.vo.virtualCurrency;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
@Schema(title = "代理虚拟币添加对象")
public class AgentVirtualCurrencyAddVO {

    @Schema(description =  "虚拟币账号地址")
    @NotEmpty(message = "虚拟币账号地址不能为空")
    private String virtualCurrencyAddress;

    @Schema(description =  "确认虚拟币账号地址")
    @NotEmpty(message = "确认虚拟币账号地址不能为空")
    private String confirmVirtualCurrencyAddress;

    @Schema(description =  "虚拟币种类")
    @NotEmpty(message = "虚拟币种类不能为空")
    private String virtualCurrencyType;

    @Schema(description =  "虚拟币协议")
    @NotEmpty(message = "虚拟币协议不能为空")
    private String virtualCurrencyProtocol;

    @Schema(description =  "虚拟币账号地址-别名")
    @NotEmpty(message = "虚拟币账号地址-别名不能为空")
    private String virtualCurrencyAddressAlias;

    @Schema(description =  "登录密码")
    private String loginPassword;

    private String agentAccount;

    private String agentId;

}
