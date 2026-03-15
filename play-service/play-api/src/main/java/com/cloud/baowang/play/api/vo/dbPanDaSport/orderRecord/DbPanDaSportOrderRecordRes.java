package com.cloud.baowang.play.api.vo.dbPanDaSport.orderRecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DbPanDaSportOrderRecordRes {


    /**
     * 用户名称
     */
    private String userName;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 订单Id
     */
    private String orderNo;

    /**
     * 订单状态(具体见字段映射)
     */
    private Integer orderStatus;

    /**
     * 注单类型:1=常规，2=预约，3=合买
     */
    private Integer orderType;

    /**
     * 投注时间(13位时间戳)
     */
    private Long createTime;

    /**
     * 订单更新时间
     */
    private Long modifyTime;

    /**
     * 订单实际投注金额
     */
    private BigDecimal orderAmount;

    /**
     * 注单项数量
     */
    private Integer betCount;

    /**
     * 结算时间
     */
    private Long settleTime;

    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 最高可赢金额(注单金额*注单赔率)
     */
    private BigDecimal maxWinAmount;

    /**
     * 结算金额
     */
    private BigDecimal settleAmount;

    /**
     * 提前结算投注金额
     */
    private BigDecimal preBetAmount;

    /**
     * 盈利金额
     */
    private BigDecimal profitAmount;

    /**
     * 订单结算结果
     */
    private Integer outcome;

    /**
     * 串关类型(请参考附录)
     */
    private Integer seriesType;

    /**
     * 串关值
     */
    private String seriesValue;

    /**
     * 结算次数
     */
    private Integer settleTimes;

    /**
     * 设备类型 1:H5 2:PC 3:Android 4:IOS
     */
    private String deviceType;

    /**
     * 移动设备标识
     */
    private String deviceImei;

    /**
     * IP 地址
     */
    private String ip;

    /**
     * 币种
     */
    private String currency;

    /**
     * vip 级别 : 0,1
     */
    private Integer vipLevel;

    /**
     * DbPanDaSportPreOrderStatusEnum.java
     * 预约状态
     * 0:预约中
     * 1:预约成功
     * 2:风控预约失败
     * 3:风控取消预约注单
     * 4:用户手动取消预约投注
     */
    private Integer preOrderStatus;

    /**
     * 是否预约注单 0:否 1:是
     */
    private Integer preOrder;

    /**
     * DbPanDaSportBillStatusEnum.java
     * 结算状态 0:未结算 1:已结算 2:结算异常
     */
    private Integer billStatus;

    /**
     * DbPanDaSportMatchCodeEnum.java
     * 比赛类型 1:常规赛事 2:冠军赛事 3:VR赛事 4:电子赛事
     */
    private Integer matchCode;

    /**
     * 赔率
     */
    private BigDecimal odds;

    /**
     * 明细列表
     */
    private List<DbPanDaSportOrderDetail> detailList;

}
