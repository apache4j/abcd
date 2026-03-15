package com.cloud.baowang.agent.api.vo.agentreview.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 商务后台-登陆返回对象
 */
@Data
@Builder
@Schema(description = "商务后台-登陆返回对象")
public class AgentMerchantResultVO {

    /**
     * ID
     */
    private String id;

    @Schema(description =  "商户ID")
    private String merchantId;

    @Schema(description =  "商务账号")
    private String merchantAccount;
    @Schema(description =  "商务名称")
    private String merchantName;
    @Schema(description =  "当前语言")
    private String language;
    @Schema(description =  "token")
    private String token;
    @Schema(description =  "最后登录时间")
    private Long lastLoginTime;
    @Schema(description =  "token过期时间")
    private Long expireTime;
    @Schema(description =  "站点")
    private String siteCode;



}
