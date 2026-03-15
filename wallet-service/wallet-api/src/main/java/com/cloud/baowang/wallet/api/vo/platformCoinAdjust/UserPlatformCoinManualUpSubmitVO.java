package com.cloud.baowang.wallet.api.vo.platformCoinAdjust;

import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author: qiqi
 */
@Data
@Schema(title = "会员平台币上分-提交 Request")
public class UserPlatformCoinManualUpSubmitVO {

    @Schema(title = "会员账号,金额,倍数")
    private List<UserManualAccountVO> userAccounts;


    @Schema(title = "调整类型:1.会员VIP优惠,2.会员活动 3.其他调整")
    @NotNull(message = "调整类型不能为空")
    private Integer adjustType;
    @Schema(title = "类型为2时，需要传入活动模板")
    private String activityTemplate;

    @Schema(title = "活动编号")
    private String activityId;

    /**
     * 币种
     */
    @Schema(description = "币种code")
    private String currencyCode;


    @Schema(title = "申请原因")
    @NotEmpty(message = "申请原因不能为空")
    private String applyReason;

    @Schema(title = "上传附件地址")
    private String certificateAddress;
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "operator", hidden = true)
    private String operator;

}
