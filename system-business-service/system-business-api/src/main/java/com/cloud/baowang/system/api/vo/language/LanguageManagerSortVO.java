package com.cloud.baowang.system.api.vo.language;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@Builder
@Schema(description = "语言管理新增vo")
@NoArgsConstructor
@AllArgsConstructor
public class LanguageManagerSortVO implements Serializable {
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String id;
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer sort;

}
