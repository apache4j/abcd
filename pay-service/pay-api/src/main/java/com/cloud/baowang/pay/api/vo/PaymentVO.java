package com.cloud.baowang.pay.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代收VO")
@Builder
public class PaymentVO {

    @Schema(description = "用户类型 0 会员 1 代理")
    private Integer accountType;

    @Schema(description = "用户Id/代理ID")
    private String userId;


    @Schema(description = "支付金额")
    private String amount;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "订单id")
    private String orderId;

    @Schema(description = "通道Id")
    private String channelId;

    @Schema(description = "订单创建时间")
    private Date createTime;

    @Schema(description = "申请IP")
    private String applyIp;

    @Schema(description = "国家代码，两位简写")
    private String countryCode;

    @Schema(description = "银行代码")
    private String bankCode;

    @Schema(description = "邮箱，部分三方需要")
    private String email;

    @Schema(description = "手机号，部分三方需要")
    private String phoneNum;

    @Schema(description = "firstName")
    private String firstName;

    /**
     * 存充值名字
     */
    @Schema(description = "存充值名字")
    private String depositName;

    /**
     * 充值渠道信息VO
     */
    private SystemRechargeChannelVO rechargeChannelVO;

}
