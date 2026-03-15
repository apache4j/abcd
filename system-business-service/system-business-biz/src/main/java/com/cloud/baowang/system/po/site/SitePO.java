package com.cloud.baowang.system.po.site;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 2024/7/26 11:16
 * @Version : 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("site_info")
public class SitePO extends BasePO implements Serializable {

    /* 站点编号 */
    private String siteCode;

    /* 站点名称 */
    private String siteName;

    /* 站点前缀 */
    private String sitePrefix;

    /* 所属公司 */
    private String company;

    /* 站点类型 */
    private Integer siteType;

    /* 站点模式 */
    private Integer siteModel;

    /* 状态 */
    private Integer status;

    /*抽成方案*/
    private Integer commissionPlan;

    /*后台名称*/
    private String bkName;

    /*皮肤管理*/
    private String skin;

    /*长logo*/
    private String longLogo;

    /*短logo*/
    private String shortLogo;

//    /*黑夜-长logo*/
//    private String blackLongLogo;
//
//    /*黑夜-短logo*/
//    private String blackShortLogo;

    /* 站点管理员账号 */
    private String siteAdminAccount;

    /* 备注 */
    private String remark;
    /**
     * 站点时区
     */
    private String timezone;
    /**
     * 站点的平台币
     */
    private String platCurrencyCode;

    /**
     * 站点的平台币名称
     */
    private String platCurrencyName;

    /**
     * 站点的平台币符号
     */
    private String platCurrencySymbol;

    /**
     * 站点的平台币图标
     */
    private String platCurrencyIcon;

    /* 最近的一次步骤 */
    private Integer lastStep;

    /**
     * 维护时间-开始时间
     */
    private Long maintenanceTimeStart;

    /**
     * 维护时间-结束时间
     */
    private Long maintenanceTimeEnd;

    /**
     * 返水开关
     */
    private Integer rebateStatus;


    /**
     * 保证金开关
     */
    private Integer guaranTeeFlag;

    /**
     * 盘口模式
     */
    private Integer handicapMode;
}
