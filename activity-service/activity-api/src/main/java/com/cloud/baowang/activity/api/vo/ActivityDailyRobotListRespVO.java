package com.cloud.baowang.activity.api.vo;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 每日竞赛机器人列表
 */
@Data
@Schema(title = "每日竞赛机器人列表")
@I18nClass
public class ActivityDailyRobotListRespVO {

    @Schema(description = "竞赛名称")
    @I18nField
    private Integer name;

    @Schema(description = "id")
    private String id;

    private List<ActivityDailyRobotRespVO> list;

}