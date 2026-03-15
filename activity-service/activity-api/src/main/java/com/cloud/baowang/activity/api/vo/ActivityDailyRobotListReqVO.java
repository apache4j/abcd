package com.cloud.baowang.activity.api.vo;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 每日竞赛机器人列表
 */
@Data
@Schema(title = "每日竞赛机器人列表")
@I18nClass
public class ActivityDailyRobotListReqVO {

    @Schema(description = "活动ID")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String activityId;

    @Schema(description = "竞赛ID")
    private String detailId;



}