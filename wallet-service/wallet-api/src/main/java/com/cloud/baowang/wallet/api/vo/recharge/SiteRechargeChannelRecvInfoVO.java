package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Desciption:
 * @Author: ford
 * @Date 2025-07-17
 **/
@Data
@Schema(description = "站点存款通道收款信息")
public class SiteRechargeChannelRecvInfoVO {
    @Schema(description = "主键ID")
    @NotNull(message = "主键ID不能为空")
    private String id;

    @Schema(description = "姓名/电子钱包姓名")
    private String recvUserName;

    @Schema(description = "银行编码")
    private String recvBankCode;

    @Schema(description = "银行名称")
    private String recvBankName;
    @Schema(description = "开户行")
    private String recvBankBranch;
    @Schema(description = "银行帐号/电子钱包地址/虚拟币地址")
    private String recvBankCard;

    @Schema(description = "电子钱包账户")
    private String recvBankAccount;


    @Schema(description = "收款码")
    private String recvQrCode;

    @Schema(description = "当前操作用户",hidden = false)
    private String currentUserNo;
}


