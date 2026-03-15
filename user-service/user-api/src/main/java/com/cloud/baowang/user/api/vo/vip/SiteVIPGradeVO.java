package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 2024/8/2 10:05
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(title = "VIP权益配置返回对象")
@I18nClass
public class SiteVIPGradeVO implements Serializable {

    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(description = "VIP等级code")
    private Integer vipGradeCode;

    @Schema(description = "VIP等级名称")
    private String vipGradeName;

    @Schema(description = "VIP段位code")
    private Integer vipRankCode;
    @I18nField
    @Schema(description = "vip段位名称")
    private String vipRankName;


    @Schema(description = "升级条件所需XP")
    private BigDecimal upgradeXp;

    @Schema(description = "晋级礼金")
    private BigDecimal upgradeBonus;

    @Schema(description = "图标")
    private String picIcon;
    @Schema(description = "完整图标地址")
    private String picIconImage;
}
