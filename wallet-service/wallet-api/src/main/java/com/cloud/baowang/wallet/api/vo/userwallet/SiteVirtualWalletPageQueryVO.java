package com.cloud.baowang.wallet.api.vo.userwallet;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "站点虚拟币配置查询对象")
public class SiteVirtualWalletPageQueryVO extends PageVO {
    @Schema(description = "站点编号")
    private String siteCode;

    @Schema(description = "站点名称")
    private String siteName;


    @Schema(description = "所属公司")
    private String parentCompany;
}
