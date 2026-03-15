package com.cloud.baowang.activity.api.vo.task;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 任务基础信息的所有字段属性
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(title = "查询任务列表入参")
public class TaskFlashCardBaseReqVO extends PageVO implements Serializable  {


    /**
     * 站点code
     */
    @Schema(title = "站点code", hidden = true)
    private String siteCode;

    /**
     * 活动名称-多语言
     */
    @Schema(title = "活动名称-多语言-指定")
    private String activityName;

    @Schema(title = "活动名称-多语言-指定",hidden = true)
    private List<String> activityNameCodeList;





    /**
     * 活动展示终端
     */
    @Schema(title = "活动展示终端 device_terminal")
    private List<String> showTerminals;


    /**
     * 最近操作人
     */
    @Schema(title = "最近操作人")
    private String operator;



    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;

    /**
     * 根据当前语言获取
     */
    @Schema(title = "语言", hidden = true)
    private String lang;

    /**
     * 活动生效的账户类型
     * activity_deadLine
     */
    @Schema(title = "活动生效的账户类型")
    private Integer accountType;


    /**
     * 活动时效-
     * ActivityDeadLineEnum
     */
    @Schema(title = "活动时效 0-限时，1-长期")
    private Integer activityDeadline;



    /**
     * 活动展示终端
     */
    @Schema(title = "活动展示终端")
    private String showTerminal;
    /**
     * 状态 0已禁用 1开启中
     */
    @Schema(title = "状态 0已禁用 1开启中")
    private Integer status;

    private Long showStartTime;

    private Long showEndTime;

    private Long activityStartTime;

    private Long activityEndTime;


    @Schema(title = "活动时效 该字段是特殊字段,查询出现在正在进行的活动,例如 限时活动 -startTime,endTime,符合的")
    private Boolean activityDeadlineShow;

    private String id;

    private List<String> ids;

}