package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Desciption:
 * @Author: qiqi
 * @Date 2025-09-11
 **/
@Data
@Schema(description = "站点存款通道VIP等级使用范围")
public class SiteRechargeChangeVipUseScopeVO {
    @Schema(description = "主键ID")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String id;

    /**
     * VIP等级使用范围
     */
    @Schema(description = "VIP等级使用范围，字符串 逗号隔开")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String vipGradeUseScope;

}


