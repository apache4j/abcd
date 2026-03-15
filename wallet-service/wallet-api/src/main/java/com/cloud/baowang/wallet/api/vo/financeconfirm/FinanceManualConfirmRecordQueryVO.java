package com.cloud.baowang.wallet.api.vo.financeconfirm;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title ="会员提款人工确认记录query")
public class FinanceManualConfirmRecordQueryVO extends PageVO {
    @Schema(title = "申请时间-开始")
    private Long applyStartTime;

    @Schema(title = "申请时间-结束")
    private Long applyEndTime;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "会员ID")
    private String userAccount;

    @Schema(title = "会员注册信息")
    private String userRegister;

    @Schema(title = "订单状态")
    private String status;

}
