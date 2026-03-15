package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("user_information_change")
public class UserInformationChangePO extends BasePO {
    /**
     * 操作时间
     */
    @TableField(value = "operating_time")
    private Long operatingTime;
    /**
     * 会员账号
     */
    @TableField(value = "member_account")
    private String memberAccount;

    /**
     * 操作人
     */
    @TableField(value = "operator")
    private String operator;

    /**
     * 变更类型
     */
    @TableField(value = "change_type")
    private String changeType;
    /**
     * 账号类型
     */
    @TableField(value = "account_type")
    private String accountType;
    @TableField(value = "Information_before_change")
    @Schema(title = "变更前信息")
    private String InformationBeforeChange;
    @TableField(value = "Information_after_change")
    @Schema(title = "变更后信息")
    private String InformationAfterChange;
    @TableField(value = "submit_Information")
    @Schema(title = "提交信息")
    private String submitInformation;
    /**
     * 站点code
     */
    @TableField(value = "site_code")
    private String siteCode;

}
