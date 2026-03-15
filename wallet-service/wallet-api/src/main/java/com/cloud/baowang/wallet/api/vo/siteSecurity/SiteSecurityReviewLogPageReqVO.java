package com.cloud.baowang.wallet.api.vo.siteSecurity;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 09:16
 * @Version: V1.0
 **/
@Data
@Schema(description = "发起审核对象")
public class SiteSecurityReviewLogPageReqVO extends PageVO {
    @Schema(description = "申请开始时间")
    private Long applyTimeStart;

    @Schema(description = "申请结束时间")
    private Long applyTimeEnd;

    @Schema(description = "审核开始时间")
    private Long firstReviewTimeStart;

    @Schema(description = "审核结束时间")
    private Long firstReviewTimeEnd;

    @Schema(description = "审核单号")
    private String reviewOrderNumber;

    @Schema(description = "审核状态(3-审核通过,4-审核拒绝) 同system_param review_status code值")
    private Integer reviewStatus;

    @Schema(description = "站点名称")
    private String siteName;
    /**
     * siteSecurityReviewEnums
     */
    @Schema(description = "调整类型")
    private Integer adjustType;

    @Schema(description = "审核人")
    private String firstReviewer;


}
