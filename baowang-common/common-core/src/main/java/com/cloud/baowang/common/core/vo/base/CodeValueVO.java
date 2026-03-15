package com.cloud.baowang.common.core.vo.base;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title = "后台-code和value返回对象")
public class CodeValueVO implements Serializable {

    @Schema(title = "类型")
    private String type;


    @Schema(title = "编码")
    private String code;

    @I18nField
    @Schema(title = "值")
    private String value;


    public CodeValueVO(String code, String value) {
        this.code = code;
        this.value = value;
    }
}
