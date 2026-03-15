package com.cloud.baowang.activity.api.vo.v2;

import com.cloud.baowang.activity.api.vo.v2.newHand.*;
import com.cloud.baowang.activity.api.vo.v2.newHand.condition.RuleFirstDepositVO;
import com.cloud.baowang.activity.api.vo.v2.newHand.condition.RuleFirstWithdrawalVO;
import com.cloud.baowang.activity.api.vo.v2.newHand.condition.RuleNegativeProfitVO;
import com.cloud.baowang.activity.api.vo.v2.newHand.condition.RuleSignInVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 首存活动-详情实体
 */
@Data
@Schema(description = "新手活动详细信息")
@Builder
@I18nClass
public class ActivityNewHandRespVO extends ActivityBaseV2RespVO implements Serializable {


    @Schema(description = "活动币种类型（0.平台币，1. 法币）")
    private String platformOrFiatCurrency;


    /**
     * 参与方式,0 手动参与 1 自动参与
     * {@link com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum}
     */
    @Schema(description = "参与方式,0 手动参与 1 自动参与")
    @NotNull(message = "参与方式不能为空")
    @Min(value = 0, message = "参与方式不能小于0")
    @Max(value = 1, message = "参与方式不能大于1")
    private Integer participationMode;


    /**
     * 派发方式,0.玩家自领-过期作废，1.玩家自领-过期作废 2.立即派发
     * {@link com.cloud.baowang.activity.api.enums.ActivityDistributionTypeEnum}
     */
    @Schema(description = "派发方式,0.玩家自领-过期作废，1.玩家自领-过期作废 2.立即派发")
    @NotNull(message = "派发方式不能为空")
    @Min(value = 0, message = "派发方式不能小于0")
    @Max(value = 1, message = "派发方式不能大于1")
    private Integer distributionType;

    
    @Schema(description = "匹配条件首次存款")
    private RuleFirstDepositVO firstDepositConditionVO;

    @Schema(description = "匹配条件首次提款")
    private RuleFirstWithdrawalVO firstWithdrawalConditionVO;

    @Schema(description = "匹配条件签到")
    private RuleSignInVO signInConditionVO;

    @Schema(description = "匹配条件负盈利")
    private RuleNegativeProfitVO negativeProfitConditionVO;











}
