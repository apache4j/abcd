package com.cloud.baowang.activity.po;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 补签次数余额表
 */
@Data
@NoArgsConstructor
@TableName(value = "site_makeup_count_balance")
public class SiteMakeupCountBalancePO extends SiteBasePO {

    /**
     * 会员ID
     */
    private String userId;

    /**
     * 会员账号
     */
    private String userAccount;

    /**
     * 当前补签次数余额
     */
    private Integer balance;

    /**
     * 站点日期的起始时间戳（毫秒）
     */
    private Long monthMillis;

    /**
     * 站点月字符串，仅供查看
     */
    private String monthStr;
}