package com.cloud.baowang.report.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("report_site_statistics")
public class SiteStatisticsPO extends BasePO {

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 所属公司
     */
    private String companyName;

    /**
     * 平台编号
     */
    private String siteCode;

    /**
     * 平台类型
     */
    private Integer siteType;

    /**
     * 统计币种
     */
    private String currencyCode;
    /**
     * 当前汇率
     */
    private BigDecimal currentFinalRate;


    /**
     * 新增会员人数
     */
    private Integer newMembers;

    /**
     * 首存人数
     */
    private Integer firstDepositCount;

    /**
     * 首存金额
     */
    private BigDecimal firstDepositAmount;

    /**
     * 存款金额
     */
    private BigDecimal depositAmount;

    /**
     * 存款次数
     */
    private Integer depositCount;

    /**
     * 取款金额
     */
    private BigDecimal withdrawalAmount;

    /**
     * 取款次数
     */
    private Integer withdrawalCount;

    /**
     * 大额取款次数
     */
    private Integer largeWithdrawalCount;

    /**
     * 存取差
     */
    private BigDecimal depositWithdrawalDifference;

    /**
     * VIP福利
     */
    private BigDecimal siteVipBenefits;

    /**
     * 活动优惠
     */
    private BigDecimal sitePromotionalOffers;

    /**
     * 已使用优惠
     */
    private BigDecimal siteUsedOffers;

    /**
     * 其他调整
     */
    private BigDecimal siteOtherAdjustments;

    /**
     * 注单量
     */
    private Integer betCount;

    /**
     * 投注金额
     */
    private BigDecimal bettingAmount;

    /**
     * 有效投注
     */
    private BigDecimal validBetting;

    /**
     * 平台输赢
     */
    private BigDecimal memberProfitLoss;

    /**
     * 净盈利
     */
    private BigDecimal netProfit;

    /**
     * 调整金额(其他调整)-平台币
     */
    private BigDecimal platAdjustAmount;
    /**
     * 打赏金额
     */
    private BigDecimal tipsAmount;

    /**
     * 封控金额-主货币
     */
    private BigDecimal riskAmount;

}
