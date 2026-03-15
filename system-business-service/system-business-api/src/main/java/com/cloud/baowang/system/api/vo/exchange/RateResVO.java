package com.cloud.baowang.system.api.vo.exchange;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/5/20 20:50
 * @Version: V1.0
 **/
@Data
@Schema(description = "货币汇率")
@I18nClass
public class RateResVO extends BaseVO {

    private String siteCode;

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    /**
     * 货币名称
     */
    @Schema(description = "货币名称")
    private String currencyName;

    /**
     * 货币符号
     */
    @Schema(description = "货币符号")
    private String currencySymbol;

    /**
     * 主货币代码
     */
    @Schema(description = "主货币代码")
    private String baseCurrencyCode;

    /**
     * 三方汇率
     */
    @Schema(description = "三方汇率")
    private BigDecimal thirdRate;

    @Schema(description = "类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.EXCHANGE_RATE_SHOW_WAY)
    private String showWay;

    @Schema(description = "汇率调整类型")
    private String showWayText;

    /**
     * 汇率调整方式
     */
    @Schema(description = "汇率调整方式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.EXCHANGE_RATE_ADJUST_WAY)
    private String adjustWay;


    @Schema(description = "汇率调整方式名称")
    private String adjustWayText;

    /**
     * 调整数值
     */
    @Schema(description = "调整数值")
    private String adjustNum;

    /**
     * 调整后站点汇率
     */
    private BigDecimal finalRate;


    /**
     * 三方汇率更新时间
     */
    @Schema(description = "三方汇率更新时间")
    private Long thirdRateTime;


}
