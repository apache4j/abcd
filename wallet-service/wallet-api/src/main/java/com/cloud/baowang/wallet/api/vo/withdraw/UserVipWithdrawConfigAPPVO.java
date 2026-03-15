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
@Schema(title ="会员提款配置返回信息对象-APP")
@I18nClass
public class UserVipWithdrawConfigAPPVO {



    @Schema(description ="货币代码")
    private String currencyCode;

   /* @Schema(description ="VIP段位")
    private Integer vipRankCode;

    @Schema(description ="VIP段位名称")
    private Integer vipRankCodeName;*/

    /*@I18nField
    @Schema(description = "vip段位名称i18Code")
    private String vipRankNameI18nCode;*/


    /**
     * 会员等级
     */
    @Schema(description = "vip等级")
    private Integer vipGradeCode;
    /**
     * 会员等级
     */
    @Schema(description = "vip等级名称")
    private String vipGradeCodeName;


    @Schema(description ="单日提款总次数-免费")
    private Integer singleDayWithdrawCount;

    @Schema(description ="单日最高提款总额-免费")
    private BigDecimal singleMaxWithdrawAmount;



    /**
     * 单日提款次数上限
     */
    @Schema(description ="单日提款次数上限-收费")
    private Integer dailyWithdrawalNumsLimit;
    /**
     * 单日提款额度最大值
     */
    @Schema(description ="单日提款金额上限-收费")
    private BigDecimal dailyWithdrawAmountLimit;


}
