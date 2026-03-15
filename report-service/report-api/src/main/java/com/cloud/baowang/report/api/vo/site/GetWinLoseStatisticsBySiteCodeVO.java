package com.cloud.baowang.report.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(description = "站点-总输赢 按天统计 VO")
public class GetWinLoseStatisticsBySiteCodeVO implements Serializable {

    @Schema(description = "日期")
    private String myDay;


    @Schema(description = "日期")
    private Integer myDayOrder;

    public Integer getMyDayOrder() {
        if(StringUtils.isBlank(myDay)){
            return 0;
        }
        String str = myDay.replaceAll("-","");
        return Integer.valueOf(str);
    }


    @Schema(description = "输赢金额总和")
    private BigDecimal betWinLose = BigDecimal.ZERO;

    @Schema(description = "净盈亏")
    private BigDecimal profitAndLoss = BigDecimal.ZERO;

    /**
     * 币种
     */
    private String currencyCode;
}
