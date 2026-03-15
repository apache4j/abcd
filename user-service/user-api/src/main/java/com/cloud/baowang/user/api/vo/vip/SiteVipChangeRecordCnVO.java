package com.cloud.baowang.user.api.vo.vip;

import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "vip等级变更记录视图")
@I18nClass
public class SiteVipChangeRecordCnVO {
    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "升降级标识(0:升级,1:降级")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.VIP_LEVEL_CHANGE_TYPE)
    private Integer changeType;

    @Schema(description = "变更类型名称")
    @ColumnWidth(10)
    private String changeTypeText;

    @Schema(description = "会员id")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "账号类型code")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;

    @Schema(description = "账号类型名称")
    @ColumnWidth(10)
    private String accountTypeText;

    @Schema(description = "账号状态")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.USER_ACCOUNT_STATUS)
    private String accountStatus;

    @Schema(description = "账号状态")
    private String accountStatusText;

    @Schema(description = "会员标签id")
    private String userLabelId;

    @Schema(description = "会员标签")
    private String userLabel;

    @Schema(description = "会员风控层级id")
    private String userRiskLevelId;

    @Schema(description = "会员风控层级")
    private String userRiskLevel;

    @Schema(description = "变更前VIP")
    private Integer vipOld;

    @Schema(description = "变更后VIP")
    private Integer vipNow;

    @Schema(description = "变更前VIP名称")
    private String vipOldName;

    @Schema(description = "变更后VIP名称")
    private String vipNowName;

    @Schema(description = "操作人")
    private String creator;

    @Schema(description = "变更时间")
    private Long createdTime;
}
