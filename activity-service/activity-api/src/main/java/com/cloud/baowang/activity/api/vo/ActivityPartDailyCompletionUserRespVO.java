package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.cloud.baowang.common.core.serializer.SensitiveDataJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(title = "每日竞赛-用户投注信息")
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityPartDailyCompletionUserRespVO {

    @Schema(description = "排名")
    private Integer ranking;

    @Schema(description = "账号")
    private String userAccount;

//    @Schema(description = "奖金")
//    private BigDecimal awardAmount;

    @Schema(description = "头像")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;

    @Schema(title = "头像")
    private String iconFileUrl;

    @Schema(description = "投注金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal betAmount;

    @Schema(description = "距离上榜投注金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal lackBetAmount;

    @Schema(description = "币种符号")
    private String currencySymbol;

    @Schema(description = "0:未上榜,1:第一名,2:已上榜")
    private Integer userStatus;

}
