package com.cloud.baowang.wallet.api.vo.userCoin;

import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema( description = "会员钱包账变结果对象")
public class CoinRecordResultVO implements Serializable {

    @Schema( description ="账变id")
    private String Id;

    @Schema( description ="账变结果 ture false")
    private Boolean result;

    @Schema( description ="账变后余额")
    private BigDecimal coinAfterBalance;

    @Schema( description ="账变时间")
    private Long coinRecordTime;

    @Schema( description ="账变结果CODE 0 成功  2订单号重复 3支出无钱包信息 4订单账变金额小于0  9账变执行错误")
    private UpdateBalanceStatusEnums resultStatus;
}
