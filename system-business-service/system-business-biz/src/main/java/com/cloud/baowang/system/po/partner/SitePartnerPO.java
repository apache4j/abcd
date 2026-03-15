package com.cloud.baowang.system.po.partner;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

@Data
@TableName("site_partner")
public class SitePartnerPO extends BasePO {
    /**
     * 系统合作赞助商ID
     */
    private Long systemPartnerId;
    /**
     * 站点code
     */
    private String siteCode;
    /**
     * 赞助商名称
     */
    private String partnerName;
    /**
     * 赞助商图标
     */
    private String partnerIcon;
    /**
     * 启用状态0.禁用，1.启用
     */
    private Integer status;
    /**
     * 排序字段，数值越小越靠前
     */
    private Integer sort;
}
