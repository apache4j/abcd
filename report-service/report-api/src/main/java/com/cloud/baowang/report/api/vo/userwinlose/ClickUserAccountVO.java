package com.cloud.baowang.report.api.vo.userwinlose;

import com.cloud.baowang.common.core.utils.CurrReqUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(title = "点击会员账号 VO")
public class ClickUserAccountVO {

    @Schema(title = "yyyy-MM-dd 00:00:00对应的时间戳")
    private String dayMillis;

    @Schema(title = "上级代理账号")
    private String superAgentAccount;

    @Schema(title = "注单量")
    private Integer betNum;

    @Schema(title = "投注金额")
    private BigDecimal betAmount;

    @Schema(title = "有效投注")
    private BigDecimal validBetAmount;

    @Schema(title = "流水纠正")
    private BigDecimal runWaterCorrect;

    @Schema(title = "投注盈亏")
    private BigDecimal betWinLose;

    @Schema(title = "优惠金额")
    private BigDecimal ActivityAmount;

    @Schema(title = "返水")
    private BigDecimal rebateAmount;


    @Schema(title = "vip金额")
    private BigDecimal vipAmount = BigDecimal.ZERO;
    @Schema(title = "已经使用优惠")
    private BigDecimal alreadyUseAmount = BigDecimal.ZERO;

    @Schema(title = "调整金额(其他调整)")
    private BigDecimal adjustAmount;

    @Schema(title = "净盈亏")
    private BigDecimal profitAndLoss;

    @Schema(title = "主货币")
    private String mainCurrency;

    @Schema(title = "平台币")
    private String platCurrency;

    public String getPlatCurrency() {
        return CurrReqUtils.getPlatCurrencyName();
    }

    @Schema(title = "调整金额(其他调整)-平台币")
    private BigDecimal platAdjustAmount = BigDecimal.ZERO;
    @Schema(title = "打赏金额")
    private BigDecimal tipsAmount = BigDecimal.ZERO;

    @Schema(title = "封控金额-主货币")
    private BigDecimal riskAmount = BigDecimal.ZERO;
}
