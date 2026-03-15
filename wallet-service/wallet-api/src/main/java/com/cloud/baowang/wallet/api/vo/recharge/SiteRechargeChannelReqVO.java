package com.cloud.baowang.wallet.api.vo.recharge;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: qiqi
 **/
@Data
@Schema(description = "站点存款通道查询条件")
public class SiteRechargeChannelReqVO extends PageVO {

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    /**
     * 通道类型
     */
    @Schema(description = "通道类型")
    private String channelType;

    /**
     * 充值方式
     */
    @Schema(description = "充值方式")
    private String rechargeWayId;

    /**
     * 通道名称
     */
    @Schema(description = "通道名称")
    private String channelName;

    /**
     * 状态 0:禁用 1:启用
     */
    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;
    @Schema(description = "站点编码",hidden = true)
    private String siteCode;
}
