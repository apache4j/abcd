package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(title = "每日竞赛")
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityPartDailyRecordRespVO {

    @Schema(description = "今日活动时间")
    private String time;

    @Schema(description = "今日排名信息")
    private List<ActivityPartUserRankingDailyRespVO> list;

}
