package com.cloud.baowang.activity.api.vo.v2.newHand;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Data
@Schema(title = "新人7日投注签到")
@I18nClass
public class ConditionSignInRespVO {

    @Schema(title = "日有效投注金额")
    @NotNull(message = "日有效投注金额不能为空")
    private BigDecimal validBetAmount= BigDecimal.ZERO;

    @Schema(title = "奖励金额")
    @NotNull(message = "奖励金额不能为空")
    private BigDecimal rewardAmount = BigDecimal.ZERO;
    @Schema(title = "洗码倍率")
    @NotNull(message = "洗码倍率不能为空")
    private BigDecimal washRatio = BigDecimal.ZERO;

    @Schema(title = "币种")
    @NotNull(message = "币种不能为空")
    private String currencyCode;

    @Schema(title = "明细图-移动-白天")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String detailShowI18nCode;
    private String detailShowI18nCodeFileUrl;

    @Schema(title = "明细图-PC-白天")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String detailShowPcI18nCode;
    private String detailShowPcI18nCodeFileUrl;

    @Schema(title = "明细图-移动-黑夜")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String detailShowDarkI18nCode;
    private String detailShowDarkI18nCodeFileUrl;

    @Schema(title = "明细图-PC-黑夜")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String detailShowPcDarkI18nCode;
    private String detailShowPcDarkI18nCodeFileUrl;

    @Schema(description = "状态CODE,10000=立即参与,30047=已参与过该活动")
    private Integer status;

    @Schema(description = "签到列表，七天用0和1表示，已签：1，未完成签到：0")
    private List<Integer> signInList = Arrays.asList(0,0,0,0,0,0,0);

    @Schema(description = "累计签到天数")
    private long signInCount ;

    public long getSignInCount(){
        return this.signInList.stream().filter(integer -> integer == 1).count();
    }

    @Schema(title = "当日有效投注金额")
    private BigDecimal curDayValidBetAmount = BigDecimal.ZERO;
}
