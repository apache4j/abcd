package com.cloud.baowang.activity.api.vo;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 活动基础信息的所有字段属性
 */
@Data
@Schema(title = "查询活动列表入参")
public class ActivityBasePartReqVO extends PageVO implements Serializable  {
    @Schema(title = "主键id",hidden = true)
    private String userId;

    /**
     * id
     */
    @Schema(title = "主键id")
    private String id;
    /**
     * 站点code
     */
    @Schema(title = "站点code", hidden = true)
    private String siteCode;

    /**
     * 活动分类-活动分类主键
     */
    @Schema(title = "活动分类-活动页签主键")
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
     * 活动展示终端
     */
    @Schema(title = "活动展示终端")
    private List<String> showTerminals;

    private String showTerminal;



    /**
     * 最近操作人
     */
    @Schema(title = "最近操作人")
    private String operator;

    /**
     * 根据当前语言获取
     */
    @Schema(title = "语言", hidden = true)
    private String lang;


    /**
     * 根据当前语言获取
     */
    @Schema(title = "活动推荐设备类型", hidden = true)
    private String recommendTerminals;



}