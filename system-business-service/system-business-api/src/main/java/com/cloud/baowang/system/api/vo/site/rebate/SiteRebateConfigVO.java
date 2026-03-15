package com.cloud.baowang.system.api.vo.site.rebate;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.serializer.AppBigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@Schema(description = "角色列表参数对象")
@I18nClass
public class SiteRebateConfigVO implements Serializable {


    private String id;

    private String currencyCode;
    @Schema(description = "vip等级code")
    private String vipGradeCode;

    @Schema(description = "vip等级名称")
    @I18nField
    private String vipGradeName;

    @Schema(description = "体育返水配置")
    @NotNull(message = "体育返水比例不能为空")
    @DecimalMin(value ="0.00",message = "体育返水比例最小为0")
    @DecimalMax(value = "100",message = "体育返水比例最大为100")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal sportsRebate = BigDecimal.ZERO;

    @Schema(description = "电竞返水配置")
    @NotNull(message = "电竞返水比例不能为空")
    @DecimalMin(value ="0.00",message = "电竞返水比例最小为0")
    @DecimalMax(value = "100",message = "电竞返水比例最大为100")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal esportsRebate = BigDecimal.ZERO;

    @Schema(description = "视讯返水配置")
    @NotNull(message = "视讯返水比例不能为空")
    @DecimalMin(value ="0.00",message = "视讯返水比例最小为0")
    @DecimalMax(value = "100",message = "视讯返水比例最大为100")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal videoRebate = BigDecimal.ZERO;

    @Schema(description = "棋牌返水配置")
    @NotNull(message = "棋牌返水比例不能为空")
    @DecimalMin(value ="0.00",message = "棋牌返水比例最小为0")
    @DecimalMax(value = "100",message = "棋牌返水比例最大为100")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal pokerRebate = BigDecimal.ZERO;

    @Schema(description = "电子返水配置")
    @NotNull(message = "电子返水比例不能为空")
    @DecimalMin(value ="0.00",message = "电子返水比例最小为0")
    @DecimalMax(value = "100",message = "电子返水比例最大为100")
    private BigDecimal slotsRebate = BigDecimal.ZERO;

    @Schema(description = "彩票返水配置")
    @NotNull(message = "彩票返水比例不能为空")
    @DecimalMin(value ="0.00",message = "彩票返水比例最小为0")
    @DecimalMax(value = "100",message = "彩票返水比例最大为100")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal lotteryRebate = BigDecimal.ZERO;

    @Schema(description = "斗鸡返水配置")
    @NotNull(message = "斗鸡返水比例不能为空")
    @DecimalMin(value ="0.00",message = "斗鸡返水比例最小为0")
    @DecimalMax(value = "100",message = "斗鸡返水比例最大为100")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal cockfightingRebate = BigDecimal.ZERO;

    @Schema(description = "捕鱼返水配置")
    @NotNull(message = "捕鱼返水比例不能为空")
    @DecimalMin(value ="0.00",message = "捕鱼返水比例最小为0")
    @DecimalMax(value = "100",message = "捕鱼返水比例最大为100")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal fishingRebate = BigDecimal.ZERO;

    @Schema(description = "娱乐返水配置")
    @NotNull(message = "娱乐返水比例不能为空")
    @DecimalMin(value ="0.00",message = "娱乐返水比例最小为0")
    @DecimalMax(value = "100",message = "娱乐返水比例最大为100")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal marblesRebate = BigDecimal.ZERO;


    @Schema(description = "单日返水上限")
    @NotNull(message = "单日返水上限不能为空")
    @DecimalMin(value ="0.00",message = "单日返水上限最小为0")
    @DecimalMax(value = "999999999.99",message = "单日返水上限最大为999999999.99")
    private BigDecimal dailyLimit = BigDecimal.ZERO;

    @Schema(description = "当前段位返水配置是否可编辑 0-不可 1-可以")
    private Integer status;

}
