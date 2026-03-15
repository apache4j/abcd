package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import lombok.Data;


@Data
@TableName("site_check_in_record")
public class SiteCheckInRecordPO extends SiteBasePO {
    /**
     * 站点日期 当天起始时间戳,签到日期
     */
    private Long dayMillis;

    /**
     * day字段对应的字符串(签到日期)
     */
    private String dayStr;

    /**
     * 用户编号
     */
    private String userId;

    /**
     * 会员账号
     */
    private String userAccount;

    /**
     * 主货币
     */
    private String mainCurrency;

    /**
     * 上级代理账号
     */
    private String agentAccount;

    /**
     * 上级代理编号
     */
    private String agentId;

    /**
     * 站点编码
     */
    private String siteCode;

    /**
     * 账号类型 1-测试 2-正式
     */
    private String accountType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 备注
     */
    private String orderNo;

    /**
     * 状态 0未签到 1已签到
     */
    private Integer status;

    /**
     * VIP等级
     */
    private Integer vipGradeCode;

    /**
     * VIP段位code
     */
    private Integer vipRankCode;

    /**
     * 1-周奖励，2-月奖励，3-累计奖励
     */
    private Integer rewardType;

    /**
     * 配置阶梯值，
     * 1-周奖励(1-7)，2-月奖励(1-12)，3-累计奖励
     */
    private Integer rewardTypeCode;

}
