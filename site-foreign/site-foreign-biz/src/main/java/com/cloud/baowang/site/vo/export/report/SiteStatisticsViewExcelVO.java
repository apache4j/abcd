package com.cloud.baowang.site.vo.export.report;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "平台报表导出对象")
@ExcelIgnoreUnannotated
@I18nClass
public class SiteStatisticsViewExcelVO {

    @ExcelProperty("日期")
    @ColumnWidth(10)
    @Schema(description = "日期")
    private String dateStr;

    @Schema(description = "站点名称")
    @ExcelProperty("平台名称")
    @ColumnWidth(5)
    private String siteName;

    @Schema(description = "所属公司")
    @ExcelProperty("所属公司")
    @ColumnWidth(5)
    private String companyName;

    @Schema(description = "站点编号")
    @ExcelProperty("平台编号")
    @ColumnWidth(5)
    private String siteCode;

    @Schema(description = "站点类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_TYPE)
    private Integer siteType;

    @Schema(description = "站点类型")
    @ExcelProperty("平台类型")
    @ColumnWidth(5)
    private String siteTypeText;

    @Schema(description = "统计币种")
    @ExcelProperty("统计币种")
    @ColumnWidth(3)
    private String currencyCode;

    @Schema(description = "现有会员人数")
    @ExcelProperty("现有会员人数")
    @ColumnWidth(5)
    private Long totalMembers;

    @Schema(description = "新增会员人数")
    @ExcelProperty("新增会员人数")
    @ColumnWidth(5)
    private Integer newMembers;

    @Schema(description = "首存人数")
    @ExcelProperty("首存人数")
    @ColumnWidth(5)
    private Integer firstDepositCount;

    @Schema(description = "首存金额")
    @ExcelProperty("首存金额")
    @ColumnWidth(6)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal firstDepositAmount;

    @Schema(description = "存款金额")
    @ExcelProperty("存款金额")
    @ColumnWidth(6)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal depositAmount;

    @Schema(description = "存款次数")
    @ExcelProperty("存款次数")
    @ColumnWidth(5)
    private Integer depositCount;

    @Schema(description = "取款金额")
    @ExcelProperty("取款金额")
    @ColumnWidth(6)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal withdrawalAmount;

    @Schema(description = "取款次数")
    @ExcelProperty("取款次数")
    @ColumnWidth(6)
    private Integer withdrawalCount;

    @Schema(description = "大额取款次数")
    @ExcelProperty("大额取款次数")
    @ColumnWidth(5)
    private Integer largeWithdrawalCount;

    @Schema(description = "存取差")
    @ExcelProperty("存取差")
    @ColumnWidth(6)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal depositWithdrawalDifference;

    @Schema(description = "VIP福利")
    @ExcelProperty("VIP福利")
    @ColumnWidth(6)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal siteVipBenefits;

    @Schema(description = "活动优惠")
    @ExcelProperty("活动优惠")
    @ColumnWidth(6)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal sitePromotionalOffers;

    @Schema(description = "已使用优惠")
    @ExcelProperty("已使用优惠")
    @ColumnWidth(6)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal siteUsedOffers;

    @Schema(description = "其他调整")
    @ExcelProperty("其他调整")
    @ColumnWidth(10)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal siteOtherAdjustments;

    @ExcelProperty("平台币其他调整")
    @ColumnWidth(6)
    @Schema(description = "平台币其他调整")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal platAdjustAmount;

    @ExcelProperty("风控调整")
    @ColumnWidth(6)
    @Schema(description = "风控调整")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal riskAmount;

    @Schema(description = "投注人数")
    @ExcelProperty("投注人数")
    @ColumnWidth(5)
    private Long betUserCount;

    @Schema(description = "注单量")
    @ExcelProperty("注单量")
    @ColumnWidth(5)
    private Integer betCount;

    @Schema(description = "投注金额")
    @ExcelProperty("投注金额")
    @ColumnWidth(6)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal bettingAmount;

    @Schema(description = "有效投注")
    @ExcelProperty("有效投注")
    @ColumnWidth(6)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal validBetting;

    @ExcelProperty("打赏金额")
    @ColumnWidth(6)
    @Schema(description = "打赏金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal tipsAmount;

    @Schema(description = "会员输赢")
    @ExcelProperty("平台输赢")
    @ColumnWidth(6)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal memberProfitLoss;

    @Schema(description = "平台净盈利")
    @ExcelProperty("平台净盈利")
    @ColumnWidth(6)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal netProfit;

}
