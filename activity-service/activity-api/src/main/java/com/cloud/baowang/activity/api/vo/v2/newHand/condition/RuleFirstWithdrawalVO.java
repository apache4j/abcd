package com.cloud.baowang.activity.api.vo.v2.newHand.condition;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.activity.api.vo.v2.newHand.ConditionFirstDepositVO;
import com.cloud.baowang.activity.api.vo.v2.newHand.ConditionFirstWithdrawalVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "规则条件")
@I18nClass
public class RuleFirstWithdrawalVO {

    @Schema(description = "0:平台币, 1: 法币")
    private String platformOrFiatCurrency;


    @Schema(description = "匹配条件")
    private List<ConditionFirstWithdrawalVO> conditionVOS;

    /*@Schema(description = "匹配条件首次存款")
    private List<ConditionFirstDepositVO> conditionFirstDepositVOS;

    @Schema(description = "匹配条件首次提款")
    private List<ConditionFirstWithdrawalVO> conditionFirstWithdrawalVOS;

    @Schema(description = "匹配条件签到")
    private List<ConditionSignInVO> conditionSignInVOS;

    @Schema(description = "匹配条件负盈利")
    private List<ConditionNegativeProfitVO> conditionNegativeProfitVOS;*/
}
