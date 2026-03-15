package com.cloud.baowang.activity.api.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(title = "每日竞赛页面入参")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityDailyContestReqVO {

    @Schema(description = "id")
    //@NotNull(message = ConstantsCode.PARAM_ERROR)
    private String id;


   // @Schema(description = "时间,年月日,yyyy-MM-dd")
    //private String day;//废弃

    @Schema(description = "时间戳-当前选择天转时间戳")
    private Long dayTimeStamp;

}
