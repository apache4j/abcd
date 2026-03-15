package com.cloud.baowang.report.api.vo.game;

import cn.hutool.core.util.ObjUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "游戏报表站点返回vo")
public class ReportGameQueryCenterVO extends ReportGameAmountBase{
    @Schema(title = "站点code")
    private String siteCode;
    @Schema(title = "站点名称")
    private String siteName;
    @Schema(title = "币种")
    private String currency;
    @Schema(title = "平台币code")
    private String platCurrency = CommonConstant.PLAT_CURRENCY_CODE;

    @Schema(title = "分页汇总:true")
    private Boolean pageSumType;

//    @Schema(title = "投注人数")
//    private Long bettorNum;
//    @Schema(title = "注单量")
//    private Long betNum;
//    @Schema(title = "投注金额")
//    @JsonSerialize(using = BigDecimalJsonSerializer.class)
//    private BigDecimal betAmount;
//    @Schema(title = "有效投注金额")
//    @JsonSerialize(using = BigDecimalJsonSerializer.class)
//    private BigDecimal validBetAmount;
//
//    @Schema(title = "打赏金额")
//    @JsonSerialize(using = BigDecimalJsonSerializer.class)
//    private BigDecimal tipsAmount;
//
//    @Schema(title = "输赢金额")
//    @JsonSerialize(using = BigDecimalJsonSerializer.class)
//    private BigDecimal winLoseAmount;
}
