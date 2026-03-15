package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "测试充值webSocket")
public class TestWebScoketVO {

    private String siteCode;

    private String userId;

    private String orderNo;

    private String customerStatus;

    private Long udpatedTime;
}
