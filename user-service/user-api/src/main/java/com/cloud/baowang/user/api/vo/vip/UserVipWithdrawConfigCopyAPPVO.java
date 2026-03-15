package com.cloud.baowang.user.api.vo.vip;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(title ="会员提款配置返回信息对象-APP-copy")
@I18nClass
public class UserVipWithdrawConfigCopyAPPVO {



    @Schema(description ="货币代码")
    private String currencyCode;




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
