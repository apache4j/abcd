package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "VIP段位-币种对应配置信息")
@I18nClass
public class SiteVipRankCurrencyConfigVO implements Serializable {
    @Schema(hidden = true)
    private String siteCode;
    @Schema(hidden = true)
    private Integer vipRankCode;
    @Schema(description = "币种代码")
    private String currencyCode;
    @Schema(description = "单日免费提款次数")
    private Integer dailyWithdrawals;
    @Schema(description = "单日免费提款最大值")
    private BigDecimal dayWithdrawLimit;

    @Schema(description = "单日提款次数上限")
    private Integer dailyWithdrawalNumsLimit;
    @Schema(description = "单日提款额度最大值")
    private BigDecimal dailyWithdrawAmountLimit;
    @Schema(description = "提款方式,手续费相关配置数组")
    @Valid
    private List<SiteVipRankCurrencyWithdrawConfigVO> withdrawConfigVOS;
}
