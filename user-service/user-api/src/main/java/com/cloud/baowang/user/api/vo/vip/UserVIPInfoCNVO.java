package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @Author : dami
 * @Date : 11/6/24 11:20 AM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP权益配置返回对象-大陆")
@I18nClass
public class UserVIPInfoCNVO implements Serializable {

    @Schema(title = "id")
    private String id;

    @Schema(description = "会员ID")
    private String userAccount;

    @Schema(description = "vip当前等级段位")
    private Integer vipRank;

  /*  @Schema(description = "vip当前段位图片")
    private String vipIcon;

    @Schema(description = "vip当前段位颜色")
    private String rankColor;*/

    @Schema(description = "vip下一个等级段位")
    private Integer nextVipRank;

   /* @Schema(description = "vip下一段位图片")
    private String nextVipIcon;

    @Schema(description = "vip下一段位颜色")
    private String nextRankColor;*/

    @Schema(description = "vip当前等级")
    private Integer vipGradeCode;

    @Schema(description = "vip当前等级名称")
    private String vipGradeName;

    @Schema(description = "vip升级后的等级")
    private Integer vipGradeUp;

    @Schema(description = "vip升级后的等级名称")
    private String vipGradeUpName;

    @Schema(description = "用户头像标识")
    private String avatarCode;

    @Schema(description = "体育类游戏积分经验")
    private BigDecimal sportExe;

    @Schema(description = "当前经验值-cn")
    private BigDecimal currentExp;

    @Schema(description = "当前等级经验值")
    private BigDecimal currentVipExp;


    @Schema(description = "升级所需经验值")
    private BigDecimal upgradeVipExp;

    @Schema(description = "升级还需经验值")
    private BigDecimal upgradeVipNeedExp;

    @Schema(description = "保级流水金额")
    private BigDecimal finishRelegationAmount;

    @Schema(description = "保级总流水金额")
    private BigDecimal gradeRelegationAmount;

    @Schema(description = "剩余保级流水金额 -cn")
    private BigDecimal leftRelegationAmount = BigDecimal.ZERO;
    /**
     * 是否显示反水特权配置
     */
    @Schema(description = "是否显示反水特权配置 0 不显示 1 显示")
    private Integer rebateConfig;
    @Schema(description = "当前反水列表集合数据")
    private SiteRebateConfigWebCopyVO rebates;
    @Schema(description = "保级/降级 天数")
    private Integer relegationDays;
    @Schema(description = "还剩多少天降级-cn")
    private Integer leftRelegationDays = 0;

//    public BigDecimal getUpgradeVipExp() {
//        return Optional.ofNullable(upgradeVipExp).orElse(BigDecimal.ZERO);
//    }
//
//    public BigDecimal getUpgradeVipNeedExp() {
//        return Optional.ofNullable(upgradeVipNeedExp).orElse(BigDecimal.ZERO);
//    }

/*    @Schema(description = "vip提款额度")
    private List<UserVipWithdrawConfigCopyAPPVO> userVipWithdrawConfig;*/
    /**
     * vip 卡片
     */
    @Schema(description = "vip卡片显示以及奖励")
    private List<VIPGradeBenefitsAPP> vipBenefitAPPs;
    /**
     * 用户VIP提款信息
     */
    @Schema(description = "vip详情-用户VIP提款信息")
    private UserVipWithdrawConfigCopyAPPVO userVipWithdrawalAPP;

    /*@Schema(description = "vip等级福利")
    private List<SiteVIPBenefitVO> vipGradeBenefit;*/
    /**
     * 返水
     */
    @Schema(description = "vip详情-返水")
    private SiteRebateConfigWebCopyVO siteRebate;

    public BigDecimal getCurrentExp() {
        return Optional.ofNullable(currentExp).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getCurrentVipExp() {
        return Optional.ofNullable(currentVipExp).orElse(BigDecimal.ZERO).setScale(0);
    }


    /**
     * 返水
     */
    @Schema(description = "是否展示返水配置 true 展示 false 不展示")
    private Boolean  isShowRebate;



}
