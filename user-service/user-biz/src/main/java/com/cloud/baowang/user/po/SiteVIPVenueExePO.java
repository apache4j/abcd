package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 2024/8/13 18:42
 * @Version : 1.0
 */
@Data
@TableName("site_vip_venue_exe")
public class SiteVIPVenueExePO extends BasePO {

    private String siteCode;

    private Integer venueType;

    private BigDecimal experience;
}
