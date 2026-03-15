package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(title = "每日竞赛-场馆列表")
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityPartDailyCompletionVenueDetailRespVO {

    @Schema(description = "活动名称")
    @I18nField
    private String activityName;

    private String id;



}
