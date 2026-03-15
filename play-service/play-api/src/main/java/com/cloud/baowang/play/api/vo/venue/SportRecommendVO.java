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

import java.io.Serializable;

/**
 * @author sheldon
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "体育推荐-返回")
@I18nClass
public class SportRecommendVO implements Serializable {

    @Schema(description = "id")
    private String id;

    @Schema(description = "球类,字典CODE: sport_recommend_type")
    private String sportType;

    @Schema(description = "球类名称")
    private String sportName;

    @Schema(description = "联赛名称")
    private String leagueName;

    @Schema(description = "球队名称")
    private String teamName;

    @Schema(description = "赛事ID")
    private String eventsId;

    @Schema(description = "是否置顶,true=置顶,false=未置顶")
    private Boolean pinStatus;

    @Schema(description = "状态,字典CODE: sport_recommend_status")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SPORT_RECOMMEND_STATUS)
    private Integer status;

    @Schema(description = "状态名称")
    private String statusText;

    @Schema(description = "置顶时间")
    private Long sortTime;

    @Schema(description = "开赛时间")
    private Long startTime;

    @Schema(description = "结束时间")
    private Long endTime;


    @Schema(description = "开赛时间")
    private Long dateTime;

    @Schema(description = "操作人")
    private String updater;

    @Schema(description = "最近操作时间")
    private Long updatedTime;


}
