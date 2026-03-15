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
@TableName(value = "system_activity_template")
public class SystemActivityTemplatePO extends BasePO implements Serializable {

    /**
     * 活动编号
     */
    private String activityTemplate;

    /**
     * 活动名称
     */
    private String activityName;

}
