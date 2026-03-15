package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 2024/8/7 11:44
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP等级配置编辑对象")
public class VIPGradeUpdateVO {

    @Schema(description = "VIP段位code")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String vipRankCode;

    @Schema(description = "VIP等级code")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String vipGradeCode;

    @Schema(description = "客户端显示名称",hidden = true)
    private String clientShow;

    @Schema(description = "升级条件所需xp")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Min(value = 0,message = ConstantsCode.PARAM_ERROR)
    private BigDecimal upgradeXp;

    @Schema(description = "晋级奖级")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Min(value = 0,message = ConstantsCode.PARAM_ERROR)
    private BigDecimal upgradeBonus;

    @Schema(description = "vip图标",hidden = true)
    private String picIcon;
}
