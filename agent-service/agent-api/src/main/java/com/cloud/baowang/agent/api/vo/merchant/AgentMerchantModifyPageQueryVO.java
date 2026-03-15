package com.cloud.baowang.agent.api.vo.merchant;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "商务变更分页查询vo")
@Data
public class AgentMerchantModifyPageQueryVO extends PageVO {
    @Schema(description = "站点", hidden = true)
    private String siteCode;

    @Schema(description = "发起类型,目前只有账号状态变更,所以这里直接隐藏后台写死", hidden = true)
    private Integer reviewApplicationType;

    @Schema(description = "当前登录人",hidden = true)
    private String operator;

    @Schema(description = "商务账号")
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String merchantAccount;

    @Schema(description = "商务名称")
    private String merchantName;

    @Schema(description = "审核操作")
    private Integer reviewOperation;

    @Schema(description = "审核状态")
    private Integer reviewStatus;

    @Schema(description = "锁单状态")
    private Integer lockStatus;

    @Schema(description = "申请时间-开始")
    private Long applicationTimeStart;

    @Schema(description = "申请时间-结束")
    private Long applicationTimeEnd;

    @Schema(description = "审核时间-开始")
    private Long firstReviewTimeStart;

    @Schema(description = "审核时间-结束")
    private Long firstReviewTimeEnd;

    @Schema(description = "审核人")
    private String firstInstance;

    @Schema(description = "申请人")
    private String applicant;


}
