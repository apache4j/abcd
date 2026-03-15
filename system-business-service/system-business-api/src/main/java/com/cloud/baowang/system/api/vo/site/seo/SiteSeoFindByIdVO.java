package com.cloud.baowang.system.api.vo.site.seo;


import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Schema(description = "检索信息配置查询详情VO")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteSeoFindByIdVO {

    /**
     *  主键id
     */
    @Schema(title = "id")
    @NotNull(message = ConstantsCode.PARAM_MISSING)
    private String id;
}
