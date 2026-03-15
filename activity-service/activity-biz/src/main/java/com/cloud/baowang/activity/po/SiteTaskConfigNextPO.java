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
@TableName(value = "site_task_config_next")
public class SiteTaskConfigNextPO extends SiteBasePO {
    /**
     * 任务名称-多语言
     */
    private String taskNameI18nCode;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 子任务类型
     */
    private String subTaskType;

    /**
     * 最小配置金额
     */
    private BigDecimal minBetAmount = BigDecimal.ZERO;

    /**
     * 彩金奖励
     */
    private BigDecimal rewardAmount = BigDecimal.ZERO;

    /**
     * 货币代码
     */
    private String currencyCode;

    /**
     * 洗码倍率
     */
    private BigDecimal washRatio;

    /**
     * 场馆类型: 1:体育 2:视讯 3:棋牌 4:电子
     */
    private Integer venueType;

    /**
     * 游戏场馆CODE
     */
    private String venueCode;

    /**
     * 移动端任务图标
     */
    private String taskPictureI18nCode;

    /**
     * PC任务图标
     */
    private String taskPicturePcI18nCode;

    /**
     * 任务说明,多语言
     */
    private String taskDescI18nCode;

    /**
     * 任务m描述,多语言
     */
    private String taskDescriptionI18nCode;

    /**
     * 顺序
     */
    private Integer sort;

    /**
     * 状态 0已禁用 1开启中
     */
    private Integer status;
    /** 生效开始时间，记录生效的起始时间 */
    private Long effectiveStartTime;

    /** 生效结束时间，记录生效的终止时间 */
    //private Long effectiveEndTime;

    /** 是否最新修改标识，1 表示是最新，0 表示不是 */
    //private Integer latest;

    /**
     * ALTER TABLE `site_task_config`
     * ADD COLUMN `task_config_json` text NULL COMMENT '任务配置（每日任务-日累计存款；每周任务-邀请好友）' ;
     *
     * ALTER TABLE `site_task_config_next`
     * ADD COLUMN `task_config_json` text NULL COMMENT '任务配置（每日任务-日累计存款；每周任务-邀请好友）' ;
     * 任务配置（每日任务-日累计存款；每周任务-邀请好友）
     */
    private String taskConfigJson;

}
