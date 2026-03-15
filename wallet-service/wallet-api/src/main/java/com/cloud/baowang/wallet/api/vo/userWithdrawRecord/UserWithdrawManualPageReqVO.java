package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author qiqi
 */
@Data
@Schema(title = "会员人工出款列表请求对象")
public class UserWithdrawManualPageReqVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "operator", hidden = true)
    private String operator;


    @Schema(description = "提款时间-开始")
    private Long startTime;

    @Schema(description = "提款时间-结束")
    private Long endTime;

    @Schema(description = "订单号")
    private String orderNo;


    @Schema(description = "币种代码  code：currencyCode")
    private String currencyCode;


    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "提款方式ID code：withdraw_way")
    private String withdrawWayId;

    @Schema(description = "状态集合",hidden = true)
    private List<String> customerStatusList;

    @Schema(description = "状态 code：deposit_withdraw_customer_status")
    private String customerStatus;
}
