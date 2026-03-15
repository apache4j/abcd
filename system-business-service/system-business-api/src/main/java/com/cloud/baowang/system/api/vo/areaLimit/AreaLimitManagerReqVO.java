package com.cloud.baowang.system.api.vo.areaLimit;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "区域限制分页查询条件vo")
public class AreaLimitManagerReqVO extends PageVO {
    @Schema(description = "名称")
    private String name;
    @Schema(description = "国家code")
    private String areaCode;
    @Schema(description = "生效状态")
    private Integer status;
    @Schema(description = "操作人")
    private String operator;
}
