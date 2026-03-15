package com.cloud.baowang.system.api.vo.area;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "手机区号分页查询条件vo")
public class AreaCodeManageReqVO extends PageVO {
    @Schema(description = "国家名称")
    private String countryName;
    @Schema(description = "区号")
    private String areaCode;
    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;
}
