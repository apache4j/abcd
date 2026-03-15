package com.cloud.baowang.play.api.vo.order.client;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "客户端注单记录请求入参")
public class OrderRecordClientReqVO extends PageVO {
    @Schema(description = "场馆code", hidden = true)
    private String siteCode;
    @Schema(description = "会员id", hidden = true)
    private String userId;
    @Schema(description = "会员账号", hidden = true)
    private String userAccount;
    @Schema(description = "时区", hidden = true)
    private String timezone;


    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "游戏类别", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer venueType;
    @Schema(description = "场馆 数组", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> venueCodes;
    @Schema(description = "注单状态 数组", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<Integer> orderClassifyList;
    @Schema(description = "投注开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long betStartTime;
    @Schema(description = "投注结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long betEndTime;
    @Schema(description = "沙巴用户token venueType = 1 必传", hidden = true)
    private String sabToken;
}
