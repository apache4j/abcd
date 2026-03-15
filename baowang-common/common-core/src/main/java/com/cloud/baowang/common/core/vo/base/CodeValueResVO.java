package com.cloud.baowang.common.core.vo.base;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "后台-code和value返回对象集合")
@I18nClass
public class CodeValueResVO {
    @Schema(title = "类型")
    private String type;

    @I18nField
    @Schema(title = "类型具体值")
    private List<CodeValueVO> codeValues;
}
