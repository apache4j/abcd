package com.cloud.baowang.agent.api.vo.agent;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kimi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "设置代理语言")
public class SetAgentLanguageVO {

    @Schema(description ="语言lang")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String lang;
}
