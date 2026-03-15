package com.cloud.baowang.agent.api.vo.merchant;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "新增商务 vo")
public class AddMerchantPageQueryVO extends PageVO implements Serializable {
    @Schema(description = "站点", hidden = true)
    private String siteCode;
    @Schema(description = "商务账号")
    private String merchantAccount;
    @Schema(description = "商务名称")
    private String merchantName;
    @Schema(description = "账号状态")
    private Integer status;
    @Schema(description = "风控id")
    private String riskId;
    @Schema(description = "注册时间-开始时间")
    private Long registerTimeStart;
    @Schema(description = "注册时间-结束时间")
    private Long registerTimeEnd;
    @Schema(description = "最近操作时间-开始时间")
    private Long updatedTimeStart;
    @Schema(description = "最近操作时间-开始时间")
    private Long updatedTimeEnd;
}
