package com.cloud.baowang.activity.api.vo.v2;

import com.cloud.baowang.activity.api.vo.CheckInRewardResultVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className: CheckInRewardResultRespVO
 * @author: wade
 * @description: 签到活动-结果
 * @date: 26/5/25 11:06
 */
@Schema(description = "签到活动-结果")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@I18nClass
public class CheckInRewardResultV2RespVO {

    @Schema(description = "日奖励")
    private CheckInRewardResultVO dailyReward;
    @Schema(description = "月奖励")
    private CheckInRewardResultVO monthReward;
    @Schema(description = "累计奖励")
    private CheckInRewardResultVO totalReward;
}
