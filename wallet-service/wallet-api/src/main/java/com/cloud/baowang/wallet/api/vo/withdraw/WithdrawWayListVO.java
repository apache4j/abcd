package com.cloud.baowang.wallet.api.vo.withdraw;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@I18nClass
public class WithdrawWayListVO {


    @Schema(description = "提现类型CODE bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币 ")
    private String withdrawTypeCode;

    @Schema(description = "提现方式Id")
    private String id;

    /**
     * 提现方式
     */
    @Schema(description = "提现方式")
    @I18nField
    private String withdrawWay;


    /**
     * 图标
     */
    @Schema(description = "图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String wayIcon;

    /**
     * 图标
     */
    @Schema(description = "图标完整路径")
    private String wayIconFileUrl;

    /**
     * 手续费 5 代表5%
     */
    @Schema(description = "手续费 5 代表5%")
    private BigDecimal wayFee;

    @Schema(description = "快捷金额，逗号隔开")
    private String quickAmount;

    @Schema(description = "是否推荐 0:未推荐 1:推荐")
    private Integer recommendFlag;

    /**
     * 网络协议类型 TRC20 ERC20
     */
    @Schema(description = "网络协议类型 TRC20 ERC20")
    private String networkType;

    /**
     * 货币代码
     */
    @Schema(description = "币种")
    private String currencyCode;

}
