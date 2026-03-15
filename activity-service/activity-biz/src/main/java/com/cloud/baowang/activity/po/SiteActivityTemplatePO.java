package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "site_activity_template")
public class SiteActivityTemplatePO extends BasePO implements Serializable {
    /**
     * 站点code
     */
    private String siteCode;
    /**
     * 活动编号
     */
    private String activityTemplate;

    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 绑定状态 1绑定,0解绑
     */
    private Integer bindStatus;
}
