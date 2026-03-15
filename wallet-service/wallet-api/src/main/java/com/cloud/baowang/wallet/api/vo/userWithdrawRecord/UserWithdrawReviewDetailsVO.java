package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetRegisterInfoVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.ReviewInfoVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.RiskControlVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author: qiqi
 */
@Data
@I18nClass
@Schema(title = "会员提款审核详情返回对象")
public class UserWithdrawReviewDetailsVO {

    @Schema(description="会员注册信息")
    private GetRegisterInfoVO registerInfo;

    @Schema(description="会员账号信息")
    private WithdrawUserInfoVO userInfo;

    @Schema(description="账号风控层级")
    private RiskControlVO riskControl;


    @Schema(description="近期提款信息")
    private RecentlyDepositWithdrawVO recentlyDepositWithdrawVO;

    @Schema(description="本次提款信息")
    private WithdrawReviewDetailVO withdrawReviewDetailVO;

    @Schema(description="审核信息")
    private List<WithdrawReviewInfoVO> reviewInfos;
}
