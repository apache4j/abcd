package com.cloud.baowang.report.api.vo.user.complex;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "总台-综合数据报表")
public class AdminIntegrateDataTempRspVO {
    @Schema( description = "日期")
    @ExcelProperty("日期")
    private String staticDate;
    @ExcelProperty("站点")
    private String siteCode;
    @ExcelProperty("站点名称")
    private String siteName;
    @ExcelProperty("币种")
    private String currencyCode;

    @ExcelProperty("会员注册总人数")
    @Schema( description = "会员注册总人数")
    private Integer registerTotal;

    @ExcelProperty("会员后台注册人数")
    @Schema( description = "会员后台注册人数")
    private Integer registerBacked;

    @ExcelProperty("会员pc注册人数")
    @Schema( description = "会员pc注册人数")
    private Integer registerPc;

    @ExcelProperty("会员android_H5注册人数")
    @Schema( description = "会员android_H5注册人数")
    private Integer registerAndroidH5;

    @ExcelProperty("会员android_app注册人数")
    @Schema( description = "androidAPP")
    private Integer registerAndroidAPP;

    @ExcelProperty("会员ios_app注册人数")
    @Schema( description = "ios-app")
    private Integer registerIosAPP;

    @ExcelProperty("会员ios_h5注册人数")
    @Schema( description = "ios-h5")
    private Integer registerIosH5;

    @ExcelProperty("会员登录总人数")
    @Schema( description = "会员登陆总人数")
    private Integer loginTotal;

    @ExcelProperty("会员后台登录人数")
    @Schema( description = "后台")
    private Integer loginBacked;

    @ExcelProperty("会员pc登录人数")
    @Schema( description = "pc")
    private Integer loginPc;

    @ExcelProperty("会员android_H5登录人数")
    @Schema( description = "androidH5")
    private Integer loginAndroidH5;

    @ExcelProperty("会员android_app登录人数")
    @Schema( description = "androidAPP")
    private Integer loginAndroidAPP;

    @ExcelProperty("会员ios_app登录人数")
    @Schema( description = "ios-app")
    private Integer loginIosAPP;

    @ExcelProperty("会员ios_h5登录人数")
    @Schema( description = "ios-h5")
    private Integer loginIosH5;

    @ExcelProperty("会员总存款")
    @Schema( description = "总存款")
    private BigDecimal totalDeposit;

    @ExcelProperty("会员存款人数")
    @Schema( description = "存款人数")
    private Integer depositPeopleNums ;

    @ExcelProperty("会员存款次数")
    @Schema( description = "存款次数")
    private Integer depositNums ;

    @ExcelProperty("会员总取款")
    @Schema( description = "总取款")
    private BigDecimal totalWithdraw;

    @ExcelProperty("会员取款人数")
    @Schema( description = "取款人数")
    private Integer withdrawPeopleNums ;

    @ExcelProperty("会员取款次数")
    @Schema( description = "取款次数")
    private Integer withdrawNums ;

    @ExcelProperty("会员大额取款人数")
    @Schema( description = "大额取款人数")
    private Integer largeWithdrawPeopleNums ;

    @ExcelProperty("会员大额取款次数")
    @Schema( description = "大额取款次数")
    private Integer largeWithdrawNums ;

    @ExcelProperty("会员存取差")
    @Schema( description = "会员存取差")
    private BigDecimal memberAccessDifference;

    @ExcelProperty("首存金额")
    @Schema(description = "首存金额总计")
    private BigDecimal firstDepositAmount;

    @ExcelProperty("首存人数")
    @Schema(description = "首存人数")
    private Integer firstDepositPeopleNums;

    @ExcelProperty("会员投注金额")
    @Schema( description = "投注金额")
    private BigDecimal betAmount;

    @ExcelProperty("会员有效投注")
    @Schema( description = "有效投注金额")
    private BigDecimal effectiveBetAmount;

    @ExcelProperty("投注人数")
    @Schema( description = "投注人数")
    private Integer bettorNums;

    @ExcelProperty("注单量")
    @Schema( description = "注单量")
    private Integer bettingOrderAmount;

    @ExcelProperty("投注输赢")
    @Schema( description = "投注输赢")
    private BigDecimal memberWinOrLose;

    @ExcelProperty("会员VIP福利")
    @Schema(description = "会员vip福利金额")
    private BigDecimal vipWelfareAmount;

    @ExcelProperty("会员VIP福利人数")
    @Schema(description = "会员vip福利人数")
    private Integer vipWelfarePeopleNums;

    @ExcelProperty("会员优惠活动金额")
    @Schema(description = "优惠活动金额")
    private BigDecimal promotionAmount;

    @ExcelProperty("会员优惠活动人数")
    @Schema(description = "优惠活动人数")
    private Integer promotionPeopleNums;


    @ExcelProperty("已使用优惠")
    @Schema( description = "已使用优惠")
    private BigDecimal usedPromotion;

    @ExcelProperty("会员调整额")
    @Schema( description = "会员调整额")
    private BigDecimal totalAdjust;

    @ExcelProperty("会员加额")
    @Schema( description = "会员加额")
    private BigDecimal addAmount ;

    @ExcelProperty("加额人数")
    @Schema( description = "会员加额人数")
    private Integer addAmountPeopleNum;

    @ExcelProperty("会员减额")
    @Schema( description = "会员减额")
    private BigDecimal reduceAmount ;

    @ExcelProperty("减额人数")
    @Schema( description = "会员减额人数")
    private Integer reduceAmountPeopleNums ;

    @ExcelProperty("代理注册总人数")
    @Schema( description = "代理注册总人数")
    private Integer agentRegisterTotal;

    @ExcelProperty("代理后台注册人数")
    @Schema( description = "代理后台注册")
    private Integer agentRegisterBacked;

    @ExcelProperty("代理pc注册人数")
    @Schema( description = "代理pc注册")
    private Integer agentRegisterPc;

    @ExcelProperty("代理android_H5注册人数")
    @Schema( description = "代理androidH5")
    private Integer agentRegisterAndroidH5;

    @ExcelProperty("代理android_app注册人数")
    @Schema( description = "androidAPP")
    private Integer agentRegisterAndroidAPP;

    @ExcelProperty("代理ios_app注册人数")
    @Schema( description = "ios-app")
    private Integer agentRegisterIosAPP;

    @ExcelProperty("代理ios_h5注册人数")
    @Schema( description = "ios-h5")
    private Integer agentRegisterIosH5;
//
//    @ExcelProperty("代理总存款")
//    @Schema( description = "总存款")
//    private BigDecimal agentTotalDeposit;
//
//    @ExcelProperty("代理存款人数")
//    @Schema( description = "存款人数")
//    private Integer agentDepositPeopleNums ;
//
//    @ExcelProperty("代理存款次数")
//    @Schema( description = "存款次数")
//    private Integer agentDepositNums ;
//
//    @ExcelProperty("代理总取款")
//    @Schema( description = "代理总取款")
//    private BigDecimal agentTotalWithdraw;
//
//    @ExcelProperty("代理取款人数")
//    @Schema( description = "取款人数")
//    private Integer agentWithdrawPeopleNums ;
//
//    @ExcelProperty("代理取款次数")
//    @Schema( description = "取款次数")
//    private Integer agentWithdrawNums ;
//
//    @ExcelProperty("代理大额取款人数")
//    @Schema( description = "大额取款人数")
//    private Integer agentLargeWithdrawPeopleNums ;
//
//    @ExcelProperty("代理大额取款次数")
//    @Schema( description = "大额取款次数")
//    private Integer agentLargeWithdrawNums ;

//    @ExcelProperty("代理存取差")
//    @Schema( description = "代理存取差")
//    private BigDecimal agentAccessDifference;

    @ExcelProperty("代存会员金额")
    @Schema( description = "代存会员-额度")
    private BigDecimal agentCredit;

    @ExcelProperty("代存会员人数")
    @Schema( description = "额度-人数")
    private Integer agentCreditPeopleNums;

    @ExcelProperty("代存会员次数")
    @Schema( description = "额度-次数")
    private Integer agentCreditTimes;




    @ExcelProperty("上下分总额")
    @Schema( description = "上下分总额")
    private BigDecimal platformTotalAdjust;

    @ExcelProperty("上分总额")
    @Schema( description = "上分总额")
    private BigDecimal platformAddAmount ;

    @ExcelProperty("上分人数")
    @Schema( description = "上分人数")
    private Integer platformAddPeopleNum;

    @ExcelProperty("下分总额")
    @Schema( description = "下分总额")
    private BigDecimal platformReduceAmount ;

    @ExcelProperty("下分人数")
    @Schema( description = "下分人数")
    private Integer platformReducePeopleNums ;


    @ExcelProperty("打赏金额")
    @Schema( description = "打赏金额")
    private BigDecimal tipsAmount ;

    @Schema( description = "封控总调整")
    private BigDecimal riskAmount ;

    @Schema( description = "封控加额")
    private BigDecimal riskAddAmount ;

    @Schema( description = "封控加额人数")
    private Integer riskAddPeopleNum ;

    @Schema( description = "封控减额")
    private BigDecimal riskReduceAmount ;

    @Schema( description = "封控减额人数")
    private Integer riskReducePeopleNum ;






//    @Schema( description = "额度")
//    private BigDecimal agentTransferAmount;
//
//    @Schema( description = "转账人数")
//    private Integer agentTransferPeopleNums;
//
//    @Schema( description = "转账次数")
//    private Integer agentTransferTimes;
//
//
//    @Schema(description = "金额")
//    private BigDecimal agentTotalOffer;
//
//    @Schema(description = "人数")
//    private Integer offerPeopleNums;
//
//
//    @Schema( description = "调整额")
//    private BigDecimal agentTotalAdjust;
//
//    @Schema( description = "加额")
//    private BigDecimal agentAddAmount ;
//
//    @Schema( description = "加额人数")
//    private Integer agentAddAmountPeopleNum;
//
//    @Schema( description = "减额")
//    private BigDecimal agentReduceAmount ;
//
//    @Schema( description = "减额人数")
//    private InternalError agentReduceAmountPeopleNums ;
}

