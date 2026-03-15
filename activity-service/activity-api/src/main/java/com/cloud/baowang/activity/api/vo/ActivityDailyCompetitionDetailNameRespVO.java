package com.cloud.baowang.activity.api.vo;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 流水排行榜详情配置实体
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@I18nClass
public class ActivityDailyCompetitionDetailNameRespVO implements Serializable {

    private String id;

    @I18nField
    @Schema(description = "竞赛名称")
    private String activityNameI18nCode;

}
