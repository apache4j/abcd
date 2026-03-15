package com.cloud.baowang.system.api.vo.operations;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="绑定域名请求对象")
public class DomainBindVO {

    @Schema(description ="id")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<String> idList;

    @Schema(description ="站点Code")
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String siteCode;

    @Schema(description ="updaterName", hidden = true)
    private String operator;
}
