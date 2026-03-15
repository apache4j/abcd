package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "site_task_flash_card_base")
public class SiteTaskFlashCardBasePO extends SiteBasePO implements Serializable {


    /**
     * 活动名称-多语言
     */
    private String activityNameI18nCode;


    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 活动生效的账户类型
     */
    private String accountType;

    /**
     * 活动展示终端
     */
    private String showTerminal;

    /**
     * 入口图-移动端
     */
    private String entrancePictureI18nCode;

    /**
     * 入口图-PC端
     */
    private String entrancePicturePcI18nCode;


    /**
     * 状态 0已禁用 1开启中
     */
    private Integer status;


    /**
     * 活动描述,多语言
     */
    private String activityIntroduceI18nCode;


}
