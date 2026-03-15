package com.cloud.baowang.activity.api.vo.test;


import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会员每日盈亏-MQ参数
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(title = "会员每日盈亏-MQ参数")
public class UserWinLoseMqVOTest extends MessageBaseVO {

    @Schema(title = "流水纠正")
    private BigDecimal runWaterCorrect;

    // 注意:
    // 业务场景:下注  结算  人工加额  人工减额  优惠活动  会员返水  会员VIP福利
    // 只统计会员/代理"正式"和"商务"账号类型下的数据 (2正式 3商务)

    // ---------------------------------------------------------
    // 必填参数
    /**
     * 日期时间戳: 传递yyyy-MM-dd xx:00:00对应的时间戳，utc整点对应的时间戳
     * 使用该方法
     * {@link  com.cloud.baowang.common.core.utils.TimeZoneUtils#convertToUtcStartOfHour(long)}
     * * 1下注 ：下注时间
     * * 2结算： 三方单结算时间
     * * 3人工加额：人工加额会员帐变时间
     * * 4人工减额：人工减额会员帐变时间
     * * 5优惠活动：活动发放奖励，还是发放时间
     * * 6会员VIP福利：会员VIP福利发放时间
     */
    @Schema(title = "日期时间戳  必填参数  结算的时候，传递下注时间")
    private Long dayHourMillis;
    // 必填参数
    /*@Schema(title = "会员账号  非必填参数")
    private String userAccount;*/
    @Schema(title = "会员Id  必填参数")
    private String userId;

    // 必填参数
    /*@Schema(title = "代理账号  必填参数")
    private String agentAccount;*/

    @Schema(title = "代理账号Id  必填参数")
    private String agentId;
    /**
     * 1下注 ：下注时间
     * 2结算： 三方单结算时间
     * 3人工加额：人工加额会员帐变时间
     * 4人工减额：人工减额会员帐变时间
     * 5优惠活动：活动发放奖励，还是发放时间
     * 6会员VIP福利：会员VIP福利发放时间
     */
    @Schema(title = "业务场景code  必填参数")
    private Integer bizCode;
    /**
     * 1下注
     * 2结算：我方注单号
     * 3人工加额：人工加额订单号
     * 4人工减额：人工减额订单号
     * 5优惠活动：优惠活动订单号
     * 6.已使用优惠，转换订单号
     * 7会员VIP福利：会员VIP福利订单号
     */
    @Schema(title = "我方注单号，且是必填写")
    private String orderId;
    // ---------------------------------------------------------

    @Schema(title = "下注和结算 接收一个list")
    private List<UserWinLoseMqVOTest> orderList;


    // ---------------------------------------------------------
    // 业务场景1:下注
    @Schema(title = "投注金额")
    private BigDecimal betAmount;
    @Schema(title = "终端类型")
    private Integer deviceType;

    // ---------------------------------------------------------

    // ---------------------------------------------------------
    // 业务场景2:结算
    /**
     * 订单状态-结算
     * 如果为投注 {@link com.cloud.baowang.play.api.enums.order.OrderStatusEnum}
     */
    @Schema(title = "订单状态-结算")
    private Integer orderStatus;
    @Schema(title = "投注金额-结算------不是有效投注")
    private BigDecimal betAmountSettlement;
    /**
     * 此字段只针对 下注取消的场景，是否需要增加注单量。 如果是取消的，或者是重新结算的，则不增加注单量
     */
    @Schema(title = "打赏")
    private String playType;
    @Schema(title = "打赏金额")
    private BigDecimal tipsAmount = BigDecimal.ZERO;
    @Schema(title = "上一次结算时间 日期-小时维度")
    private Long lastDayHour;
    @Schema(title = "上一次投注额")
    private BigDecimal lastBetAmount;
    @Schema(title = "上一次有效投注")
    private BigDecimal lastValidBetAmount;
    @Schema(title = "上一次 投注盈亏 会员输传递负数 会员赢传递正数")
    private BigDecimal lastBetWinLose;
    @Schema(title = "上次打赏金额")
    private BigDecimal lastTipsAmount;
    @Schema(title = "是否加注单量  0不加 1加")
    private Integer isAddBetNum;
    @Schema(title = "有效投注")
    private BigDecimal validBetAmount;
    @Schema(title = "投注盈亏 会员输传递负数 会员赢传递正数")
    private BigDecimal betWinLose;
   /* @Schema(title = "我方注单号")
    private String orderIdSettlement;*/
    // ---------------------------------------------------------

    // ---------------------------------------------------------

    /**
     * {@link com.cloud.baowang.common.core.enums.manualDowmUp.ManualAdjustTypeEnum}
     * 业务场景3:人工加额 人工加额传递正数
     * upCode限制: 3其他调整 4会员存款(后台)  5会员VIP福利 6.会员活动 7补单-缺失额度  9补单-其他调整  0其他调整
     */
    @Schema(title = "人工加额-类型code")
    private Integer upCode;
    @Schema(title = "人工加额-金额")
    private BigDecimal upAmount;
    //todo 补充vip订单号
    /*@Schema(title = "人工加额订单号")
    private String upOrderNo;*/
    // ---------------------------------------------------------

    // ---------------------------------------------------------

    /**
     * {@link com.cloud.baowang.common.core.enums.manualDowmUp.ManualDownAdjustTypeEnum}
     * 业务场景4:人工减额 人工减额传递负数
     * downCode限制:1人工加减款  3其他调整 5会员VIP福利 6会员活动  7补单-缺失额度 9补单-其他调整
     */
    @Schema(title = "人工减额-类型code")
    private Integer downCode;// 3
    @Schema(title = "人工减额-金额")
    private BigDecimal downAmount;
    //todo 补充vip订单号
    /*@Schema(title = "人工减额订单号")
    private String downOrderNo;*/
    // ---------------------------------------------------------

    // ---------------------------------------------------------
    /**
     * 业务场景5:优惠活动，该金额单位是主货币，如果是平台币，则统计到优惠金额，如果是主货币，则统计到已使用优惠。
     */
    @Schema(title = "优惠活动-金额")
    private BigDecimal activityAmount;

    @Schema(title = "币种")
    private String currency;

    /**
     * vip福利 都是平台币。活动优惠 有的是平台币，有的是主货币
     */
    @Schema(title = "是否平台币")
    private Boolean platformFlag = Boolean.FALSE ;

    //todo 补充vip订单号
    /*@Schema(title = "优惠活动订单号")
    private String activityOrderNo;*/
    // 待添加
    // ---------------------------------------------------------

    // ---------------------------------------------------------
    // 业务场景6:会员返水
    /*@Schema(title = "返水金额")
    private BigDecimal rebateAmounts;*/

    /**
     * 业务场景6:已经使用优惠金额
     */
    @Schema(title = "已经使用优惠金额")
    private BigDecimal alreadyUseAmount;
    /*@Schema(title = "返水订单号")
    private String rebateOrderNo;*/
    // ---------------------------------------------------------

    // ---------------------------------------------------------
    /**
     * 业务场景7:会员VIP福利 ，把平台币换算为主货币
     */
    @Schema(title = "会员VIP福利-金额-主货币")
    private BigDecimal vipBenefitAmount;

    @Schema(title = "会员VIP福利-金额-主货币")
    private BigDecimal rebateAmount;
    // ---------------------------------------------------------
   /* @Schema(title = "会员VIP福利订单号")
    private String vipBenefitOrderNo;*/

    // ---------------------------------------------------------
   /* @Schema(title = "会员VIP福利订单号")
    private String vipBenefitOrderNo;*/





    /**
     * 账号类型 1测试 2正式
     */
    private Integer accountType;

    @Schema(title = "会员返水-金额-平台币")
    private String betType;

}
