package com.cloud.baowang.account.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员中心钱包账变记录对象
 *
 * @author qiqi
 */
@Data
@TableName("user_coin_record")
public class UserCoinRecordPO extends BasePO {

    /**
     * 站点CODE
     */
    private String siteCode;

    /**
     * 会员名称
     */
    private String userName;

    /**
     * 会员账号
     */
    private String userAccount;
    /**
     * 会员ID
     */
    private String userId;

    /**
     * 账号状态
     */
    private String accountStatus;

    /**
     * 账号类型1测试 2正式
     */
    private String accountType;

    /**
     * 会员标签IDS
     */
    private String userLabelId;


    /**
     * 风控级别Id
     */
    private String riskControlLevelId;

    /**
     * 风控级别
     */
    private String riskControlLevel;


    /**
     * VIP段位
     */
    private Integer vipRank;

    /**
     * VIP等级
     */
    private Integer vipGradeCode;

    /**
     * 代理ID
     */
    private String  agentId;

    /**
     * 代理名称
     */
    private String agentName;

    /**
     * 钱包类型 1 中心钱包
     */
    private String walletType;

    /**
     * 币种
     */
    private String currency;

    /**
     * 关联订单号
     */
    private String orderNo;


    /**
     * 业务类型 1 会员存款 2 上级转下级 3 代客充值 4 佣金转中心钱包 5 会员取款 6 自身钱包互转 7 会员返水 8 会员活动 9 会员VIP优惠  0 其他调整
     */
    private String businessCoinType;

    /**
     *账变类型 1会员存款 2会员存款（后台）3代客充值 4上级转入 5 转到B端钱包 6 转回中心钱包 7 会员提款 8 会员提款(后台)
     *  9 会员返水 10 返水增加金额 11 返水扣除金额 12 优惠活动 13 优惠活动增加金额 14 会员活动扣除金额 15 其他增加调整 16 其他扣除调整
     *  17 会员VIP优惠 18 会员VIP优惠增加调整 19 会员VIP优惠扣除调整 20 佣金转回
     */
    private String coinType;

    /**
     * 客户端账变类型
     */
    private String customerCoinType;

    /**
     * 收支类型1收入,2支出 3冻结 4 解冻
     */
    private String balanceType;

    /**
     * 金额改变数量
     */
    private BigDecimal coinValue;

    /**
     * 账变前金额
     */
    private BigDecimal coinFrom;

    /**
     * 账变后金额
     */
    private BigDecimal coinTo;

    /**
     * '当前金额
     */
    private BigDecimal coinAmount;



    /**
     * 备注
     */
    private String remark;

    /**
     * 账变计数-主要用于重结算多次账变
     */
    private Integer coinNum;

    /**
     * 描述信息，用于存特殊场馆的一些备注
     */
    private String descInfo;

    /**
     * 三方交易单号，唯一
     */
    private String exId1;

    /**
     * 备用字段
     */
    private String exId2;

    /**
     * 备用字段
     */
    private String exId3;



}

