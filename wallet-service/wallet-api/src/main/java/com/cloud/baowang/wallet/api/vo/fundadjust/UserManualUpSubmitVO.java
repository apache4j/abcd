package com.cloud.baowang.wallet.api.vo.fundadjust;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema(title = "会员人工添加额度申请-提交 Request")
public class UserManualUpSubmitVO {

    @Schema(title = "会员账号,金额,倍数")
    private List<UserManualAccountVO> userAccounts;


    @Schema(title = "调整类型:3.其他调整,4.会员提款(后台),5.会员VIP优惠,6.会员活动")
    @NotNull(message = "调整类型不能为空")
    private Integer adjustType;
    @Schema(title = "类型为6时，需要传入活动模板")
    private String activityTemplate;

    @Schema(title = "活动编号")
    private String activityId;

    /**
     * 币种
     */
    @Schema(description = "币种code")
    private String currencyCode;

    /*@Schema(title = "调整金额")
    @NotEmpty(message = "调整金额不能为空")
    @Size(max = 11, message = "调整金额最大11位")
    private String adjustAmount;

    @Schema(title = "流水倍数")
    @NotEmpty(message = "流水倍数不能为空")
    private String runningWaterMultiple;*/

    @Schema(title = "申请原因")
    @NotEmpty(message = "申请原因不能为空")
    private String applyReason;

    @Schema(title = "上传附件地址")
    private String certificateAddress;
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;


}
