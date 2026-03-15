package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 签到活动 - 补签次数记录表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value = "site_activity_makeup_count_record")
public class SiteActivityMakeupCountRecordPO extends SiteBasePO {

    /**
     * 活动ID
     */
    private String activityId;

    /**
     * 活动编号
     */
  /*  private String activityNo;*/

    /**
     * 奖励来源
     */
    private String prizeSource;

    /**
     * 操作类型：0-减少，1-增加
     */
    private Integer operationType;

    /**
     * 变更前次数
     */
    private Integer startCount;

    /**
     * 变更次数
     */
    private Integer rewardCount;

    /**
     * 变更后次数
     */
    private Integer endCount;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 账号类型
     */
    private String accountType;

    /**
     * 站点日期的起始时间戳（毫秒）
     */
    private Long dayMillis;

    /**
     * 站点日期字符串，仅供查看
     */
    private String dayStr;

    /**
     * 奖励订单号（每日可重复）
     */
    private String orderNumber;
}