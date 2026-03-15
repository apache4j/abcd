package com.cloud.baowang.system.api.vo.partner;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "合作赞助商视图对象")
public class SystemPartnerPageQueryVO extends PageVO {


    @Schema(description = "赞助商名称")
    private String partnerName;


    @Schema(description = "启用状态0.禁用，1.启用 system_param enable_status")
    private Integer status;

}
