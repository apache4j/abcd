package com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title = "会员账号修改审核返回对象")
public class UserAccountUpdateReviewResVO {
    @Schema(title = "唯一主键ID")
    private String id;
    @Schema(title = "申请时间")
    private Long applicationTime;
    @Schema(title = "申请信息")
    private String applicationInfo;
    @Schema(title = "一审完成时间")
    private Long firstReviewTime;
    @Schema(title = "审核单号")
    private String reviewOrderNumber;
    @Schema(title = "审核操作code")
    private String reviewOperation;
    @Schema(title = "审核操作Name")
    @I18nField
    private String reviewOperationName;
    @Schema(title = "审核状态code")
    private String reviewStatus;
    @Schema(title = "审核状态name")
    @I18nField
    private String reviewStatusName;
    @Schema(title = "申请人")
    private String applicant;
    @Schema(title = "是否是当前申请人 0否 1是")
    private String isApplicant;
    @Schema(title = "一审人")
    private String firstInstance;
    @Schema(title = "锁单状态code")
    private String lockStatus;
    @Schema(title = "锁单状态name")
    private String lockStatusName;
    @Schema(title = "锁单人")
    private String locker;
    @Schema(title = "锁单人是否当前登录人 0否 1是")
    private Integer isLocker;
    @Schema(title = "审核申请类型code")
    private String reviewApplicationType;
    @Schema(title = "审核申请类型name")
    @I18nField
    private String reviewApplicationTypeName;
    @Schema(title = "会员账号")
    private String memberAccount;
    @Schema(title = "账号类型 1测试 2正式 3商务 4置换")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;
    @Schema(title = "账号类型名称")
    private String accountTypeText;
    @Schema(title = "修改前")
    private String beforeFixing;
    @Schema(title = "修改后")
    private String afterModification;


    /**
     * 扩展参数
     */
    private String extParam;

}
