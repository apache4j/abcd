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

/**
 * @Author : 小智
 * @Date : 2024/8/6 11:11
 * @Version : 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(title = "站点游戏授权返回分页对象")
@I18nClass
public class SiteGameResponsePageVO {

    @Schema(title = "主键id(用于修改，新增)")
    private String id;

    @Schema(title = "游戏ID")
    private String gameId;

    @Schema(title = "游戏名称")
    private String gameName;

    @Schema(title = "场馆名称")
    @I18nField
    private String venueName;

    @Schema(title = "场馆类型 1:体育,2:视讯,3:棋牌,4:电子,5:彩票,6:斗鸡,7:电竞")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_TYPE)
    private Integer venueType;

    @Schema(title = "场馆类型名称")
    private String venueTypeText;

    @Schema(title = "状态code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PLATFORM_CLASS_STATUS_TYPE)
    private Integer status;

    @Schema(title = "状态名称")
    private String statusText;

    @Schema(title = "操作时间")
    private Long operatorTime;

    @Schema(title = "操作人")
    private String operator;

    @Schema(description = "选中状态(0:未选中,1:选中)")
    private Integer chooseFlag;
}
