package com.cloud.baowang.wallet.po.vipV2;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 26/6/23 2:47 PM
 * @Version : 1.0
 */
@Data
@Accessors(chain = true)
@TableName("site_vip_award_record_v2")
@Schema(title = "VIP奖励记录")
@FieldNameConstants
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VIPAwardRecordV2PO extends BasePO {

    /* 订单号 */
    private String orderId;

    private String userId;

    private String siteCode;

    /* 领取时间 */
    private Long receiveTime;

    /* 奖励类型 */
    private String awardType;
    /* 币种 */
    private String currency;

    /* 奖励金额 */
    private BigDecimal awardAmount;

    /* 领取状态 */
    private Integer receiveStatus;

    /* 领取方式 */
    private Integer receiveType;

    /* 过期时间 */
    private long expiredTime;

    /* 会员账号 */
    private String userAccount;
    /* 上级代理id */
    private String agentId;
    /* 代理账号 */
    private String agentAccount;
    /* VIP等级 */
    private Integer vipGradeCode;
    /* VIP段位 */
    private Integer vipRankCode;
    /* 统计开始时间 */
    private long recordStartTime;
    /* 统计结束时间 */
    private long recordEndTime;

    /* 账号类型 */
    private String accountType;

    /* 需要完成的打码量 */
    private BigDecimal requireTypingAmount;
}
