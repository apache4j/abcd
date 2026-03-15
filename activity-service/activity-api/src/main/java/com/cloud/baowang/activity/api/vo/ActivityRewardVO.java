package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "领取活动礼金")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@I18nClass
public class ActivityRewardVO {


    @Schema(description = "状态CODE,10000=成功," +
            "80017=活动礼金未到领取时间" +
            "80028=已过期，" +
            "80029=领取失败")
    private Integer status;

    @I18nField
    @Schema(description = "说明")
    private String message;


}
