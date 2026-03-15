package com.cloud.baowang.wallet.api.vo.userwallet;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "大陆盘-会员收款信息查询VO")
public class UserReceiveAccountQueryVO {



    /**
     * 收款类型  bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币
     */
    @Schema(description = "收款类型  bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币")
    private String receiveType;




}
