package com.cloud.baowang.report.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("report_bet_user_remaining")
@Schema(title = "留存报表")
public class ReportBetUserRemainingPO extends BasePO {

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 日期串（例： 2025-0-01）
     */
    private String dayStr;

    /**
     * 日期开始时间戳
     */
    private Long dayMillis;

    /**
     * 注册人数
     */
    private long registerCount;

    /**
     * 次日投注过的玩家数
     */
    private long secondDayBetUserCount;

    /**
     * 次日投注过的玩家比例
     */
    private BigDecimal secondDayBetUserRate= BigDecimal.ZERO;

    /**
     * 三日投注过的玩家数
     */
    private long threeDayBetUserCount;

    /**
     * 三日投注过的玩家比例
     */
    private BigDecimal threeDayBetUserRate= BigDecimal.ZERO;

    /**
     * 七日投注过的玩家数
     */
    private long sevenDayBetUserCount;

    /**
     * 七日投注过的玩家比例
     */
    private BigDecimal sevenDayBetUserRate= BigDecimal.ZERO;

    /**
     * 15日投注过的玩家数
     */
    private long fifteenDayBetUserCount;

    /**
     * 15日投注过的玩家比例
     */
    private BigDecimal fifteenDayBetUserRate= BigDecimal.ZERO;

    /**
     * 30日投注过的玩家数
     */
    private long thirtyDayBetUserCount;

    /**
     * 30日投注过的玩家比例
     */
    private BigDecimal thirtyDayBetUserRate = BigDecimal.ZERO;

}
