package com.cloud.baowang.system.api.vo.partner;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "站点支付商分页查询对象")
public class SitePaymentVendorPageQueryVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "支付商名称")
    private String vendorName;

    @Schema(description = "启用状态0.禁用，1.启用")
    private Integer status;

}
