package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

@Data
@Accessors(chain = true)
@TableName("user_change_type_history_record")
@FieldNameConstants
public class UserChangeTypeHistoryRecordPO extends BasePO {
    /**
     * 会员账号
     */
    @TableField(value = "member_account")
    private String memberAccount;
    /**
     * 变更类型code
     */
    @TableField(value = "code")
    private Long code;
    /**
     * 备注信息
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 站点code
     */
    @TableField(value = "site_code")
    private String siteCode;

}
