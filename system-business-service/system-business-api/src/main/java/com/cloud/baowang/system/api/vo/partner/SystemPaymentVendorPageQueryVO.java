package com.cloud.baowang.system.api.vo.partner;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统支付商分页查询视图对象
 */
@Data
@Schema(description = "支付商分页查询视图对象")
public class SystemPaymentVendorPageQueryVO extends PageVO {

    @Schema(description = "支付商名称")
    private String vendorName;

    @Schema(description = "启用状态0.禁用，1.启用")
    private Integer status;
}
