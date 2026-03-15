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

import java.math.BigDecimal;

@Data
@I18nClass
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "代理下会员游戏记录请求返回参数")
public class AgentUserOrderRecordPageVO {
    @Schema(title = "游戏账号")
    private String userAccount;
    @Schema(title = "注单号")
    private String orderId;
    @Schema(title = "场馆编码")
    private String venueCode;
    @Schema(title = "游戏平台-名称")
    private String venueCodeText;

    @Schema(title = "游戏平台名称")

    @I18nField
    private String venuePlatformName;

    @Schema(title = "游戏名称")
    private String gameName;
    @Schema(title = "游戏code")
    private String thirdGameCode;
    @Schema(title = "币种")
    private String currency;
    @Schema(title = "投注金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal betAmount;
    @Schema(title = "有效投注")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal validAmount;
    @Schema(title = "投注时间")
    private Long betTime;
    @Schema(title = "注单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ORDER_CLASSIFY)
    private Integer orderClassify;
    @Schema(title = "注单状态-文本")
    private String orderClassifyText;
    @Schema(title = "输赢金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal winLossAmount;
    @Schema(title = "注单详情")
    private String orderInfo;
    @Schema(title = "牌结果")
    private String resultList;

    @Schema(title = "玩法")
    private String playInfo;

    @Schema(description = "闪电龙虎-税费")
    private BigDecimal lightningAmount;

    @Schema(description = "闪电龙虎-投注本金")
    private BigDecimal totalAmount;
}
