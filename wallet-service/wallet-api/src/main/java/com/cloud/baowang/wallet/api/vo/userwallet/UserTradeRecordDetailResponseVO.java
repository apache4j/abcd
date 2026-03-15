package com.cloud.baowang.wallet.api.vo.userwallet;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.wallet.api.vo.recharge.UserDepositOrderDetailVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "会员交易记录详情返回对象-非存款")
@I18nClass
public class UserTradeRecordDetailResponseVO {

   /* @Schema(description = "存款详情")
    private UserDepositOrderDetailVO depositOrderDetailVO;*/

    @Schema(description = "提款详情")
    private UserWithdrawDetailVO withdrawOrderDetailVO;

    @Schema(description = "人工加减额详情")
    private UserManualUpDownDetailVO manualUpDownDetailVO;

    @Schema(description = "上级转入详情")
    private UserSuperTransferDetailVO superTransferDetailVO;

    @Schema(description = "平台币兑换详情")
    private UserPlatformTransferDetailVO platformTransferDetailVO;

}
