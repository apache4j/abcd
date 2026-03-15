package com.cloud.baowang.system.po.site.area;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("area_site_manage")
public class AreaSiteManagePO extends BasePO {
    @Schema(description = "siteCode")
    private String siteCode;
    @Schema(description = "区号ID")
    private String areaId;
    @Schema(description = "区号")
    private String areaCode;
    @Schema(description = "国家名称")
    private String countryName;
    @Schema(description = "国家简写")
    private String countryCode;
    @Schema(title = "最大长度")
    private Integer maxLength;
    @Schema(title = "最小长度")
    private Integer minLength;
    @Schema(description = "状态  0 禁用  1 启用")
    private Integer status;
    @Schema(description = "图标地址")
    private String icon;
}
