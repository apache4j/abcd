package com.cloud.baowang.play.api.vo.pp.res;


import com.alibaba.fastjson2.JSONArray;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class PPGameLimitCurrencyResVO implements Serializable {

    //币种
    @Schema(description = "币种")
    String currency;
    //每注最小值
    @Schema(description = "每注最小值")
    BigDecimal betPerLineMin;
    //每注最大值
    @Schema(description = "每注最大值")
    BigDecimal betPerLineMax;
    @Schema(description = "限制投注大小")
    JSONArray betPerLineScales;

}
