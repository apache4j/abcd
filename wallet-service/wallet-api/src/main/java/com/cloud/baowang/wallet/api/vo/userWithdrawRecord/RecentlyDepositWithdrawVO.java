package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@I18nClass
@Schema(title = "近期存提款信息")
public class RecentlyDepositWithdrawVO {


    @Schema(description = "上次提款时间")
    private Long lastWithdrawTime;

    @Schema(description = "上次提款金额")
    private BigDecimal lastWithdrawAmount;

    @Schema(description = "上次提款至本次提款间存款金额")
    private BigDecimal lastWithdrawAfterDepositAmount;

    @Schema(description = "上次提款类型")
    private String depositWithdrawType;
    @I18nField
    @Schema(description = "上次提款方式")
    private String lastDepositWithdrawWay;

    @Schema(description = "上次是否为大额提款")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isBigMoney;

    @Schema(description = "上次是否为大额提款名称")
    private String isBigMoneyText;

    @Schema(description = "上次提款方式对应的币种")
    private String currencyCode;

}
