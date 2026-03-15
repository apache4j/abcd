package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查询会员的最后下注信息")
public class GetLastOrderRecordVO {
    /**
     * 会员账号
     */
    private String userAccount;
    /**
     * 投注时间
     */
    private Long betTime;
    /**
     * 注单ID
     */
    private String orderId;
    /**
     * 注单状态(1:已结算,2:未结算)
     */
    private Integer orderStatus;
    /**
     * 币种
     */
    private String currency;
}
