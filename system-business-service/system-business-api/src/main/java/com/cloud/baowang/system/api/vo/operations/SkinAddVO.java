package com.cloud.baowang.system.api.vo.operations;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "新增皮肤对象")
public class SkinAddVO {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "皮肤编码")
    @NotBlank(message = "皮肤编码不能为空")
    @Size(max = 30, message = ConstantsCode.PARAM_ERROR)
    private String skinCode;

    @Schema(description = "皮肤名称")
    @NotBlank(message = "皮肤名称不能为空")
    @Size(max = 20, message = ConstantsCode.PARAM_ERROR)
    private String skinName;

    /***@Schema(description = "PC皮肤地址")
    @NotBlank(message = "pc皮肤地址不能为空")
    @Size(max = 100, message = "PC皮肤地址长度在1-100个字符之间")
    private String pcAddr;

    @Schema(description = "H5皮肤地址")
    @NotBlank(message = "H5皮肤地址不能为空")
    @Size(max = 100, message = "H5皮肤地址长度在1-100个字符之间")
    private String h5Addr;***/

    @Schema(description = "备注")
    @Size(max = 500, message = ConstantsCode.PARAM_ERROR)
    private String remark;

    @Schema(description = "updaterName", hidden = true)
    private String updaterName;

}
