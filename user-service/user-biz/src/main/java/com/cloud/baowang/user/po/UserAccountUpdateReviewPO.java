package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "会员账号修改审核")
@TableName(value = "user_account_update_review")
public class UserAccountUpdateReviewPO extends BasePO {
    @TableField(value = "application_time")
    @Schema(title = "申请时间")
    private Long applicationTime;
    @TableField(value = "first_review_time")
    @Schema(title = "一审完成时间")
    private Long firstReviewTime;
    @TableField(value = "review_remark")
    @Schema(title = "一审备注")
    private String reviewRemark;
    @TableField(value = "review_order_number")
    @Schema(title = "审核单号")
    private String reviewOrderNumber;
    @TableField(value = "review_operation")
    @Schema(title = "审核操作")
    private String reviewOperation;
    /**
     * {@link com.cloud.baowang.common.core.enums.ReviewStatusEnum}
     */
    @TableField(value = "review_status")
    @Schema(title = "审核状态")
    private String reviewStatus;
    @TableField(value = "applicant")
    @Schema(title = "申请人")
    private String applicant;
    @TableField(value = "first_instance")
    @Schema(title = "一审人")
    private String firstInstance;
    @TableField(value = "lock_status")
    @Schema(title = "锁单状态")
    private String lockStatus;
    @TableField(value = "locker")
    @Schema(title = "锁单人")
    private String locker;
    @TableField(value = "review_application_type")
    @Schema(title = "审核申请类型")
    private String reviewApplicationType;
    @TableField(value = "member_account")
    @Schema(title = "会员账号")
    private String memberAccount;
    @TableField(value = "account_type")
    @Schema(title = "账号类型")
    private String accountType;
    @TableField(value = "before_fixing")
    @Schema(title = "修改前")
    private String beforeFixing;
    @TableField(value = "after_modification")
    @Schema(title = "修改后")
    private String afterModification;
    @TableField(value = "application_information")
    @Schema(title = "申请信息")
    private String applicationInformation;

    /**
     * 站点code
     */
    @TableField(value = "site_code")
    private String siteCode;

     /**
     * 扩展参数
     */
    @TableField(value = "ext_param")
    private String extParam;


}
