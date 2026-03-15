package com.cloud.baowang.system.api.vo.operations;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="查询客服通道列表对象")
public class CustomerChannelRequestVO extends PageVO {

    @Schema(description ="通道名称")
    private String channelName;

    @Schema(description ="通道代码")
    private String channelCode;

    @Schema(description ="状态;1-启用,0-禁用 字典CODE:ENABLE_DISABLE_TYPE")
    private String status;

    @Schema(description ="站点code")
    private String siteCode;

    @Schema(description = "三方平台code")
    private String platformCode;

}
