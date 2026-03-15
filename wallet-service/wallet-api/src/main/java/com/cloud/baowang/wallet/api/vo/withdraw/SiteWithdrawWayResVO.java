package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Schema(description = "站点提款方式返回")
@I18nClass
public class SiteWithdrawWayResVO {

    @Schema(description = "提款方式id")
    private String withdrawId;

    @Schema(description = "提款方式编码-多语言")
    @I18nField
    private String withdrawWayI18;

    private String withdrawTypeCode;

}
