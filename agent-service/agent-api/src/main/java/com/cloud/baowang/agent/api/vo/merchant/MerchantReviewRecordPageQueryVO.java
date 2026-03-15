package com.cloud.baowang.agent.api.vo.merchant;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 商务审核信息
 */
@Data
@Schema(description = "分页查询vo")
@I18nClass
public class MerchantReviewRecordPageQueryVO extends PageVO implements Serializable {
    /**
     * 站点编号
     */
    @Schema(description = "站点编号")
    private String siteCode;
    /**
     * 商务账号
     */
    @Schema(description = "商务账号")
    private String merchantAccount;

    /**
     * 商务名称
     */
    @Schema(description = "商务名称")
    private String merchantName;

    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "锁单状态")
    private Integer lockStatus;


    /**
     * 申请人
     */
    @Schema(description = "申请人")
    private String applicant;

    /**
     * 申请时间
     */
    @Schema(description = "申请时间-开始时间")
    private Long applicationTimeStart;

    @Schema(description = "申请时间-结束时间")
    private Long applicationTimeEnd;

    /**
     * 审核状态,通system_param review_status
     * {@link com.cloud.baowang.common.core.enums.ReviewStatusEnum}
     */
    @Schema(description = "审核状态")
    private Integer reviewStatus;

    /**
     * 审核操作(归集状态,同system_param review_operation)
     * {@link com.cloud.baowang.common.core.enums.ReviewOperationEnum}
     */
    @Schema(description = "审核操作")
    private Integer reviewOperation;


    /**
     * 审核人
     */
    @Schema(description = "审核人")
    private String auditName;

    /**
     * 审核时间
     */
    @Schema(description = "审核时间-开始时间")
    private Long auditTimeStart;

    @Schema(description = "审核时间-结束时间")
    private Long auditTimeEnd;


}
