package com.cloud.baowang.activity.api.vo;


import com.cloud.baowang.common.core.annotations.I18nClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 流水排行榜详情配置实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
public class ActivityDailyCompetitionRespVO extends ActivityBaseRespVO implements Serializable {

    private List<ActivityDailyCompetitionDetailRespVO> list;

}
