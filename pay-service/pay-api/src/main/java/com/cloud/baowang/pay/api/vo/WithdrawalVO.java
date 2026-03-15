package com.cloud.baowang.pay.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "代付VO")
public class WithdrawalVO {

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "真实姓名-提款用")
    private String userName;

    @Schema(description = "通道Id")
    private String channelId;

    @Schema(description = "提款订单号")
    private String orderNo;

    @Schema(description = "用户类型 0 会员 1 代理")
    private Integer accountType;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "国家代码，两位简写")
    private String countryCode;

    @Schema(description = "提款金额")
    private String amount;

    @Schema(description = "银行卡姓名")
    private String bankUserName;

    @Schema(description = "银行名称")
    private String bankName;

    @Schema(description = "银行卡号")
    private String bankNo;

    @Schema(description = "银行Code")
    private String bankCode;

    @Schema(description = "支行")
    private String bankBranch;

    @Schema(description = "订单创建时间")
    private Long createTime;

    @Schema(description = "设备类型")
    private String deviceType;

    @Schema(description = "申请IP")
    private String applyIp;

    @Schema(description = "邮箱，部分三方需要")
    private String email;

    @Schema(description = "链类型")
    private String chainType;

    @Schema(description = "提现地址")
    private String toAddress;

    private String ownerUserType;

    private String siteCode;

    private String telephone;

    private String ifscCode;

    /**
     * 提现渠道VO
     */
    private SystemWithdrawChannelVO withdrawChannelVO;
}
