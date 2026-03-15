package com.cloud.baowang.wallet.api.vo.platformCoinAdjust;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetByUserInfoVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.GetRegisterInfoVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.PlatformCoinReviewDetailVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.ReviewDetailVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.ReviewInfoVO;
import com.cloud.baowang.wallet.api.vo.fundrecord.RiskControlVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author: qiqi
 */
@Data
@Schema(description = "会员平台币上分审核详情 返回")
@I18nClass
public class UserPlatformCoinUpReviewDetailsVO {

    @Schema(description = "会员注册信息")
    private GetRegisterInfoVO registerInfo;

    @Schema(description = "会员账号信息")
    private GetByUserInfoVO userInfo;

    @Schema(description = "账号风控层级")
    private RiskControlVO riskControl;

    @Schema(description = "审核详情")
    private PlatformCoinReviewDetailVO reviewDetail;

    @Schema(description = "审核信息")
    private List<ReviewInfoVO> reviewInfos;
}
