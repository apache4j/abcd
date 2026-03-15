package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @className: SiteActivityLotteryRecordPO
 * @author: wade
 * @description: 转盘活动奖励次数记录
 * @date: 10/9/24 20:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "site_activity_lottery_record")
public class SiteActivityLotteryRecordPO extends SiteBasePO implements Serializable {

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * vip段位code
     */
    private Integer vipRankCode;

    /**
     * VIP等级code
     */
    private Integer vipGradeCode;

    /**
     * 获取来源 system-param(activity_prize_source)
     */
    private String prizeSource;

    /**
     * 获取来源 operation_type 0表示减少，1表示增加
     */
    private Integer operationType;


    @Schema(description = "活动id")
    private String activityId;

    @Schema(description = "活动编号")
    private String activityNo;

    @Schema(description = "活动模板")
    private String activityTemplate;

    @Schema(description = "获取来源|活动名称")
    private String activityTemplateName;

    /**
     * 原次数
     */
    private Integer startCount;

    /**
     * 获取次数
     */
    private Integer rewardCount;

    /**
     * 获取后次数
     */
    private Integer endCount;

    /**
     * 会员id
     */
    private String userId;

    /**
     * 会员账号
     */
    private String userAccount;

    //账号类型
    private String accountType;
    /**
     * 奖励订单号
     */
    private String orderNumber;


}
