package com.cloud.baowang.play.api.vo.agent;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@I18nClass
@Schema(description = "代理客户端 首页游戏输赢 查询最新的5条注单 VO")
public class GetNewest5OrderRecordVO {

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "场馆code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_CODE)
    private String venueCode;
    @Schema(description ="场馆名称")
    private String venueCodeText;
    @Schema(title = "游戏平台名称")
    private String venuePlatformName;

    @Schema(description = "输赢金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal winLossAmount;

    @Schema(description = "状态 0未结算 1已结算 2已取消 3重结算")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ORDER_CLASSIFY)
    private Integer orderClassify;
    @Schema(description = "状态 - text")
    private String orderClassifyText;
    @Schema(description = "状态 - Name")
    private String orderClassifyName;

    @Schema(description = "时间")
    private Long settleTime;
    @Schema(description = "币种")
    private String currencyCode;

    public String getOrderClassifyName() {
        return this.getOrderClassifyText();
    }
}
