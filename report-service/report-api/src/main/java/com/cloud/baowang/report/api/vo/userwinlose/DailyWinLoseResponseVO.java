package com.cloud.baowang.report.api.vo.userwinlose;


import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 每日盈亏 resp
 */
@Data
@Schema(title = "每日盈亏 resp")
public class DailyWinLoseResponseVO {

    /**
     * utc 整点对应时间戳
     */
    @Schema(title = "yyyy-MM-dd 00:00:00对应的时间戳")
    private Long dayMillisTime;

    @Schema(title = "yyyy-MM-dd HH:00:00对应的时间戳")
    private Long dayHourMillis;

    @Schema(title = "当天时间")
    private String dayStr;

    @Schema(title = "主货币")
    private String mainCurrency;

    @Schema(title = "平台币")
    private String platformCurrency;

    @Schema(title = "vip福利")
    private BigDecimal vipAmount = BigDecimal.ZERO;

    @Schema(title = "优惠金额")
    private BigDecimal activityAmount = BigDecimal.ZERO;

    @Schema(title = "已经使用优惠")
    private BigDecimal alreadyUseAmount = BigDecimal.ZERO;

    @Schema(title = "调整金额(其他调整)")
    private BigDecimal adjustAmount = BigDecimal.ZERO;

    @Schema(title = "投注人数")
    private Integer betMemNum = 0;

    @Schema(title = "注单量")
    private Integer betNum = 0;

    @Schema(title = "投注金额")
    private BigDecimal betAmount = BigDecimal.ZERO;

    @Schema(title = "有效投注")
    private BigDecimal validBetAmount = BigDecimal.ZERO;

    @Schema(title = "平台投注输赢")
    private BigDecimal betWinLose = BigDecimal.ZERO;

    @Schema(title = "净盈亏")
    private BigDecimal profitAndLoss = BigDecimal.ZERO;

    @Schema(title = "转换为平台币")
    private boolean toPlatCurr;

    @Schema(title = "返水")
    private BigDecimal rebateAmount = BigDecimal.ZERO;


    @Schema(title = "其它调整 WTC")
    private BigDecimal platAdjustAmount = BigDecimal.ZERO;


    @Schema(title = "打赏金额")
    private BigDecimal tipsAmount = BigDecimal.ZERO;



}
