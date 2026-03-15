package com.cloud.baowang.wallet.api.vo.fundrecord;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(title = "会员加额审核记录-列表 返回")
@I18nClass
public class GetRecordResponseVO {

    @Schema(title = "id")
    private String id;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "会员ID")
    private String userId;

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "订单状态")
    private Integer orderStatus;
    @Schema(title = "订单状态-Name")
    private String orderStatusName;

    @Schema(title = "调整类型")
    private Integer adjustType;
    @Schema(title = "调整类型-Name")
    private String adjustTypeName;

    @Schema(title = "调整金额")
    private BigDecimal adjustAmount;

    @Schema(title = "申请时间")
    private Long applyTime;
    @Schema(title = "申请时间 - 导出")
    private String applyTimeExport;

    @Schema(title = "一审人")
    private String oneReviewer;
    @Schema(title = "二审人")
    private String twoReviewer;

    @Schema(title = "一审完成时间")
    private Long oneReviewFinishTime;
    @Schema(title = "一审完成时间 - 导出")
    private String oneReviewFinishTimeExport;

    @Schema(title = "二审完成时间")
    private Long twoReviewFinishTime;
    @Schema(title = "二审完成时间 - 导出")
    private String twoReviewFinishTimeExport;

    @Schema(title = "一审开始时间")
    private Long oneReviewStartTime;
    @Schema(title = "二审开始时间")
    private Long twoReviewStartTime;

    @Schema(title = "一审审核用时")
    private String oneReviewUseTime;
    @Schema(title = "二审审核用时")
    private String twoReviewUseTime;

    @Schema(title = "一审备注")
    private String oneReviewRemark;
    @Schema(title = "二审备注")
    private String twoReviewRemark;
}
