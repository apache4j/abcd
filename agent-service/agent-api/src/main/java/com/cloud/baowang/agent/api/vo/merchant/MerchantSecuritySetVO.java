package com.cloud.baowang.agent.api.vo.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "代理安全设置响应对象")
public class MerchantSecuritySetVO {

    @Schema(description = "邮箱设置 1已设置 0 未设置")
    private Integer emailSet;

    @Schema(description = "登录密码设置 1已设置 0 未设置")
    private Integer agentPasswordSet;

    @Schema(description = "谷歌验证秘钥 1已设置 0未设置")
    private Integer googleAuthKeySet;

}
