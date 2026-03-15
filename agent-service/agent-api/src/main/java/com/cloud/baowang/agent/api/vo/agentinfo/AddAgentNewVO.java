package com.cloud.baowang.agent.api.vo.agentinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


/**
 * @author: xiaozhi
 */
@Data
@Schema(title = "新增代理 新版 Request")
public class AddAgentNewVO {

    @Schema(title = "代理账号")
    @NotEmpty(message = "代理账号不能为空")
    private String agentAccount;
    @Schema(title = "站点编号",hidden = true)
    private String siteCode;

    @Schema(title = "登录密码")
    @NotEmpty(message = "登录密码不能为空")
    private String agentPassword;

    @Schema(title = "设备号")
    private String deviceNo;

    @Schema(description = "佣金方案")
    private String planCode;

    @Schema(description = "会员福利 多个中间逗号分隔 公共下拉框参数: agent_user_benefit")
    private String userBenefit;
}
