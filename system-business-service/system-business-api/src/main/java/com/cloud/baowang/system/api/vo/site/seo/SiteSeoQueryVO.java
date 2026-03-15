package com.cloud.baowang.system.api.vo.site.seo;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


@Data
@Schema(description = "检索信息配置查询vo")
@Builder
public class SiteSeoQueryVO extends PageVO implements Serializable {

    private String siteCode;

    @Schema(description = "语言")
    private String lang;

    @Schema(description = "id")
    private String id;



}
