package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "会员标签记录")
@TableName(value = "site_user_label_record")
public class SiteUserLabelRecordsPO extends SiteBasePO {

    @TableField(value = "before_change")
    @Schema(description = "变更前")
    private String beforeChange;

    @TableField(value = "after_change")
    @Schema(description = "变更后")
    private String afterChange;

    @TableField(value = "member_account")
    @Schema(description = "会员账号")
    private String memberAccount;

    @TableField(value = "account_type")
    @Schema(description = "账号类型")
    private String accountType;

    @TableField(value = "risk_control_level")
    @Schema(description = "风控层级")
    private String riskControlLevel;

    @TableField(value = "account_status")
    @Schema(description = "账号状态")
    private String accountStatus;

    @TableField(value = "updater")
    @Schema(description = "操作人")
    private String updater;

    @TableField(value = "operator")
    @Schema(description = "创建人")
    private String operator;

}
