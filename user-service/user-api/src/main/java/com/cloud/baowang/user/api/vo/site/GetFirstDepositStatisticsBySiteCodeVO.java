package com.cloud.baowang.user.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(description = "代理客户端-首存人数 按天统计 VO")
public class GetFirstDepositStatisticsBySiteCodeVO {

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

    @Schema(description = "首存人数总和")
    private BigDecimal firstDepositNumber;
}
