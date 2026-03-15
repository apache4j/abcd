package com.cloud.baowang.wallet.api.vo.bank;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@I18nClass
@Schema(description = "银行卡管理详情返回vo")
public class BankCardManagerInfoResVO {
    @Schema(description = "id")
    private String id;
    @Schema(description = "银行名称")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String bankName;
    @Schema(description = "银行名称-i18n")
    private List<I18nMsgFrontVO> bankNameList;
    @Schema(description = "银行代码")
    private String bankCode;
    @Schema(description = "图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;
    @Schema(description = "图标完整url")
    private String iconFileUrl;
    @Schema(description = "币种")
    private String currency;
}
