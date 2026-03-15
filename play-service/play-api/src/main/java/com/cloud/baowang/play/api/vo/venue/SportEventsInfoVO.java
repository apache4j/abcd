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
@Schema(description = "体育联赛-返回")
@I18nClass
public class SportEventsInfoVO implements Serializable {

    @Schema(description = "id")
    private String id;

    @Schema(description = "场馆")
    private String venueCode;

    @Schema(description = "场馆")
    private String venueName;

    @Schema(description = "球类,字典CODE: sport_recommend_type")
    private Integer sportType;

    @Schema(description = "球类名称")
    private String sportName;

    @Schema(description = "联赛名称")
    private String leagueName;

    @Schema(description = "联赛ID")
    private String leagueId;

    @Schema(description = "排序")
    private Long sort;

    @Schema(description = "是否置顶,true=置顶,false=未置顶")
    private Boolean pinStatus;

    @Schema(description = "操作人")
    private String updater;

    @Schema(description = "最近操作时间")
    private Long updatedTime;

    @Schema(description = "排序联赛ID",hidden = true)
    private String siteEventsId;


}
