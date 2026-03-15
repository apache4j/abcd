package com.cloud.baowang.wallet.api.vo.bank;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "银行列表返回vo")
@I18nClass
public class BankManageVO {

    @Schema(description = "银行名称")
    @I18nField
    private String bankName;

    @Schema(description = "银行代码")
    private String bankCode;

    @Schema(description = "图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;

    @Schema(description = "图标完整url")
    private String iconFileUrl;

}
