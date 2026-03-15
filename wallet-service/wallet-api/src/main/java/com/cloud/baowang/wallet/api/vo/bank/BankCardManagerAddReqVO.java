package com.cloud.baowang.wallet.api.vo.bank;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "银行卡管理新增入参vo")
public class BankCardManagerAddReqVO {
    @NotEmpty(message = ConstantsCode.MISSING_PARAMETERS)
    @Schema(description = "银行名称")
    private List<I18nMsgFrontVO> bankNameList;
    @NotBlank(message = ConstantsCode.MISSING_PARAMETERS)
    @Schema(description = "银行代码")
    private String bankCode;
    @NotBlank(message = ConstantsCode.MISSING_PARAMETERS)
    @Schema(description = "图标")
    private String icon;
    @NotBlank(message = ConstantsCode.MISSING_PARAMETERS)
    @Schema(description = "币种")
    private String currency;
}
