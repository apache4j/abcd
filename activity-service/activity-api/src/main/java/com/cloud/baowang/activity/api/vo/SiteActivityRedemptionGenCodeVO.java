package com.cloud.baowang.activity.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 兑换码生成表
 * @author brence
 * @date 2025-10-27
 */
@Data
@Builder
@AllArgsConstructor
public class SiteActivityRedemptionGenCodeVO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 兑换码生成ID
     */
    @Schema(description = "兑换码生成ID")
    private Long id;

    /**
     * 兑换码明细表主键
     */
    @Schema(description = "兑换码明细表主键")
    private Long activityDetailId;

    /**
     * 6位兑换码，生成规则:数值字母随机组合
     */
    @Schema(description = "6位兑换码，生成规则:数值字母随机组合")
    @NotNull(message = "兑换码不能为空")
    private String code;

    /**
     * 批次号，10位数字
     */
    @Schema(description = "批次号")
    @NotNull(message = "批次号不能为空")
    private String batchNo;

    /**
     * 币种，冗余字段
     */
    @Schema(description = "币种")
    private String currency;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Long createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Long updatedTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updater;

    @Schema(description = "状态,0:未兑换,1:已兑换")
    private Integer status;
}
