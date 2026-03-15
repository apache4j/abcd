package com.cloud.baowang.play.vo.sh;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderResponseVO implements Serializable {
    private String userName;
    private String nickName;
    //商户号
    private String merchantNo;
    //商户名称
    private String merchantName;
    //订单ID 全局唯一
    private String orderId;
    //结算状态 0 未结算 1 已结算 2 撤销
    private Integer orderStatus;
    //桌号
    private String deskNo;

    /**
     * 卓台号,该字段只有视讯会有,一辉单独在视讯系统加上去的
     */
    private String deskNumber;

    //局号
    private String gameNo;
    //靴号
    private String bootNo;
    //下注总金额
    private BigDecimal totalAmount;
    //有效金额
    private BigDecimal validAmount;
    //预扣款
    private BigDecimal priorAmount;
    //预扣款 + 下注总金额
    private BigDecimal costAmount;
    //游戏类型 百家乐  龙湖  牛牛
    private Integer gameTypeId;
    //玩法
    private String playType;
    //赔率
    private BigDecimal rate;
    //派奖金额
    private BigDecimal payAmount;
    //输赢金额
    private BigDecimal winLossAmount;
    //下注时间
    private String betTime;
    private Long betTimestamp;
    //结算时间
    private String settlementTime;
    private Long settlementTimestamp;
    //结算次数
    private Integer settlementNum;
    //开奖结果
    private String betResult;
    //币种
    private String currency;

    //下注IP
    private String betIp;
    //下注设备
    private String betDevice;

    private String createdTime;
    private Long createdTimestamp;

    //一辉单独加上的结果牌模板
    private String betResultProtocol;

    //闪电龙湖的数据源
    private String betResultVoucherSource;

    /**
     * 游戏名称
     */
    private String gameTypeName;
    private String betResultVoucher;
    private BigDecimal lightningAmount;
}
