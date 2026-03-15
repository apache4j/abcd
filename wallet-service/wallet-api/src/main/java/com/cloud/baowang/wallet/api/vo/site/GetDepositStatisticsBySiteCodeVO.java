package com.cloud.baowang.wallet.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(description = "站点-存取款金额 按天统计 VO")
public class GetDepositStatisticsBySiteCodeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

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


    @Schema(description = "存款金额总和")
    private BigDecimal depositAmount;

    @Schema(description = "取款金额总和")
    private BigDecimal withdrawAmount;

    @Schema(description = "用户汇总")
    private BigDecimal userCount;

    /**
     * 币种
     */
    @Schema(description = "币种")
    private String currencyCode;
}
