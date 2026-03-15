package com.cloud.baowang.wallet.api.vo.recharge;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "不再提醒请求对象")
public class NotRemindRequestVO {

    @Schema(description = "协议")
    private String netWorkType;

    @Schema(description = "币种")
    private String currencyCode;
}
