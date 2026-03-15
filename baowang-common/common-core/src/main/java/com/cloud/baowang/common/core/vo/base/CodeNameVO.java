package com.cloud.baowang.common.core.vo.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "后台-code和name返回对象")
public class CodeNameVO {

    @Schema(title = "编码")
    private String code;

    @Schema(title = "描述|名称")
    private String name;
}
