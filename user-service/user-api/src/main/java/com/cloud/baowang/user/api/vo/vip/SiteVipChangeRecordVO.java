package com.cloud.baowang.user.api.vo.vip;

import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "vip等级变更记录视图")
@I18nClass
public class SiteVipChangeRecordVO implements Serializable {
    @Schema(description = "会员id,预留")
    private String userId;

    @Schema(description = "会员账号")
    @ColumnWidth(10)
    private String userAccount;

    @Schema(description = "账号类型code")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;

    @Schema(description = "账号类型名称")
    @ColumnWidth(10)
    private String accountTypeText;

    @Schema(description = "变更类型code")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.VIP_LEVEL_CHANGE_TYPE)
    private String changeType;

    @Schema(description = "变更类型名称")
    @ColumnWidth(10)
    private String changeTypeText;

    @Schema(description = "变更前")
    @ColumnWidth(5)
    private String beforeChange;

    @Schema(description = "变更后")
    @ColumnWidth(5)
    private String afterChange;

    @Schema(description = "变更时间")
    private Long changeTime;

    @Schema(description = "用户标签value 用于导出")
    @ColumnWidth(50)
    private String userLabelName;

    @Schema(description = "风控等级")
    @ColumnWidth(10)
    private String controlRank;

    @Schema(description = "用户标签ids")
    private String userLabel;

    @Schema(description = "用户标签key value数组")
    private List<GetUserLabelByIdsVO> userLabelByIdsVOS;

    @Schema(description = "账号状态")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.USER_ACCOUNT_STATUS)
    private String accountStatus;

    @Schema(description = "账号状态")
    private String accountStatusText;

    @Schema(description = "操作人")
    private String operator;


}
