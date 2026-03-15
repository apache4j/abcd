package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(title = "会员提款配置详情设置请求")
public class UserWithdrawConfigDetailAddOrUpdateVO {

    @Schema(description = "站点编码",hidden = true)
    private String siteCode;

    @Schema(description = "会员账号")
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String userAccount;


    @Schema(description = "会员id",hidden = true)
    private String userId;

    @Schema(description ="货币代码",hidden = true)
    private String currencyCode;

    @Schema(description ="VIP段位code",hidden = true)
    private Integer vipRankCode;

    /**
     * 会员等级
     */
    @Schema(description ="会员等级",hidden = true)
    private Integer vipGradeCode;


    @Schema(description ="单日提款次数上限")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Range(min = 1, max=999,message = ConstantsCode.PARAM_ERROR)
    private Integer dayWithdrawCount;

    @Schema(description ="单日提款金额上限")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @PositiveOrZero(message = ConstantsCode.PARAM_ERROR)
    @Digits(integer = 9,fraction = 2,message = ConstantsCode.PARAM_ERROR )
    private BigDecimal maxWithdrawAmount;

    @Schema(description ="单日提款免费次数")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Range(min = 0, max=999,message = ConstantsCode.PARAM_ERROR)
    private Integer singleDayWithdrawCount;

    @Schema(description ="单日最高免费金额")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @PositiveOrZero(message = ConstantsCode.PARAM_ERROR)
    @Digits(integer = 9,fraction = 2,message = ConstantsCode.PARAM_ERROR )
    private BigDecimal singleMaxWithdrawAmount;

    private String creator;

    private String updater;



}
