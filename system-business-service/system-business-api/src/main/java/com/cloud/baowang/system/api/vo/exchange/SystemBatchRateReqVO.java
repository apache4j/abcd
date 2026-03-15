package com.cloud.baowang.system.api.vo.exchange;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SystemBatchRateReqVO implements Serializable {

    // 币种
    private String currencyCode;
    //平台币转换汇率
    private BigDecimal finalRate;
    //修改人
    private String updater;
}
