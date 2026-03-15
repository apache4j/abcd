package com.cloud.baowang.user.api.vo.userlabel.userLabelRecord;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会员标签记录查询返回对象")
@I18nClass
public class UserLabelRecordsResVO implements Serializable {
    @Schema(description = "变更时间")
    private Long updatedTime;

    @Schema(description = "变更前")
    private String beforeChange;

    @Schema(description = "变更后")
    private String afterChange;

    @Schema(description = "会员账号")
    private String memberAccount;

    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    @Schema(description = "账号类型code")
    private String accountType;

    @Schema(description = "账号类型名称")
    private String accountTypeText;

    @Schema(description = "变更类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_CHANGE_TYPE)
    private String changeType;

    @Schema(description = "变更类型名称")
    private String changeTypeText;

    @Schema(description = "风控层级")
    private String riskControlLevel;

    @TableField(value = "account_status")
    @Schema(description = "账号状态")
    private String accountStatus;

    @Schema(description = "账号状态名称")
    private List<CodeValueVO> accountStatusName$Arr;

    @Schema(description = "操作人")
    private String updaters;

    @Schema(description = "操作人名称")
    private String operator;
}
