package com.cloud.baowang.es.sync.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
public class OrderRecord implements Serializable {

    public final static String COLLECTION_NAME = "order_record";

    /**
     *  必须有 id,这里的 id 是全局唯一的标识，等同于 es 中的"_id"
     */
    private String id;
    /**
     * 站点code
     */
    private String siteCode;
    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 会员id
     */
    private String userId;

    /**
     * 会员账号
     */
    private String userAccount;
    /**
     * 会员姓名
     */
    private String userName;
    /**
     * 账号类型 1测试 2正式 3商务 4置换
     */
    private Integer accountType;
    /**
     * 三方会员账号
     */
    private String casinoUserName;
    /**
     * 上级代理id
     */
    private String agentId;
    /**
     * 上级代理账号
     */
    private String agentAcct;
    /**
     * VIP段位
     */
    private Integer vipRank;
    /**
     * VIP等级
     */
    private Integer vipGradeCode;

    /**
     * 游戏平台CODE
     */
    private String venueCode;
    /**
     * 游戏类别 lookup venue_type
     */
    private Integer venueType;
    /**
     * 游戏名称
     */
    private String gameName;

    /**
     * 三方游戏code
     */
    private String thirdGameCode;
    /**
     * 房间类型
     */
    private String roomType;
    /**
     * 房间类型名称
     */
    private String roomTypeName;
    /**
     * 玩法类型
     */
    private String playType;

    /**
     * 投注时间
     */
    private Long betTime;
    /**
     * 结算时间
     */
    private Long settleTime;

    /**
     * 首次结算时间
     */
    private Long firstSettleTime;
    /**
     * 投注额
     */
    private BigDecimal betAmount;
    /**
     * 有效投注
     */
    private BigDecimal validAmount;
    /**
     * 派彩金额
     */
    private BigDecimal payoutAmount;
    /**
     * 输赢金额
     */
    private BigDecimal winLossAmount;
    /**
     * 注单ID
     */
    private String orderId;
    /**
     * 三方注单ID
     */
    private String thirdOrderId;
    /**
     * 注单状态
     */
    private Integer orderStatus;
    /**
     * 注单归类
     */
    private Integer orderClassify;
    /**
     * 赔率
     */
    private String odds;
    /**
     * 局号/期号
     */
    private String gameNo;
    /**
     * 桌号
     */
    private String deskNo;
    /**
     * 靴号
     */
    private String bootNo;
    /**
     * 结果牌 /结果
     */
    private String resultList;
    /**
     * 下注内容
     */
    private String betContent;
    /**
     * 变更状态
     */
    private Integer changeStatus;
    /**
     * 变更次数
     */
    private Integer changeCount;
    /**
     * 变更时间
     */
    private Long changeTime;
    /**
     * 投注IP
     */
    private String betIp;
    /**
     * 币种
     */
    private String currency;
    /**
     * 设备类型
     */
    private Integer deviceType;
    /**
     * 串关信息
     */
    private String parlayInfo;
    /**
     * 备注
     */
    private String remark;
    private Long createdTime;
    private Long updatedTime;

    /**
     * 结果产生时间(重结算后结算时间不会变化的场景, 使用该字段来判断是否发生重结算)
     */
    private Long resultTime;

    /**
     * 最新变更时间，有重结算和撤销等异常时变更，初始值为落库时间
     */
    private Long latestTime;

    /**
     * 注单详情
     */
    private String orderInfo;

    /**
     * 玩法
     */
    private String playInfo;

    /**
     * 赛事信息
     */
    private String eventInfo;

    /**
     * 转账ID
     */
    private String transactionId;

    /**
     * 备用字段
     */
    private String exId1;

    /**
     * 备用字段
     */
    private String exId2;
}
