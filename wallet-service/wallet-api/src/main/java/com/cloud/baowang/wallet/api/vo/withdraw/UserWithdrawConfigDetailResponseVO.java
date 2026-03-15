package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@I18nClass
@Schema(title = "会员提款配置详情返回VO")
public class UserWithdrawConfigDetailResponseVO {

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "会员id")
    private String userId;

    @Schema(description ="货币代码")
    private String currencyCode;

    @Schema(description ="VIP段位code")
    private Integer vipRankCode;



    @I18nField
    @Schema(description ="VIP段位名称")
    private String vipRankCodeName;

    @Schema(description ="VIP等级")
    private Integer vipGradeCode;

    @Schema(description ="VIP等级名称")
    private Integer vipGradeCodeName;

    @Schema(description ="单日提款次数上限")
    private Integer dayWithdrawCount;

    @Schema(description ="单日提款金额上限")
    private BigDecimal maxWithdrawAmount;

    @Schema(description ="单日提款免费次数")
    private Integer singleDayWithdrawCount;

    @Schema(description ="单日最高免费金额")
    private BigDecimal singleMaxWithdrawAmount;

    private String creator;

    private String updater;



}
