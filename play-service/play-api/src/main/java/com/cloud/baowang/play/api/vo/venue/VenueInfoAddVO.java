package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author qiqi
 */
@Data
@Schema(description = "游戏平台添加请求对象")
public class VenueInfoAddVO {


    @Schema(description = "游戏平台CODE,字典code:venue_code", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String venueCode;

    @Schema(description = "币种")
    private List<String> currencyCodeList;

    @Schema(description = "场馆类型 1:体育,2:视讯,3:棋牌,4:电子,5:彩票,6:斗鸡,7:电竞")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer venueType;

    @Schema(description = "API URL", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String apiUrl;

    @Schema(description = "商户编码", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String merchantNo;

    @Schema(description = "AES 密钥", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String aesKey;


    @Schema(description = "商户密钥", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String merchantKey;


    @Schema(description = "场馆费率", required = true)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal venueProportion;

    @Schema(description = "PC-图标-多语言", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> pcIconI18nCodeList;

    @Schema(description = "H5-图标-多语言", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> h5IconI18nCodeList;


    private String creator;

    private String updater;


}
