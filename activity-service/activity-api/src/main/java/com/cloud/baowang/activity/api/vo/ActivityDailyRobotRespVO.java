package com.cloud.baowang.activity.api.vo;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 每日竞赛机器人列表
 */
@Data
@Schema(title = "每日竞赛机器人列表")
@I18nClass
public class ActivityDailyRobotRespVO{

    @Schema(description = "机器人ID", hidden = true)
    private String robotId;

    @Schema(description = "会员排名", hidden = true)
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVITY_DAILY_RANKING)
    private Integer ranking;

    @Schema(description = "会员排名")
    private String rankingText;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "主货币流水")
    private BigDecimal currencyAmount;

    @Schema(description = "WTC-流水")
    private BigDecimal platBetAmount;

    @Schema(description = "流水增长百分比")
    private BigDecimal betGrowthPct;

    @Schema(description = "是否编辑过（1是,0否）,这个字段每天晚上会重新恢复成0")
    private Boolean edit;

    @Schema(description = "是否是机器人,true=机器人.false=真实用户")
    private Boolean type;

    @Schema(description = "初始化-投注金额(流水WTC)")
    private BigDecimal initRobotBetAmount;

    @Schema(description = "初始化-主货币流水")
    private BigDecimal initRobotCurrencyBetAmount;

    @Schema(description = "机器人流水最高阀值")
    private BigDecimal maxRobotBetAmount;


}