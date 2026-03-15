package com.cloud.baowang.activity.api.vo.v2.newHand;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(title = "新人7日负盈利")
@I18nClass
public class ConditionNegativeProfitVO {

    @Schema(title = "7日负盈利金额")
    @NotNull(message = "7日负盈利金额不能为空")
    private BigDecimal negativeProfitAmount;

    @Schema(title = "奖励彩金百分比")
    @NotNull(message = "奖励彩金百分比不能为空")
    private BigDecimal rewardPct;

    @Schema(title = "奖励彩金上限")
    @NotNull(message = "奖励彩金上限不能为空")
    private BigDecimal rewardMax;

    @Schema(title = "洗码倍率")
    @NotNull(message = "洗码倍率不能为空")
    private BigDecimal washRatio;

    @Schema(title = "币种")
    @NotNull(message = "币种不能为空")
    private String currencyCode;

    @Schema(title = "明细图-移动-白天")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String detailShowI18nCode;
    private List<I18nMsgFrontVO> detailShowI18nCodeList;

    @Schema(title = "明细图-PC-白天")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String detailShowPcI18nCode;
    private List<I18nMsgFrontVO> detailShowPcI18nCodeList;

    @Schema(title = "明细图-移动-黑夜")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String detailShowDarkI18nCode;
    private List<I18nMsgFrontVO> detailShowDarkI18nCodeList;

    @Schema(title = "明细图-PC-黑夜")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String detailShowPcDarkI18nCode;
    private List<I18nMsgFrontVO> detailShowPcDarkI18nCodeList;
}
