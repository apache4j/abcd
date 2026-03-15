package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : 小智
 * @Date : 2024/7/30 15:49
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="提款通道请求参数")
public class WithdrawChannelReqVO extends PageVO {

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "通道名称")
    private String channelName;

    @Schema(description = "通道代码")
    private String channelCode;

    @Schema(description = "提款方式Id")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String withdrawWayId;

    @Schema(description = "状态")
    private String status;
}
