package com.cloud.baowang.user.api.vo.userTeam;


import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author : 小智
 * @Date : 12/10/23 9:44 PM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "代理团队信息返回对象")
@I18nClass
public class UserTeamVO implements Serializable {

    private String siteCode;

    @Schema(description = "下级代理人数")
    private Long underAgentCount;

    @Schema(description = "直属代理人数")
    private Long directlyAgentCount;

    /**
     * 所有下级会员
     */
    @Schema(description = "下级会员人数")
    private Long underUserCount;

    @Schema(description = "直属会员人数")
    private Long directlyUserCount;

    @Schema(description = "首存人数")
    private Long firstDepositCount;

    @Schema(description = "首存金额 平台币")
    private BigDecimal firstDepositAmount;

   // @Schema(description = "有效会员")
    //private Long validUserCount;

    //@Schema(description = "今日新增会员账号")
   // private List<String> todayAddAccount = new ArrayList<>();

    @Schema(description = "今日新增")
    private Long todayAddCount;

   // @Schema(description = "今日活跃人数")
    //private Long todayActiveCount;

  //  @Schema(description = "今日新增活跃人数")
  //  private Long todayAddActiveCount;

    @Schema(description = "今日有效活跃人数")
    private Integer todayValidActiveCount;

    @Schema(description = "今日有效新增人数")
    private Integer todayValidAddCount;

   // @Schema(description = "本月新增会员账号")
   // private List<String> monthAddAccount = new ArrayList<>();

    @Schema(description = "本月新增")
    private Long monthAddCount;

   // @Schema(description = "本月活跃人数")
   // private Long monthActiveCount;

   // @Schema(description = "本月新增活跃人数")
 //   private Long monthAddActiveCount;

    @Schema(description = "本月有效活跃人数")
    private Integer monthValidActiveCount;

    @Schema(description = "本月有效新增人数")
    private Integer monthValidAddCount;

    @Schema(description = "今日优惠 平台币")
    private BigDecimal todayDiscount;

    @Schema(description = "今日返水 平台币")
    private BigDecimal todayRebate;

    @Schema(description = "今日净输赢 平台币")
    private BigDecimal todayWinLoss;

    @Schema(description = "今日总投注 平台币")
    private BigDecimal todayTotalBetAmount;

    @Schema(description = "今日总有效投注 平台币")
    private BigDecimal todayTotalValidBetAmount;

    @Schema(description = "今日总输赢 平台币")
    private BigDecimal todayTotalWinLoss;

    @Schema(description = "本月优惠 平台币")
    private BigDecimal monthDiscount;

    @Schema(description = "本月返水 平台币")
    private BigDecimal monthRebate;

    @Schema(description = "本月净输赢 平台币")
    private BigDecimal monthWinLoss;

    @Schema(description = "本月总投注 平台币")
    private BigDecimal monthTotalBetAmount;

    @Schema(description = "本月总有效投注 平台币")
    private BigDecimal monthTotalValidBetAmount;

    @Schema(description = "本月总输赢 平台币")
    private BigDecimal monthTotalWinLoss;

    @Schema(description = "输赢前3")
    private List<UserVenueTopVO> winLoseTopThree;

    @Schema(description = "投注前3")
    private List<UserVenueTopVO> betsTopThree;

    @Schema(description = "平台币币种")
    private String platCurrencyCode;

/*    public BigDecimal getTodayDiscount() {
        return Optional.ofNullable(todayDiscount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTodayRebate() {
        return Optional.ofNullable(todayRebate).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTodayWinLoss() {
        return Optional.ofNullable(todayWinLoss).orElse(BigDecimal.ZERO);
    }*/

    public BigDecimal getTodayTotalBetAmount() {
        return Optional.ofNullable(todayTotalBetAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTodayTotalValidBetAmount() {
        return Optional.ofNullable(todayTotalValidBetAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTodayTotalWinLoss() {
        return Optional.ofNullable(todayTotalWinLoss).orElse(BigDecimal.ZERO);
    }

/*    public BigDecimal getMonthDiscount() {
        return Optional.ofNullable(monthDiscount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getMonthRebate() {
        return Optional.ofNullable(monthRebate).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getMonthWinLoss() {
        return Optional.ofNullable(monthWinLoss).orElse(BigDecimal.ZERO);
    }*/

    public BigDecimal getMonthTotalBetAmount() {
        return Optional.ofNullable(monthTotalBetAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getMonthTotalValidBetAmount() {
        return Optional.ofNullable(monthTotalValidBetAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getMonthTotalWinLoss() {
        return Optional.ofNullable(monthTotalWinLoss).orElse(BigDecimal.ZERO);
    }

    public List<UserVenueTopVO> getWinLoseTopThree() {
        return Optional.ofNullable(winLoseTopThree).orElse(new ArrayList<>());
    }

    public List<UserVenueTopVO> getBetsTopThree() {
        return Optional.ofNullable(betsTopThree).orElse(new ArrayList<>());
    }

    public void addTodayDiscount(BigDecimal totalDiscount) {
        this.todayDiscount=this.todayDiscount==null?BigDecimal.ZERO:this.todayDiscount;
        totalDiscount=totalDiscount==null?BigDecimal.ZERO:totalDiscount;
        this.todayDiscount=this.todayDiscount.add(totalDiscount);
    }

    public void addTodayRebate(BigDecimal todayRebate) {
        this.todayRebate=this.todayRebate==null?BigDecimal.ZERO:this.todayRebate;
        todayRebate=todayRebate==null?BigDecimal.ZERO:todayRebate;
        this.todayRebate=this.todayRebate.add(todayRebate);
    }

    public void addTodayWinLoss(BigDecimal todayWinLoss) {
        this.todayWinLoss=this.todayWinLoss==null?BigDecimal.ZERO:this.todayWinLoss;
        todayWinLoss=todayWinLoss==null?BigDecimal.ZERO:todayWinLoss;
        this.todayWinLoss=this.todayWinLoss.add(todayWinLoss);
    }

    public void addTodayTotalBetAmount(BigDecimal todayTotalBetAmount) {
        this.todayTotalBetAmount=this.todayTotalBetAmount==null?BigDecimal.ZERO:this.todayTotalBetAmount;
        todayTotalBetAmount=todayTotalBetAmount==null?BigDecimal.ZERO:todayTotalBetAmount;
        this.todayTotalBetAmount=this.todayTotalBetAmount.add(todayTotalBetAmount);
    }

    public void addTodayTotalValidBetAmount(BigDecimal todayTotalValidBetAmount) {
        this.todayTotalValidBetAmount=this.todayTotalValidBetAmount==null?BigDecimal.ZERO:this.todayTotalValidBetAmount;
        todayTotalValidBetAmount=todayTotalValidBetAmount==null?BigDecimal.ZERO:todayTotalValidBetAmount;
        this.todayTotalValidBetAmount=this.todayTotalValidBetAmount.add(todayTotalValidBetAmount);
    }

    public void addTodayTotalWinLoss(BigDecimal todayTotalWinLoss) {
        this.todayTotalWinLoss=this.todayTotalWinLoss==null?BigDecimal.ZERO:this.todayTotalWinLoss;
        todayTotalWinLoss=todayTotalWinLoss==null?BigDecimal.ZERO:todayTotalWinLoss;
        this.todayTotalWinLoss=this.todayTotalWinLoss.add(todayTotalWinLoss);
    }






    public void addMonthDiscount(BigDecimal totalDiscount) {
        this.monthDiscount=this.monthDiscount==null?BigDecimal.ZERO:this.monthDiscount;
        totalDiscount=totalDiscount==null?BigDecimal.ZERO:totalDiscount;
        this.monthDiscount=this.monthDiscount.add(totalDiscount);
    }

    public void addMonthRebate(BigDecimal monthRebate) {
        this.monthRebate=this.monthRebate==null?BigDecimal.ZERO:this.monthRebate;
        monthRebate=monthRebate==null?BigDecimal.ZERO:monthRebate;
        this.monthRebate=this.monthRebate.add(monthRebate);
    }

    public void addMonthWinLoss(BigDecimal monthWinLoss) {
        this.monthWinLoss=this.monthWinLoss==null?BigDecimal.ZERO:this.monthWinLoss;
        monthWinLoss=monthWinLoss==null?BigDecimal.ZERO:monthWinLoss;
        this.monthWinLoss=this.monthWinLoss.add(monthWinLoss);
    }

    public void addMonthTotalBetAmount(BigDecimal monthTotalBetAmount) {
        this.monthTotalBetAmount=this.monthTotalBetAmount==null?BigDecimal.ZERO:this.monthTotalBetAmount;
        monthTotalBetAmount=monthTotalBetAmount==null?BigDecimal.ZERO:monthTotalBetAmount;
        this.monthTotalBetAmount=this.monthTotalBetAmount.add(monthTotalBetAmount);
    }

    public void addMonthTotalValidBetAmount(BigDecimal monthTotalValidBetAmount) {
        this.monthTotalValidBetAmount=this.monthTotalValidBetAmount==null?BigDecimal.ZERO:this.monthTotalValidBetAmount;
        monthTotalValidBetAmount=monthTotalValidBetAmount==null?BigDecimal.ZERO:monthTotalValidBetAmount;
        this.monthTotalValidBetAmount=this.monthTotalValidBetAmount.add(monthTotalValidBetAmount);
    }

    public void addMonthTotalWinLoss(BigDecimal monthTotalWinLoss) {
        this.monthTotalWinLoss=this.monthTotalWinLoss==null?BigDecimal.ZERO:this.monthTotalWinLoss;
        monthTotalWinLoss=monthTotalWinLoss==null?BigDecimal.ZERO:monthTotalWinLoss;
        this.monthTotalWinLoss=this.monthTotalWinLoss.add(monthTotalWinLoss);
    }
}
