package com.cloud.baowang.wallet.api.vo.userwallet;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "大陆盘-会员收款信息")
@I18nClass
public class UserReceiveAccountResponseVO {



    /**
     * 收款类型  bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币
     */
    @Schema(description = "收款类型  bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.WITHDRAW_TYPE )
    private String receiveType;

    @Schema(description = "收款类型名称")
    private String receiveTypeText;

    @Schema(description = "银行卡已绑定数量")
    private Integer boundNums;

    @Schema(description = "银行卡可绑定数量")
    private Integer bindableNums;

    @Schema(description = "收款详细信息集合")
    private List<UserReceiveAccountVO> userReceiveAccountVOS;


}
