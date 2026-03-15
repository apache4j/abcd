package com.cloud.baowang.agent.api.vo.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "编辑商务风控信息vo")
@Data
public class MerchantRiskUpdateVO {
    private String merchantAccount;
    private String siteCode;
    private String riskId;
    /**
     * 操作人
     */
    private String account;
}
