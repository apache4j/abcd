package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 兑换码基础信息PO
 */
@Data
@NoArgsConstructor
@TableName("site_activity_redemption_code_base_info")
public class SiteActivityRedemptionCodeBasePO extends BasePO implements Serializable {



    /**
     * 兑换码订单号
     */
    private String orderNo;

    /**
     * 兑换码类型，0:通用兑换码，1:唯一兑换码
     */
    private Integer category;

    /**
     * 平台币或法币：0:平台币，1:法币
     */
    private String platformOrFiatCurrency;

    /**
     * 活动规则，多语言
     */
    private String activityRuleI18nCode;

    /**
     * 活动规则描述，多语言
     */
    private String activityRuleDescI18nCode;

    /**
     * app端活动头图
     */
    private String headPictureAppI18nCode;

    /**
     * PC端活动头图
     */
    private String headPicturePcI18nCode;

    /**
     * 创建时间（最初操作时间）
     */
    private Long createdTime;

    /**
     * 修改时间（最新操作时间）
     */
    private Long updatedTime;

    /**
     * 创建人（最初操作人）
     */
    private String creator;

    /**
     * 修改人（最新操作人）
     */
    private String updater;

    /**
     * 客户端开关，0:关闭，1:开启
     */
    private Integer clientSwitch;

    /**
     * 活动时效类型，0:限时，1:长期
     */
    private Integer deadlineType;

    /**
     * 站点编码
     */
    private String siteCode;

    /**
     * 状态：0:禁用，1:正常
     */
    private Integer status;
}
