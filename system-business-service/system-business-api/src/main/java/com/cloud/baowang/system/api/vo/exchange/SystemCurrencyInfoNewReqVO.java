package com.cloud.baowang.system.api.vo.exchange;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 13:55
 * @Version: V1.0
 **/
@Data
@Schema(description = "币种信息")
public class SystemCurrencyInfoNewReqVO {

    private String siteCode;

    @Schema(description = "操作人 ",hidden = true)
    private String operatorUserNo;

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
     * 货币名称 多语言
     */
    @Schema(description = "货币名称 多语言List")
    private List<I18nMsgFrontVO> currencyNameI18List;

    /**
     * 货币符号
     */
    @Schema(description = "货币符号")
    private String currencySymbol;

    /**
     * 精度 TWO:2位小数 K:千位
     */
    @Schema(description = "精度 TWO:2位小数 K:千位")
    private String currencyDecimal;

    /**
     * 图标
     */
    @Schema(description = "图标")
    private String currencyIcon;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;

    /**
     * 平台币兑换汇率
     */
    @Schema(description = "平台币兑换汇率")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal finalRate;

}
