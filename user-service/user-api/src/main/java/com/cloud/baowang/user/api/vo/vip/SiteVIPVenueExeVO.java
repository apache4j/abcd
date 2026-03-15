package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 2024/8/13 18:37
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "站点VIP场馆经验实体对象")
@I18nClass
public class SiteVIPVenueExeVO implements Serializable {

    @Schema(description = "场馆大类code 场馆类型 1:体育,2:视讯,3:棋牌,4:电子,5:彩票,6:斗鸡,7:电竞")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.VENUE_TYPE)
    private Integer venueType;

    @Schema(description = "场馆大类名称")
    private String venueTypeText;

    @Schema(description = "经验值")
    private BigDecimal experience;
}
