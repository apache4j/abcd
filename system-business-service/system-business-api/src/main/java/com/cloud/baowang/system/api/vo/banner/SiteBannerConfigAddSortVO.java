package com.cloud.baowang.system.api.vo.banner;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@I18nClass
@Schema(description = "站点banner排序实体")
public class SiteBannerConfigAddSortVO {
    @Schema(description = "所在位置")
    private String gameOneClassId;

    @Schema(description = "轮播图区域，标识轮播图所属区域")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.BANNER_AREA)
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer bannerArea;

    @Schema(description = "区域名称")
    private String bannerAreaText;

    @Schema(description = "排序")
    private Integer sort;

}
