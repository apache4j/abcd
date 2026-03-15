package com.cloud.baowang.wallet.api.vo.fundrecord;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema(description = "会员人工加额审核详情 返回")
@I18nClass
public class UserUpReviewDetailsVO {

    @Schema(description = "会员注册信息")
    private GetRegisterInfoVO registerInfo;

    @Schema(description = "会员账号信息")
    private GetByUserInfoVO userInfo;

    @Schema(description = "账号风控层级")
    private RiskControlVO riskControl;

    @Schema(description = "审核详情")
    private ReviewDetailVO reviewDetail;

    @Schema(description = "审核信息")
    private List<ReviewInfoVO> reviewInfos;
}
