package com.cloud.baowang.agent.api.vo.domian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "添加链接访问量vo")
@Data
public class AddVisCountVO {
    @Schema(description = "域名")
    private String domainName;
    @Schema(description = "邀请码")
    private String inviteCode;
}
