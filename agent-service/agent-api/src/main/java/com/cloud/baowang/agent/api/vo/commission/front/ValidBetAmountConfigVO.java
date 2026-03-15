package com.cloud.baowang.agent.api.vo.commission.front;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/09/19 17:05
 * @description: 流水返点配置
 */
@Data
@I18nClass
@Schema(title = "有效流水配置", description = "有效流水配置")
public class ValidBetAmountConfigVO implements Serializable {
    @Schema(title = "方案编码")
    private String planCode;

    @Schema(description = "游戏类别")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_TYPE)
    private Integer venueType;

    @Schema(description = "游戏类别-文本")
    @ColumnWidth(15)
    private String venueTypeText;



    @Schema(title = "币种")
    private String currency;

    @Schema(title = "等级")
    private Integer tierNum;

    @Schema(title = "有效投注")
    private BigDecimal betAmount;

    @Schema(title = "有效投注(平台币)")
    private BigDecimal betPlatAmount;

    @Schema(title = "返佣比例")
    private BigDecimal rate;
}
