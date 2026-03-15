package com.cloud.baowang.wallet.api.vo.userwallet;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "大陆盘-会员收款信息解绑请求对象")
@I18nClass
public class UserReceiveAccountUnBindVO {


    /**
     * id
     */
    @Schema(description = "id")
    private String id;

    @Schema(description = "收款类型  bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String receiveType;

    /**
     * 会员账号
     */
    @Schema(description = "会员账号")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String userAccount;

    @Schema(description = "站点编码",hidden = true)
    private String siteCode;




}
