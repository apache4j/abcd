package com.cloud.baowang.activity.po.v2;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value = "site_activity_order_record_v2")
public class SiteActivityOrderRecordV2PO extends BasePO {
    /**
     * 站点code
     */
    private String siteCode;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 所属活动
     */
    private String activityId;

    /**
     * 活动编号
     */
    private String activityNo;

    /**
     * 所属活动名称
     */
    private String activityNameI18nCode;

    /**
     * 活动模板
     * {@link com.cloud.baowang.activity.api.enums.ActivityTemplateEnum}
     */
    private String activityTemplate;
    /**
     * 会员id
     */
    private String userId;
    /**
     * 会员账号
     */
    private String userAccount;

    /**
     * 账户名称
     */
    private String userName;

    /**
     * 会员账号类型
     */
    private String accountType;
    /**
     * 代理账号
     */
    private String superAgentId;

    /**
     * VIP等级
     */
    private Integer vipGradeCode;

    /**
     * vip段位code
     */
    private Integer vipRankCode;


    /**
     * 派发方式: 0:玩家自领-过期作废，1:玩家自领-过期自动派发，2:立即派发
     * {@link com.cloud.baowang.activity.api.enums.ActivityDistributionTypeEnum}
     */
    private Integer distributionType;
    /**
     * 可领取开始时间
     */
    private Long receiveStartTime;
    /**
     * 可领取结束时间
     */
    private Long receiveEndTime;
    /**{@link ActivityReceiveStatusEnum}
     * 领取状态
     */
    private Integer receiveStatus;

    /**
     * 发放礼金时的汇率
     */
    private BigDecimal finalRate;

    /**
     * 活动赠送金额
     */
    private BigDecimal activityAmount;


    /**
     * 币种
     */
    private String currencyCode;
    /**
     * 流水倍数
     */
    private BigDecimal runningWaterMultiple;

    /**
     * 流水要求
     */
    private BigDecimal runningWater;

    /**
     * 备注
     */
    private String remark;
    /**
     * 领取时间
     */
    private Long receiveTime;
    /**
     * 领取时用户-设备号
     */
    private String deviceNo;
    /**
     * 领取时用户-ip
     */
    private String ip;
    /**
     * 红包雨session id
     */
    private String redbagSessionId;

    /**
     * （转盘活动）奖品类型 转盘奖励段位，青铜，白银，黄金
     */
    private Integer rewardRank;

    /**
     * （转盘活动）奖品类型 id
     */
    private String prizeType;

    /**
     * （转盘活动）奖品名称
     */
    private String prizeName;

    /**
     * 本金
     */
    private BigDecimal principalAmount;


    /**
     * 赠送金额-转成平台币金额-用于报表
     */
    private BigDecimal platActivityAmount;
    /**
     * 展示标志位
     */
    private Integer showFlag  = 1;


}
