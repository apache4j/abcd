package com.cloud.baowang.system.api.vo.site;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/7/27 11:58
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="站点配置对象")
public class SiteConfigVO {

    @Schema(description ="后台名称")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String bkName;

    @Schema(description ="皮肤模版")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String skin;

    @Schema(description ="白底-长logo")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String longLogo;

    @Schema(description ="白底-短logo")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String shortLogo;

    @Schema(description ="黑底-长logo")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String blackLongLogo;

    @Schema(description ="黑底-短logo")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String blackShortLogo;

    @Schema(description ="选中的活动模版")
    private List<String> checkActivityTemplate;
}
