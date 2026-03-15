package com.cloud.baowang.activity.api.vo;


import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 活动基础信息的所有字段属性
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(title = "查询活动列表入参")
public class ActivityBaseReqVO extends PageVO implements Serializable  {

    /**
     * id
     */
    @Schema(title = "活动id(短)")
    private String activityNo;
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
     * 活动分类-活动分类主键
     */
    @Schema(title = "活动页签-活动分类主键")
    private String labelId;


    /**
     * 活动模板-同system_param activity_template
     * red_envelope_rain	LOOKUP_30010	红包雨
     * FIRST_DEPOSIT	LOOKUP_30011	首存活动
     * next_deposit	LOOKUP_30012	次存活动
     * free_spins	LOOKUP_30013	周三免费旋转
     * designated_date_eposit	LOOKUP_30014	指定日期存款
     * loss_in_sports	LOOKUP_30015	体育负盈利
     * turnover_ranking	LOOKUP_30016	流水排行榜
     * daily_competition	LOOKUP_30018	每日竞赛
     * spin_wheel	LOOKUP_30019	转盘
     */
    @Schema(title = "活动模板-字典CODE： activity_template ")
    private String activityTemplate;

    /**
     * 洗码倍率
     */
    @Schema(title = "洗码倍率")
    private BigDecimal washRatio;


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
     * 最近操作人
     */
    @Schema(title = "最近操作人")
    private String updater;

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

    /**
     * 根据当前语言获取
     */
    @Schema(title = "活动推荐设备类型", hidden = true)
    private String recommendTerminals;

}