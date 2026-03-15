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
@Schema(title = "新人首充app显示实体")
@I18nClass
public class ConditionFirstDepositRespVO {

    @Schema(title = "充值金额")
    private BigDecimal depositAmount = BigDecimal.ZERO;
    @Schema(title = "奖励金额")
    private BigDecimal rewardAmount  = BigDecimal.ZERO;
    @Schema(title = "洗码倍率")
    private BigDecimal washRatio  = BigDecimal.ZERO;

    @Schema(title = "币种")
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
    private Integer status = 10000;

}
