package com.cloud.baowang.play.game.cmd.constant;

/**
 * nextSpin常量类
 *
 * @author: mufan
 * @creat: 2025/7/19 09:49
 */
public final class CmdConstant {

    /**
     * 拉取的时间间隔
     */
    public static final Integer DEFAULT_TIME_INTERVAL = 10 * 60 * 1000;


    /**
     * 请求Partner退款, 若赌注因危险球情况而被取消 (取消下注)
     */
    public static final int DangerRefund = 2001;


    /**
     * 请求Partner更新余额, 若异常状态人工接拒注单 (取消下注)
     */
    public static final int ResettleTicket = 2002;
    /**
     * 请求Partner更新会员账户, 若注单已提錢兑现 （派彩）
     */
    public static final int BTBuyBack = 3001;


    /**
     * 请求Partner更新会员余额, 若上半场已得到结算 派彩）
     */
    public static final int SettleHT = 4001;
    /**
     * 请求Partner更新会员余额, 若全场已得到结算 派彩）
     */
    public static final int SettleFT = 4002;


    /**
     * 请求Partner更新会员余额, 若混合过关已得到结算 （派彩）
     */
    public static final int SettleParlay = 4003;
    /**
     * 请求Partner退款, 若赌注因危险球情况而被取消 （取消派彩）
     */
    public static final int UnsettleHT = 5001;


    /**
     * 请求Partner更新会员余额, 若已结算的全场赛果改成进行中 (取消派彩）
     */
    public static final int UnsettleFT = 5002;

    /**
     * 请求Partner更新会员余额, 若已结算的混合过关改成进行中 (取消派彩）
     */
    public static final int UnsettleParlay = 5003;


    /**
     * 请求Partner退回投注金额给会员, 若上半场比赛被取消 (平局 派彩)
     */
    public static final int CancelHT = 6001;
    /**
     * 请求Partner退回投注金额给会员, 若全场比赛被取消  (平局 派彩)
     */
    public static final int CancelFT = 6002;


    /**
     * 请求Partner更新会员余额, 若回滚已被取消的上半场比赛  (平局 派彩)
     */
    public static final int UncancelHT = 7001;
    /**
     * 请求Partner更新会员余额, 若回滚已被取消的全场比赛  (平局 派彩)
     */
    public static final int UncancelFT = 7002;


    /**
     * 系统调帐 重派彩 根据传递金额加减）
     */
    public static final int SystemAdjustment = 9000;



}
