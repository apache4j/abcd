package com.cloud.baowang.wallet.api.vo.bank;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "银行卡管理编辑vo")
public class BankCardManagerEditVO {
    @NotNull(message = ConstantsCode.MISSING_PARAMETERS)
    @Schema(description = "id")
    private String id;
    @Schema(description = "银行名称-i18n")
    private List<I18nMsgFrontVO> bankNameList;
    @Schema(description = "银行代码")
    private String bankCode;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "排序")
    private Integer sort;
}
