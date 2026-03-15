package com.cloud.baowang.user.po;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 勋章奖励获取记录
 * </p>
 *
 * @author ford
 * @since 2024-08-09 06:46:42
 */
@Getter
@Setter
@TableName("medal_reward_record")
public class MedalRewardRecordPO extends BasePO {

    private static final long serialVersionUID = 1L;


    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 站点代码
     */
    private String siteCode;

    /**
     * 会员账号
     */
    private String userAccount;

    /**
     * 上级代理id
     */
    private String superAgentId;

    /**
     * 上级代理账号
     */
    private String superAgentAccount;

    /**
     * 达成条件时间
     */
    private Long completeTime;

    /**
     * 打开时间
     */
    private Long openTime;

    /**
     * 奖励编号
     */
    private Integer rewardNo;

    /**
     * 奖励金额
     */
    private BigDecimal rewardAmount;

    /**
     * 打码倍数
     */
    private BigDecimal typingMultiple;

    /**
     * 达成数量
     */
    private Integer condNum;

    /**
     * 解锁状态 0:可打开 1:已打开
     */
    private Integer openStatus;

}
