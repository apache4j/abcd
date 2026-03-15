package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author qiqi
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@I18nClass
@Schema(description = "场馆币种对象")
public class VenueInfoCurrencyVO implements Serializable {

    @Schema(description = "场馆ID")
    private String id;

    @Schema(description = "币种CODE")
    private String currencyCode;

    @I18nField
    @Schema(description = "币种多语言名称")
    private String currencyName;

    @Schema(description = "商户编码", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String merchantNo;


    @Schema(description = "betKey", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String betKey;


    @Schema(description = "AES 密钥", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String aesKey;


    @Schema(description = "商户密钥", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String merchantKey;

}
