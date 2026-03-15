package com.cloud.baowang.agent.api.vo.depositWithdraw;


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
@Schema(title = "代理近期存提款信息")
public class AgentRecentlyDepositWithdrawVO {


    @Schema(description = "上次提款时间")
    private Long lastWithdrawTime;

    @Schema(description = "上次提款金额")
    private BigDecimal lastWithdrawAmount;

    @Schema(description = "上次提款至本次提款间存款金额")
    private BigDecimal lastWithdrawAfterDepositAmount;

    @Schema(description = "上次提款类型")
    private String depositWithdrawType;

    @Schema(description = "上次提款方式")
    @I18nField
    private String lastDepositWithdrawMethod;

    @Schema(description = "上次提款信息")
    private String lastDepositWithdrawInfo;

    @Schema(description = "上次是否为大额提款")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isBigMoney;

    @Schema(description = "上次是否为大额提款名称")
    private String isBigMoneyText;

    @Schema(description = "平台币符号")
    private String currencyCode;

}
