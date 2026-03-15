package com.cloud.baowang.user.api.vo;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 审核列表 Request
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Schema(title = "审核列表 Request")
public class UserReviewPageVO extends PageVO {

    @Schema(title = "申请时间-开始")
    private Long applyTimeStart;

    @Schema(title = "申请时间-结束")
    private Long applyTimeEnd;

    @Schema(title = "一审完成时间-开始")
    private Long oneReviewFinishTimeStart;

    @Schema(title = "一审完成时间-结束")
    private Long oneReviewFinishTimeEnd;

    @Schema(title = "审核状态 1待处理 2处理中 3审核通过 4一审拒绝; 字典code: review_status")
    private List<Integer> reviewStatus;

    @Schema(title = "锁单状态 0未锁 1已锁; 字典code: lock_status")
    private Integer lockStatus;

    @Schema(title = "审核操作 1一审审核 2结单查看; 字典code: review_operation")
    private Integer reviewOperation;

    @Schema(title = "申请人")
    private String applicant;

    @Schema(title = "一审人")
    private String reviewer;

    @Schema(title = "审核单号")
    private String reviewOrderNo;

    @Schema(title = "站点code")
    private String siteCode;

}
