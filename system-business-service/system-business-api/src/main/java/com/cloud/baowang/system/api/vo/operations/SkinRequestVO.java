package com.cloud.baowang.system.api.vo.operations;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="查询皮肤列表对象")
public class SkinRequestVO extends PageVO {

    @Schema(description ="皮肤名称")
    private String skinName;

    @Schema(description ="皮肤代码")
    private String skinCode;

    @Schema(description ="状态 字典CODE:ENABLE_DISABLE_TYPE")
    private Integer status;

}
