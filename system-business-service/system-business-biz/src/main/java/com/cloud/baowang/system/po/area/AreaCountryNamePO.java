package com.cloud.baowang.system.po.area;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("area_country_name")
public class AreaCountryNamePO extends BasePO {
    @Schema(description = "区号")
    private String areaCode;
    @Schema(description = "国家名称")
    private String countryName;
    @Schema(description = "国家简写")
    private String countryCode;
    @Schema(description = "语言")
    private String language;

}
