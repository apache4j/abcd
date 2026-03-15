package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 2024/8/6 10:05
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "站点场馆授权分页返回参数")
@I18nClass
public class SiteVenueResponsePageVO {

    @Schema(title = "场馆ID")
    private String venueId;

    @Schema(title = "场馆名称")
    @I18nField
    private String venueName;

    @Schema(title = "场馆代码")
    private String venueCode;

    @Schema(title = "场馆类型 1:体育,2:视讯,3:棋牌,4:电子,5:彩票,6:斗鸡,7:电竞")
    private Integer venueType;

    @Schema(title = "游戏类名称")
    @I18nField
    private String venueTypeName;

    @Schema(title = "预授权游戏数")
    private Integer gameNum;

    @Schema(title = "负盈利手续费")
    private BigDecimal handlingFee;

    @Schema(description = "场馆有效流水费率")
    private BigDecimal validProportion;

    @Schema(title = "状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PLATFORM_CLASS_STATUS_TYPE)
    private Integer status;

    @Schema(title = "状态名称")
    private String statusText;

    @Schema(title = "操作人")
    private String operator;

    @Schema(title = "操作时间")
    private Long operatorTime;

    @Schema(description = "选中状态(0:未选中,1:选中)")
    private Integer chooseFlag;

    @Schema(description = "场馆接入类型:1:数据源,2:场馆,3:游戏")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_JOIN_TYPE)
    private Integer venueJoinType;

    @Schema(title = "场馆接入类型:1:数据源,2:场馆,3:游戏")
    private String venueJoinTypeText;

}
