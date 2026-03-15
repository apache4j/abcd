package com.cloud.baowang.agent.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author kimi
 */
@Builder
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "后台-id请求对象")
public class AdjustCommissionVO {

    @Schema(description = "id")
    @NotEmpty(message = "id不能为空")
    private String id;

    /**
     * 数据脱敏 true 需要脱敏 false 不需要脱敏
     */
    private Boolean dataDesensitization = false;

    @Schema(title = "站点code",hidden = true)
    private String siteCode;


    @Schema(description = "调整负盈利佣金")
    @NotNull(message = "调整资金不能为空")
    private BigDecimal adjustCommissionAmount;

    @Schema(description = "负盈利佣金-备注")
    @NotNull(message = "备注不能为空")
    private String adjustCommissionRemark;

    @Schema(description = "操作人账号",hidden = true)
    private String operatorName;

    public AdjustCommissionVO(String id) {
        this.id = id;
    }
}
