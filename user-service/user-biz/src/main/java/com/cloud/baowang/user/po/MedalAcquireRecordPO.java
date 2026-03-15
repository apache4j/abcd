package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 勋章获取记录
 * </p>
 *
 * @author ford
 * @since 2024-07-27 03:13:20
 */
@Getter
@Setter
@TableName("medal_acquire_record")
public class MedalAcquireRecordPO extends BasePO {

    /**
     * 站点代码
     */
    private String siteCode;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 会员ID
     */
    private String userId;

    /**
     * 会员账号
     */
    private String userAccount;

    @Schema(description = "主货币")
    private String mainCurrency;

    @Schema(description = "上级代理id")
    private String superAgentId;

    @Schema(description = "上级代理账号")
    private String superAgentAccount;

    @Schema(description = "vip当前等级")
    private Integer vipGradeCode;

    @Schema(description = "vip等级名称")
    private String vipGradeName;

    /**
     * 勋章编号
     */
    private String medalId;

    /**
     * 勋章代码
     */
    private String medalCode;

    /**
     * 勋章名称
     */
    private String medalName;

    /**
     * 勋章名称多语言
     */
    private String medalNameI18;

    /**
     * 达成条件时间
     */
    private Long completeTime;

    /**
     * 解锁时间
     */
    private Long unlockTime;

    /**
     *  解锁状态 0:可点亮 1:已解锁
     */
    private Integer lockStatus;

    /**
     * 奖励金额
     */
    private BigDecimal rewardAmount;

    /**
     * 打码倍数
     */
    private BigDecimal typingMultiple;

    /**
     * 达成条件 N
     */
    private String condNum1;

    private String condNum2;

    /**
     * 解锁条件说明
     */
    private String medalDesc;

}
