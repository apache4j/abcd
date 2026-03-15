package com.cloud.baowang.system.po.site.rebate;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("site_non_rebate_config")
public class SiteNonRebateConfigPO extends BasePO implements Serializable {

    private String siteCode;

    private String venueType;

    private String venueValue;

    private String venueCode;

    private String venueName;

    private String gameInfo;

    private String updater;

}
