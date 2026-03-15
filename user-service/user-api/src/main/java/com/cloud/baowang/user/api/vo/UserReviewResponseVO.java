package com.cloud.baowang.user.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(title = "审核列表 返回")
@I18nClass
public class UserReviewResponseVO {

    @Schema(title = "id")
    private String id;

    @Schema(title = "审核单号")
    private String reviewOrderNo;

    @Schema(title = "申请信息")
    private String applyInfo;

    @Schema(title = "申请时间")
    private Long applyTime;

    @Schema(title = "申请人")
    private String applicant;

    @Schema(title = "一审完成时间")
    private Long oneReviewFinishTime;

    @Schema(title = "一审人")
    private String reviewer;

    @Schema(title = "审核操作 1一审审核 2结单查看")
    private Integer reviewOperation;

    @I18nField
    @Schema(title = "审核操作 1一审审核 2结单查看")
    private String reviewOperationName;

    @Schema(title = "审核状态 1待处理 2处理中 3审核通过 4一审拒绝")
    private Integer reviewStatus;

    @I18nField
    @Schema(title = "审核状态 1待处理 2处理中 3审核通过 4一审拒绝")
    private String reviewStatusName;

    @Schema(title = "锁单状态 0未锁 1已锁")
    private Integer lockStatus;

    @Schema(title = "锁单人")
    private String locker;

    @Schema(title = "锁单人是否当前登录人 0否 1是")
    private Integer isLocker;

    @Schema(title = "申请人是否当前登录人 0否 1是")
    private Integer isApplicant;
}
