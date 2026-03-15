package com.cloud.baowang.wallet.api.vo.financeconfirm;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema(title ="会员提款人工确认query")
public class FinanceManualConfirmQueryVO extends PageVO {
    @Schema(title = "申请时间-开始")
    private Long applyStartTime;

    @Schema(title = "申请时间-结束")
    private Long applyEndTime;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "锁单状态")
    private String lockStatus;

    @Schema(title = "审核员")
    private String auditUser;

    @Schema(title = "会员ID")
    private String userAccount;

    @Schema(title = "会员注册信息")
    private String userRegister;

    @Schema(title = "提款类型")
    private String depositWithdrawType;

    @Schema(title = "提款类型")
    private List<String> depositWithdrawMethods;

    @Schema(title = "提款币种")
    private String currency;

}
