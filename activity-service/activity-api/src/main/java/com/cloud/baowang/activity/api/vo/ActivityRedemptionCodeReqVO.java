package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 兑换码请求对象--列表页面的查询条件封装
 */
@Data
@AllArgsConstructor
public class ActivityRedemptionCodeReqVO extends PageVO implements Serializable {

    private String id;

    @Schema(description = "操作人")
    private String creator;
    @Schema(description = "最近操作人")
    private String updater;

    @Schema(description = "币种")
    private String currency;

    /**
     * 操作开始时间
     */
    @Schema(description = "操作开始时间")
    private Long createdStartTime;

    /**
     * 操作结束时间
     */
    @Schema(description = "操作结束时间")
    private Long createdEndTime;

    /**
     * 最近操作时间
     */
    @Schema(description = "最近操作开始时间")
    private Long updatedStartTime;

    /**
     * 最近操作时间
     */
    @Schema(description = "最近操作结束时间")
    private Long updatedEndTime;

    @Schema(description = "兑换码类型：0:通用兑换码,1:唯一兑换码")
    private Integer category;

    @Schema(description = "兑换条件,1:无限制用户，2:存款用户，3：当天存款用户（兑换码生效当天）；4：三天内存款用户")
    private Integer condition;

    @Schema(description = "订单号")
    private String orderNo;
}
