package com.cloud.baowang.activity.api.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 流水排行榜详情配置实体
 */
@Data
@Schema(description = "每日竞赛-详细信息")
public class ActivityDailyCompetitionVO extends ActivityBaseVO implements Serializable {
    @Schema(description = "每日竞赛")
    private List<ActivityDailyCompetitionDetailVO> list;
}
