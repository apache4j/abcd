package com.cloud.baowang.system.po.risk;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

@Data
@TableName(value = "risk_ctrl_level")
public class RiskCtrlLevelPO extends BasePO {

    @TableField(value = "risk_control_type")
    private String riskControlType;

    @TableField(value = "risk_control_level")
    private String riskControlLevel;

    @TableField(value = "risk_control_level_code")
    private Long riskControlLevelCode;

    @TableField(value = "risk_control_level_describe")
    private String riskControlLevelDescribe;

    /**
     * 0.删除，1.正常
     * {@link com.cloud.baowang.common.core.enums.EnableStatusEnum}
     */
    @TableField(value = "status")
    private Integer status;
    /**
     * 站点code
     */
    @TableField(value = "site_code")
    private String siteCode;
}
