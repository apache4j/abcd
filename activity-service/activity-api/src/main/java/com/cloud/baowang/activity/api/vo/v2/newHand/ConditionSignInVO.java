package com.cloud.baowang.activity.api.vo.v2.newHand;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
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
@Schema(title = "新人7日投注签到")
@I18nClass
public class ConditionSignInVO {

    @Schema(title = "日有效投注金额")
    @NotNull(message = "日有效投注金额不能为空")
    private BigDecimal validBetAmount;

    @Schema(title = "奖励金额")
    @NotNull(message = "奖励金额不能为空")
    private BigDecimal rewardAmount;
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
