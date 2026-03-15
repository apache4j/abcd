package com.cloud.baowang.report.api.vo.site;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.AppBigDecimalJsonSerializer;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "站点统计数据")
@I18nClass
public class SiteStatisticsVO {
    private String id;

    /**
     * 时间
     */
    @Schema(description = "日期字符串，格式为 yyyy-MM-dd")
    private String dateStr;

    /**
     * 投注人数
     */
    @Schema(description = "投注人数")
    private Long betUserCount;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 所属公司
     */
    @Schema(description = "所属公司名称")
    private String companyName;

    /**
     * 平台编号
     */
    @Schema(description = "平台编号")
    private String siteCode;

    /**
     * 平台类型
     */
    @Schema(description = "平台类型（例如：1 - 类型A，2 - 类型B）")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SITE_TYPE)
    private Integer siteType;

    /**
     * 平台类型
     */
    @Schema(description = "平台类型（例如：1 - 类型A，2 - 类型B）")
    private String siteTypeText;

    /**
     * 统计币种
     */
    @Schema(description = "统计的币种代码")
    private String currencyCode;

    /**
     * 当前汇率
     */
    @Schema(description = "当前平台与基础币种的汇率")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal currentFinalRate;

    /**
     * 现有会员人数
     */
    @Schema(description = "当前会员总数")
    private Long totalMembers;

    /**
     * 新增会员人数
     */
    @Schema(description = "当天新增会员人数")
    private Integer newMembers;

    /**
     * 首存人数
     */
    @Schema(description = "首存会员人数")
    private Integer firstDepositCount;

    /**
     * 首存金额
     */
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    @Schema(description = "首存金额")
    private BigDecimal firstDepositAmount;

    /**
     * 存款金额
     */
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    @Schema(description = "总存款金额")
    private BigDecimal depositAmount;

    /**
     * 存款次数
     */
    @Schema(description = "存款次数")
    private Integer depositCount;

    /**
     * 取款金额
     */
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    @Schema(description = "总取款金额")
    private BigDecimal withdrawalAmount;

    /**
     * 取款次数
     */
    @Schema(description = "取款次数")
    private Integer withdrawalCount;

    /**
     * 大额取款次数
     */
    @Schema(description = "大额取款次数（超过一定金额的取款）")
    private Integer largeWithdrawalCount;

    /**
     * 存取差
     */
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    @Schema(description = "存款与取款的差额")
    private BigDecimal depositWithdrawalDifference;

    /**
     * VIP福利
     */
    @Schema(description = "VIP会员福利金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal siteVipBenefits;

    /**
     * 活动优惠
     */
    @Schema(description = "平台活动提供的优惠金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal sitePromotionalOffers;

    /**
     * 已使用优惠
     */
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    @Schema(description = "已使用的优惠金额")
    private BigDecimal siteUsedOffers;

    /**
     * 其他调整
     */
    @Schema(description = "平台的其他调整金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal siteOtherAdjustments;

    /**
     * 注单量
     */
    @Schema(description = "投注注单数量")
    private Integer betCount;

    /**
     * 投注金额
     */
    @Schema(description = "投注总金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal bettingAmount;

    /**
     * 有效投注
     */
    @Schema(description = "有效投注金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal validBetting;

    /**
     * 会员输赢
     */
    @Schema(description = "会员输赢金额（正值代表会员赢，负值代表会员输）")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal memberProfitLoss;

    /**
     * 净盈利
     */
    @Schema(description = "平台的净盈利金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal netProfit;

    /**
     * 平台币符号
     */
    @Schema(description = "平台币符号")
    private String platCurrencyCode;

    /**
     * 调整金额(其他调整)-平台币
     */
    @Schema(description = "调整金额(其他调整)-平台币")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal platAdjustAmount;
    /**
     * 打赏金额
     */
    @Schema(description = "打赏金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal tipsAmount;

    /**
     * 封控金额-主货币
     */
    @Schema(description = "封控金额-主货币")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal riskAmount;

}
