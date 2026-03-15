package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * VIP奖励发放记录
 * </p>
 *
 * @author 作者
 * @since 2024-10-28
 */
@Getter
@Setter
@Schema(description = "VIP奖励发放记录")
@TableName("site_vip_award_record")
public class SiteVipAwardRecordPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 奖励类型(0:升级礼金，1:周流水,2:月流水,3:周体育流水)
     */
    private String awardType;

    /**
     * 奖励金额
     */
    private BigDecimal awardAmount;

    /**
     * 上级代理id
     */
    private String agentId;

    /**
     * 代理账号
     */
    private String agentAccount;

    /**
     * 领取方式(0:手动领取,1:自动领取)
     */
    private String receiveType;

    /**
     * 领取状态(0:未领取,1:已领取,2:已过期)
     */
    private String receiveStatus;

    /**
     * 会员id
     */
    private String userId;

    /**
     * 会员账号
     */
    private String userAccount;

    /**
     * VIP等级
     */
    private Integer vipGradeCode;

    /**
     * VIP段位
     */
    private Integer vipRankCode;

    /**
     * 账号类型
     */
    private String accountType;

    /**
     * 统计开始时间
     */
    private Long recordStartTime;

    /**
     * 统计结束时间
     */
    private Long recordEndTime;

    /**
     * 领取时间
     */
    private Long receiveTime;

    /**
     * 过期时间
     */
    private Long expiredTime;

    /**
     * 创建人
     */
    private Long creator;

    /**
     * 创建时间
     */
    private Long createdTime;

    /**
     * 更新人
     */
    private Long updater;

    /**
     * 更新时间
     */
    private Long updatedTime;


}
