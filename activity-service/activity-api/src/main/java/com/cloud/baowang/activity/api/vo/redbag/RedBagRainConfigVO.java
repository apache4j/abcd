package com.cloud.baowang.activity.api.vo.redbag;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@I18nClass
@Schema(description = "红包雨活动中奖配置附表")
public class RedBagRainConfigVO {
    @Schema(description = "站点code", hidden = true)
    private String siteCode;

    @Schema(description = "段位code")
    private Integer vipRankCode;

    @I18nField
    @Schema(description = "段位名称")
    private String vipRankName;

    @Schema(description = "有效红包数量上限")
    private Integer redBagMaximum;

    @Schema(description = "红包金额类型 1 固定金额 2 随机金额")
    private Integer amountType;

    @Schema(description = "序号")
    private Integer sort;

    @Schema(description = "段位中奖配置")
    private List<RedBagRainRankConfigVO> rankConfigList;

}
