package com.cloud.baowang.wallet.api.vo.userCoinRecord;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(title ="会员链上资金管理请求对象")
public class AgentHotWalletAddressRequestVO extends PageVO {

    @Schema(description="站点编码",hidden = true)
    private String siteCode;

    @Schema(description="代理账号")
    private String agentAccount;

    @Schema(description="trc钱包地址")
    private String trcAddress;

    @Schema(description="erc钱包地址")
    private String ercAddress;

    @Schema(description = "是否导出查询 true 是 false 否", hidden = true)
    private Boolean exportFlag = false;
}
