package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "代理下会员存款记录Param")
public class UserDepositRecordParam extends PageVO {

    @Schema(description = "siteCode")
    private String siteCode;

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
    @NotNull(message = "存款结束时间不能为空")
    private Long endTime;

    @Schema(title = "订单状态")
    private Integer customerStatus;

    @Schema(title = "agentIds",hidden = true)
    private List<String> agentIds;

}
