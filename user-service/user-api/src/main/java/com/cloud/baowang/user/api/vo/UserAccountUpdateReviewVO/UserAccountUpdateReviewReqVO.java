package com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "会员账号修改审核接参对象")
public class UserAccountUpdateReviewReqVO extends PageVO {
    @Schema(title = "开始申请时间")
    private String startApplicationTime;
    @Schema(title = "结束申请时间")
    private String endApplicationTime;
    @Schema(title = "开始一审完成时间")
    private String startFirstReviewTime;
    @Schema(title = "结束一审完成时间")
    private String endFirstReviewTime;
    @Schema(title = "会员账号")
    private String memberAccount;
    @Schema(title = "账号类型")
    private String[] accountType;
    @Schema(title = "审核申请类型")
    private String[] reviewApplicationType;
    @Schema(title = "审核操作")
    private String reviewOperation;
    @Schema(title = "审核状态")
    private String[] reviewStatus;
    @Schema(title = "申请人")
    private String applicant;
    @Schema(title = "一审人")
    private String firstInstance;
    @Schema(title = "锁单状态")
    private String lockStatus;
    @Schema(title = "审核单号")
    private String reviewOrderNumber;

    @Schema(title = "站点code",hidden = true)
    private String siteCode;


    private String adminName;

    private Boolean dataDesensitization = false;
}
