package com.cloud.baowang.wallet.api.vo.fundadjust;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "会员存款记录 Request")
public class UserDepositRecordPageVO extends PageVO {

    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(title = "存款时间-开始")
    private Long createStartTime;

    @Schema(title = "存款时间-结束")
    private Long createEndTime;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "订单来源")
    private Integer deviceType;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(title = "存款IP")
    private String applyIp;
    @Schema(title = "订单状态")
    private Integer status;
    @Schema(title = "客户端状态")
    private Integer customerStatus;

    @Schema(title = "充值方式")
    private String depositWithdrawWay;

    @Schema(title = "存款通道")
    private String depositWithdrawChannelName;

    @Schema(title = "完成开始时间")
    private Long finishStartTime;

    @Schema(title = "完成结束时间")
    private Long finishEndTime;

    @Schema(title = "存款通道code")
    private String depositWithdrawChannelCode;
}
