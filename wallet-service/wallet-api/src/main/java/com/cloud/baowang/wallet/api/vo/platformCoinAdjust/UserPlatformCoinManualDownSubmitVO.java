package com.cloud.baowang.wallet.api.vo.platformCoinAdjust;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @author: qiqi
 */
@Data
@Schema(description = "会员平台币下分-提交 Request")
public class UserPlatformCoinManualDownSubmitVO {
    @Schema(hidden = true, description = "siteCode")
    private String siteCode;


    @Schema(description = "operator", hidden = true)
    private String operator;
    @Schema(description = "币种")

    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String currencyCode;

    @Schema(title = "类型为2时，需要传入活动模板")
    private String activityTemplate;

    @Schema(title = "活动编号")
    private String activityId;

    @Schema(description = "会员账号,金额")
    @NotEmpty(message = ConstantsCode.DATA_NOT_EXIST)
    private List<UserManualDownAccountVO> userAccounts;

    @Schema(title = "调整类型:1.会员VIP优惠,2.会员活动 3.其他调整")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer adjustType;

    @Schema(description = "上传附件地址")
    private String certificateAddress;

    @Schema(description = "申请原因")
    @NotBlank(message = ConstantsCode.DATA_NOT_EXIST)
    @Length(min = 2, max = 50, message = "申请原因长度为2-50个字符")
    private String applyReason;

}
