package com.cloud.baowang.activity.api.vo;

import cn.hutool.db.Page;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 兑换码配置类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityRedemptionCodeConfigVO  implements Serializable {

    @Schema(description = "id")
    private String id;

    @Schema(description = "操作人",hidden = true)
    private String operator;
    /**
     * 站点code
     */
    @Schema(title = "站点code",hidden = true)
    private String siteCode;

    /**
     * 兑换码订单号
     */
    @Schema(description = "兑换码订单号")
    private String orderNo;

    /**
     * 兑换码类型，0:通用兑换码，1:唯一兑换码
     */
    @Schema(description = "兑换码类型，0:通用兑换码，1:唯一兑换码")
    private Integer category;

    /**
     * 平台币或法币：0:平台币，1:法币
     */
    @Schema(description = "平台币或法币：0:平台币，1:法币")
    private String platformOrFiatCurrency;

    @Schema(description = "兑换码列表")
    private List<SiteActivityRedemptionCodeDetailVO> siteActivityRedemptionCodeDetailVOS;

}
