package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "代理存款记录Param")
public class DepositRecordParam extends PageVO {

    @Schema(title = "币种代码")
    private String currencyCode;

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "支付方式")
    private String paymentMethod;

    @Schema(title = "存款开始时间", required = true)
    @NotNull(message = "存款时间不能为空")
    private Long startTime;

    @Schema(title = "存款结束时间", required = true)
    @NotNull(message = "存款时间不能为空")
    private Long endTime;

    @Schema(title = "订单状态")
    private Integer customerStatus;

    @Schema(title = "代理账号，后端自动获取，前端不传递")
    private String agentAccount;

    @Schema(title = "站点编号")
    private String siteCode;
}
