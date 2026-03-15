package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @className: SiteTaskConfigPO
 * @author: wade
 * @description: 任务配置
 * @date: 18/9/24 14:38
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "site_task_over_view_config")
public class SiteTaskOverViewConfigPO extends SiteBasePO {


    /**
     * 状态 任务配置1展开，2-隐藏，默认是1
     */
    private Integer expandStatus;




}
