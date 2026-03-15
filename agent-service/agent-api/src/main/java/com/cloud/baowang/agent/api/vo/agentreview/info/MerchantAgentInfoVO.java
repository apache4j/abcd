package com.cloud.baowang.agent.api.vo.agentreview.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商务包含总代信息")
public class MerchantAgentInfoVO {
    @Schema(description = "商务账号")
    private String merchantAccount;
    @Schema(description = "商务名称")
    private String merchantName;
    @Schema(description = "总代人数")
    private Long agentCount;

    /**
     * 商务id-短
     */
    @Schema(description = "商务id")
    private String merchantId;

    /**
     * 当前语言
     */
    @Schema(description = "当前语言")
    private String language;

    /**
     * 账号状态 1正常 2登录锁定
     */
    @Schema(description = "账号状态 1正常 2登录锁定")
    private String status;

    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间")
    private Long lastLoginTime;

    /**
     * 站点编码
     */
    @Schema(description = "站点编码")
    private String siteCode;

    /**
     * 风控id
     */
    @Schema(description = "风控id")
    private String riskId;
    /**
     * 注册时间
     */
    @Schema(description = "注册时间")
    private Long registerTime;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;
}
