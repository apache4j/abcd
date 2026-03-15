package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "去参与活动返回")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@I18nClass
public class ToActivityVO {


    @Schema(description = "状态CODE,10000=success," +
            "30045=很抱歉。您不符合参与活动条件 参与活动前需要验证绑定您的手机号,请尽快完善资料" +
            "30053=很抱歉。您不符合参与活动条件 参与活动前需要验证绑定您的邮箱，请尽快完善资料" +
            "30046=很抱歉。您不符合参与活动条件 您所在IP已有账号参与该活动" +
            "30048 = 很抱歉。您不符合参与活动条件" +
            "30047 = 很抱歉。不可以重复参与" +
            "30049=您还未存款" +
            "30055=您还已经结束" +
            "80027:活动未开启")
    private Integer status;

    @I18nField
    @Schema(description = "说明")
    private String message;




}
