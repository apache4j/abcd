package com.cloud.baowang.system.api.vo.banner;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@I18nClass
@Schema(description = "appBanner查询对象")
public class SiteBannerConfigAppQueryVO extends PageVO {

    @Schema(description = "站点编码，标识所属站点", hidden = true)
    private String siteCode;

    @Schema(description = "展示位置(一级分类id,首页顶部为0)")
    private String gameOneClassId;

    @Schema(description = "当前时间,筛选满足条件的banner")
    private Long siteTime;

    @Schema(description = "启用状态")
    private Integer status;

}
