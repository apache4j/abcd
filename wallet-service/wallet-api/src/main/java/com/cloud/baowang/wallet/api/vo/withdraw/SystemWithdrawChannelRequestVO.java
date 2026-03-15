package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 14:03
 * @Version: V1.0
 **/
@Data
@Schema(description = "提现类型查询条件")
public class SystemWithdrawChannelRequestVO extends PageVO {

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
     * 提现方式
     */
    @Schema(description = "提现方式Id")
    private String withdrawWayId;

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
}
