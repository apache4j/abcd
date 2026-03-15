package com.cloud.baowang.wallet.api.vo.recharge;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.wallet.api.vo.withdraw.WithdrawWayListVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "代理币种提款方式VO")
@I18nClass
public class AgentCurrencyWithdrawWayVO {

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    @Schema(description = "货币名称 多语言")
    @I18nField
    private String currencyNameI18;

    /**
     * 货币符号
     */
    @Schema(description = "货币符号")
    private String currencySymbol;

    /**
     * 图标
     */
    @Schema(description = "图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String currencyIcon;

    @Schema(description = "图标全路径")
    private String currencyIconFileUrl;

    @Schema(description = "币种对应提款方式")
    private List<WithdrawWayListVO> withdrawWayListVOS;
}
