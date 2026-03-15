package com.cloud.baowang.play.api.vo.agent;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Author : 小智
 * @Date : 2/6/23 11:13 AM
 * @Version : 1.0
 */
@Data
@I18nClass
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "代理统计代理下级会员投注记录游戏场馆分类返回对象")
public class AgentBetGameOrderVO implements Serializable {

    /**
     * 三方平台名字
     */
    private String venueName;
    /**
     * 三方平台游戏类型(如AG电子:AG1)
     */
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_CODE)
    private String venueCode;
    @Schema(title = "游戏平台-名称")
    private String venueCodeText;

    @Schema(title = "游戏平台名称")
    private String venuePlatformName;
    /**
     * 注单号
     */
    private String orderId;

    /**
     * 游戏图标
     */
    @Schema(title = "游戏图标")
    private String gameIcon;

    @Schema(title = "投注额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal betAmount = BigDecimal.ZERO;

    @Schema(title = "有效投注")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal validAmount = BigDecimal.ZERO;

    @Schema(title = "币种")
    private String currency;

    @Schema(title = "注单数")
    private Long betNum;

    @Schema(description = "投注时间")
    private Long betTime;

    @Schema(title = "输赢金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal winLossAmount = BigDecimal.ZERO;

   /* public BigDecimal getBetAmount() {
        return betAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getValidAmount() {
        return validAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getWinLossAmount() {
        return winLossAmount.setScale(2, RoundingMode.HALF_UP);
    }*/
}
